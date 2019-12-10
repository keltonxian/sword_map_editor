package app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class WorldMapEditor {

	public static ProgressBar progress;
	public static Composite loadingShell;
	public static void main(String[] args) {
		Display display = new Display();
		loadingShell = new Shell(display,SWT.APPLICATION_MODAL);
		FormLayout formLayout = new FormLayout();
		loadingShell.setLayout(formLayout);
		Label label = new Label(loadingShell,SWT.LEAD);
//		label.setImage(new Image(display,"res/splash.png"));
		FormData formData = new FormData();
		formData = new FormData();
		formData.top=new FormAttachment(0);
		formData.left=new FormAttachment(0);
		formData.right=new FormAttachment(100);
		label.setLayoutData(formData);
		progress = new ProgressBar(loadingShell,SWT.LEAD);
		formData = new FormData();
		formData.top=new FormAttachment(label);
		formData.left=new FormAttachment(0);
		formData.bottom=new FormAttachment(100);
		formData.right=new FormAttachment(100);
		progress.setLayoutData(formData);
		progress.setMaximum(100);
		progress.setMinimum(0);
		progress.setSelection(0);
		loadingShell.pack();
		Rectangle loadingRectangle = loadingShell.getBounds();
		loadingShell.setLocation(
				display.getBounds().width-loadingRectangle.width>>1,
				display.getBounds().height-loadingRectangle.height-40>>1
				);
		((Shell)loadingShell).open();
		readSetting();
		loadingShell.dispose();
		loadingShell=null;
		Shell shell = new Shell(display);
		EditorShell EditorUI = new EditorShell(shell, display);


//		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	public static void readSetting(){
		City.initCity();
		progress.setSelection(80);
		loadingShell.redraw();
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
