package uenp.kettle.XMLIn;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class XMLInDialog extends BaseStepDialog implements StepDialogInterface{

	private XMLInMeta 	input;
	
	private Label		wlFileName;
	private Button		wbFileName;
	private TextVar		wFileName;
	private FormData	fwlFileName, fwbFileName, fwFileName;
	
	
	public XMLInDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input = (XMLInMeta)in;
	}

	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, input);
		
		ModifyListener lsMod = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				input.setChanged();
			}
		};
		
		changed = input.hasChanged();
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		
		shell.setLayout(formLayout);
		shell.setText(Messages.getString("XMLOutDialog.Shell.Title"));
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		
		
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("XMLOutDialog.StepName.Label"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0,0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		
		
		//File 
		wlFileName = new Label(shell, SWT.RIGHT);
		wlFileName.setText(Messages.getString("XMLOutDialog.FileSave.Label"));
		props.setLook(wlFileName);
		fwlFileName = new FormData();
		fwlFileName.left = new FormAttachment(0,0);
		fwlFileName.top = new FormAttachment(wStepname, margin);
		fwlFileName.right = new FormAttachment(middle, -margin);
		wlFileName.setLayoutData(fwlFileName);
		wbFileName = new Button(shell, SWT.PUSH | SWT.CENTER);
		props.setLook(wbFileName);
		wbFileName.setText(Messages.getString("XMLOutDialog.ButtonFile"));
		fwbFileName = new FormData();
		fwbFileName.right = new FormAttachment(100,0);
		fwbFileName.top = new FormAttachment(wStepname,0);
		wbFileName.setLayoutData(fwbFileName);
		wFileName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wFileName);
		wFileName.addModifyListener(lsMod);
		fwFileName = new FormData();
		fwFileName.width = 400;
		fwFileName.left = new FormAttachment(middle, 0);
		fwFileName.top = new FormAttachment(wStepname, margin);
		fwFileName.right = new FormAttachment(wbFileName, -margin);
		wFileName.setLayoutData(fwFileName);
		
		//Botoes
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("XMLOutDialog.ButtonOk.Label"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("XMLOutDialog.ButtonCancel.Label"));
		
		BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK,  wCancel}, margin, wbFileName);
		
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e){
				ok();
			}
		};
		
		wStepname.addSelectionListener(lsDef);
		
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e){
				cancel();
			}
		});
		
		wbFileName.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e){
						FileDialog dialog = new FileDialog(shell, SWT.SAVE);
						dialog.setFilterExtensions(new String[]{"*.xml"});
						if(wFileName.getText()!=null){
							dialog.setFileName(transMeta.environmentSubstitute(wFileName.getText()));
						}
						if(dialog.open()!=null){
							wFileName.setText(dialog.getFilterPath()+System.getProperty("file.separator")+dialog.getFileName());
						}
					}
				}
		);
		
		lsOK = new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				ok();
			}
		};
		
		lsCancel = new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				cancel();
			}
		};
		
		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);
		setSize();
		
		getData();
		
		input.setChanged(changed);
		shell.open();
		while(!display.readAndDispatch()){
			if(!display.readAndDispatch()) display.sleep();
		}
		
		return stepname;
	}
	
	public void getData(){
		wStepname.selectAll();
		if(input.getUrl() != null){
			wFileName.setText(String.valueOf(input.getUrl().getValueData()));
		}
	}
	
	public void cancel(){
		stepname = null;
		input.setChanged(changed);
		dispose();
	}
	
	public void ok(){
		stepname = wStepname.getText();
		input.setUrl(new ValueMetaAndData(new ValueMeta("url", ValueMetaInterface.TYPE_STRING), new String(wFileName.getText())));
		dispose();
		
	}
	public ArrayList carregarDimens(String url) {
		File arquivo = new File(url);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(arquivo);
			ArrayList dimension= new ArrayList();
			Element raiz = doc.getDocumentElement();
			NodeList dimensions = raiz.getElementsByTagName("dimension");
			for(int i=0; i<dimensions.getLength();i++){
				dimension.add(dimensions.item(i));
			}
			return dimension;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

}
