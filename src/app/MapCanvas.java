package app;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

public class MapCanvas extends Canvas  implements Runnable,MouseListener,PaintListener,MouseMoveListener{

	
	public static final byte DIR_RIGHT   = 0;
	public static final byte DIR_LEFT    = 1;
	public static final byte DIR_DOWN    = 2;
	public static final byte DIR_UP      = 3;

	
	public static MapCanvas instance;
	//线程
	private boolean isRunning = false;
	private boolean isPause = false;
	private final static int TIME_FRAME= 60;
	
	//系统对象
	private Composite shell;
	private Display device;
	
	//界面控件对象
	private ScrollBar hBar;
	private ScrollBar vBar;
	
	//观察窗 x y 相对于地图坐标
	private int viewX;
	private int viewY;
	private int viewW;
	private int viewH;
	
	//地图尺寸像素级
	
	//鼠标执行模式
	public int mouseMode =0;
	public Point pressPoint;
	public Point currentPoint;
	private City linkCity0;
	private int linkDir0 = DIR_UP;
	private City linkCity1;
	private int linkDir1 = DIR_UP;
	
	// 显示设定
	public boolean isShowLink=true;
	public boolean isShowBG=true;
	
	WorldMap worldMap;
	
	public WorldMap getWorldMap(){
		return worldMap;
	}

	public MapCanvas(Composite parent, int style) {
		super(parent, style);
		instance=this;
		addMouseListener(this);
		addMouseMoveListener(this);
		addPaintListener(this);
		shell = parent;
		device = shell.getDisplay();
		setVisible(true);
		hBar=getHorizontalBar();
		vBar=getVerticalBar();
		hBar.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event) {
				viewX=hBar.getSelection();
				instance.redraw();
			}
		});
		vBar.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event) {
				viewY=vBar.getSelection();
				instance.redraw();
			}
		});
		
//		WorldMap.loadTiles();
	}
	
	public void start(){
		if(isRunning)
			return;
		isRunning = true;
//		System.out.println("thread start");
		Thread thread = new Thread(this);
//		device.timerExec(60,this);
		thread.start();
	}
	public void stop(){
		isRunning = false;
	}
	public void pause(){
		isPause = true;
	}
	public void resume(){
		isPause = false;
	}
	
	public void run(){
//		System.out.println("run");
//		update();
//		
		long timeFrameStart,timeFrameUsed;
		PaintThread paintThread = new PaintThread();
		while(isRunning){
			timeFrameStart= System.currentTimeMillis();
			if(device.isDisposed())
				break;
			device.asyncExec(paintThread);
			timeFrameUsed =  System.currentTimeMillis()- timeFrameStart;
			try{
				if(timeFrameUsed<TIME_FRAME-10)
					Thread.sleep(TIME_FRAME - timeFrameUsed);
				else
					Thread.sleep(10);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	boolean isPainting=false;
	public void paint(GC g){
		if(isPainting)
			return;
		isPainting = true;
		g.setBackground(Const.COLOR_GRAY);
		Rectangle viewBound = getBounds();
		viewW = viewBound.width;
		viewH = viewBound.height;
		g.fillRectangle(0, 0, viewW, viewH);
		
		if(worldMap==null){
			vBar.setVisible(false);
			hBar.setVisible(false);
			isPainting=false;
			return;
		}
		if(viewW>=worldMap.w){
			viewX = worldMap.w-viewW>>1;

			hBar.setVisible(false);
		}
		else{
			hBar.setMaximum(worldMap.w);
			hBar.setMinimum(0);
			hBar.setThumb(viewW);
			viewX = hBar.getSelection();
			hBar.setVisible(true);
		}
		if(viewH>=worldMap.h){
			viewY = worldMap.h-viewH>>1;
			vBar.setVisible(false);
		}
		else{
			vBar.setMaximum(worldMap.h);
			vBar.setMinimum(0);
			vBar.setThumb(viewH);
			viewY = vBar.getSelection();
			vBar.setVisible(true);
		}
		
		worldMap.paint(g,-viewX,-viewY);
		
		//画鼠标显示
		if(mouseMode == Const.MOUSE_MODE_SELECT){
			Rectangle rect = new Rectangle(
					Math.min(currentPoint.x,pressPoint.x),
					Math.min(currentPoint.y,pressPoint.y),
					Math.abs(currentPoint.x-pressPoint.x),
					Math.abs(currentPoint.y-pressPoint.y)
			);
			g.setLineStyle(SWT.LINE_DOT);
			g.setForeground(Const.COLOR_BLACK);
			g.drawRectangle(rect);
		}
		// 连接线模式
		if(mouseMode == Const.MOUSE_MODE_LINK){
			
		}
		
		
		isPainting=false;
	}



	public void mouseDoubleClick(MouseEvent e) {
		if(worldMap==null){
			return;
		}
		if(mouseMode==Const.MOUSE_MODE_LINK)
			return;
		worldMap.newCity(e.x+viewX, e.y+viewY);
	}



	public void mouseDown(MouseEvent e) {
		if(worldMap==null){
			return;
		}
		// 连线模式
		if(mouseMode==Const.MOUSE_MODE_LINK){
			if(linkCity0==null){
				Point p = new Point(e.x+viewX, e.y+viewY);
				linkCity0 = worldMap.getCity(p.x,p.y);
				if(linkCity0!=null){
					linkCity1 = new City();
					linkCity1.w=0;
					linkCity1.h=0;
					linkCity1.y=0;
					linkCity1.x=p.x;
					linkCity1.y=p.y;
					int x = p.x;
					int y = p.y;
					int x0=linkCity0.x;
					int y0=linkCity0.y;
					int direction=DIR_UP;
					if(y<=y0&&Math.abs(x-x0)<=y0-y){
						direction=DIR_UP;
					}
					else if(x>=x0 &&Math.abs(y-y0)<=x-x0){
						direction=DIR_RIGHT;
					}
					if(y>=y0&&Math.abs(x-x0)<=y-y0){
						direction=DIR_DOWN;
					}
					else if(x<=x0 &&Math.abs(y-y0)<=x0-x){
						direction=DIR_LEFT;
					}
					// 清掉对应城市的链接
					if(linkCity0.linkCities[direction]!=null){
						City c=linkCity0.linkCities[direction];
						c.linkCities[linkCity0.linkDoor[direction]]=null;
					}
					linkDir0 = direction;
					linkDir1 = direction^2;
					linkCity0.linkCities[linkDir0] = linkCity1;
					linkCity0.linkDoor[linkDir0] = linkDir1;
					linkCity1.linkCities[linkDir1] = linkCity0;
					linkCity1.linkDoor[linkDir1] = linkDir0;
					
				}
			}
			else{
				// 已点选一个起点, 连接目标城市
				Point p = new Point(e.x+viewX, e.y+viewY);
				City c = worldMap.getCity(p.x,p.y);
				if(c!=null){
					int x = p.x;
					int y = p.y;
					int x0=c.x;
					int y0=c.y;
					int direction=DIR_UP;
					if(y<=y0&&Math.abs(x-x0)<=y0-y){
						direction=DIR_UP;
					}
					else if(x>=x0 &&Math.abs(y-y0)<=x-x0){
						direction=DIR_RIGHT;
					}
					if(y>=y0&&Math.abs(x-x0)<=y-y0){
						direction=DIR_DOWN;
					}
					else if(x<=x0 &&Math.abs(y-y0)<=x0-x){
						direction=DIR_LEFT;
					}
					if(c.linkCities[direction]!=null){
						c.linkCities[direction].linkCities[c.linkDoor[direction]]=null;
					}
					c.linkCities[direction]=linkCity0;
					c.linkDoor[direction]=linkDir0;
					linkCity0.linkCities[linkDir0] = c;
					linkCity0.linkDoor[linkDir0] = direction;
				}
				else{
					linkCity0.linkCities[linkDir0] = null;
				}
				if(linkCity1!=null)
					linkCity1.linkCities[linkDir1]=null;
				linkCity0=null;
				linkCity1=null;
			}
			redraw();
			return;
		}
		// 编辑模式
		City _city = worldMap.getCity(e.x+viewX, e.y+viewY);
		if(_city!=null){
			if(!worldMap.selectedCities.contains(_city)){
				worldMap.selectCity(_city);
			}else{
//				CityPropShell.instance.setCity(_city);
			}
			redraw();
			mouseMode = Const.MOUSE_MODE_MOVE;
		}else{
			worldMap.selectCity((City)null);
			redraw();
			mouseMode = Const.MOUSE_MODE_SELECT;
		}
		pressPoint = new Point(e.x, e.y);
		currentPoint = new Point(e.x, e.y);
		
	}
	public void mouseUp(MouseEvent e) {
		if(worldMap==null){
			return;
		}
		// 连线模式
		if(mouseMode==Const.MOUSE_MODE_LINK){
			
			
			return;
		}
		if(mouseMode == Const.MOUSE_MODE_SELECT){
			Rectangle rect = new Rectangle(
					Math.min(e.x,pressPoint.x)+viewX,
					Math.min(e.y,pressPoint.y)+viewY,
					Math.abs(e.x-pressPoint.x),
					Math.abs(e.y-pressPoint.y)
			);
			if(rect.width>0&&rect.height>0)
				worldMap.selectCity(rect);
			redraw();
		}
		
		pressPoint = null;
		currentPoint = null;
		mouseMode = Const.MOUSE_MODE_NONE;
		
	}

	public void mouseMove(MouseEvent e) {
		if(worldMap==null){
			return;
		}
		int px = e.x+viewX;
		int py = e.y+viewY;
		EditorShell.instance.coorLabel.setText("Pixel("+px+","+py+")");
		// 连线模式
		if(mouseMode==Const.MOUSE_MODE_LINK){
			if(linkCity1!=null){
				linkCity1.x=e.x+viewX;
				linkCity1.y=e.y+viewY;
				linkCity1.w=0;
				linkCity1.h=0;
				// 所在地检测
				Point p = new Point(e.x+viewX, e.y+viewY);
				City c = worldMap.getCity(p.x,p.y);
				if(c!=null){
					int x = p.x;
					int y = p.y;
					int x0=c.x;
					int y0=c.y;
					int direction=DIR_UP;
					if(y<=y0&&Math.abs(x-x0)<=y0-y){
						direction=DIR_UP;
					}
					else if(x>=x0 &&Math.abs(y-y0)<=x-x0){
						direction=DIR_RIGHT;
					}
					if(y>=y0&&Math.abs(x-x0)<=y-y0){
						direction=DIR_DOWN;
					}
					else if(x<=x0 &&Math.abs(y-y0)<=x0-x){
						direction=DIR_LEFT;
					}
					linkCity1.x=c.x;
					linkCity1.y=c.y;
					linkCity1.w=c.w;
					linkCity1.h=c.h;
					linkCity1.linkCities[linkDir1]=null;
					linkCity1.linkCities[direction]=linkCity0;
					linkCity1.linkDoor[direction]=linkDir0;
					linkCity0.linkDoor[linkDir0]=direction;
				}
				redraw();
			}
			return;
		}

		if(mouseMode == Const.MOUSE_MODE_MOVE){
			if(currentPoint==null)
				return;
			int dx,dy;
			City _city = null;
			dx = e.x-currentPoint.x;
			dy = e.y-currentPoint.y;
			for(int i = 0;i<worldMap.selectedCities.size();i++){
				_city = (City)worldMap.selectedCities.get(i);
//				_city.visible = false;
//				redraw(_city.x-_city.w,_city.y-_city.h,dx+(_city.w<<1),dy+(_city.h<<1),false);
				_city.x+=dx;
				_city.y+=dy;
				_city.visible = true;
			}
			redraw();
			currentPoint.x= e.x;
			currentPoint.y= e.y;

		}
		else if(mouseMode == Const.MOUSE_MODE_SELECT){
			/*Rectangle rect = new Rectangle(Math.min(Math.min(e.x,currentPoint.x),pressPoint.x),
					Math.min(Math.min(e.y,currentPoint.y),pressPoint.y),
					Math.max(Math.abs(e.x-pressPoint.x),Math.abs(currentPoint.x-pressPoint.x))+1,
					Math.max(Math.abs(e.y-pressPoint.y),Math.abs(currentPoint.y-pressPoint.y))+1
					);*/
			currentPoint.x= e.x;
			currentPoint.y= e.y;
			redraw(/*rect.x,rect.y,rect.width,rect.height,false*/);
		}
		
	}

	public void paintControl(PaintEvent e) {
		
		paint(e.gc);
		
	}
	
	class PaintThread implements Runnable{
		public void run(){
			if(!device.isDisposed() && !shell.isDisposed())
				redraw();
		}
	}
	
}
