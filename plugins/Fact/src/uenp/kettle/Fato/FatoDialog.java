package uenp.kettle.Fato;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
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
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FatoDialog extends BaseStepDialog implements StepDialogInterface {

	private FatoMeta input;
	private ArrayList<String> field_dimension_time = new ArrayList<String>();

	// Conexao
	private DatabaseMeta conection;

	// Tabs
	private CTabFolder wTabFolder;
	private FormData fdTabFolder;
	private CTabItem wSQLTab, wDimensionTab, wTimeDimensionTab, wMeasuresTab,
			wNovoSQLTab;
	private Composite wSQLComp, wDimensionComp, wTimeDimensionComp,
			wMeasuresComp, wNovoSQLComp;
	private FormData fdSQLComp, fdDimensionComp, fdTimeDimensionComp,
			fdMeasuresComp, fdNovoSQLComp;

	// Novo SQL
	private TableView wQueryBuilder;
	private FormData fdQueryBuilder;

	// SQL
	private StyledTextComp wSQL;
	private Button fbSQL;
	private FormData fdSQL, fdbSQL;

	// Dimensions
	private ArrayList<ColumnInfo> fieldColumns = new ArrayList<ColumnInfo>();
	private TableView FieldDimension;
	private FormData fdFieldDimension;

	// Dimension Time
	private ArrayList<ColumnInfo> fieldColumnsTime = new ArrayList<ColumnInfo>();
	private TableView FieldDimensionTime;
	private FormData fdFieldDimensionTime;

	// Medidas
	private ArrayList<ColumnInfo> fieldColumnsMeasures = new ArrayList<ColumnInfo>();
	private TableView FieldMeasures;
	private FormData fdFieldMeasures;

	public FatoDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (FatoMeta) in;
	}

	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX
				| SWT.MIN);
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
		shell.setText(Messages.getString("FatoDialog.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Nome do Passo
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("FatoDialog.Shell.NameStep"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -10);
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

		// Tabs
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

		// Tab Novo SQL
		wNovoSQLTab = new CTabItem(wTabFolder, SWT.NONE);
		wNovoSQLTab.setText(Messages.getString("FatoDialog.Shell.TabNovoSQL"));

		wNovoSQLComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wNovoSQLComp);

		FormLayout LayoutNovoSQL = new FormLayout();
		LayoutNovoSQL.marginWidth = margin;
		LayoutNovoSQL.marginHeight = margin;
		wNovoSQLComp.setLayout(LayoutNovoSQL);

		fdNovoSQLComp = new FormData();
		fdNovoSQLComp.left = new FormAttachment(0, 0);
		fdNovoSQLComp.top = new FormAttachment(0, 0);
		fdNovoSQLComp.right = new FormAttachment(100, 0);
		fdNovoSQLComp.bottom = new FormAttachment(100, 0);
		wNovoSQLComp.setLayoutData(fdNovoSQLComp);

		wNovoSQLComp.layout();
		wNovoSQLTab.setControl(wNovoSQLComp);

		// new table view dimension usage

		int cols = 4;
		int rows = 10;

		final ColumnInfo infoQueryBuilder[] = new ColumnInfo[cols];
		infoQueryBuilder[0] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabNovoSQL.Table.Field"),
				ColumnInfo.COLUMN_TYPE_TEXT, new String[] { " ", " ", " " },
				false);

		infoQueryBuilder[1] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabNovoSQL.NickName"),
				ColumnInfo.COLUMN_TYPE_TEXT, new String[] { " ", " ", " " },
				false);
		infoQueryBuilder[2] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabNovoSQL.Tables"),
				ColumnInfo.COLUMN_TYPE_TEXT, new String[] { " ", " ", " " },
				false);
		infoQueryBuilder[3] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabNovoSQL.Conditions"),
				ColumnInfo.COLUMN_TYPE_TEXT, new String[] { " ", " ", " " },
				false);

		wQueryBuilder = new TableView(transMeta, wNovoSQLComp, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI, infoQueryBuilder, rows,
				lsMod, props);
		fdQueryBuilder = new FormData();
		fdQueryBuilder.height = 265;
		fdQueryBuilder.left = new FormAttachment(0, 0);
		fdQueryBuilder.top = new FormAttachment(0, 0);
		fdQueryBuilder.right = new FormAttachment(100, 0);
		wQueryBuilder.setLayoutData(fdQueryBuilder);
		props.setLook(wQueryBuilder);

		// Tab SQL
		wSQLTab = new CTabItem(wTabFolder, SWT.NONE);
		wSQLTab.setText(Messages.getString("FatoDialog.Shell.TabSQL"));

		wSQLComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wSQLComp);

		FormLayout LayoutSQL = new FormLayout();
		LayoutSQL.marginWidth = margin;
		LayoutSQL.marginHeight = margin;
		wSQLComp.setLayout(LayoutSQL);

		fbSQL = new Button(wSQLComp, SWT.PUSH);
		props.setLook(fbSQL);
		fbSQL.setText(Messages.getString("FatoDialog.Shell.VerificSQL"));
		fdbSQL = new FormData();
		fdbSQL.right = new FormAttachment(100, 0);
		fdbSQL.bottom = new FormAttachment(100, 0);
		fbSQL.setLayoutData(fdbSQL);

		wSQL = new StyledTextComp(transMeta, wSQLComp, SWT.MULTI | SWT.LEFT
				| SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, "");
		props.setLook(wSQL, Props.WIDGET_STYLE_FIXED);
		fdSQL = new FormData();
		fdSQL.left = new FormAttachment(0, 0);
		fdSQL.top = new FormAttachment(0, 0);
		fdSQL.right = new FormAttachment(100, 0);
		fdSQL.bottom = new FormAttachment(fbSQL, -20);
		wSQL.setLayoutData(fdSQL);

		fdSQLComp = new FormData();
		fdSQLComp.left = new FormAttachment(0, 0);
		fdSQLComp.top = new FormAttachment(0, 0);
		fdSQLComp.right = new FormAttachment(100, 0);
		fdSQLComp.bottom = new FormAttachment(100, 0);
		wSQLComp.setLayoutData(fdSQLComp);

		wSQLComp.layout();
		wSQLTab.setControl(wSQLComp);

		// Tab Dimensao
		wDimensionTab = new CTabItem(wTabFolder, SWT.NONE);
		wDimensionTab.setText(Messages
				.getString("FatoDialog.Shell.TabDimension"));

		wDimensionComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wDimensionComp);

		FormLayout layoutDimension = new FormLayout();
		layoutDimension.marginWidth = margin;
		layoutDimension.marginHeight = margin;
		wDimensionComp.setLayout(layoutDimension);

		final int fieldcols = 2;
		final int fieldrows = 10;

		ColumnInfo info[] = new ColumnInfo[fieldcols];
		info[0] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabDimension.Attribute"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { " ", " ", " ",
						" " }, false);
		info[1] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabDimension.Dimension"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { " ", " ", " ",
						" " }, false);

		fieldColumns.add(info[0]);
		FieldDimension = new TableView(transMeta, wDimensionComp, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI, info, fieldrows, lsMod, props);
		fdFieldDimension = new FormData();
		fdFieldDimension.height = 265;
		fdFieldDimension.left = new FormAttachment(0, 0);
		fdFieldDimension.top = new FormAttachment(0, 0);
		fdFieldDimension.right = new FormAttachment(100, 0);
		FieldDimension.setLayoutData(fdFieldDimension);

		fdDimensionComp = new FormData();
		fdDimensionComp.left = new FormAttachment(0, 0);
		fdDimensionComp.top = new FormAttachment(0, 0);
		fdDimensionComp.right = new FormAttachment(100, 0);
		fdDimensionComp.bottom = new FormAttachment(100, 0);
		wDimensionComp.setLayoutData(fdDimensionComp);

		wDimensionComp.layout();
		wDimensionTab.setControl(wDimensionComp);

		// Tab Dimensao Tempo
		wTimeDimensionTab = new CTabItem(wTabFolder, SWT.NONE);
		wTimeDimensionTab.setText(Messages
				.getString("FatoDialog.Shell.TabTimeDimension"));

		wTimeDimensionComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wTimeDimensionComp);

		FormLayout layoutDimensionTime = new FormLayout();
		layoutDimensionTime.marginWidth = margin;
		layoutDimensionTime.marginHeight = margin;
		wTimeDimensionComp.setLayout(layoutDimensionTime);

		final int fieldcolsTime = 3;
		final int fieldrowsTime = 10;

		ColumnInfo infoTime[] = new ColumnInfo[fieldcolsTime];
		infoTime[0] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabTimeDimension.Attribute"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { " ", " ", " ",
						" " }, false);
		infoTime[1] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabTimeDimension.Comparation"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { " ", " ", " ",
						" " }, false);
		infoTime[2] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabTimeDimension.NameDimension"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { " ", " ", " ",
						" " }, false);

		fieldColumnsTime.add(infoTime[0]);
		FieldDimensionTime = new TableView(transMeta, wTimeDimensionComp,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, infoTime,
				fieldrowsTime, lsMod, props);
		fdFieldDimensionTime = new FormData();
		fdFieldDimensionTime.height = 265;
		fdFieldDimensionTime.left = new FormAttachment(0, 0);
		fdFieldDimensionTime.top = new FormAttachment(0, 0);
		fdFieldDimensionTime.right = new FormAttachment(100, 0);
		FieldDimensionTime.setLayoutData(fdFieldDimensionTime);

		fdTimeDimensionComp = new FormData();
		fdTimeDimensionComp.left = new FormAttachment(0, 0);
		fdTimeDimensionComp.top = new FormAttachment(0, 0);
		fdTimeDimensionComp.right = new FormAttachment(100, 0);
		fdTimeDimensionComp.bottom = new FormAttachment(100, 0);
		wTimeDimensionComp.setLayoutData(fdTimeDimensionComp);

		wTimeDimensionComp.layout();
		wTimeDimensionTab.setControl(wTimeDimensionComp);

		// Tab Medidas
		wMeasuresTab = new CTabItem(wTabFolder, SWT.NONE);
		wMeasuresTab
				.setText(Messages.getString("FatoDialog.Shell.TabMeasures"));

		wMeasuresComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wMeasuresComp);

		FormLayout layoutMeasures = new FormLayout();
		layoutMeasures.marginWidth = margin;
		layoutMeasures.marginHeight = margin;
		wMeasuresComp.setLayout(layoutMeasures);

		final int fieldcolsMeasures = 2;
		final int fieldrowsMeasures = 10;

		ColumnInfo infoMeasures[] = new ColumnInfo[fieldcolsMeasures];
		infoMeasures[0] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabMeasures.Attribute"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { " ", " ", " ",
						" " }, false);
		infoMeasures[1] = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabMeasures.Measures"),
				ColumnInfo.COLUMN_TYPE_TEXT, null, false);

		fieldColumnsMeasures.add(infoMeasures[0]);
		FieldMeasures = new TableView(transMeta, wMeasuresComp, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI, infoMeasures,
				fieldrowsMeasures, lsMod, props);
		fdFieldMeasures = new FormData();
		fdFieldMeasures.height = 265;
		fdFieldMeasures.left = new FormAttachment(0, 0);
		fdFieldMeasures.top = new FormAttachment(0, 0);
		fdFieldMeasures.right = new FormAttachment(100, 0);
		FieldMeasures.setLayoutData(fdFieldMeasures);

		fdMeasuresComp = new FormData();
		fdMeasuresComp.left = new FormAttachment(0, 0);
		fdMeasuresComp.top = new FormAttachment(0, 0);
		fdMeasuresComp.right = new FormAttachment(100, 0);
		fdMeasuresComp.bottom = new FormAttachment(100, 0);
		wMeasuresComp.setLayoutData(fdMeasuresComp);

		wMeasuresComp.layout();
		wMeasuresTab.setControl(wMeasuresComp);

		// Tabs Form
		fdTabFolder = new FormData();
		fdTabFolder.height = 270;
		fdTabFolder.left = new FormAttachment(0, 0);
		fdTabFolder.top = new FormAttachment(wStepname, margin);
		fdTabFolder.right = new FormAttachment(100, 0);
		fdTabFolder.bottom = new FormAttachment(100, -50);
		wTabFolder.setLayoutData(fdTabFolder);

		// Botoes
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("FatoDialog.Shell.ButtonOk"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("FatoDialog.Shell.ButtonCancel"));

		BaseStepDialog.positionBottomButtons(shell,
				new Button[] { wOK, wCancel }, margin, wTabFolder);

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

		lsSQL = new Listener() {
			@Override
			public void handleEvent(Event e) {
				checkSQL();
			}
		};

		wTabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				changeFields();
				wSQL.setText(montaQuery());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				changeFields();
				wSQL.setText(montaQuery());
			}
		});

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
		fbSQL.addListener(SWT.Selection, lsSQL);

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);

		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		changestepXML();
		setSize();
		getData();
		input.setChanged(changed);
		wTabFolder.setSelection(0);

		shell.open();
		while (!shell.isDisposed()) {
			if (display.readAndDispatch()) {
				display.sleep();
			}
		}

		return stepname;
	}

	public void getData() {
		wStepname.selectAll();
		setTempoDimension();
		if (input.getSql() != null) {
			if (input.getSql().getValueData() != null) {
				wSQL.setText(String.valueOf(input.getSql().getValueData()));
			}
		}

		if (input.getDimensions() != null) {
			for (int i = 0; i < input.getDimensions().length; i++) {
				if (i > 9)
					FieldDimension.add("");
				FieldDimension.setText(String.valueOf(i + 1), 0, i);
				if (input.getDimensions()[i][0].getValueData() != null)
					FieldDimension.setText(
							String.valueOf(input.getDimensions()[i][0]
									.getValueData()), 1, i);
				if (input.getDimensions()[i][1].getValueData() != null)
					FieldDimension.setText(
							String.valueOf(input.getDimensions()[i][1]
									.getValueData()), 2, i);
			}
		}
		if (input.getTime_dimensions() != null) {
			for (int i = 0; i < input.getTime_dimensions().length; i++) {
				if (i > 9)
					FieldDimensionTime.add("");
				FieldDimensionTime.setText(String.valueOf(i + 1), 0, i);
				if (input.getTime_dimensions()[i][0].getValueData() != null)
					FieldDimensionTime.setText(String.valueOf(input
							.getTime_dimensions()[i][0].getValueData()), 1, i);
				if (input.getTime_dimensions()[i][1].getValueData() != null)
					FieldDimensionTime.setText(String.valueOf(input
							.getTime_dimensions()[i][1].getValueData()), 2, i);
				if (input.getTime_dimensions()[i][2].getValueData() != null)
					FieldDimensionTime.setText(String.valueOf(input
							.getTime_dimensions()[i][2].getValueData()), 3, i);
			}
		}
		if (input.getMeasure() != null) {
			for (int i = 0; i < input.getMeasure().length; i++) {
				if (i > 9)
					FieldMeasures.add("");
				FieldMeasures.setText(String.valueOf(i + 1), 0, i);
				if (input.getMeasure()[i][0].getValueData() != null)
					FieldMeasures.setText(
							String.valueOf(input.getMeasure()[i][0]
									.getValueData()), 1, i);
				if (input.getMeasure()[i][1].getValueData() != null)
					FieldMeasures.setText(
							String.valueOf(input.getMeasure()[i][1]
									.getValueData()), 2, i);
			}
		}
		if (input.getNovoSQL() != null) {
			for (int i = 0; i < input.getNovoSQL().length; i++) {
				if (i > 9)
					wQueryBuilder.add("");
				wQueryBuilder.setText(String.valueOf(i + 1), 0, i);
				if (input.getNovoSQL()[i][0].getValueData() != null)
					wQueryBuilder.setText(
							String.valueOf(input.getNovoSQL()[i][0]
									.getValueData()), 1, i);
				if (input.getNovoSQL()[i][1].getValueData() != null)
					wQueryBuilder.setText(
							String.valueOf(input.getNovoSQL()[i][1]
									.getValueData()), 2, i);
				if (input.getNovoSQL()[i][2].getValueData() != null)
					wQueryBuilder.setText(
							String.valueOf(input.getNovoSQL()[i][2]
									.getValueData()), 3, i);
				if (input.getNovoSQL()[i][3].getValueData() != null)
					wQueryBuilder.setText(
							String.valueOf(input.getNovoSQL()[i][3]
									.getValueData()), 4, i);

			}
		}
	}

	public void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	public void ok() {
		stepname = wStepname.getText();
		input.setSql(new ValueMetaAndData(new ValueMeta("sql",
				ValueMetaInterface.TYPE_STRING), new String(wSQL.getText())));
		input.setFactor(new ValueMetaAndData(new ValueMeta("factor",
				ValueMetaInterface.TYPE_STRING),
				new String(wStepname.getText())));
		ValueMetaAndData[][] dimensions = new ValueMetaAndData[FieldDimension
				.getItemCount()][2];
		for (int i = 0; i < FieldDimension.getItemCount(); i++) {
			dimensions[i][0] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "dimensions", ValueMetaInterface.TYPE_STRING),
					new String(FieldDimension.getItem(i, 1)));
			dimensions[i][1] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "dimensions", ValueMetaInterface.TYPE_STRING),
					new String(FieldDimension.getItem(i, 2)));
		}
		input.setDimensions(dimensions);
		ValueMetaAndData[][] timedimensions = new ValueMetaAndData[FieldDimensionTime
				.getItemCount()][3];
		for (int i = 0; i < FieldDimensionTime.getItemCount(); i++) {
			timedimensions[i][0] = new ValueMetaAndData(new ValueMeta("row_"
					+ i + "time_dimensions", ValueMetaInterface.TYPE_STRING),
					new String(FieldDimensionTime.getItem(i, 1)));
			timedimensions[i][1] = new ValueMetaAndData(new ValueMeta("row_"
					+ i + "time_dimensions", ValueMetaInterface.TYPE_STRING),
					new String(FieldDimensionTime.getItem(i, 2)));
			timedimensions[i][2] = new ValueMetaAndData(new ValueMeta("row_"
					+ i + "time_dimensions", ValueMetaInterface.TYPE_STRING),
					new String(FieldDimensionTime.getItem(i, 3)));
		}
		input.setTime_dimensions(timedimensions);
		ValueMetaAndData[][] measures = new ValueMetaAndData[FieldMeasures
				.getItemCount()][2];
		for (int i = 0; i < FieldMeasures.getItemCount(); i++) {
			measures[i][0] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "measures", ValueMetaInterface.TYPE_STRING), new String(
					FieldMeasures.getItem(i, 1)));
			measures[i][1] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "measures", ValueMetaInterface.TYPE_STRING), new String(
					FieldMeasures.getItem(i, 2)));
		}
		input.setMeasure(measures);
		ValueMetaAndData[][] novoSQL = new ValueMetaAndData[wQueryBuilder
				.getItemCount()][4];
		for (int i = 0; i < wQueryBuilder.getItemCount(); i++) {
			novoSQL[i][0] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "measures", ValueMetaInterface.TYPE_STRING), new String(
					wQueryBuilder.getItem(i, 1)));
			novoSQL[i][1] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "measures", ValueMetaInterface.TYPE_STRING), new String(
					wQueryBuilder.getItem(i, 2)));
			novoSQL[i][2] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "measures", ValueMetaInterface.TYPE_STRING), new String(
					wQueryBuilder.getItem(i, 3)));
			novoSQL[i][3] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "measures", ValueMetaInterface.TYPE_STRING), new String(
					wQueryBuilder.getItem(i, 4)));
		}
		input.setNovoSQL(novoSQL);

		dispose();
	}

	public void setTempoDimension() {
		String aux[] = new String[field_dimension_time.size()];
		for (int i = 0; i < aux.length; i++) {
			aux[i] = field_dimension_time.get(i);
		}
		ColumnInfo info = new ColumnInfo(
				Messages.getString("FatoDialog.Shell.TabTimeDimension.Comparation"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, aux, false);
		FieldDimensionTime.setColumnInfo(1, info);
	}

	public void checkSQL() {
		MessageBox yn = new MessageBox(shell, SWT.OK);
		if (conection != null) {
			Database db = new Database(loggingObject, conection);
			db.shareVariablesWith(transMeta);
			try {
				db.connect();
				Object ob = db.getRows(wSQL.getText(), 1);
				if (ob != null) {
					yn.setMessage(Messages
							.getString("FatoDialog.Shell.MensagesQuery"));
				}
			} catch (Exception e) {
				yn.setMessage(e.getMessage());
			}
		} else {
			yn.setMessage(Messages
					.getString("FatoDialog.Shell.MensagesNoDataBase"));
		}
		yn.open();
	}

	public void changeFields() {
		if (conection != null) {
			Database db = new Database(loggingObject, conection);
			db.shareVariablesWith(transMeta);
			try {
				db.connect();
				RowMetaInterface fields = db.getQueryFields(wSQL.getText(),
						false);
				String aux[] = new String[fields.size()];
				for (int i = 0; i < fields.size(); i++) {
					ValueMetaInterface field = fields.getValueMeta(i);
					aux[i] = field.getName();
				}
				ColumnInfo info = new ColumnInfo(
						Messages.getString("FatoDialog.Shell.TabTimeDimension.Attribute"),
						ColumnInfo.COLUMN_TYPE_CCOMBO, aux, false);
				FieldDimension.setColumnInfo(0, info);
				FieldDimensionTime.setColumnInfo(0, info);
				FieldMeasures.setColumnInfo(0, info);
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

	public void changestepXML() {
		try {
			logBasic("Fuck yeah!");
			String xml = "<xml>";
			RowMetaInterface prev = transMeta.getStepFields(stepname);
			String[] a = prev.getFieldNames();
			for (int i = 0; i < a.length; i++) {
				if (a[i] != null) {
					xml = xml + a[i];
				}
			}
			xml = xml + "</xml>";

			logBasic(xml);
			input.setXml(new ValueMetaAndData(new ValueMeta("xml",
					ValueMetaInterface.TYPE_STRING), new String(xml)));
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
				Document doc = db.parse(is);

				Element raiz = doc.getDocumentElement();

				NodeList c = raiz.getElementsByTagName("conection");

				for (int i = 0; i < c.getLength(); i++) {
					Element con = (Element) c.item(0);
					conection = transMeta.findDatabase(con.getTextContent());
					input.setDatabaseMeta(transMeta.findDatabase(con
							.getTextContent()));
				}
				NodeList dimensions = raiz.getElementsByTagName("dimension");

				ArrayList<String> dim = new ArrayList<String>();
				ArrayList<String> dim_time = new ArrayList<String>();
				for (int j = 0; j < dimensions.getLength(); j++) {
					Element dimension = (Element) dimensions.item(j);
					if (dimension.getAttributeNode("style") == null) {
						dim.add(dimension.getAttributeNode("name")
								.getNodeValue());
					} else {
						dim_time.add(dimension.getAttributeNode("name")
								.getNodeValue());
						NodeList table = dimension
								.getElementsByTagName("table");
						Element fields = (Element) table.item(0);
						NodeList field = fields.getElementsByTagName("field");
						for (int p = 0; p < field.getLength(); p++) {
							boolean status = true;
							for (int z = 0; z < field_dimension_time.size(); z++) {
								if (field_dimension_time.get(z).equals(
										field.item(p).getTextContent())) {
									status = false;
									break;
								}
							}
							if (status) {
								field_dimension_time.add(field.item(p)
										.getTextContent());
							}
						}
					}
				}
				String aux[] = new String[dim.size()];
				for (int i = 0; i < aux.length; i++) {
					aux[i] = dim.get(i);
				}

				ColumnInfo info = new ColumnInfo(
						Messages.getString("FatoDialog.Shell.TabDimension"),
						ColumnInfo.COLUMN_TYPE_CCOMBO, aux, false);
				FieldDimension.setColumnInfo(1, info);

				aux = new String[dim_time.size()];
				for (int i = 0; i < aux.length; i++) {
					aux[i] = dim_time.get(i);
				}

				ColumnInfo info_time = new ColumnInfo(
						Messages.getString("FatoDialog.Shell.TabTimeDimension.NameDimension"),
						ColumnInfo.COLUMN_TYPE_CCOMBO, aux, false);
				FieldDimensionTime.setColumnInfo(2, info_time);

			} catch (Exception error) {
				System.err.println(error);
			}
		} catch (KettleStepException e) {
			e.printStackTrace();
		}
	}

	public String montaQuery() {
		String campos, tabelas, condições = new String();
		campos = "select \n";
		tabelas = "\n from ";
		condições = "\n where ";
		int qntCampos = 0, qntTabelas = 0;

		for (int i = 0; i < wQueryBuilder.getItemCount(); i++) {

			// campos
			if (qntCampos != 0) {
				if (!wQueryBuilder.getItem(i, 1).equals("")) {
					campos += "\n, " + wQueryBuilder.getItem(i, 1);
					qntCampos++;
				}
			} else {
				if (!wQueryBuilder.getItem(i, 1).equals("")) {
					campos += wQueryBuilder.getItem(i, 1);
					qntCampos++;
				}
			}

			// apelidos
			if (!wQueryBuilder.getItem(i, 2).equals("")) {
				campos += " " + wQueryBuilder.getItem(i, 2);
			}

			// tabelas
			if (qntTabelas != 0) {
				if (!wQueryBuilder.getItem(i, 3).equals("")) {
					tabelas += "\n, " + wQueryBuilder.getItem(i, 3);
					qntTabelas++;
				}
			} else {
				if (!wQueryBuilder.getItem(i, 3).equals("")) {
					tabelas += "\n" + wQueryBuilder.getItem(i, 3);
					qntTabelas++;
				}
			}

			// condições
			if (!wQueryBuilder.getItem(i, 4).equals("")) {
				condições += " \n" + wQueryBuilder.getItem(i, 4);
			}
		}
		return campos + tabelas + condições;
	}
}
