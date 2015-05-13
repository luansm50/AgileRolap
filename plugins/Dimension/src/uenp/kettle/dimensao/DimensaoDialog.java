package uenp.kettle.dimensao;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.database.dialog.DatabaseExplorerDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.core.row.RowMetaInterface;

public class DimensaoDialog extends BaseStepDialog implements StepDialogInterface{
	
	private DimensaoMeta input;
	
	//Variaveis Auxiliares
	public int tbl_linhas = 0;
	
	//Nome do Passo
	private Label wlPasso;
	private Text wPasso;
	private FormData fdlPasso, fdPasso;
	
	//Conexao
	private CCombo addConexao;
	private ModifyListener conectionChange;
	private DatabaseMeta conection;
	
	
	//Tabs
	private CTabFolder wTabFolder;
	private FormData fdTabFolder;
	private CTabItem wTabelaTab, wAtributosTab;
	private Composite wTabelaComp, wAtributosComp;
	private FormData fdTabelaComp, fdAtributosComp;
	TableROLAP wFields;

	private TableROLAP wFields2;
	private FormData fdFields, fdFields2;
	
	//Botao Tabela
	private Button wTable;
	private FormData fdbTable;
	private Listener lsbTable;
	
	private ArrayList<ColumnInfo> fieldColumns = new ArrayList<ColumnInfo>();
	private ArrayList<ColumnInfo> fieldColumns2 = new ArrayList<ColumnInfo>();
	
	
	public DimensaoDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input = (DimensaoMeta)in;
	}
	
	
	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, input);
		
		ModifyListener lsMod = new ModifyListener(){
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
		shell.setText(Messages.getString("DimensaoDialog.Shell.Title"));
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		
		//Nome do Passo
		wlPasso = new Label(shell, SWT.RIGHT);
		wlPasso.setText(Messages.getString("DimensaoDialog.NameDimension"));
		props.setLook(wlPasso);
		fdlPasso = new FormData();
		fdlPasso.left = new FormAttachment(0,0);
		fdlPasso.right = new FormAttachment(middle, -10);
		fdlPasso.top = new FormAttachment(0, margin);
		wlPasso.setLayoutData(fdlPasso);
		wPasso = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wPasso.setText(stepname);
		props.setLook(wPasso);
		wPasso.addModifyListener(lsMod);
		fdPasso = new FormData();
		fdPasso.left = new FormAttachment(middle, 0);
		fdPasso.top = new FormAttachment(0, margin);
		fdPasso.right = new FormAttachment(100, 0);
		wPasso.setLayoutData(fdPasso);
		
		//Conexao
		addConexao = addConnectionLine(shell, wPasso, middle, margin);
		if (input.getDatabaseMeta()==null && transMeta.nrDatabases()==1) addConexao.select(0);
		
		//Escolha das Tabelas
		wTable = new Button(shell, SWT.PUSH | SWT.CENTER);
		props.setLook(wTable);
		wTable.setText(Messages.getString("DimensaoDialog.ChangeTable"));
		fdbTable = new FormData();
		fdbTable.right = new FormAttachment(100, 0);
		fdbTable.top = new FormAttachment(addConexao, margin);
		wTable.setLayoutData(fdbTable);
		
		//Tabs
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
		
		//Tab Tabela
		wTabelaTab = new CTabItem(wTabFolder, SWT.NONE);
		wTabelaTab.setText(Messages.getString("DimensaoDialog.Table"));
		
		wTabelaComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wTabelaComp);
		
		FormLayout tabelaLayout = new FormLayout();
		tabelaLayout.marginWidth = margin;
		tabelaLayout.marginHeight = margin;
		wTabelaComp.setLayout(tabelaLayout);
		
		final int fieldsCols = 4;
		final int fieldsRows = 10;
		
		ColumnInfo[] colinf = new ColumnInfo[fieldsCols];
		colinf[0] = new ColumnInfo(Messages.getString("DimensaoDialog.FiledTable.Table"), ColumnInfo.COLUMN_TYPE_TEXT, null , true);
		colinf[1] = new ColumnInfo(Messages.getString("DimensaoDialog.FiledTable.PrimaryKey"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]{" "," "," "," "}, false);
		colinf[2] = new ColumnInfo(Messages.getString("DimensaoDialog.FiledTable.ForeignTable"), ColumnInfo.COLUMN_TYPE_TEXT, null, true);
		colinf[3] = new ColumnInfo(Messages.getString("DimensaoDialog.FiledTable.ForeignKey"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]{" "," "," "," "}, false);
		
	
		fieldColumns.add(colinf[0]);
		int aux[] = new int[]{Variables.miRowUp, Variables.miRowDown, Variables.miDelAll};
		wFields = new TableROLAP(transMeta, wTabelaComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, fieldsRows, lsMod, props, aux);
		fdFields = new FormData();
		fdFields.height = 270;
		fdFields.left = new FormAttachment(0,0);
		fdFields.top = new FormAttachment(0, 0);
		fdFields.right = new FormAttachment(100, 0);
		wFields.setLayoutData(fdFields);
	
		fdTabelaComp = new FormData();
		fdTabelaComp.left = new FormAttachment(0,0);
		fdTabelaComp.top = new FormAttachment(0,0);
		fdTabelaComp.right = new FormAttachment(100,0);
		fdTabelaComp.bottom = new FormAttachment(100,0);
		wTabelaComp.setLayoutData(fdTabelaComp);
		
		wTabelaComp.layout();
		wTabelaTab.setControl(wTabelaComp);
		
		//Tab Atributos
		wAtributosTab = new CTabItem(wTabFolder, SWT.NONE);
		wAtributosTab.setText(Messages.getString("DimensaoDialog.Atributtes"));
		
		wAtributosComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wAtributosComp);
		
		FormLayout atributoLayout = new FormLayout();
		atributoLayout.marginWidth = margin;
		atributoLayout.marginHeight = margin;
		wAtributosComp.setLayout(atributoLayout);
		
		final int fieldsCols2 = 2;
		
		ColumnInfo[] colinf2 = new ColumnInfo[fieldsCols2];
		colinf2[0] = new ColumnInfo(Messages.getString("DimensaoDialog.FiledAtributtes.Table"), ColumnInfo.COLUMN_TYPE_TEXT, null, true);
		colinf2[1] = new ColumnInfo(Messages.getString("DimensaoDialog.FiledAtributtes.Atributtes"), ColumnInfo.COLUMN_TYPE_TEXT, null, true);
		
		fieldColumns2.add(colinf2[0]);
		wFields2 = new TableROLAP(transMeta, wAtributosComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf2, fieldsRows, lsMod, props, new int[]{Variables.miDelAll});
		
		fdFields2 = new FormData();
		fdFields2.height = 270;
		fdFields2.left = new FormAttachment(0,0);
		fdFields2.top = new FormAttachment(0, 0);
		fdFields2.right = new FormAttachment(100, 0);
		wFields2.setLayoutData(fdFields2);
		
		fdAtributosComp = new FormData();
		fdAtributosComp.left = new FormAttachment(0,0);
		fdAtributosComp.top = new FormAttachment(0,0);
		fdAtributosComp.right = new FormAttachment(100,0);
		fdAtributosComp.bottom = new FormAttachment(100,0);
		wAtributosComp.setLayoutData(fdAtributosComp);
		
		wAtributosComp.layout();
		wAtributosTab.setControl(wAtributosComp);
				
		//Tabs Form
		fdTabFolder = new FormData();
		fdTabFolder.left = new FormAttachment(0,0);
		fdTabFolder.top = new FormAttachment(wTable, margin);
		fdTabFolder.right = new FormAttachment(100,0);
		fdTabFolder.bottom= new FormAttachment(100,-50);
		wTabFolder.setLayoutData(fdTabFolder);
		
		//Botoes
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("DimensaoDialog.Ok"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("DimensaoDialog.Cancel"));
		
		
		BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK, wCancel}, margin, wTabFolder);
		
		lsCancel = new Listener() {
			@Override
			public void handleEvent(Event e) {
				cancel();
			}
		};
		
		lsOK = new Listener() {
			@Override
			public void handleEvent(Event e) {
				ok();
			}
		};
		
		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
		
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e){
				ok();
			}
		};
		
		lsbTable = new Listener() {
			
			@Override
			public void handleEvent(Event e) {
				getSQL();
				
			}
		};
		
		conectionChange = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				conectionChange();
			}
		};
		
		wPasso.addSelectionListener(lsDef);
		wTable.addListener(SWT.Selection, lsbTable);
		addConexao.addModifyListener(conectionChange);
		
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e){
				cancel();
			}
		});
		
		wTabFolder.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				delete();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
		setSize();
		
		getData();
		input.setChanged(changed);
		
		wTabFolder.setSelection(0);
		
		shell.open();
		while(!shell.isDisposed()){
			if(display.readAndDispatch()){
				display.sleep();
			}
		}
		
		
		return stepname;
	}
	
	public void getData(){
		wPasso.selectAll();
		if(input.getConection()!= null){
			addConexao.setText(String.valueOf(input.getConection().getValueData()));
		}
		conectionChange();
		DatabaseExplorerDialog sdt = new DatabaseExplorerDialog(shell, SWT.NONE, conection, transMeta.getDatabases());
		wFields.setConexao(conection, transMeta, loggingObject, sdt.getSchemaName());
		if(input.getTables()!= null){
			for(int i = 0; i < input.getTables().length; i++){
				if(i > 9)wFields.add("");
				wFields.setText(String.valueOf(i+1), 0, i);
				wFields.setText(String.valueOf(input.getTables()[i][0]), 1, i);
				wFields.setText(String.valueOf(input.getTables()[i][1]), 2, i);
				if(i>0 && String.valueOf(input.getTables()[i][0])!=null){
					if(!String.valueOf(input.getTables()[i][0]).equals("")){
						wFields.setText(String.valueOf(input.getTables()[i-1][0]), 3, i);
					}
				}
				wFields.setText(String.valueOf(input.getTables()[i][2]), 4, i);
			}
		}
		if(input.getAttributes()!= null){
			int aux = 0;
			for(int i = 0; i < input.getAttributes().length; i++){
				if(String.valueOf(input.getAttributes()[i][1])!=null){
					if(aux > 9)wFields2.add("");
					wFields2.setText(String.valueOf(aux+1), 0, aux);
					wFields2.setText(String.valueOf(input.getAttributes()[i][0]), 1, aux);
					wFields2.setText(String.valueOf(input.getAttributes()[i][1]), 2, aux);
					aux++;
				}
			}
		}
	}
	
	public void cancel(){
		stepname = null;
		input.setChanged(changed);
		dispose();
	}
	
	public void ok(){
		stepname = wPasso.getText();
		input.setConection(new ValueMetaAndData(new ValueMeta("conection", ValueMetaInterface.TYPE_STRING), new String(conection.getName())));
		input.setNameDimension(new ValueMetaAndData(new ValueMeta("dimension", ValueMetaInterface.TYPE_STRING), new String(wPasso.getText())));
		ValueMetaAndData[][] tbl = new ValueMetaAndData[wFields.getItemCount()][3];
		ValueMetaAndData[][] atr = new ValueMetaAndData[wFields2.getItemCount()][2];
		for(int i = 0; i < wFields.getItemCount(); i++){
			tbl[i][0] = new ValueMetaAndData(new ValueMeta("row_"+i+"table", ValueMetaInterface.TYPE_STRING), new String(wFields.getItem(i, 1)));
			tbl[i][1] = new ValueMetaAndData(new ValueMeta("row_"+i+"primaryKey", ValueMetaInterface.TYPE_STRING), new String(wFields.getItem(i, 2)));
			tbl[i][2] = new ValueMetaAndData(new ValueMeta("row_"+i+"foreignKey", ValueMetaInterface.TYPE_STRING), new String(wFields.getItem(i, 4)));
		}
		input.setTables(tbl);
		for(int i = 0; i < wFields2.getItemCount(); i++){
			atr[i][0] = new ValueMetaAndData(new ValueMeta("row_"+i+"table", ValueMetaInterface.TYPE_STRING), new String(wFields2.getItem(i, 1)));
			atr[i][1] = new ValueMetaAndData(new ValueMeta("row_"+i+"atrName", ValueMetaInterface.TYPE_STRING), new String(wFields2.getItem(i, 2)));
		}
		input.setAttributes(atr);
		dispose();
	}
	
	private void conectionChange(){
		if(addConexao.getText()!="" && addConexao.getText()!=null){
			if(conection != null){
				if(!conection.equals(transMeta.findDatabase(addConexao.getText()))){
					boolean add = false;
					for(int i = 0; i < wFields.getItemCount(); i++){
						if(!wFields.getItem(i, 1).equals("") && !wFields.getItem(i, 1).equals(null)){
							add = true;
							break;
						}
					}
					if(add){
						MessageBox yn = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
						yn.setMessage(Messages.getString("DimensaoDialog.ChangeTable.Question"));
						switch (yn.open()) {
						case SWT.YES:
							for(int i = 0; i < wFields.getItemCount(); i++){
								wFields.setText("", 1, i);
								wFields.setText("", 2, i);
								wFields.setText("", 4, i);
								if(i >= 10){
									wFields.remove(i);
									i--;
								}
							}
							for(int i = 0; i < wFields2.getItemCount(); i++){
								wFields2.setText("", 1, i);
								wFields2.setText("", 2, i);
								if(i >= 10){
									wFields2.remove(i);
									i--;
								}
							}
							conection = transMeta.findDatabase(addConexao.getText());
							break;
						case SWT.NO:
							addConexao.setText(conection.getName());
							break;
						}
					}else{
						conection = transMeta.findDatabase(addConexao.getText());
					}		
				}	
			}else{
				conection = transMeta.findDatabase(addConexao.getText());
			}
		}
	}
	
	private void getSQL(){
		if(conection!=null){
			DatabaseExplorerDialog sdt = new DatabaseExplorerDialog(shell, SWT.NONE, conection, transMeta.getDatabases());
			if(sdt != null){
					if(sdt.open()){
						int x = 0;
						while(wFields.getItem(x, 1)!=""){
							x++;
						}
						wFields.setText(""+(x+1), 0, x);
						wFields.setText(sdt.getTableName(), 1, x);
						if(x>0 && !sdt.getTableName().equals("")){
							wFields.setText(wFields.getItem(x-1, 1), 3, x);
						}
						wFields.setConexao(conection, transMeta, loggingObject, sdt.getSchemaName());
						
						Database db = new Database(loggingObject, conection);
						db.shareVariablesWith(transMeta);
						try {
							db.connect();
							int aux = 0;
							for(int i = 0; i < wFields.getItemCount(); i++){
								String valor = wFields.getItem(i, 1);
								if(valor != "" && valor != null){
									String sql = "SELECT *"+Const.CR+"FROM "+conection.getQuotedSchemaTableCombination(sdt.getSchemaName(), valor)+Const.CR;
									RowMetaInterface fields = db.getQueryFields(sql, false);
									for(int j =0; j < fields.size(); j++){
										if(aux >= wFields2.getItemCount()){
											wFields2.add("");
										}
										ValueMetaInterface field = fields.getValueMeta(j);
										wFields2.setText(""+(aux+1), 0, aux);
										wFields2.setText(valor, 1, aux);
										wFields2.setText(field.getName(), 2, aux);
										aux++;
									}
								}
							}
						} catch (KettleDatabaseException e) {
							e.printStackTrace();
						}
						if(x >= 9){
							wFields.add("");
						}
					}		
			}
		}else{
			logBasic("Base nao Selecionada");
		}		
	}
	
	public void delete(){
		for(int i = 0; i < wFields2.getItemCount(); i++){
			boolean aux = false;
			for(int j = 0; j < wFields.getItemCount(); j++){
				if(wFields2.getItem(i, 1).equals(wFields.getItem(j, 1))){
					aux = true;
					break;
				}
			}
			if(!aux){
				wFields2.remove(i);
				i--;
			}
		}
	}
}
