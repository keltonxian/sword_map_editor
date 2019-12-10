package app;

import java.io.File;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public interface Const {

	public static final String TEXT_APPLICATION_NAME = "MMO3世界地图编辑器 v0.01";
	
	public static final String PROP_WORKSPACE = "WORKSPACE";
	public static final String PROP_HISTORY_WORKSPACE = "HISTORY_WORKSPACE";
	public static final String PROP_HISTORY_FILE = "HISTORY_FILE";
	public static final String PROP_LASTDIR = "LAST_DIR";
	
	public static final String PATH_USER = System.getProperty("user.dir")+File.separator;
	public static final String PATH_WORKSPACE = "workspace";
	public static final String PATH_RESOURCE = "res";
	public static final String PATH_TILE = "image";
	public static final String PATH_IMAGE = "image";
	public static final String PATH_SPRITE = "sprite";
	public static final String PATH_MAP = "map";
	public static final String PATH_MAP_SURFACE = "maps";
	public static final String PATH_MISSION = "mission";
	public static final String PATH_EVENT = "event";
	public static final String PATH_TEXT_EVENT = "txt_event";
	public static final String PATH_TEXT_MISSION= "txt_mission";
	
	
	public static final String FILE_PROPERTY = "mapeditor.prop";
	public static final String FILE_HISTORY = "history.xml";
	public static final String FILE_BASIC_TILESET = "tileset.xml";
	public static final String FILE_IMAGE_SET = "imageset.xml";
	public static final String FILE_SPRITE_SET = "spriteset.xml";
	public static final String FILE_WORLD_MAP = "world.xml";
	
	public static final int DEFAULT_TILE_WIDTH=24;
	public static final int DEFAULT_TILE_HEIGHT=24;
	
	//鼠标模式定义
	public static final int MOUSE_MODE_NONE=0;
	public static final int MOUSE_MODE_MOVE=1;
	public static final int MOUSE_MODE_SELECT=2;
	public static final int MOUSE_MODE_LINK=3;
	
	//旋转定义
	public static final int TRANS_NONE =0;
	public static final int TRANS_MIRROR_ROT180 =1; //上下翻转
	public static final int TRANS_MIRROR =2; //左右翻转
	public static final int TRANS_MIRROR_ROT270 =4; //135度镜面翻转
	public static final int TRANS_MIRROR_ROT90 =7; //45度镜面翻转
	public static final int TRANS_ROT180 =3;
	public static final int TRANS_ROT90 =5;
	public static final int TRANS_ROT270 =6;
	
	//锚点定义
	public static final int HCENTER = 1;
	public static final int VCENTER = 2;
	public static final int LEFT = 4;
	public static final int RIGHT = 8;
	public static final int TOP = 16;
	public static final int BOTTOM = 32;
	public static final int BASELINE = 64;
	
	public static final int TOP_LEFT = LEFT | TOP;
	public static final int TOP_HCENTER = TOP | HCENTER;
	public static final int TOP_RIGHT = TOP | RIGHT;
	public static final int BOTTOM_HCENTER = BOTTOM | HCENTER;
	public static final int BOTTOM_LEFT = BOTTOM | LEFT;
	public static final int BOTTOM_RIGHT = BOTTOM | RIGHT;
	public static final int H_V = HCENTER | VCENTER;
	public static final int LEFT_VCENTER = LEFT | VCENTER;
	public static final int RIGHT_VCENTER = RIGHT | VCENTER;
	
	//工具定义
    public final static int TOOL_RECT_SELECT = 0;
    public final static int TOOL_EDITALL = 1;
    public final static int TOOL_PENCIL = 2;
    public final static int TOOL_ERASER = 3;
    public final static int TOOL_BUCKET_FILL= 4;
    public final static int TOOL_COLOR_PICKER = 5;
    public final static int TOOL_ERASER_FILL = 6;

    //画切片层方式
    public final static int PAINT_TILE = 1;
    public final static int PAINT_GROUP = 2;

    public final static byte FLAG_KEY_UP = 0x1;
    public final static byte FLAG_KEY_DOWN = 0x2;
    public final static byte FLAG_KEY_LEFT = 0x4;
    public final static byte FLAG_KEY_RIGHT = 0x8;
    
    
    // 颜色
    public final static Color COLOR_BLACK = new Color(null,0,0,0);
    public final static Color COLOR_WHITE = new Color(null,255,255,255);
    public final static Color COLOR_RED = new Color(null,255,0,0);
    public final static Color COLOR_GREEN = new Color(null,0,255,0);
    public final static Color COLOR_DARK_BLUE = new Color(null,0,0,100);
    public final static Color COLOR_LIGHT_BLUE = new Color(null,150,150,255);
    public final static Color COLOR_GRAY = new Color(null,100,100,100);
    public final static Color COLOR_BLOCK = new Color(null,255,160,60);
    public final static Color COLOR_YELLOW = new Color(null,255,255,70);
    
    
	//--------编辑器UI文字---------
	public static final String TEXT_MENU_FILE = "文件(&F)";
	public static final String TEXT_MENU_EDIT = "编辑(&E)";
	public static final String TEXT_MENU_VIEW = "查看(&V)";
	public static final String TEXT_MENU_TASK = "任务(&T)";
	
	public static final String TEXT_MENU_NEW = "新建\tCtrl+N";
	public static final String TEXT_MENU_OPEN = "打开\tCtrl+O";
	public static final String TEXT_MENU_LOAD_BG = "导入地图";
	public static final String TEXT_MENU_CLOSE = "关闭";
	public static final String TEXT_MENU_SAVE = "&保存\tCtrl+S";
	public static final String TEXT_MENU_SAVE_AS = "另存为...\tCtrl+Shift+S";
	public static final String TEXT_MENU_EXIT = "退出";
	
	public static final String TEXT_MENU_UNDO = "Undo\tCtrl+Z";
	public static final String TEXT_MENU_REDO = "Redo\tCtrl+Y";
	public static final String TEXT_MENU_CUT = "剪切\tCtrl+X";
	public static final String TEXT_MENU_COPY = "复制\tCtrl+C";
	public static final String TEXT_MENU_PASTE = "粘帖\tCtrl+V";
	public static final String TEXT_MENU_DEL = "删除\tDel";
	public static final String TEXT_MENU_SELECT_ALL = "全选\tCtrl+A";
	
	public static final String TEXT_MENU_VIEW_BG = "背景";
	public static final String TEXT_MENU_VIEW_LINK = "连接线";
	public static final String TEXT_MENU_VIEW_ZOOMIN = "放大\t+";
	public static final String TEXT_MENU_VIEW_ZOOMOUT = "缩小\t-";
	
	public static final String TEXT_MENU_TASK_LIST = "任务列表";
	public static final String TEXT_MENU_TASK_NEW = "新增任务";
	
	public static final String STRING_SEPARATOR = "--";

    
    
	public static final String[] MENU_MAIN={
		TEXT_MENU_FILE,
		TEXT_MENU_EDIT,
		TEXT_MENU_VIEW,
//		TEXT_MENU_TASK,
		
	};
	
	public static final String[] MENU_FILE={
//		TEXT_MENU_NEW,
//		TEXT_MENU_OPEN,
//		STRING_SEPARATOR,
//		TEXT_MENU_CLOSE,
//		STRING_SEPARATOR,
		TEXT_MENU_SAVE,
//		TEXT_MENU_SAVE_AS,
		STRING_SEPARATOR,
//		TEXT_MENU_LOAD_BG,
//		STRING_SEPARATOR,
		TEXT_MENU_EXIT,
	};
	public static final int[] CONST_CTRL_MENU_FILE={
//		SWT.CTRL+'n',
//		SWT.CTRL+'o',
//		-1,
//		-1,
//		-1,
		SWT.CTRL+'s',
//		SWT.CTRL+SWT.SHIFT+'s',
		-1,
//		-1,
//		-1,
		-1,
	};
	public static final String[] MENU_EDIT={
		TEXT_MENU_UNDO,
		TEXT_MENU_REDO,
		STRING_SEPARATOR,
		TEXT_MENU_SELECT_ALL,
		TEXT_MENU_CUT,
		TEXT_MENU_COPY,
		TEXT_MENU_PASTE,
		TEXT_MENU_DEL,
	};
	public static final int[] CONST_CTRL_MENU_EDIT={
		SWT.CTRL+'z',
		SWT.CTRL+'y',
		-1,
		SWT.CTRL+'a',
		SWT.CTRL+'x',
		SWT.CTRL+'c',
		SWT.CTRL+'v',
		SWT.DEL,
	};
	public static final String[] MENU_VIEW={
		TEXT_MENU_VIEW_BG,
		TEXT_MENU_VIEW_LINK,
		STRING_SEPARATOR,
		TEXT_MENU_VIEW_ZOOMIN,
		TEXT_MENU_VIEW_ZOOMOUT,
	};
	public static final int[] CONST_CTRL_MENU_VIEW={
		-1,
		-1,
		-1,
		SWT.KEYPAD_ADD,
		SWT.KEYPAD_DECIMAL,
	};
	public static final String[] MENU_TASK={
		TEXT_MENU_TASK_LIST,
		TEXT_MENU_TASK_NEW,
	};
	public static final int[] CONST_CTRL_MENU_TASK={
		-1,
		-1,
	};
	
	public static final String[][] MENU_MAIN_SUBMENU ={
		MENU_FILE,
		MENU_EDIT,
		MENU_VIEW,
		MENU_TASK,
	};
	public static final int[][] CONST_CTRL_MENU_ACCELERATOR={
		CONST_CTRL_MENU_FILE,
		CONST_CTRL_MENU_EDIT,
		CONST_CTRL_MENU_VIEW,
		CONST_CTRL_MENU_TASK,
	};
}
