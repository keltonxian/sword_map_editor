package control;

import java.util.Vector;

import app.MapCanvas;

public class ControlPool {

	private static int pointer = -1;
	public static Vector controlList = new Vector();
	
	public static void appendAndDo(Control ctrl){
		
		// 清除没用的control
		while(pointer<controlList.size()-1)
			controlList.removeElementAt(controlList.size()-1);
		
		if(pointer==controlList.size()-1){
			controlList.addElement(ctrl);
			redo();
		}
	}
	
	public static void undo(){
		if(pointer<0)
			return;
		((Control) controlList.elementAt(pointer)).undo();
		pointer--;
		MapCanvas.instance.redraw();
	}
	public static void redo(){
		if(pointer>=controlList.size()-1)
			return;
		pointer++;
		((Control) controlList.elementAt(pointer)).execute();
		MapCanvas.instance.redraw();
	}
	
	public static boolean isModified(){
		return pointer>=0;
	}
	
	public static void clear(){
		controlList.removeAllElements();
		pointer=-1;
	}
}
