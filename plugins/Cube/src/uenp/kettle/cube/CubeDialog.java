package uenp.kettle.cube;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CubeDialog extends BaseStepDialog implements StepDialogInterface {

	private CubeMeta input;
	private static Class<?> PKG = CubeDialog.class;

	// file
	Label wlFileName;
	FormData fwlFileName;
	Button wbFileName;
	FormData fwbFileName;
	TextVar wFileName;
	FormData fwFileName;

	// tabs

	private CTabFolder wTabFolder;
	private FormData fdTabFolder;
	private CTabItem wCubeTab, wMeasuresTab;
	private Composite wCubeComp, wMeasuresComp;
	private FormData fdCubeComp, fdMeasuresComp;

	// groups

	private Group wgCube;
	private FormLayout flgCube;
	private FormData fdgCube;
	private Group wgDimensonUsage;
	private FormLayout flgDimensionUsage;
	private FormData fdgDimensionUsage;

	// cube name

	private Label wlCubeName;
	private FormData fdlCubeName;
	private Text wCubeName;
	private FormData fdCubeName;

	// cube table

	private Label wlTable;
	private FormData fdlTable;
	private Combo wTable;
	private FormData fdTable;

	// cube visible

	private Label wlVisible;
	private FormData fdlVisible;
	private Button wVisible;
	private FormData fdVisible;

	// dimension usage
	private TableView wDimensionUsage;
	private FormData fdDimensionUsage;

	// arrays para carregar
	private String foreignKeys[];
	private String tables[];
	private String dimensions[] = new String[] { " ", " ", " ", " " };
	private String measures[];

	// measures table
	private TableView wMeasures;
	private FormData fdMeasures;

	public CubeDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (CubeMeta) in;
		logBasic("Executando - MeuPlugin3Dialog");
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
		shell.setText("MeuPlugin3");

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		lerXMLAnterior();

		// logBasic(measures[0],measures[1]);
		// new label step name

		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText("Nome do Passo: ");
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -middle);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		// new text step name

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		// File
		wlFileName = new Label(shell, SWT.RIGHT);
		wlFileName.setText(Messages.getString("File.Load"));
		props.setLook(wlFileName);
		fwlFileName = new FormData();
		fwlFileName.left = new FormAttachment(0, 0);
		fwlFileName.top = new FormAttachment(wStepname, margin);
		fwlFileName.right = new FormAttachment(middle, -middle);
		wlFileName.setLayoutData(fwlFileName);
		wbFileName = new Button(shell, SWT.PUSH | SWT.CENTER);
		props.setLook(wbFileName);
		wbFileName.setText(Messages.getString("File.Button"));
		fwbFileName = new FormData();
		fwbFileName.right = new FormAttachment(100, 0);
		fwbFileName.top = new FormAttachment(wStepname, 0);
		wbFileName.setLayoutData(fwbFileName);
		wFileName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT
				| SWT.BORDER);
		props.setLook(wFileName);
		wFileName.addModifyListener(lsMod);
		fwFileName = new FormData();
		fwFileName.width = 400;
		fwFileName.left = new FormAttachment(middle, 0);
		fwFileName.top = new FormAttachment(wStepname, margin);
		fwFileName.right = new FormAttachment(wbFileName, -margin);
		wFileName.setLayoutData(fwFileName);

		wbFileName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.xml" });
				if (wFileName.getText() != null) {
					dialog.setFileName(transMeta
							.environmentSubstitute(wFileName.getText()));
				}
				if (dialog.open() != null) {
					wFileName.setText(dialog.getFilterPath()
							+ System.getProperty("file.separator")
							+ dialog.getFileName());
				}
			}
		});

		// new tab folder

		wTabFolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
		fdTabFolder = new FormData();
		fdTabFolder.height = 270;
		fdTabFolder.left = new FormAttachment(0, 0);
		fdTabFolder.top = new FormAttachment(wFileName, margin);
		fdTabFolder.right = new FormAttachment(100, 0);
		fdTabFolder.bottom = new FormAttachment(100, -50);
		wTabFolder.setLayoutData(fdTabFolder);

		// new tab cube

		wCubeTab = new CTabItem(wTabFolder, SWT.NONE);
		wCubeTab.setText(Messages.getString("Cube.TabName"));
		wCubeComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wCubeComp);

		FormLayout LayoutCube = new FormLayout();
		LayoutCube.marginWidth = margin;
		LayoutCube.marginHeight = margin;
		wCubeComp.setLayout(LayoutCube);

		fdCubeComp = new FormData();
		fdCubeComp.left = new FormAttachment(0, 0);
		fdCubeComp.top = new FormAttachment(0, 0);
		fdCubeComp.right = new FormAttachment(100, 0);
		fdCubeComp.bottom = new FormAttachment(100, 0);
		wCubeComp.setLayoutData(fdCubeComp);
		wCubeComp.layout();
		wCubeTab.setControl(wCubeComp);

		// new tab measures

		wMeasuresTab = new CTabItem(wTabFolder, SWT.NONE);
		wMeasuresTab.setText(Messages.getString("Measures.TabName"));
		wMeasuresComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wMeasuresComp);

		FormLayout LayoutMeasures = new FormLayout();
		LayoutMeasures.marginWidth = margin;
		LayoutMeasures.marginHeight = margin;
		wMeasuresComp.setLayout(LayoutMeasures);

		fdMeasuresComp = new FormData();
		fdMeasuresComp.left = new FormAttachment(0, 0);
		fdMeasuresComp.right = new FormAttachment(100, 0);
		fdMeasuresComp.bottom = new FormAttachment(100, 0);
		wMeasuresComp.setLayoutData(fdMeasuresComp);
		wMeasuresComp.layout();
		wMeasuresTab.setControl(wMeasuresComp);
		wTabFolder.setSelection(0);
		FocusListener lstabfolder = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				logBasic("achou");

			}
		};
		wTabFolder.addFocusListener(lstabfolder);

		// new group cube

		wgCube = new Group(wCubeComp, SWT.SHADOW_NONE);
		props.setLook(wgCube);
		wgCube.setText(BaseMessages.getString(PKG, "Cube.GroupName"));
		flgCube = new FormLayout();
		flgCube.marginWidth = 10;
		flgCube.marginHeight = 10;
		fdgCube = new FormData();
		fdgCube.top = new FormAttachment(wStepname, margin);
		fdgCube.left = new FormAttachment(0, 0);
		fdgCube.right = new FormAttachment(100, 0);
		wgCube.setLayout(flgCube);
		wgCube.setLayoutData(fdgCube);

		// new label cube name

		wlCubeName = new Label(wgCube, SWT.RIGHT);
		wlCubeName.setText(BaseMessages.getString(PKG, "Cube.Name"));
		props.setLook(wlCubeName);
		fdlCubeName = new FormData();
		fdlCubeName.top = new FormAttachment(0, margin);
		fdlCubeName.left = new FormAttachment(0, 0);
		fdlCubeName.right = new FormAttachment(middle, -middle);
		wlCubeName.setLayoutData(fdlCubeName);

		// new text cube name

		wCubeName = new Text(wgCube, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wCubeName.setText("");
		props.setLook(wCubeName);
		fdCubeName = new FormData();
		fdCubeName.left = new FormAttachment(middle, 0);
		fdCubeName.right = new FormAttachment(100, 0);
		fdCubeName.top = new FormAttachment(0, margin);
		wCubeName.setLayoutData(fdCubeName);
		wCubeName.addModifyListener(lsMod);

		// new label table

		wlTable = new Label(wgCube, SWT.RIGHT);
		wlTable.setText(BaseMessages.getString(PKG, "Cube.Table"));
		props.setLook(wlTable);
		fdlTable = new FormData();
		fdlTable.left = new FormAttachment(0, 0);
		fdlTable.right = new FormAttachment(middle, -middle);
		fdlTable.top = new FormAttachment(wCubeName, margin);
		wlTable.setLayoutData(fdlTable);

		// new combo table

		wTable = new Combo(wgCube, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wTable);
		fdTable = new FormData();
		fdTable.left = new FormAttachment(middle, 0);
		fdTable.top = new FormAttachment(wCubeName, margin);
		fdTable.right = new FormAttachment(100, 0);
		wTable.setLayoutData(fdTable);
		FocusListener lsTable;
		lsTable = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (!wFileName.getText().equals("")) {
					lerTabelas(wFileName.getText());
					logBasic("chamou");
					wTable.removeAll();
					for (int i = 0; i < tables.length; i++) {
						wTable.add(tables[i]);
					}
				}
			}
		};
		wTable.addFocusListener(lsTable);

		// new label cube visible

		wlVisible = new Label(wgCube, SWT.RIGHT);
		wlVisible.setText(BaseMessages.getString(PKG, "Cube.Visible"));
		props.setLook(wlVisible);
		fdlVisible = new FormData();
		fdlVisible.top = new FormAttachment(wTable, margin);
		fdlVisible.left = new FormAttachment(0, 0);
		fdlVisible.right = new FormAttachment(middle, -middle);
		wlVisible.setLayoutData(fdlVisible);

		// new check cube visible

		wVisible = new Button(wgCube, SWT.CHECK);
		props.setLook(wVisible);
		fdVisible = new FormData();
		fdVisible.top = new FormAttachment(wTable, margin);
		fdVisible.left = new FormAttachment(middle, margin);
		fdVisible.right = new FormAttachment(100, 0);
		wVisible.setLayoutData(fdVisible);
		wVisible.setSelection(true);

		// new group dimension usage

		wgDimensonUsage = new Group(wCubeComp, SWT.SHADOW_NONE);
		props.setLook(wgDimensonUsage);
		wgDimensonUsage.setText(BaseMessages.getString(PKG,
				"Cube.DimensionUsage.GroupName"));
		flgDimensionUsage = new FormLayout();
		flgDimensionUsage.marginWidth = 10;
		flgDimensionUsage.marginHeight = 10;
		fdgDimensionUsage = new FormData();
		fdgDimensionUsage.top = new FormAttachment(wgCube, margin);
		fdgDimensionUsage.left = new FormAttachment(0, 0);
		fdgDimensionUsage.right = new FormAttachment(100, 0);
		wgDimensonUsage.setLayout(flgDimensionUsage);
		wgDimensonUsage.setLayoutData(fdgDimensionUsage);

		// new table view dimension usage

		int fieldcols = 2;
		int fieldrows = 10;

		final ColumnInfo info[] = new ColumnInfo[fieldcols];
		info[0] = new ColumnInfo(
				Messages.getString("Cube.DimensionUsage.Name"),
				ColumnInfo.COLUMN_TYPE_TEXT,
				new String[] { " ", " ", " ", " " }, false);

		info[1] = new ColumnInfo(
				Messages.getString("Cube.DimensionUsage.Source"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, dimensions, false);

		wDimensionUsage = new TableView(transMeta, wgDimensonUsage, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI, info, fieldrows, lsMod, props);
		fdDimensionUsage = new FormData();
		fdDimensionUsage.height = 265;
		fdDimensionUsage.left = new FormAttachment(0, 0);
		fdDimensionUsage.top = new FormAttachment(0, 0);
		fdDimensionUsage.right = new FormAttachment(100, 0);
		wDimensionUsage.setLayoutData(fdDimensionUsage);
		props.setLook(wDimensionUsage);

		// new table view measures

		String[] aggregators = new String[] { "sum", "count", "min", "max",
				"avg", "distinct count" };
		final ColumnInfo[] info2 = new ColumnInfo[4];
		info2[0] = new ColumnInfo(Messages.getString("Measures.Measures.Name"),
				ColumnInfo.COLUMN_TYPE_TEXT,
				new String[] { " ", " ", " ", " " }, false);
		info2[1] = new ColumnInfo(
				Messages.getString("Measures.Measures.Aggregator"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, aggregators, false);
		info2[2] = new ColumnInfo(
				Messages.getString("Measures.Measures.Column"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "         ",
						"         ", "         ", " " }, false);
		info2[3] = new ColumnInfo(
				Messages.getString("Measures.Measures.Visible"),
				ColumnInfo.COLUMN_TYPE_CCOMBO,
				new String[] { "true", "false" }, false);

		wMeasures = new TableView(transMeta, wMeasuresComp, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI, info2, 10, lsMod, props);
		fdMeasures = new FormData();
		fdMeasures.height = 265;
		fdMeasures.left = new FormAttachment(0, 0);
		fdMeasures.top = new FormAttachment(0, 0);
		fdMeasures.right = new FormAttachment(100, 0);
		wMeasures.setLayoutData(fdMeasures);
		props.setLook(wMeasures);
		Table teste = wMeasures.getTable();
		FocusListener lsMedida = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (!wFileName.getText().equals("")) {
					lerMedidas(wFileName.getText());
					info2[2] = new ColumnInfo(
							Messages.getString("Measures.Measures.Column"),
							ColumnInfo.COLUMN_TYPE_CCOMBO, measures, false);
				}
			}
		};
		teste.addFocusListener(lsMedida);
		// lerMedidas(wFileName.getText());

		// buttons ok e cancel

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText("OK");

		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText("Cancelar");

		BaseStepDialog.positionBottomButtons(shell,
				new Button[] { wOK, wCancel }, margin, wTabFolder);

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

		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);

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

		setSize();
		getData();
		input.setChanged(changed);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return stepname;
	}

	private void ok() {
		stepname = wStepname.getText();
		input.setPath(new ValueMetaAndData(new ValueMeta("path",
				ValueMetaInterface.TYPE_STRING),
				new String(wFileName.getText())));
		input.setCubeName(new ValueMetaAndData(new ValueMeta("cubeName",
				ValueMetaInterface.TYPE_STRING),
				new String(wCubeName.getText())));
		input.setCubeTable(new ValueMetaAndData(new ValueMeta("cubeTable",
				ValueMetaInterface.TYPE_STRING), new String(wTable.getText())));
		input.setCubeVisible(new ValueMetaAndData(new ValueMeta("cubeVisible",
				ValueMetaInterface.TYPE_STRING), new String(String
				.valueOf(wVisible.getSelection()))));
		ValueMetaAndData[][] dimensionsUsages = new ValueMetaAndData[wDimensionUsage
				.getItemCount()][2];
		for (int i = 0; i < wDimensionUsage.getItemCount(); i++) {
			dimensionsUsages[i][0] = new ValueMetaAndData(new ValueMeta("row_"
					+ i + "name", ValueMetaInterface.TYPE_STRING), new String(
					wDimensionUsage.getItem(i, 1)));
			dimensionsUsages[i][1] = new ValueMetaAndData(new ValueMeta("row_"
					+ i + "source", ValueMetaInterface.TYPE_STRING),
					new String(wDimensionUsage.getItem(i, 2)));
		}
		input.setDimensionsUsages(dimensionsUsages);
		ValueMetaAndData[][] measures2 = new ValueMetaAndData[wMeasures
				.getItemCount()][4];
		for (int i = 0; i < wMeasures.getItemCount(); i++) {
			measures2[i][0] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "name", ValueMetaInterface.TYPE_STRING), new String(
					wMeasures.getItem(i, 1)));
			measures2[i][1] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "aggregator", ValueMetaInterface.TYPE_STRING),
					new String(wMeasures.getItem(i, 2)));
			measures2[i][2] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "column", ValueMetaInterface.TYPE_STRING), new String(
					wMeasures.getItem(i, 3)));
			measures2[i][3] = new ValueMetaAndData(new ValueMeta("row_" + i
					+ "visible", ValueMetaInterface.TYPE_STRING), new String(
					wMeasures.getItem(i, 4)));
		}
		input.setMeasures(measures2);
		// gerarXML();
		dispose();
	}

	private void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	public void getData() {
		wStepname.selectAll();
		if (input.getPath().getValueData() != null) {
			wFileName.setText(String.valueOf(input.getPath().getValueData()));
		}
		if (input.getCubeName().getValueData() != null) {
			if (input.getCubeName().getValueData() != null) {
				wCubeName.setText(String.valueOf(input.getCubeName()
						.getValueData()));
			}
		}
		if (input.getCubeTable().getValueData() != null) {
			if (input.getCubeTable().getValueData() != null) {
				wTable.setText(String.valueOf(input.getCubeTable()
						.getValueData()));
			}
		}
		if (input.getCubeVisible().getValueData() != null) {
			if (input.getCubeVisible().getValueData() != null) {
				wVisible.setSelection(Boolean.valueOf(String.valueOf(input
						.getCubeVisible().getValueData())));
			}
		}

		if (input.getDimensionsUsages() != null) {
			for (int i = 0; i < input.getDimensionsUsages().length; i++) {
				if (i > 9)
					wDimensionUsage.add("");
				wDimensionUsage.setText(String.valueOf(i + 1), 0, i);
				if (input.getDimensionsUsages()[i][0].getValueData() != null)
					wDimensionUsage.setText(String.valueOf(input
							.getDimensionsUsages()[i][0].getValueData()), 1, i);
				if (input.getDimensionsUsages()[i][1].getValueData() != null)
					wDimensionUsage.setText(String.valueOf(input
							.getDimensionsUsages()[i][1].getValueData()), 2, i);
			}
		}
		if (input.getMeasures() != null) {
			for (int i = 0; i < input.getMeasures().length; i++) {
				if (i > 9)
					wMeasures.add("");
				wMeasures.setText(String.valueOf(i + 1), 0, i);
				if (input.getMeasures()[i][0].getValueData() != null)
					wMeasures.setText(String.valueOf(input.getMeasures()[i][0]
							.getValueData()), 1, i);
				if (input.getMeasures()[i][1].getValueData() != null)
					wMeasures.setText(String.valueOf(input.getMeasures()[i][1]
							.getValueData()), 2, i);
				if (input.getMeasures()[i][2].getValueData() != null)
					wMeasures.setText(String.valueOf(input.getMeasures()[i][2]
							.getValueData()), 3, i);
				if (input.getMeasures()[i][3].getValueData() != null)
					wMeasures.setText(String.valueOf(input.getMeasures()[i][3]
							.getValueData()), 4, i);
			}
		}
	}

	public void lerTabelas(String url) {
		File arquivo = new File(url);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(arquivo);
			Element raiz = doc.getDocumentElement();
			NodeList fatos = raiz.getElementsByTagName("fact");
			tables = new String[fatos.getLength()];
			for (int i = 0; i < fatos.getLength(); i++) {
				Element aux = (Element) fatos.item(i);
				tables[i] = aux.getAttribute("table");
				// logBasic("table " + aux.getAttribute("table"));
			}
		} catch (Exception e) {
		}
	}

	public void lerMedidas(String url) {
		File arquivo = new File(url);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(arquivo);
			Element raiz = doc.getDocumentElement();
			Element fato = null;
			NodeList fatos = raiz.getElementsByTagName("fact");
			for (int i = 0; i < fatos.getLength(); i++) {
				fato = (Element) fatos.item(i);
				if (fato.getAttribute("table").equals(wTable.getText())) {
					i = fatos.getLength();
				}
			}
			fatos = fato.getElementsByTagName("measure");
			// logBasic(fatos.item(1).getTextContent());
			measures = new String[fatos.getLength()];
			for (int i = 0; i < fatos.getLength(); i++) {
				Element aux = (Element) fatos.item(i);
				measures[i] = aux.getTextContent();
			}
		} catch (Exception e) {
		}
	}

	public void lerXMLAnterior() {
		try {
			String xml = "<xml>";
			RowMetaInterface r = transMeta.getStepFields(stepname);
			String[] a = r.getFieldNames();
			for (int i = 0; i < a.length; i++) {
				if (a[i] != null) {
					xml = xml + a[i];
				}
			}
			xml = xml + "</xml>";
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
				Document doc = db.parse(is);

				Element raiz = doc.getDocumentElement();

				NodeList nodes = raiz.getElementsByTagName("nameDimension");
				dimensions = new String[nodes.getLength()];
				if (nodes != null) {
					for (int i = 0; i < nodes.getLength(); i++) {
						dimensions[i] = nodes.item(i).getTextContent();
					}
				}
			} catch (Exception e) {

			}
		} catch (Exception e) {

		}
	}
}
