package uenp.kettle.feriado;

import java.util.ArrayList;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class FeriadoDialog extends BaseStepDialog implements StepDialogInterface{

	private FeriadoMeta input;
	
	
	private ArrayList<ColumnInfo> fieldColumns = new ArrayList<ColumnInfo>();
	private TableView FieldHoliday;
	private FormData fdFieldHoliday;
	
	public FeriadoDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (FeriadoMeta)in, transMeta, sname);
		input = (FeriadoMeta)in;
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
		shell.setText(Messages.getString("HolidayDialog.Shell.Title"));
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("HolidayDialog.Shell.NameStep"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		
		final int fieldcols = 3;
		final int fieldrows = 10;
		
		ColumnInfo info[] = new ColumnInfo[fieldcols];
		info[0] = new ColumnInfo(Messages.getString("HolidayDialog.Shell.Day"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"}, true);
		info[1] = new ColumnInfo(Messages.getString("HolidayDialog.Shell.Month"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]{Messages.getString("HolidayDialog.Shell.Month.1"),Messages.getString("HolidayDialog.Shell.Month.2"),Messages.getString("HolidayDialog.Shell.Month.3"),Messages.getString("HolidayDialog.Shell.Month.4"),Messages.getString("HolidayDialog.Shell.Month.5"),Messages.getString("HolidayDialog.Shell.Month.6"),Messages.getString("HolidayDialog.Shell.Month.7"),Messages.getString("HolidayDialog.Shell.Month.8"),Messages.getString("HolidayDialog.Shell.Month.9"),Messages.getString("HolidayDialog.Shell.Month.10"),Messages.getString("HolidayDialog.Shell.Month.11"),Messages.getString("HolidayDialog.Shell.Month.12")}, true);
		info[2] = new ColumnInfo(Messages.getString("HolidayDialog.Shell.Description"), ColumnInfo.COLUMN_TYPE_TEXT, false);
		
		fieldColumns.add(info[0]);
		FieldHoliday = new TableView(transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, info, fieldrows, lsMod, props);
		fdFieldHoliday = new FormData();
		fdFieldHoliday.height = 265;
		fdFieldHoliday.left = new FormAttachment(0,0);
		fdFieldHoliday.top = new FormAttachment(wStepname,10);
		fdFieldHoliday.right = new FormAttachment(100,0);
		FieldHoliday.setLayoutData(fdFieldHoliday);
		
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("HolidayDialog.ButtonOk.Label"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("HolidayDialog.ButtonCancel.Label"));
		
		BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK, wCancel}, margin,  FieldHoliday);
		
		lsCancel = new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				cancel();
			}
		};
		
		lsOK = new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				ok();
			}
		};
		
		wCancel.addListener(SWT.Selection,  lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
		
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e){ ok();}
		};
		
		wStepname.addSelectionListener(lsDef);
		
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e){ cancel();}
		});
		
		setSize();
		
		getData();
		input.setChanged(changed);
		
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()){
				display.sleep();
			}
		}
		
		return stepname;
	}
	
	public void getData(){
		wStepname.selectAll();
		
		if(input.getHoliday()!= null){
			for(int i = 0; i < input.getHoliday().length; i++){
				if(i > 9)FieldHoliday.add("");
				FieldHoliday.setText(String.valueOf(i+1), 0, i);
				if(input.getHoliday()[i][0].getValueData()!=null)
					FieldHoliday.setText(String.valueOf(input.getHoliday()[i][0].getValueData()), 1, i);
				if(input.getHoliday()[i][1].getValueData()!=null)
					FieldHoliday.setText(String.valueOf(input.getHoliday()[i][1].getValueData()), 2, i);
				if(input.getHoliday()[i][2].getValueData()!=null)
					FieldHoliday.setText(String.valueOf(input.getHoliday()[i][2].getValueData()), 3, i);
			}
		}
		
	}
	
	private void cancel(){
		stepname = null;
		input.setChanged(changed);
		dispose();
	}
	
	private void ok(){
		stepname = wStepname.getText();
		ValueMetaAndData[][] holiday = new ValueMetaAndData[FieldHoliday.getItemCount()][3];
		for(int i = 0; i < FieldHoliday.getItemCount(); i++){
			holiday[i][0] = new ValueMetaAndData(new ValueMeta("row_"+i+"holyday", ValueMetaInterface.TYPE_STRING), new String(FieldHoliday.getItem(i, 1)));
			holiday[i][1] = new ValueMetaAndData(new ValueMeta("row_"+i+"holyday", ValueMetaInterface.TYPE_STRING), new String(FieldHoliday.getItem(i, 2)));
			holiday[i][2] = new ValueMetaAndData(new ValueMeta("row_"+i+"holyday", ValueMetaInterface.TYPE_STRING), new String(FieldHoliday.getItem(i, 3)));
		}
		input.setHoliday(holiday);
		dispose();
	}

}
