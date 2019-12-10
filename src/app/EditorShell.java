package app;

import io.xml.XMLWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import control.ControlPool;
import control.DelCity;

public class EditorShell {
	
	Shell shell;
	Display display;
	
	Image imgArrow;
	Image imgLink;
	
	String currentFile=null;
	
	public static EditorShell instance;
	
	Label coorLabel;
	CityPropShell cityShell;
	
	EditorShell(Shell _shell,Display _display){
		instance = this;
		shell = _shell;
		display = _display;
		
		imgArrow = new Image(display,getClass().getResourceAsStream("/app/70.png"));
		imgLink = new Image(display,getClass().getResourceAsStream("/app/33.png"));
		Rectangle rect = _display.getBounds();
	    shell.setText(Const.TEXT_APPLICATION_NAME);
	    FormLayout layout = new FormLayout();
	    shell.setLayout(layout);
	    shell.setSize(rect.width>>1, rect.height>>1);
	    shell.setLocation(rect.width>>2, rect.height>>2);
	    initUI();
	    shell.addListener(SWT.Close,new Listener(){
			public void handleEvent(Event event) {
				int selection = ask(shell, "保存", "是否保存变更？");
				if(selection==SWT.CANCEL){
					event.doit=false;
					return;
				}else if(selection==SWT.YES){
					// save
					saveMapXML(MapCanvas.instance.getWorldMap());
				}
				event.doit=true;
				
			}
	    });
	    
	    MapCanvas.instance.worldMap=WorldMap.fromXML(Const.PATH_USER+Const.FILE_WORLD_MAP);
	    
	}
	
	/**
	 * 初始化对象
	 */
	private void initUI(){
		//菜单项初始化
		Menu menuBar = new Menu (shell, SWT.BAR);
		shell.setMenuBar (menuBar);
		MenuItem menuItem;
		Menu submenu;
		MenuItem submenuItem;
		
		for(int i=0;i<Const.MENU_MAIN.length;i++){
			//主菜单项
			menuItem = new MenuItem(menuBar,SWT.CASCADE);
			menuItem.setText(Const.MENU_MAIN[i]);
			submenu = new Menu(shell,SWT.DROP_DOWN);
			menuItem.setMenu(submenu);
			//添加子菜单
			for(int j=0;j<Const.MENU_MAIN_SUBMENU[i].length;j++){
				
				//分割线
				if(Const.MENU_MAIN_SUBMENU[i][j]==Const.STRING_SEPARATOR){
					submenuItem=new MenuItem(submenu,SWT.SEPARATOR);
					continue;
				}
				//特殊类型初始化
				if(Const.TEXT_MENU_VIEW_BG == Const.MENU_MAIN_SUBMENU[i][j] ||
						Const.TEXT_MENU_VIEW_LINK == Const.MENU_MAIN_SUBMENU[i][j]){
					submenuItem=new MenuItem(submenu,SWT.CHECK);
					submenuItem.setSelection(true);
				}
				else{
					submenuItem=new MenuItem(submenu,SWT.PUSH);
				}
				submenuItem.setText (Const.MENU_MAIN_SUBMENU[i][j]);
				//控制符
				submenuItem.addListener(SWT.Selection,new Listener(){
					public void handleEvent(Event e) {
						if(!(e.widget instanceof MenuItem)){
							return;
						}
						handleMenuSelectEvent((MenuItem)e.widget);
					}
				});
				//有快捷键则添加快捷键
				if(Const.CONST_CTRL_MENU_ACCELERATOR[i][j]>0){
					submenuItem.setAccelerator (Const.CONST_CTRL_MENU_ACCELERATOR[i][j]);
				}
			}
		}

		CoolBar coolBar = new CoolBar(shell, SWT.NONE);
		
		final ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		ToolItem itemArrow = new ToolItem(toolBar, SWT.RADIO);
		itemArrow.setImage(imgArrow);
		itemArrow.setSelection(true);
		itemArrow.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				MapCanvas.instance.mouseMode = Const.MOUSE_MODE_NONE;
				setCanvasCursor();
			}
			public void widgetSelected(SelectionEvent e) {
				MapCanvas.instance.mouseMode = Const.MOUSE_MODE_NONE;
				setCanvasCursor();
			}
		});		
		ToolItem itemLink= new ToolItem(toolBar, SWT.RADIO);
		itemLink.setImage(imgLink);
		itemLink.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				MapCanvas.instance.mouseMode = Const.MOUSE_MODE_LINK;
				setCanvasCursor();
			}
			public void widgetSelected(SelectionEvent e) {
				MapCanvas.instance.mouseMode = Const.MOUSE_MODE_LINK;
				setCanvasCursor();
			}
		});		
	    toolBar.pack();
	    
	    Point size = toolBar.getSize();
	    CoolItem coolItem = new CoolItem(coolBar, SWT.PUSH);
	    coolItem.setControl(toolBar);
	    Point preferred = coolItem.computeSize(size.x, size.y);
	    coolItem.setPreferredSize(preferred);
	 
		
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);
		
		
		//界面分割
	    final Sash vSash = new Sash (shell, SWT.VERTICAL); //垂直分割线
	    final FormData vSashData = new FormData();
	    vSashData.left=new FormAttachment(70);
	    vSashData.top=new FormAttachment(toolBar);
	    vSashData.bottom=new FormAttachment(100);
	    vSash.setLayoutData(vSashData);
	    vSash.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				Rectangle sashRect = vSash.getBounds ();
				Rectangle shellRect = shell.getClientArea ();
				int right = shellRect.width - sashRect.width - 100;
				e.x = Math.max (Math.min (e.x, right), 200);
				if (e.x != sashRect.x)  {
					vSashData.left = new FormAttachment (0, e.x);
					shell.layout ();
				}
			}
		});
		
	    
	    FormData formData = new FormData();
	    formData.left=new FormAttachment(0);
	    formData.top=new FormAttachment(toolBar);
	    formData.bottom=new FormAttachment(100);
	    formData.right=new FormAttachment(vSash);
	    Group mapGroup = new Group(shell, SWT.SHADOW_ETCHED_OUT);
	    FormLayout layout = new FormLayout();
	    layout.marginTop=0;
	    layout.marginBottom=2;
	    layout.marginLeft=1;
	    layout.marginRight=1;
	    mapGroup.setLayout(layout);
	    mapGroup.setLayoutData(formData);
	    
	    formData = new FormData();
	    formData.left=new FormAttachment(0);
	    formData.bottom=new FormAttachment(100);
	    formData.right=new FormAttachment(100);
	    coorLabel = new Label(mapGroup,SWT.SHADOW_OUT);
	    coorLabel.setLayoutData(formData);
	    
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL |SWT.DOUBLE_BUFFERED;
		
		MapCanvas canvas = new MapCanvas(mapGroup, style);
		FormData canvasData = new FormData();
		canvasData.left = new FormAttachment(0);
		canvasData.right = new FormAttachment(100);
		canvasData.top = new FormAttachment(0);
		canvasData.bottom = new FormAttachment(coorLabel);
		canvas.setLayoutData(canvasData);
		canvas.addListener(SWT.Resize, new Listener(){
			public void handleEvent(Event event) {
		        shell.layout();
		        MapCanvas.instance.update();
		    }
		});
//		canvas.newWorldMap();
//		canvas.start();
		MapCanvas.instance = canvas;
		setCanvasCursor();
		
		Group cityGroup = new Group(shell,SWT.SHADOW_ETCHED_IN);
	    layout = new FormLayout();
	    cityGroup.setLayout(layout);
	    formData = new FormData();
	    formData.top=new FormAttachment(toolBar);
	    formData.left=new FormAttachment(vSash);
	    formData.right=new FormAttachment(100);
	    formData.bottom=new FormAttachment(100);
	    cityGroup.setLayoutData(formData);
	    
	    cityShell = new CityPropShell(cityGroup);
	    
	    
	}
	
	public void setCanvasCursor(){
		if(MapCanvas.instance.mouseMode == Const.MOUSE_MODE_LINK){
			MapCanvas.instance.setCursor(new Cursor(Display.getCurrent(),SWT.CURSOR_CROSS));
		}
		else{
			MapCanvas.instance.setCursor(new Cursor(Display.getCurrent(),SWT.CURSOR_ARROW));
			
		}
	}

	CoolItem createItem(CoolBar coolBar, int count) {
	    ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
	    for (int i = 0; i < count; i++) {
	        ToolItem item = new ToolItem(toolBar, SWT.RADIO);
	        item.setText(i +"");
	    }
	    toolBar.pack();
	    Point size = toolBar.getSize();
	    CoolItem item = new CoolItem(coolBar, SWT.RADIO);
	    item.setControl(toolBar);
	    Point preferred = item.computeSize(size.x, size.y);
	    item.setPreferredSize(preferred);
	    return item;
	}
	
	
    public void handleMenuSelectEvent(MenuItem item){
    	String text= item.getText();
    	/**
		 * 文件部分
		 */
		if(text == Const.TEXT_MENU_SAVE){
			saveMapXML(MapCanvas.instance.getWorldMap());
		}
    	/**
		 * 编辑部分
		 */
		else if(text == Const.TEXT_MENU_UNDO){
			// 反悔
			ControlPool.undo();
		}
		else if(text == Const.TEXT_MENU_REDO){
			// 重做
			ControlPool.redo();
		}
		else if(text == Const.TEXT_MENU_DEL){
			// 删除
			if(MapCanvas.instance.getWorldMap().selectedCities.size()>0){
				ControlPool.appendAndDo(new DelCity(MapCanvas.instance.getWorldMap()));
				
				MapCanvas.instance.redraw();
			}
		}
		else if(text == Const.TEXT_MENU_EXIT){
			// 退出
			shell.close();
		}
    }
    
	int choice =0;
	/**
	 * 
	 * @param _display
	 * @return 0 取消 | 1 保存 | 2 不保存 
	 */
	public int exitAsk(){

		return ask(shell,"退出","文件被修改，是否在退出前保存？");
		
	}
	
	public static int ask(Shell _shell, String title, String question){
		final MessageBox messageBox = new MessageBox(_shell,SWT.APPLICATION_MODAL|SWT.YES|SWT.NO|SWT.CANCEL);
		messageBox.setText(title);
		messageBox.setMessage(question);
		return messageBox.open();
	}
	public int warning(String question){
		final MessageBox messageBox = new MessageBox(shell,SWT.APPLICATION_MODAL|SWT.CANCEL);
		messageBox.setMessage(question);
		return messageBox.open();
	}
	
	public void save(){
		saveMapTo(MapCanvas.instance.getWorldMap());
	}
	
	public void saveAs(){
		DirectoryDialog dd = new DirectoryDialog(shell,SWT.APPLICATION_MODAL);
		dd.setText("保存地图");
		dd.setFilterPath(Const.PATH_USER);
		String selectFile = dd.open();
		if(selectFile==null)
			return;
		File file = new File(selectFile);
		int answer = SWT.YES;
		if(file.exists()){
			answer = ask(shell,"另存为","文件已存在，覆盖吗？");
		}else{
			file.mkdirs();
		}
		
		if(answer==SWT.YES){
			saveMapTo(MapCanvas.instance.getWorldMap());
			currentFile = selectFile;
		}
	}
	
	
	public void saveMapTo(WorldMap wm){
		FileOutputStream fos=null;
		try{
			
			// 写世界地图数据
			File file = new File(Const.PATH_USER+Const.FILE_WORLD_MAP);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			byte[] data = null;
			fos.write(data);
			fos.flush();
			
			warning("保存成功");
		}catch(Exception ex){
			ex.printStackTrace();
			warning("保存失败: "+ex);
		}finally{
			try{
				if(fos!=null)
					fos.close();
			}catch(Exception ex){}
		}
	}
	
	public void saveMapXML(WorldMap map){
		// 写世界地图数据
		File file = new File(Const.PATH_USER+Const.FILE_WORLD_MAP);
		Writer writer=null;
		try{
			if(file.exists()==false){
				file.createNewFile();
			}
			writer = new FileWriter(file);
			XMLWriter xmlWriter = new XMLWriter(writer);
			map.toXML(xmlWriter);
			warning("保存成功");
		}
		catch(Exception ex){
			warning("保存错误"+ex);
		}
		finally{
			try{writer.close();}catch(Exception ex){}
		}
	}

	public void openFile(){
		// 载入一张世界地图的图片
		
	}
}
