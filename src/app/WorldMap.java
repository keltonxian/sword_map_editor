package app;

import io.xml.XMLWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import control.AddCity;
import control.ControlPool;

// sun.misc.Compare & sun.misc.Sort Removed in Java8
// Current users of sun.misc.Sort can use the sort() method of java.util.Arrays as better alternative. 
// Users of sun.misc.Compare can use java.util.Comparator as a replacement
//import sun.misc.Compare;
//import sun.misc.Sort;

import app.Const;
import app.EditorShell;
import app.MapCanvas;

//import control.AddCity;
//import control.ControlPool;

public class WorldMap {
	public final static byte TILES_WIDTH = 24;
	public final static byte TILES_HEIGHT = 24;
	public final static byte TILES_WIDTHC = TILES_WIDTH>>1;
	public final static byte TILES_HEIGHTC = TILES_HEIGHT>>1;
	
	// 方向定义
	public static final byte DIR_RIGHT   = 0;
	public static final byte DIR_LEFT    = 1;
	public static final byte DIR_DOWN    = 2;
	public static final byte DIR_UP      = 3;

	
    public int mapWidth;
    public int mapHeight;
	
	
	public String mapPath = null;
	
	public Image imgBG = null;
	
	public int w=240;
	public int h=320;
	
	public ArrayList<City> cityList = new ArrayList<City>();
	public ArrayList<City> selectedCities = new ArrayList<City>();
	public int cityTop=-1;
	
	public City currentCity = null;
	
	public WorldMap(){
		try{
			imgBG = new Image(Display.getCurrent(), Const.PATH_USER+"background.png");
			Rectangle rect = imgBG.getBounds();
			w = rect.width;
			h = rect.height;
		}catch(Exception ex){	
		}
	}
	
    
    public void drawMap(GC g,int offsetX,int offsetY){
    	Transform trans = new Transform(Display.getCurrent());
    	trans.translate(offsetX, offsetY);
    	g.setTransform(trans);
		trans.translate(-offsetX, -offsetY);
		if(imgBG!=null){
			g.drawImage(imgBG, 0, 0);
		}
		g.setTransform(trans);
    }
    
	public void paint(GC g,int offsetX,int offsetY){
		g.setBackground(Const.COLOR_WHITE);
		g.fillRectangle(offsetX, offsetY, w, h);
		
		
		if(MapCanvas.instance.isShowBG)
			drawMap(g, offsetX, offsetY);
//			g.drawImage(imgBG, offsetX, offsetY);
		City link;
		int dir =0;
		int[][] points;
		for(City _city:cityList){
			_city.paint(g, offsetX, offsetY,selectedCities.contains(_city));
		}
			//画连接线,遍历4方向
		for(City _city:cityList){
			if(!MapCanvas.instance.isShowLink)
				continue;
			for(dir=0;dir<_city.linkCities.length;dir++){
				if(_city.linkCities[dir]==null){
					continue;
				}
				link = _city.linkCities[dir];
				// 之前的城市，肯定已画过
				if(cityList.contains(link)&&cityList.indexOf(link)<cityList.indexOf(_city)){
					continue;
				}
				points = getLink(_city,dir,_city.linkCities[dir],_city.linkDoor[dir]);
				
				if(points==null)
					continue;
				g.setLineStyle(SWT.LINE_SOLID);
				g.setForeground(Const.COLOR_DARK_BLUE);
				for(int j=0;j<points.length-1;j++){
					g.drawLine(points[j][0]+offsetX, points[j][1]+offsetY, points[j+1][0]+offsetX, points[j+1][1]+offsetY);
				}
					
			}
			// 画地图名
		}
	}
	
	public int[][] getLink(City c0,int dir0,City c1,int dir1){
		//交换使其符合列举的情况
		if(
				(dir0==DIR_RIGHT && dir1==DIR_UP) ||
				(dir0==DIR_DOWN && dir1==DIR_UP) ||
				(dir0==DIR_LEFT && dir1==DIR_UP) ||
				(dir0==DIR_DOWN && dir1==DIR_RIGHT) ||
				(dir0==DIR_LEFT && dir1==DIR_RIGHT) ||
				(dir0==DIR_LEFT && dir1==DIR_RIGHT) ||
				(dir0==DIR_LEFT && dir1==DIR_DOWN) ||
				(dir0==dir1 && (
						(dir0==DIR_UP && c0.y<c1.y) ||
						(dir0==DIR_RIGHT && c0.x<c1.x) ||
						(dir0==DIR_DOWN && c0.y>c1.y) ||
						(dir0==DIR_LEFT && c0.x<c1.x)
				))
				
		){
			City tmpCity = c0;
			c0=c1;
			c1=tmpCity;
			int tmpDir = dir0;
			dir0 = dir1;
			dir1=tmpDir;
		}
		int[][] points=null;
		//中心定位，要修改xy
		int x0=c0.x;
		int y0=c0.y;
		int w0=c0.w;
		int h0=c0.h;
		int x1=c1.x;
		int y1=c1.y;
		int w1=c1.w;
		int h1=c1.h;
		int d=10;
		//计算节点数列 列举所有组合情况
		if(dir0==DIR_UP){
			y0-=h0>>1;
			if(dir0==dir1){
				y1-=h1>>1;
				if(x0>=x1-(w1>>1) && x0<=x1+(w1>>1) && y1+h1<y0){
					points = new int[][]{
							{x0,y0},
							{x0,y0+y1+h1>>1},
							{x1+(w1>>1)+d,y0+y1+h1>>1},
							{x1+(w1>>1)+d,y1-d},
							{x1,y1-d},
							{x1,y1},
					};
				}else{
					points = new int[][]{
							{x0,y0},
							{x0,Math.min(y0, y1)-d},
							{x1,Math.min(y0, y1)-d},
							{x1,y1},
					};
				}
			}
			else if(dir1==DIR_RIGHT){
				x1+=w1>>1;
				if(x1<x0 && y1<y0){
					points = new int[][]{
							{x0,y0},
							{x0,y1},
							{x1,y1},
					};
				}
				else if(x1>=x0 && y1+(h1>>1)<y0){
					points = new int[][]{
							{x0,y0},
							{x0,y0+y1+h1/2>>1},
							{x1+d,y0+y1+h1/2>>1},
							{x1+d,y1},
							{x1,y1},
					};
				}
				else if(x1<x0-w0/2 && y1>=y0){
					points = new int[][]{
							{x0,y0},
							{x0,y0-d},
							{x1+x0-w0/2>>1,y0-d},
							{x1+x0-w0/2>>1,y1},
							{x1,y1},
					};
				}
				else{
					points = new int[][]{
							{x0,y0},
							{x0,Math.min(y0, y1-h1/2)-d},
							{Math.max(x0+w0/2,x1)+d,Math.min(y0, y1-h1/2)-d},
							{Math.max(x0+w0/2,x1)+d,y1},
							{x1,y1},
					};
				}
			}
			else if(dir1==DIR_DOWN){
				y1+=h1>>1;
				if(y0>y1){
					points = new int[][]{
							{x0,y0},
							{x0,y0+y1>>1},
							{x1,y0+y1>>1},
							{x1,y1},
					};
				}
				else if(y1>=y0 && x0+w0/2 < x1-w1/2){
					points = new int[][]{
							{x0,y0},
							{x0,y0-d},
							{x0+w0/2+x1-w1/2>>1,y0-d},
							{x0+w0/2+x1-w1/2>>1,y1+d},
							{x1,y1+d},
							{x1,y1},
					};
				}
				else if(y1>=y0 && x0-w0/2 > x1+w1/2 ){
					points = new int[][]{
							{x0,y0},
							{x0,y0-d},
							{x0-w0/2+x1+w1/2>>1,y0-d},
							{x0-w0/2+x1+w1/2>>1,y1+d},
							{x1,y1+d},
							{x1,y1},
					};
				}
				else{
					int tmpX = x1>x0?Math.max(x0+w0/2,x1+w1/2)+d:Math.min(x0-w0/2,x1-w1/2)-d;
					points = new int[][]{
							{x0,y0},
							{x0,y0-d},
							{tmpX,y0-d},
							{tmpX,y1+d},
							{x1,y1+d},
							{x1,y1},
					};
				}
			}
			else if(dir1 == DIR_LEFT){
				x1-=w1>>1;
				if(x1>x0 && y1<y0){
					points = new int[][]{
							{x0,y0},
							{x0,y1},
							{x1,y1},
					};
				}
				else if(x1-w1/2<=x0 && y1+h1/2<=y0){
					points = new int[][]{
							{x0,y0},
							{x0,y1+h1/2+y0>>1},
							{x1-d,y1+h1/2+y0>>1},
							{x1-d,y1},
							{x1,y1},
					};
				}
				else if(x0+w0/2<x1 && y1>y0){
					points = new int[][]{
							{x0,y0},
							{x0,y0-d},
							{x0+w0/2+x1>>1,y0-d},
							{x0+w0/2+x1>>1,y1},
							{x1,y1},
					};
				}
				else{
					points = new int[][]{
							{x0,y0},
							{x0,Math.min(y0,y1)-d},
							{Math.min(x0, x1)-d,Math.min(y0,y1)-d},
							{Math.min(x0, x1)-d,y1},
							{x1,y1},
					};
				}
			}
		}
		else if(dir0==DIR_RIGHT){
			x0+=w0>>1;
			if(dir0==dir1){
				x1+=w1>>1;
				if(y0+h0/2>=y1 && y0-h0/2<=y1 && x0-w0<x1){
					points = new int[][]{
							{x0,y0},
							{x0+d,y0},
							{x0+d,y0+h0/2+d},
							{x0-w0+x1>>1,y0+h0/2+d},
							{x0-w0+x1>>1,y1},
							{x1,y1},
					};
				}
				else{
					points = new int[][]{
							{x0,y0},
							{Math.max(x0,x1)+d,y0},
							{Math.max(x0,x1)+d,y1},
							{x1,y1},
					};
				}
			}
			else if(dir1==DIR_DOWN){
				y1+=h1>>1;
				if(x0<x1 && y0>y1){
					points = new int[][]{
							{x0,y0},
							{x1,y0},
							{x1,y1},
					};
				}
				else if(x0<x1-w1/2 && y0<=y1){
					points = new int[][]{
							{x0,y0},
							{x0+x1-w1/2>>1,y0},
							{x0+x1-w1/2>>1,y1+d},
							{x1,y1+d},
							{x1,y1},
					};
				}
				else if(x0>=x1&&y1+h1/2<y0-h0/2){
					points = new int[][]{
							{x0,y0},
							{x0+d,y0},
							{x0+d,y0-h0/2+y1+h1/2>>1},
							{x1,y0-h0/2+y1+h1/2>>1},
							{x1,y1},
					};
				}
				else{
					points = new int[][]{
							{x0,y0},
							{Math.max(x0,x1)+d,y0},
							{Math.max(x0,x1)+d,Math.max(y0,y1)+d},
							{x1,Math.max(y0,y1)+d},
							{x1,y1},
					};
				}
			}
			else if(dir1 == DIR_LEFT){
				x1-=w1>>1;
				if(x0<x1){
					points = new int[][]{
							{x0,y0},
							{x0+x1>>1,y0},
							{x0+x1>>1,y1},
							{x1,y1},
					};
				}
				else if(x0>=x1 && y0-h0/2>y1+h1/2){
					points = new int[][]{
							{x0,y0},
							{x0+d,y0},
							{x0+d,y0-h0/2+y1+h1/2>>1},
							{x1-d,y0-h0/2+y1+h1/2>>1},
							{x1-d,y1},
							{x1,y1},
					};
				}
				else if(x0>=x1 && y0+h0/2<y1-h1/2){
					points = new int[][]{
							{x0,y0},
							{x0+d,y0},
							{x0+d,y0+h0/2+y1-h1/2>>1},
							{x1-d,y0+h0/2+y1-h1/2>>1},
							{x1-d,y1},
							{x1,y1},
					};
				}
				else{
					int tmpY = y0>y1?Math.min(y0-h0/2, y1-h1/2)-d:Math.max(y0+h0/2, y1+h1/2)+d;
					points = new int[][]{
							{x0,y0},
							{Math.max(x0,x1)+d,y0},
							{Math.max(x0,x1)+d,tmpY},
							{Math.min(x0-w0,x1-w1)-d,tmpY},
							{Math.min(x0-w0,x1-w1)-d,y1},
							{x1,y1},
					};
				}
			}
		}
		else if(dir0==DIR_DOWN){
			y0+=h0>>1;
			if(dir1==dir0){
				y1+=h1>>1;
				if(x0>=x1-w1/2 && x0<=x1+w1/2 && y0<y1-h1){
					points = new int[][]{
							{x0,y0},
							{x0,y0+y1-h1>>1},
							{x1-w1/2-d,y0+y1-h1>>1},
							{x1-w1/2-d,y1+d},
							{x1,y1+d},
							{x1,y1},
					};
				}
				else{
					points = new int[][]{
							{x0,y0},
							{x0,Math.max(y0,y1)+d},
							{x1,Math.max(y0,y1)+d},
							{x1,y1},
					};
				}
			}
			else if(dir1==DIR_LEFT){
				x1-=w1>>1;
				if(x0<x1&&y0<y1){
					points = new int[][]{
							{x0,y0},
							{x0,y1},
							{x1,y1},
					};
				}
				else if(x0>=x1 && y0<y1-h1/2){
					points = new int[][]{
							{x0,y0},
							{x0,y0+y1-h1/2>>1},
							{x1-d,y0+y1-h1/2>>1},
							{x1-d,y1},
							{x1,y1},
					};
				}
				else if(x0+w0/2<x1 && y0>=y1){
					points = new int[][]{
							{x0,y0},
							{x0,y0+d},
							{x0+w0/2+x1>>1,y0+d},
							{x0+w0/2+x1>>1,y1},
							{x1,y1},
					};
				}
				else{
					points = new int[][]{
							{x0,y0},
							{x0,Math.max(y0,y1)+d},
							{Math.min(x0-w0/2,x1-w1/2),Math.max(y0,y1)+d},
							{Math.min(x0-w0/2,x1-w1/2),y1},
							{x1,y1},
					};
				}
			}
		}
		else if(dir0==DIR_LEFT){
			x0-=w0>>1;
			if(dir1==dir0){
				x1-=w1>>1;
				if(x0>x1+w1&&y0>=y1-h1/2&&y0<=y1+h1/2){
					points = new int[][]{
							{x0,y0},
							{x0+x1+w1>>1,y0},
							{x0+x1+w1>>1,y1-h1/2-d},
							{x1-d,y1-h1/2-d},
							{x1-d,y1},
							{x1,y1},
					};
				}
				else{
					points = new int[][]{
							{x0,y0},
							{Math.min(x0,x1)-d,y0},
							{Math.min(x0,x1)-d,y1},
							{x1,y1},
					};
				}
			}
		}
		return points;
	}
	
	
	public void newCity(int x, int y){
		City city = new City(x,y);
		city.id  = ++cityTop;
		ControlPool.appendAndDo(new AddCity(this,city));
	}
	
	public void addCity(City _city){
		cityList.add(_city);
	}
	public void removeCity(City _city){
		cityList.remove(_city);
	}
	
	public void selectCity(City _city){
		currentCity=_city;
		selectedCities.clear();
		if(currentCity!=null)
			selectedCities.add(currentCity);
		
		CityPropShell.instance.setCity(_city);
	}
	
	public void selectCity(Rectangle rect){
		selectedCities.clear();
		for(City _city:cityList){
			if(rect.contains(_city.x, _city.y)){
				selectedCities.add(_city);
			}
		}
		if(selectedCities.size()>0){
			CityPropShell.instance.setCity((City)selectedCities.get(0));
		}
	}
	
	public City getCity(int x,int y){
		City _city = null;
		City temp;
		for(int i=cityList.size()-1;i>=0;i--){
			temp = (City)cityList.get(i);
			
			if(x>temp.x-(temp.w>>1)&&x<temp.x+(temp.w>>1)&&y>temp.y-(temp.h>>1)&&y<temp.y+(temp.h>>1)){
				_city = temp;
				break;
			}
		}
		
		return _city;
	}
	
	public City getCity(int _id){
		City c;
		for(int i =0;i<cityList.size();i++){
			c=cityList.get(i);
			if(_id==c.id){
				return c;
			}
		}
		return null;
	}
	
	
	public static WorldMap fromXML(String fileName){
		WorldMap map = new WorldMap();
		File file = new File(fileName);
		if(file.exists()==false){
			return map;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList nodeList = doc.getElementsByTagName("world");
			Node root = nodeList.item(0);
			if(root==null)
				return null;
			NodeList cityNodeList = root.getChildNodes();
			Node node;
			for(int i=0;i<cityNodeList.getLength();i++){
				node = cityNodeList.item(i);
				if(node==null){
					continue;
				}
				if("map".equals(node.getNodeName())==false){
					continue;
				}
				map.addCity(City.fromXML(node));
			}
			
			//重整城市
			for(City c:map.cityList){
				for(int j=0;j<c.linkCities.length;j++){
					if(c.linkCityID[j]>=0){
						c.linkCities[j]=map.getCity(c.linkCityID[j]);
					}
				}
			}
		}
		catch(Exception ex){
		}
		return map;
	}
	
	public void toXML(XMLWriter xmlWriter) throws Exception{
		xmlWriter.startDocument();
		// 写地图数据
		xmlWriter.startElement("world");
		for (City city : cityList) {
			city.toXML(xmlWriter);
		}
		xmlWriter.endElement(); // element "world"
		xmlWriter.endDocument();
	}

}

