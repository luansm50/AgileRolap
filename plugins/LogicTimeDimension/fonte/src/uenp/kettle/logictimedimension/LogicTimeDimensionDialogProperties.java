package uenp.kettle.logictimedimension;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;


public class LogicTimeDimensionDialogProperties extends BaseStepDialog implements StepDialogInterface{
	private static Class<?> PKG = LogicTimeDimensionDialogProperties.class;
	private LogicTimeDimensionMeta input;
	private TableView wProperties;
	private FormData fdProperties;
	private ArrayList<ColumnInfo> properties = new ArrayList<ColumnInfo>();
	private Group wgPropertie;
	private FormLayout flgPropertie;
	private FormData fdgPropertie;
	private Label wlColumn;
	private FormData fdlColumn;
	private Combo wColumn;
	private FormData fdColumn;
	
	
	public LogicTimeDimensionDialogProperties(Shell parent,
			BaseStepMeta baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, baseStepMeta, transMeta, stepname);
		input=(LogicTimeDimensionMeta) baseStepMeta;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String open() {
		logBasic("Executando - open");
		Shell parent = getParent();
		Display display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN
				| SWT.MAX);
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
		shell.setText(BaseMessages.getString(PKG, "Shell.Properties"));
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		
		// new table view properties
		
		ColumnInfo inf[] = new ColumnInfo[1];
		inf[0] = new ColumnInfo(BaseMessages.getString(PKG, "Properties.ColumnName"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		properties.add(inf[0]);
		wProperties = new TableView(transMeta, shell, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI, inf, 10, lsMod, props);
		fdProperties = new FormData();
		fdProperties.left = new FormAttachment(0, 0);
		fdProperties.top = new FormAttachment(shell, 10);
		fdProperties.width = 150;
		wProperties.setLayoutData(fdProperties);
		
		// new group propertie
		
		wgPropertie = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(wgPropertie);
		wgPropertie.setText(BaseMessages.getString(PKG, "Properties.Group"));
		flgPropertie = new FormLayout();
		flgPropertie.marginWidth = 10;
		flgPropertie.marginHeight = 10;
		fdgPropertie = new FormData();
		fdgPropertie.top = new FormAttachment(shell, 10);
		fdgPropertie.left = new FormAttachment(wProperties, margin);
		fdgPropertie.right = new FormAttachment(100, 0);
		wgPropertie.setLayout(flgPropertie);
		wgPropertie.setLayoutData(fdgPropertie);
		
		// new label column
		
		wlColumn = new Label(wgPropertie, SWT.RIGHT);
		wlColumn.setText(BaseMessages.getString(PKG, "Properties.Column"));
		props.setLook(wlColumn);
		fdlColumn = new FormData();
		fdlColumn.left = new FormAttachment(0, 0);
		fdlColumn.right = new FormAttachment(middle, -middle);
		fdlColumn.top = new FormAttachment(0, 10);
		wlColumn.setLayoutData(fdlColumn);

		// new combo column

		wColumn = new Combo(wgPropertie, SWT.SINGLE | SWT.LEFT
				| SWT.BORDER);
		props.setLook(wColumn);
		fdColumn = new FormData();
		fdColumn.left = new FormAttachment(middle, 0);
		fdColumn.top = new FormAttachment(0, 10);
		fdColumn.right = new FormAttachment(100, 0);
		fdColumn.width= 200;
		wColumn.setLayoutData(fdColumn);
		
		//configurando botão de ok
		
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "Step.Ok"));

		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "Step.Cancel"));

		BaseStepDialog.positionBottomButtons(shell,
				new Button[] { wOK, wCancel }, margin, wProperties);

		lsCancel = new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();

			}
		};

		lsOK = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();
			}
		};

		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);
		
		
		setSize();
		input.setChanged(changed);
		shell.open();
		return null;
		
	
	}
	
}
