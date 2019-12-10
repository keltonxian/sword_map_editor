package app;

import io.xml.XMLWriter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Node;

public class City {
	
	// 城市等级最大值设定
	public static final int MAX_LEVEL = 21;

	// 方向定义
	public static final byte DIR_RIGHT   = 0;
	public static final byte DIR_LEFT    = 1;
	public static final byte DIR_DOWN    = 2;
	public static final byte DIR_UP      = 3;
	
	public static HashMap<Integer, String> cityTable = new HashMap<Integer, String>();
	
	//城市图片资源定义
	public static Image imgCity;
	
	public static void initCity(){
		imgCity = new Image(Display.getCurrent(),Const.PATH_USER+"city.png");
		
		//载入城市名
		File dir = new File(Const.PATH_USER+Const.PATH_TEXT_EVENT);
		if(dir.exists()==false || dir.isDirectory()==false){
			return;
		}
		File[] fileList = dir.listFiles();
		FileInputStream fis=null;
		BufferedReader br=null;
		String name,fileName;
		int id = -1;
		for(File file:fileList){
			if(file.isDirectory()){
				continue;
			}
			id = -1;
			
			try{
				fileName = file.getName();
				id = Integer.parseInt(fileName.substring(0, fileName.indexOf('.')));
				fis = new FileInputStream(file);
				br=new BufferedReader(new InputStreamReader(fis,"UTF-8"));
				name = br.readLine().trim();
				cityTable.put(id, name);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			finally{
				try{br.close();}catch(Exception ex){}
				try{fis.close();}catch(Exception ex){}
			}
		}
	}
	
	/**
	 * 是否可见 
	 */
	public boolean visible=true;
	
	/**
	 * 坐标
	 */
	public int x,y;
	
	/**
	 * 尺寸
	 */
	public int w,h;
	
	/**
	 * 城市图片，类型显示</br>
	 * 0: 皇城</br>
	 * 1: 城镇</br>
	 * 2: 村庄</br>
	 * 3: 普通地点(森林,峡谷等,蓝点显示)</br>
	 * 4: 废墟</br>
	 * 5: 关卡</br>
	 * 6: 跳转点</br>
	 */
	public int type;
	
	/**
	 * 这个城市向4个方向走可到达的城市，可以null</br>
	 * 目的地城市至少有一个链接点等于本身</br>
	 * 即:</br> 
	 * (this.linkCities[n].linkCities 包含 this)= true;</br>
	 */
	public City[] linkCities=new City[4];
	public int[] linkCityID = new int[]{-1,-1,-1,-1};
	
	/**
	 * 这个城市向4个方向走可到达的城市的城门（东南西北）</br>
	 */
	public int[] linkDoor = new int[4];
	
	/**
	 * 城市名
	 */
	public String name = "";
	/**
	 * 城市id</br>
	 * 当前地图唯一
	 */
	public int id;
	
	public void init(){
		setType(3);//3 普通地点(森林,峡谷等,蓝点显示)
		name = "";
		linkCities=new City[4];
	}
	
	public City(){
		init();
	}
	
	public City(int _x,int _y){
		init();
		x=_x;
		y=_y;
	}
	
	public void setType(int _type){
		type=_type;
		Rectangle rect  = imgCity.getBounds();
		w = rect.width;
		h = rect.height;
	}
	
	public City cloneCity(){
		
		City city = new City();
		
		city.setType(type);
		
		return city;
	}
	
	public void paint(GC g,int offsetX,int offsetY,boolean isSelected){
		if(!visible){
			return;
		}
		int drawX,drawY;
		drawX = offsetX+x;
		drawY = offsetY+y;
		Image img = imgCity;
		if(img !=null){
			g.drawImage(img, drawX-(w>>1), drawY-(h>>1));
		}
		int nameW = getStringWidth(g, name);
		g.setForeground(Const.COLOR_BLACK);
		g.drawString(name, drawX-nameW/2, drawY-h/2-18);
		if(!isSelected) return;
		// 画选择框
		g.setForeground(Const.COLOR_BLACK);
		g.setLineStyle(SWT.LINE_DOT);
		g.drawRectangle(drawX-(w>>1)-2, drawY-(h>>1)-2, w+3, h+3);
		g.setForeground(Const.COLOR_WHITE);
		g.drawRectangle(drawX-(w>>1)-3, drawY-(h>>1)-3, w+5, h+5);
//		ImageData imgData = cursor.getImageData();
//		g.drawImage(cursor, drawX-(imgData.width>>1), drawY-(imgData.height>>1));
	}
	
	private static int getStringWidth(GC g,String str){
		if(str==null){
			return 0;
		}
		int width=0;
		for(int i=0;i<str.length();i++){
			width += g.getAdvanceWidth(str.charAt(i));
		}
		return width;
			
	}
	
	public byte[] toByte() throws Exception{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos= new DataOutputStream(baos);
		dos.writeInt(id);
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(type);
		// 写城出口
		for(int j=0;j<linkCities.length;j++){
			if(linkCities[j]!=null){
				dos.writeInt(linkCities[j].id);
			}
			else{
				dos.writeInt(-1);
			}
			dos.writeByte(linkDoor[j]);
		}
		dos.flush();
		return baos.toByteArray();
	}
	

	
	public static City fromByte(byte[] data) throws Exception{
		City c = new City();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		c.id = dis.readInt();
//		System.out.println("reading city id = "+c.id+"   name = "+c.name);
		c.x=dis.readInt();
		c.y=dis.readInt();
		c.type=dis.readInt();
		c.setType(c.type);
		for(int j=0;j<c.linkCityID.length;j++){
			c.linkCityID[j] = dis.readInt();
			c.linkDoor[j]=dis.readByte();
		}
		return c;
	}

	
	public static City importCity(byte[] data) throws Exception{
		City c = new City();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		c.name = dis.readUTF();
		c.x=dis.readInt();
		c.y=dis.readInt();
		c.type=dis.readInt();
		c.setType(c.type);
		return c;
	}

	
	public void toXML(XMLWriter xmlWriter)throws Exception {
		// 写城市数据
		xmlWriter.startElement("map");

		xmlWriter.writeAttribute("id", id);
		xmlWriter.writeAttribute("x", x);
		xmlWriter.writeAttribute("y", y);
		for (int i = 0; i < 4; i++) {
			xmlWriter.writeAttribute("link" + i, linkCities[i] == null ? -1
					: linkCities[i].id);
			xmlWriter.writeAttribute("door" + i, linkDoor[i]);
		}

		xmlWriter.endElement(); // element "world"
	}
	
	public static City fromXML(Node node){
		City c = new City();
		c.id = Util.getAttribute(node, "id", 0);
		c.x = Util.getAttribute(node, "x", 0);
		c.y = Util.getAttribute(node, "y", 0);
		for(int i=0;i<4;i++){
			c.linkCityID[i] = Util.getAttribute(node, "link"+i, -1);
			c.linkDoor[i] = Util.getAttribute(node, "door"+i, -1);
		}
		c.name=cityTable.get(c.id);
		if(c.name==null)
			c.name = "";
		return c;
	}
}
