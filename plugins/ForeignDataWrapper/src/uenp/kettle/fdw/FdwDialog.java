package uenp.kettle.fdw;


import java.awt.Point;
import java.awt.TextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.eclipse.swt.layout.GridData;
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
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.RowMeta;
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

public class FdwDialog extends BaseStepDialog implements StepDialogInterface{
	
	private FdwMeta input;
	int cont_execucao = 0;
	String concatenar_tipo_atributo = "";
	String nomeTabela = "";
	
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
	//private DatabaseMeta connection_fdw;
	private ModifyListener connectionChange_fdw;
	private CCombo addConexao_fdw;
	private DatabaseMeta connection_fdw;
	
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
	
	//fdw
	private Label wlUser;
	private Text wUser;
	private FormData fdlUser, fdUser;
	
	//server
	private Label wlServer;
	private Text wServer;
	private FormData fdlServer, fdServer;
	
	//host
	private Label wlHost;
	private Text wHost;
	private FormData fdlHost, fdHost;
	
	//base de dados;
	private Label wlDatabase;
	private Text wDatabase;
	private FormData fdlDatabase, fdDatabase;
	
	//Usuário FDW
	private Label wlUserFdw;
	private Text wUserFdw;
	private FormData fdlUserFdw, fdUserFdw;
	
	//Password
	private Label wlPassword;
	private Text wPassword;
	private FormData fdlPassword, fdPassword;
	
	
	private ArrayList<ColumnInfo> fieldColumns = new ArrayList<ColumnInfo>();
	private ArrayList<ColumnInfo> fieldColumns2 = new ArrayList<ColumnInfo>();
	
	
	public FdwDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input = (FdwMeta)in;
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
		shell.setText(Messages.getString("FdwDialog.Shell.Title"));
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		
		//Nome do Passo
		wlPasso = new Label(shell, SWT.RIGHT);
		wlPasso.setText(Messages.getString("FdwDialog.NameDimension"));
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
		
		
		//Conexão_fdw
		addConexao_fdw = addConnectionLine(shell, addConexao, middle, margin);
		if (input.getDatabaseMeta()==null && transMeta.nrDatabases()==1) addConexao_fdw.select(0);
		
		//Foreign Data Wrapper.
		wlUser = new Label(shell, SWT.RIGHT);
		wlUser.setText(Messages.getString("FdwDialog.NameUserPostgres"));
		props.setLook(wlUser);
		fdlUser = new FormData();
		fdlUser.left = new FormAttachment(0,0);
		fdlUser.right = new FormAttachment(middle, -10);
		fdlUser.top = new FormAttachment(addConexao_fdw, margin);
		wlUser.setLayoutData(fdlUser);
		wUser = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wUser.setText("");
		props.setLook(wUser);
		wUser.addModifyListener(lsMod);
		fdUser = new FormData();
		fdUser.left = new FormAttachment(middle, 0);
		fdUser.top = new FormAttachment(addConexao_fdw, margin);
		fdUser.right = new FormAttachment(86, 0);
		wUser.setLayoutData(fdUser);
		
		//Servidor
		wlServer = new Label(shell, SWT.RIGHT);
		wlServer.setText(Messages.getString("FdwDialog.NameServer"));
		props.setLook(wlServer);
		fdlServer = new FormData();
		fdlServer.left = new FormAttachment(0,0);
		fdlServer.right = new FormAttachment(middle, -10);
		fdlServer.top = new FormAttachment(wUser, margin);
		wlServer.setLayoutData(fdlServer);
		wServer = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wServer.setText("");
		props.setLook(wServer);
		wServer.addModifyListener(lsMod);
		fdServer = new FormData();
		fdServer.left = new FormAttachment(middle, 0);
		fdServer.top = new FormAttachment(wUser, margin);
		fdServer.right = new FormAttachment(86, 0);
		wServer.setLayoutData(fdServer);

		//host
		wlHost = new Label(shell, SWT.RIGHT);
		wlHost.setText(Messages.getString("FdwDialog.NameHost"));
		props.setLook(wlHost);
		fdlHost = new FormData();
		fdlHost.left = new FormAttachment(0,0);
		fdlHost.right = new FormAttachment(middle, -10);
		fdlHost.top = new FormAttachment(wServer, margin);
		wlHost.setLayoutData(fdlHost);
		wHost = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wHost.setText("");
		props.setLook(wHost);
		wHost.addModifyListener(lsMod);
		fdHost = new FormData();
		fdHost.left = new FormAttachment(middle, 0);
		fdHost.top = new FormAttachment(wServer, margin);
		fdHost.right = new FormAttachment(86, 0);
		wHost.setLayoutData(fdHost);

		//Base de Dados
		wlDatabase = new Label(shell, SWT.RIGHT);
		wlDatabase.setText(Messages.getString("FdwDialog.NameDatabase"));
		props.setLook(wlDatabase);
		fdlDatabase = new FormData();
		fdlDatabase.left = new FormAttachment(0,0);
		fdlDatabase.right = new FormAttachment(middle, -10);
		fdlDatabase.top = new FormAttachment(wHost, margin);
		wlDatabase.setLayoutData(fdlDatabase);
		wDatabase = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wDatabase.setText("");
		props.setLook(wDatabase);
		wDatabase.addModifyListener(lsMod);
		fdDatabase = new FormData();
		fdDatabase.left = new FormAttachment(middle, 0);
		fdDatabase.top = new FormAttachment(wHost, margin);
		fdDatabase.right = new FormAttachment(86, 0);
		wDatabase.setLayoutData(fdDatabase);

		//Usuário FDW
		wlUserFdw = new Label(shell, SWT.RIGHT);
		wlUserFdw.setText(Messages.getString("FdwDialog.NameUserFdw"));
		props.setLook(wlUserFdw);
		fdlUserFdw = new FormData();
		fdlUserFdw.left = new FormAttachment(0,0);
		fdlUserFdw.right = new FormAttachment(middle, -10);
		fdlUserFdw.top = new FormAttachment(wDatabase, margin);
		wlUserFdw.setLayoutData(fdlUserFdw);
		wUserFdw = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wUserFdw.setText("");
		props.setLook(wUserFdw);
		wUserFdw.addModifyListener(lsMod);
		fdUserFdw = new FormData();
		fdUserFdw.left = new FormAttachment(middle, 0);
		fdUserFdw.top = new FormAttachment(wDatabase, margin);
		fdUserFdw.right = new FormAttachment(86, 0);
		wUserFdw.setLayoutData(fdUserFdw);
		
		//Password
		wlPassword = new Label(shell, SWT.RIGHT);
		wlPassword.setText(Messages.getString("FdwDialog.Password"));
		props.setLook(wlPassword);
		fdlPassword = new FormData();
		fdlPassword.left = new FormAttachment(0,0);
		fdlPassword.right = new FormAttachment(middle, -10);
		fdlPassword.top = new FormAttachment(wUserFdw, margin);
		wlPassword.setLayoutData(fdlPassword);
		wPassword = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wPassword.setText("");
		props.setLook(wPassword);
		wPassword.addModifyListener(lsMod);
		fdPassword = new FormData();
		fdPassword.left = new FormAttachment(middle, 0);
		fdPassword.top = new FormAttachment(wUserFdw, margin);
		fdPassword.right = new FormAttachment(86, 0);
		wPassword.setLayoutData(fdPassword);
		
		
		//Escolha das Tabelas
		wTable = new Button(shell, SWT.PUSH | SWT.CENTER);
		props.setLook(wTable);
		wTable.setText(Messages.getString("FdwDialog.ChangeTable"));
		fdbTable = new FormData();
		fdbTable.right = new FormAttachment(100, 0);
		fdbTable.top = new FormAttachment(addConexao_fdw, margin);
		wTable.setLayoutData(fdbTable);

		//Tabs
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
		
		//Tab Tabela
		wTabelaTab = new CTabItem(wTabFolder, SWT.NONE);
		wTabelaTab.setText(Messages.getString("FdwDialog.Table"));
		
		wTabelaComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wTabelaComp);
		
		FormLayout tabelaLayout = new FormLayout();
		tabelaLayout.marginWidth = margin;
		tabelaLayout.marginHeight = margin;
		wTabelaComp.setLayout(tabelaLayout);
		
		final int fieldsCols = 4;
		final int fieldsRows = 10;
		
		ColumnInfo[] colinf = new ColumnInfo[fieldsCols];
		colinf[0] = new ColumnInfo(Messages.getString("FdwDialog.FiledTable.Table"), ColumnInfo.COLUMN_TYPE_TEXT, null , true);
		colinf[1] = new ColumnInfo(Messages.getString("FdwDialog.FiledTable.PrimaryKey"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]{" "," "," "," "}, false);
		colinf[2] = new ColumnInfo(Messages.getString("FdwDialog.FiledTable.ForeignTable"), ColumnInfo.COLUMN_TYPE_TEXT, null, true);
		colinf[3] = new ColumnInfo(Messages.getString("FdwDialog.FiledTable.ForeignKey"), ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]{" "," "," "," "}, false);
		
	
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
		fdTabelaComp.top = new FormAttachment(wServer,0);
		fdTabelaComp.right = new FormAttachment(100,0);
		fdTabelaComp.bottom = new FormAttachment(100,0);
		wTabelaComp.setLayoutData(fdTabelaComp);
		
		wTabelaComp.layout();
		wTabelaTab.setControl(wTabelaComp);
		
		//Tab Atributos
		wAtributosTab = new CTabItem(wTabFolder, SWT.NONE);
		wAtributosTab.setText(Messages.getString("FdwDialog.Atributtes"));
		
		wAtributosComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wAtributosComp);
		
		FormLayout atributoLayout = new FormLayout();
		atributoLayout.marginWidth = margin;
		atributoLayout.marginHeight = margin;
		wAtributosComp.setLayout(atributoLayout);
		
		final int fieldsCols2 = 2;
		
		ColumnInfo[] colinf2 = new ColumnInfo[fieldsCols2];
		colinf2[0] = new ColumnInfo(Messages.getString("FdwDialog.FiledAtributtes.Table"), ColumnInfo.COLUMN_TYPE_TEXT, null, true);
		colinf2[1] = new ColumnInfo(Messages.getString("FdwDialog.FiledAtributtes.Atributtes"), ColumnInfo.COLUMN_TYPE_TEXT, null, true);
		
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
		fdTabFolder.top = new FormAttachment(wPassword, margin);
		fdTabFolder.right = new FormAttachment(100,0);
		fdTabFolder.bottom= new FormAttachment(100,-50);
		wTabFolder.setLayoutData(fdTabFolder);
		
		//Botoes
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("FdwDialog.Ok"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("FdwDialog.Cancel"));
		
		
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
					try {
						getSQL();
					} catch (KettleDatabaseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		};
		
		conectionChange = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				conectionChange();
			}
		};
		
		connectionChange_fdw = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				conectionChange_fdw();			}
		};
		wPasso.addSelectionListener(lsDef);
		wTable.addListener(SWT.Selection, lsbTable);
		addConexao.addModifyListener(conectionChange);
		addConexao_fdw.addModifyListener(connectionChange_fdw);
		
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
		if(input.getConection().getValueData()!= null){
			addConexao.setText(String.valueOf(input.getConection().getValueData()));
		}
		
		if(input.getConnection_fdw().getValueData()!= null){
			addConexao_fdw.setText(String.valueOf(input.getConnection_fdw().getValueData()));
		}
		
		if(input.getNameUser().getValueData()!= null){
			wUser.setText(String.valueOf(input.getNameUser().getValueData()));
		}
		
		if(input.getNameServer().getValueData()!= null){
			wServer.setText(String.valueOf(input.getNameServer().getValueData()));
		}
		
		if(input.getNameHost().getValueData()!= null){
			wHost.setText(String.valueOf(input.getNameHost().getValueData()));
		}
		
		if(input.getNameDatabase().getValueData()!= null){
			wDatabase.setText(String.valueOf(input.getNameDatabase().getValueData()));
		}
		
		if(input.getNameUserFdw().getValueData()!= null){
			wUserFdw.setText(String.valueOf(input.getNameUserFdw().getValueData()));
		}
		
		if(input.getPassword().getValueData()!= null){
			wPassword.setText(String.valueOf(input.getPassword().getValueData()));
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
	
	public void ok() {
		stepname = wPasso.getText();
		input.setConection(new ValueMetaAndData(new ValueMeta("conection", ValueMetaInterface.TYPE_STRING), new String(conection.getName())));
		input.setNameDimension(new ValueMetaAndData(new ValueMeta("dimension", ValueMetaInterface.TYPE_STRING), new String(wPasso.getText())));
		input.setConnection_fdw(new ValueMetaAndData(new ValueMeta("connection_fdw", ValueMetaInterface.TYPE_STRING), new String(addConexao_fdw.getText())));
		input.setNameUser(new ValueMetaAndData(new ValueMeta("User Database", ValueMetaInterface.TYPE_STRING), new String(wUser.getText())));
		input.setNameServer(new ValueMetaAndData(new ValueMeta("Server", ValueMetaInterface.TYPE_STRING), new String(wServer.getText())));
		input.setNameHost(new ValueMetaAndData(new ValueMeta("Host", ValueMetaInterface.TYPE_STRING), new String(wHost.getText())));
		input.setNameDatabase(new ValueMetaAndData(new ValueMeta("Database", ValueMetaInterface.TYPE_STRING), new String(wDatabase.getText())));
		input.setNameUserFdw(new ValueMetaAndData(new ValueMeta("User Foreign Data Wrapper", ValueMetaInterface.TYPE_STRING), new String(wUserFdw.getText())));
		input.setPassword(new ValueMetaAndData(new ValueMeta("Password", ValueMetaInterface.TYPE_STRING), new String(wPassword.getText())));
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
						yn.setMessage(Messages.getString("FdwDialog.ChangeTable.Question"));
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
	
	private void conectionChange_fdw(){
		if(addConexao_fdw.getText()!="" && addConexao_fdw.getText()!=null){
			if(connection_fdw != null){
				if(!connection_fdw.equals(transMeta.findDatabase(addConexao_fdw.getText()))){
					boolean add = false;
					for(int i = 0; i < wFields.getItemCount(); i++){
						if(!wFields.getItem(i, 1).equals("") && !wFields.getItem(i, 1).equals(null)){
							add = true;
							break;
						}
					}
					if(add){
						MessageBox yn = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
						yn.setMessage(Messages.getString("FdwDialog.ChangeTable.Question"));
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
							connection_fdw = transMeta.findDatabase(addConexao_fdw.getText());
							break;
						case SWT.NO:
							addConexao_fdw.setText(connection_fdw.getName());
							break;
						}
					}else{
						connection_fdw = transMeta.findDatabase(addConexao_fdw.getText());
					}		
				}	
			}else{
				connection_fdw = transMeta.findDatabase(addConexao_fdw.getText());
			}
		}
	}
	
	private void getSQL() throws KettleDatabaseException {
		if(conection!=null){
			DatabaseExplorerDialog sdt = new DatabaseExplorerDialog(shell, SWT.NONE, conection, transMeta.getDatabases());
			//logBasic("SDT: "+ sdt.toString());
			logBasic("conection: "+ conection.getName());
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
//						logBasic("sdt.getSchemaName(): " + sdt.getSchemaName());
						Database db = new Database(loggingObject, conection);
						db.shareVariablesWith(transMeta);
						// nome da conexão;
			//			logBasic("db: " + db);
						try {
							db.connect();
							int aux = 0;
							for(int i = 0; i < wFields.getItemCount(); i++){
								String valor = wFields.getItem(i, 1);
								if(valor != "" && valor != null){
									nomeTabela = valor;
									String sql = "SELECT *"+Const.CR+"FROM "+conection.getQuotedSchemaTableCombination(sdt.getSchemaName(), valor)+Const.CR;
									RowMetaInterface fields = db.getQueryFields(sql, false);
									logBasic("nomeTabela: "+ nomeTabela);
									//auxTable = Const.CR;
									//db.GetQueryFields contém informações dos campos da tabela.
									//logBasic("db.getQueryFields: "+ db.getQueryFields(sql, false));
									
									//CONST.CR = nome da tabela.
									//logBasic("const.CR: "+ conection.getQuotedSchemaTableCombination(sdt.getSchemaName(), valor));
									
									concatenar_tipo_atributo = "";
									for(int j =0; j < fields.size(); j++){
										if(aux >= wFields2.getItemCount()){
											wFields2.add("");
										}
										ValueMetaInterface field = fields.getValueMeta(j);
										wFields2.setText(""+(aux+1), 0, aux);
										wFields2.setText(valor, 1, aux);
										wFields2.setText(field.getName(), 2, aux);
										aux++;
										//Nome dos campos
										//logBasic("nome: "+ field.getName());
										if(j+1 == fields.size()){	
											concatenar_tipo_atributo = concatenar_tipo_atributo + field.getName() + " " + field.getTypeDesc() + "\n";
											concatenar_tipo_atributo = concatenar_tipo_atributo.replaceAll("String", "character varying(45)");
											concatenar_tipo_atributo = concatenar_tipo_atributo.replaceAll("Number", "double precision");
											logBasic("concatenacao: " + concatenar_tipo_atributo);
										} else {
											concatenar_tipo_atributo = concatenar_tipo_atributo + field.getName() + " " + field.getTypeDesc() + ",\n ";
										}
										//o Tipo dos dados.
										//logBasic("tipo: " + field.getTypeDesc());
									}
								}
							}
	//						logBasic("cont_execucao: "+ cont_execucao);
						} catch (KettleDatabaseException e) {
							e.printStackTrace();
						}
						db.disconnect();
						
						logBasic("CONNECTION_FDW: "+ connection_fdw.getName());
						if(connection_fdw != null){
							//logBasic("Entreei");
							DatabaseExplorerDialog sdt_fdw = new DatabaseExplorerDialog(shell, SWT.NONE, connection_fdw, transMeta.getDatabases());
							if(sdt_fdw != null){
								//logBasic("Abri tella if1");
								Database db_fdw = new Database(loggingObject, connection_fdw);
								db_fdw.shareVariablesWith(transMeta);
									try{
										db_fdw.connect();
										logBasic("BANCO DE DADOS: " + db_fdw);
										if(cont_execucao == 0){
											String sql_extension = "create extension if not exists postgres_fdw";
											Result res_extension = db_fdw.execStatement(sql_extension);
											logBasic("result_extension: "+ res_extension.getResult());
											
											String sql_create = "CREATE SERVER "+ input.getNameServer().getValueData() + " foreign data wrapper postgres_fdw options (host '" + wHost.getText() + "', dbname '" + wDatabase.getText() + "', port '5432');";
											Result res_create = db_fdw.execStatement(sql_create);
											logBasic("result_create: "+ res_create);
											
											String permissao = "grant all on foreign data wrapper postgres_fdw to " + wUser.getText();
											Result res_permissao = db_fdw.execStatement(permissao);
											logBasic("result_permissao: "+ res_permissao);
											
											String permissao1 = "grant all on foreign server " + wServer.getText() + " to " + wUser.getText();
											Result res_permissao1 = db_fdw.execStatement(permissao1);
											logBasic("result_permissao1: "+ res_permissao1);
											
											String mapping = "create user mapping for public server " + wServer.getText() + " options(user '"+ wUserFdw.getText() +"', password '" + wPassword.getText() + "')";
											Result sql_mapping = db_fdw.execStatement(mapping);
											logBasic("result_mapping: "+ sql_mapping);
											
											//na hora de criar ta dando error ( duplicando tabelas )
											logBasic("nomeTabela: "+ nomeTabela);
											String sql_table = "create foreign table " +  nomeTabela + "_fdw ( " + concatenar_tipo_atributo + " ) server " + input.getNameServer().getValueData() + " options(table_name '"+  nomeTabela +"');";
											Result res_table = db_fdw.execStatement(sql_table);
											logBasic("result_table: "+ res_table);	
											
											concatenar_tipo_atributo = "";
											cont_execucao++;
										} else {
											logBasic("Entrei no else!!");
											String sql_table = "create foreign table " + nomeTabela + "_fdw ( "+ concatenar_tipo_atributo +" ) server " + input.getNameServer().getValueData() + " options(table_name '"+  nomeTabela +"');";
											Result res_table = db_fdw.execStatement(sql_table);
											logBasic("result_table: "+ res_table);	
										}
									} catch(KettleDatabaseException e){
										logBasic("ENTREI NO CAATCH");
										String sql_table = "create foreign table " + nomeTabela + "_fdw ( "+ concatenar_tipo_atributo +" ) server " + input.getNameServer().getValueData() + " options(table_name '"+  nomeTabela +"');";
										Result res_table = db_fdw.execStatement(sql_table);
										logBasic("result_table: "+ res_table);
									}


							}
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
