package app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * 
 * 城市属性设置窗口
 * 
 * @author Sunwing
 *
 */
public class CityPropShell{

	Composite container;
	
	//分组
	Group baseGroup;
	Group menuGroup;
	Group advanceGroup;
	
	//显示资料
	Label labelName;
	Text textID;
	
	//城市对象
	public City city;
	
	//鼠标指针变量
	int tableColumnIndex;
	Control oldEditor;
	TableEditor editor;
	int currentLevel;
	
	public static CityPropShell instance;
	
	public CityPropShell(Composite _shell) {
		container=_shell;
//		shell = _shell;
		instance = this;
//		container = shell = new Shell(_display);
		
		initShell();
		setCity(null);
	}
	
	private void initShell(){
		
		
		//----------设定基本属性的界面---------------
		FormData formData;
		baseGroup = new Group(container,SWT.SHADOW_ETCHED_IN);
		FormLayout layout = new FormLayout();
		formData = new FormData();
		formData.top = new FormAttachment(1);
		formData.left = new FormAttachment(1);
		formData.right = new FormAttachment(99);
		baseGroup.setLayoutData(formData);
		layout.spacing=7;
		layout.marginTop=5;
		layout.marginBottom=5;
		baseGroup.setLayout(layout);
		baseGroup.setText("基础设定");
		
		Label tmpLabel = new Label(baseGroup,SWT.LEAD);
		tmpLabel.setText("地点ID:");
		formData = new FormData();
		formData.left = new FormAttachment(1);
		tmpLabel.setLayoutData(formData);
		
		textID = new Text(baseGroup,SWT.SINGLE|SWT.BORDER);
		formData = new FormData();
		formData.left = new FormAttachment(tmpLabel);
		formData.right = new FormAttachment(99);
		textID.setLayoutData(formData);
//		textID.setEditable(false);
		textID.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				try{
					city.id=Integer.parseInt(textID.getText());
					city.name = City.cityTable.get(city.id);
					if(city.name==null){
						city.name="";
					}
					labelName.setText(city.name);
				}catch(Exception ex){
					
				}
			}
		});

		tmpLabel = new Label(baseGroup,SWT.LEAD);
		tmpLabel.setText("地图名称:");
		formData = new FormData();
		formData.top = new FormAttachment(textID);
		formData.left = new FormAttachment(1);
		tmpLabel.setLayoutData(formData);
		
		labelName = new Label(baseGroup,SWT.LEAD);
		formData = new FormData();
		formData.top = new FormAttachment(textID);
		formData.left = new FormAttachment(tmpLabel);
		formData.right = new FormAttachment(99);
		labelName.setLayoutData(formData);
		
	}
	
	
	
	/**
	 * 选择某个城市
	 */
	public void setCity(City _city){
		city = _city;
		if(city==null){
//			shell.setAlpha(0xaa);
			setShellEnable(false);
			return;
		}
		textID.setText(city.id+"");
		labelName.setText(city.name);
		setShellEnable(true);
	}
	
	public void setShellEnable(boolean b){
		//显示资料
		textID.setEnabled(b);
		container.setEnabled(b);
		
	}
}

