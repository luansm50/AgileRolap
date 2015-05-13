package uenp.kettle.SchemaOut;

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
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class SchemaOutDialog extends BaseStepDialog implements StepDialogInterface{

	private SchemaOutMeta 	input;
	
	private Label		wlFileName, wlSchemaName;
	private Button		wbFileName;
	private TextVar		wFileName, wSchemaName;
	private FormData	fwlFileName, fwbFileName, fwFileName, fdlSchemaName, fdSchemaName;
	
	public SchemaOutDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input = (SchemaOutMeta)in;
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
		shell.setText(Messages.getString("SchemaOutDialog.Shell.Title"));
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		
		
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("SchemaOutDialog.StepName.Label"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0,0);
		fdlStepname.right = new FormAttachment(middle, -middle);
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
		wlFileName.setText(Messages.getString("SchemaOutDialog.FileSave.Label"));
		props.setLook(wlFileName);
		fwlFileName = new FormData();
		fwlFileName.left = new FormAttachment(0, 0);
		fwlFileName.top = new FormAttachment(wStepname, margin);
		fwlFileName.right = new FormAttachment(middle, -middle);
		wlFileName.setLayoutData(fwlFileName);
		wbFileName = new Button(shell, SWT.PUSH | SWT.CENTER);
		props.setLook(wbFileName);
		wbFileName.setText(Messages.getString("SchemaOutDialog.ButtonFile"));
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
		
		//new label schema name
		wlSchemaName = new Label(shell, SWT.RIGHT);
		wlSchemaName.setText(Messages.getString("SchemaOutDialog.SchemaName.Label"));
		props.setLook(wlSchemaName);
		fdlSchemaName = new FormData();
		fdlSchemaName.left = new FormAttachment(0, 0);
		fdlSchemaName.right = new FormAttachment(middle, -middle);
		fdlSchemaName.top = new FormAttachment(wFileName, margin);
		wlSchemaName.setLayoutData(fdlSchemaName);
		
		//new text schema name
		wSchemaName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wSchemaName.setText("");
		props.setLook(wSchemaName);
		wSchemaName.addModifyListener(lsMod);
		fdSchemaName = new FormData();
		fdSchemaName.left = new FormAttachment(middle, 0);
		fdSchemaName.top = new FormAttachment(wFileName, margin);
		fdSchemaName.right = new FormAttachment(100, 0);
		wSchemaName.setLayoutData(fdSchemaName);
		
		//Botoes
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("SchemaOutDialog.ButtonOk.Label"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("SchemaOutDialog.ButtonCancel.Label"));
		
		BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK,  wCancel}, margin, wSchemaName);
		
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
		
		
		RowMetaInterface prev;
		try {
			prev = transMeta.getStepFields(stepname);
			String[] a = prev.getFieldNames();
			for(int i = 0; i <a.length; i++ ){
				logBasic(a[i]);
				
			}
		} catch (KettleStepException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return stepname;
	}
	
	public void getData(){
		wStepname.selectAll();
		if(input.getUrl().getValueData() != null){
			wFileName.setText(String.valueOf(input.getUrl().getValueData()));
		}
		if(input.getSchemaName().getValueData() != null){
			wSchemaName.setText(String.valueOf(input.getSchemaName().getValueData()));
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
		input.setSchemaName(new ValueMetaAndData(new ValueMeta("schemaName", ValueMetaInterface.TYPE_STRING), new String(wSchemaName.getText())));
		dispose();
		
	}

}
