package uenp.kettle.logictimedimension;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LogicTimeDimensionDialog extends BaseStepDialog implements
		StepDialogInterface {

	private static Class<?> PKG = LogicTimeDimensionDialog.class;
	private LogicTimeDimensionMeta input;
	// file
	private Label wlFileName;
	private FormData fwlFileName;
	private Button wbFileName;
	private FormData fwbFileName;
	private TextVar wFileName;
	private FormData fwFileName;

	// groups
	private Group wgDimension;
	private FormLayout flgDimension;
	private FormData fdgDimension;

	private Group wgHierarchy;
	private FormLayout flgHierarchy;
	private FormData fdgHierarchy;

	private Group wgLevel;
	private FormLayout flgLevel;
	private FormData fdgLevel;

	// select dimension
	private Label wlSelectDimension;
	private FormData fdlSelectDimension;
	private Combo wSelectDimension;
	private FormData fdSelectDimension;

	// name dimension
	private Label wlNameDimension;
	private FormData fdlNameDimension;
	private Text wNameDimension;
	private FormData fdNameDimension;

	// visible dimension
	private Label wlVisibleDimension;
	private FormData fdlVisibleDimension;
	private Button wVisibleDimension;
	private FormData fdVisibleDimension;

	// list hierarchy
	private Label wlHierarchy;
	private FormData fdlHierarchy;
	private List wHierarchy;
	private FormData fdHierarchy;

	// list level
	private Label wlLevel;
	private FormData fdlLevel;
	private List wLevel;
	private FormData fdLevel;

	// up hierarchy
	private Button wHierarchyUp;
	private FormData fdHierarchyUp;

	// down hierarchy
	private Button wHierarchyDown;
	private FormData fdHierarchyDown;

	// up level
	private Button wLevelUp;
	private FormData fdLevelUp;

	// down level
	private Button wLevelDown;
	private FormData fdLevelDown;

	// hierarchy name
	private Label wlHierarchyName;
	private FormData fdlHierarchyName;
	private Text wHierarchyName;
	private FormData fdHierarchyName;

	// hierarchy visible
	private Label wlVisibleHierarchy;
	private FormData fdlVisibleHierarchy;
	private Button wVisibleHierarchy;
	private FormData fdVisibleHierarchy;

	// level name
	private Label wlLevelName;
	private FormData fdlLevelName;
	private Text wLevelName;
	private FormData fdLevelName;

	// column level
	private Label wlColumnLevel;
	private FormData fdlColumnLevel;
	private Combo wColumnLevel;
	private FormData fdColumnLevel;

	// type level
	private Label wlTypeLevel;
	private FormData fdlTypeLevel;
	private Combo wTypeLevel;
	private FormData fdTypeLevel;

	// level time
	private Label wlTimeLevel;
	private FormData fdlTimeLevel;
	private Combo wTimeLevel;
	private FormData fdTimeLevel;
	
	// level visible
	private Label wlVisibleLevel;
	private FormData fdlVisibleLevel;
	private Button wVisibleLevel;
	private FormData fdVisibleLevel;

	// level properties
	//private Button wProperties;

	// new hierarchy
	private Button wNewHierarchy;
	private FormData fdNewHierarchy;

	// new level
	private Button wNewLevel;
	private FormData fdNewLevel;
	
	// delete hierarchy
	private Button wDeleteHierarchy;
	private FormData fdDeleteHierarchy;
	
	// delete level
	private Button wDeleteLevel;
	private FormData fdDeleteLevel;
	
	// logic dimension dialog properties
	//private LogicDimensionDialogProperties lddp;

	// hierarchys
	private ArrayList<Hierarchy> hierarchys = new ArrayList<Hierarchy>();

	public LogicTimeDimensionDialog(Shell parent, Object in, TransMeta transMeta,
			String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (LogicTimeDimensionMeta) in;
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
		shell.setText(BaseMessages.getString(PKG, "Shell.Dimension"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// new label stepname
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "Step.Name"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -middle);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		// new text stepname
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);

		// File
		wlFileName = new Label(shell, SWT.RIGHT);
		wlFileName.setText(Messages.getString("XMLOutDialog.FileSave.Label"));
		props.setLook(wlFileName);
		fwlFileName = new FormData();
		fwlFileName.left = new FormAttachment(0, 0);
		fwlFileName.top = new FormAttachment(wStepname, margin);
		fwlFileName.right = new FormAttachment(middle, -middle);
		wlFileName.setLayoutData(fwlFileName);
		wbFileName = new Button(shell, SWT.PUSH | SWT.CENTER);
		props.setLook(wbFileName);
		wbFileName.setText(Messages.getString("XMLOutDialog.ButtonFile"));
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

		// new group dimension
		wgDimension = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(wgDimension);
		wgDimension.setText(BaseMessages.getString(PKG, "Dimension.Group"));
		flgDimension = new FormLayout();
		flgDimension.marginWidth = 10;
		flgDimension.marginHeight = 10;
		fdgDimension = new FormData();
		fdgDimension.top = new FormAttachment(wFileName, margin);
		fdgDimension.left = new FormAttachment(0, 0);
		fdgDimension.right = new FormAttachment(100, 0);
		wgDimension.setLayout(flgDimension);
		wgDimension.setLayoutData(fdgDimension);

		// new label selectdimension
		wlSelectDimension = new Label(wgDimension, SWT.RIGHT);
		wlSelectDimension.setText(BaseMessages.getString(PKG,
				"Dimension.SelectDimension"));
		props.setLook(wlSelectDimension);
		fdlSelectDimension = new FormData();
		fdlSelectDimension.left = new FormAttachment(0, 0);
		fdlSelectDimension.right = new FormAttachment(middle, -middle);
		fdlSelectDimension.top = new FormAttachment(wStepname, margin);
		wlSelectDimension.setLayoutData(fdlSelectDimension);

		// new combo selectdimension
		wSelectDimension = new Combo(wgDimension, SWT.SINGLE | SWT.LEFT
				| SWT.BORDER);
		props.setLook(wSelectDimension);
		fdSelectDimension = new FormData();
		fdSelectDimension.left = new FormAttachment(middle, 0);
		fdSelectDimension.top = new FormAttachment(wStepname, margin);
		fdSelectDimension.right = new FormAttachment(100, 0);
		wSelectDimension.setLayoutData(fdSelectDimension);
		FocusListener lsSelectDimension = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (!wFileName.getText().equals("")) {
					wSelectDimension.setItems(carregarDimens(wFileName
							.getText()));
				}
			}
		};
		wSelectDimension.addFocusListener(lsSelectDimension);

		// new label namedimension
		wlNameDimension = new Label(wgDimension, SWT.RIGHT);
		wlNameDimension.setText(BaseMessages.getString(PKG, "Dimension.Name"));
		props.setLook(wlNameDimension);
		fdlNameDimension = new FormData();
		fdlNameDimension.left = new FormAttachment(0, 0);
		fdlNameDimension.right = new FormAttachment(middle, -middle);
		fdlNameDimension.top = new FormAttachment(wSelectDimension, margin);
		wlNameDimension.setLayoutData(fdlNameDimension);

		// new text namedimension
		wNameDimension = new Text(wgDimension, SWT.SINGLE | SWT.LEFT
				| SWT.BORDER);
		wNameDimension.setText("");
		props.setLook(wNameDimension);
		wNameDimension.addModifyListener(lsMod);
		fdNameDimension = new FormData();
		fdNameDimension.left = new FormAttachment(middle, 0);
		fdNameDimension.top = new FormAttachment(wSelectDimension, margin);
		fdNameDimension.right = new FormAttachment(100, 0);
		wNameDimension.setLayoutData(fdNameDimension);

		// new label dimension visible
		wlVisibleDimension = new Label(wgDimension, SWT.RIGHT);
		wlVisibleDimension.setText(BaseMessages.getString(PKG,
				"Dimension.Visible"));
		props.setLook(wlVisibleDimension);
		fdlVisibleDimension = new FormData();
		fdlVisibleDimension.top = new FormAttachment(wNameDimension, margin);
		fdlVisibleDimension.left = new FormAttachment(0, 0);
		fdlVisibleDimension.right = new FormAttachment(middle, -middle);
		wlVisibleDimension.setLayoutData(fdlVisibleDimension);

		// new check dimension visible
		wVisibleDimension = new Button(wgDimension, SWT.CHECK);
		props.setLook(wVisibleDimension);
		fdVisibleDimension = new FormData();
		fdVisibleDimension.top = new FormAttachment(wNameDimension, margin);
		fdVisibleDimension.left = new FormAttachment(middle, 0);
		fdVisibleDimension.right = new FormAttachment(100, 0);
		wVisibleDimension.setLayoutData(fdVisibleDimension);

		// new label hierarchy
		wlHierarchy = new Label(shell, SWT.RIGHT);
		wlHierarchy
				.setText(BaseMessages.getString(PKG, "Hierarchy.ColumnName"));
		props.setLook(wlHierarchy);
		fdlHierarchy = new FormData();
		fdlHierarchy.top = new FormAttachment(wgDimension, margin);
		fdlHierarchy.left = new FormAttachment(0, 0);
		wlHierarchy.setLayoutData(fdlHierarchy);

		// new list hierarchy
		wHierarchy = new List(shell, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		props.setLook(wHierarchy);
		fdHierarchy = new FormData();
		fdHierarchy.left = new FormAttachment(0, 0);
		fdHierarchy.top = new FormAttachment(wlHierarchy, 10);
		fdHierarchy.width = 130;
		fdHierarchy.height = 200;
		wHierarchy.setLayoutData(fdHierarchy);
		SelectionListener lsHierarchy;
		lsHierarchy = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				carregarHierarquia();
				carregarLevel();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		};
		wHierarchy.addSelectionListener(lsHierarchy);

		// new label level
		wlLevel = new Label(shell, SWT.RIGHT);
		wlLevel.setText(BaseMessages.getString(PKG, "Level.ColumnName"));
		props.setLook(wlLevel);
		fdlLevel = new FormData();
		fdlLevel.top = new FormAttachment(wgDimension, margin);
		fdlLevel.left = new FormAttachment(wHierarchy, 10);
		wlLevel.setLayoutData(fdlLevel);

		// new list level
		wLevel = new List(shell, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		props.setLook(wLevel);
		fdLevel = new FormData();
		fdLevel.left = new FormAttachment(wHierarchy, margin);
		fdLevel.top = new FormAttachment(wlLevel, 10);
		fdLevel.width = 130;
		fdLevel.height = 200;
		wLevel.setLayoutData(fdLevel);
		SelectionListener lsLevel;
		lsLevel = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				carregarLevel();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		};
		wLevel.addSelectionListener(lsLevel);

		// new button up hierarchy
		wHierarchyUp = new Button(shell, SWT.PUSH);
		props.setLook(wHierarchyUp);
		fdHierarchyUp = new FormData();
		fdHierarchyUp.top = new FormAttachment(wHierarchy, 10);
		fdHierarchyUp.left = new FormAttachment(0, 0);
		fdHierarchyUp.width = (fdHierarchy.width) / 2;
		wHierarchyUp.setLayoutData(fdHierarchyUp);
		wHierarchyUp
				.setImage(new Image(
						new Device() {

							@Override
							public long internal_new_GC(GCData arg0) {
								// TODO Auto-generated method stub
								return 0;
							}

							@Override
							public void internal_dispose_GC(long arg0,
									GCData arg1) {
								// TODO Auto-generated method stub

							}
						},
						"C:/Users/Eduardo/Desktop/Estágio/eclipse/LogicDimension/fonte/src/uenp/kettle/logicdimension/images/seta_up.jpg"));
		Listener lsHierarchyUp = null;

		lsHierarchyUp = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (wHierarchy.getFocusIndex() > 0) {
					Hierarchy aux = hierarchys.get(wHierarchy.getFocusIndex());
					hierarchys.set(wHierarchy.getFocusIndex(),
							hierarchys.get(wHierarchy.getFocusIndex() - 1));
					hierarchys.set(wHierarchy.getFocusIndex() - 1, aux);
					int levelAtual = wLevel.getFocusIndex();
					int hierarquiaAtual = wHierarchy.getFocusIndex() - 1;
					carregarHierarquias(hierarquiaAtual);
					carregarHierarquia();
					carregarLevels(levelAtual);
					carregarLevel();
				}
			}

		};
		wHierarchyUp.addListener(SWT.Selection, lsHierarchyUp);

		// new button down hierarchy
		wHierarchyDown = new Button(shell, SWT.PUSH);
		props.setLook(wHierarchyDown);
		fdHierarchyDown = new FormData();
		fdHierarchyDown.top = new FormAttachment(wHierarchy, 10);
		fdHierarchyDown.left = new FormAttachment(wHierarchyUp, 24);
		fdHierarchyDown.width = (fdHierarchy.width) / 2;
		wHierarchyDown.setLayoutData(fdHierarchyDown);
		wHierarchyDown
				.setImage(new Image(
						new Device() {

							@Override
							public long internal_new_GC(GCData arg0) {
								// TODO Auto-generated method stub
								return 0;
							}

							@Override
							public void internal_dispose_GC(long arg0,
									GCData arg1) {
								// TODO Auto-generated method stub

							}
						},
						"C:/Users/Eduardo/Desktop/Estágio/eclipse/LogicDimension/fonte/src/uenp/kettle/logicdimension/images/seta_down.jpg"));
		Listener lsHierarchyDown = null;

		lsHierarchyDown = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (wHierarchy.getFocusIndex() < (hierarchys.size() - 1)) {
					Hierarchy aux = hierarchys.get(wHierarchy.getFocusIndex());
					hierarchys.set(wHierarchy.getFocusIndex(),
							hierarchys.get(wHierarchy.getFocusIndex() + 1));
					hierarchys.set(wHierarchy.getFocusIndex() + 1, aux);
					int levelAtual = wLevel.getFocusIndex();
					int hierarquiaAtual = wHierarchy.getFocusIndex() + 1;
					carregarHierarquias(hierarquiaAtual);
					carregarHierarquia();
					carregarLevels(levelAtual);
					carregarLevel();
				}
			}
		};
		wHierarchyDown.addListener(SWT.Selection, lsHierarchyDown);

		// new button up level
		wLevelUp = new Button(shell, SWT.PUSH);
		props.setLook(wLevelUp);
		fdLevelUp = new FormData();
		fdLevelUp.top = new FormAttachment(wLevel, 10);
		fdLevelUp.left = new FormAttachment(wHierarchy, margin);
		fdLevelUp.width = (fdLevel.width) / 2;
		wLevelUp.setLayoutData(fdLevelUp);
		wLevelUp.setImage(new Image(
				new Device() {

					@Override
					public long internal_new_GC(GCData arg0) {
						// TODO Auto-generated method stub
						return 0;
					}

					@Override
					public void internal_dispose_GC(long arg0, GCData arg1) {
						// TODO Auto-generated method stub

					}
				},
				"C:/Users/Eduardo/Desktop/Estágio/eclipse/LogicDimension/fonte/src/uenp/kettle/logicdimension/images/seta_up.jpg"));
		Listener lsLevelUp = null;

		lsLevelUp = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				if (wLevel.getFocusIndex() > 0) {
					Level aux = hierarchys.get(wHierarchy.getFocusIndex())
							.getLevels().get(wLevel.getFocusIndex());
					hierarchys
							.get(wHierarchy.getFocusIndex())
							.getLevels()
							.set(wLevel.getFocusIndex(),
									hierarchys.get(wHierarchy.getFocusIndex())
											.getLevels()
											.get(wLevel.getFocusIndex() - 1));
					hierarchys.get(wHierarchy.getFocusIndex()).getLevels()
							.set(wLevel.getFocusIndex() - 1, aux);
					int levelAtual = wLevel.getFocusIndex() - 1;
					carregarLevels(levelAtual);
					carregarLevel();
				}
			}
		};
		wLevelUp.addListener(SWT.Selection, lsLevelUp);

		// new button down Level
		wLevelDown = new Button(shell, SWT.PUSH);
		props.setLook(wLevelDown);
		fdLevelDown = new FormData();
		fdLevelDown.top = new FormAttachment(wLevel, 10);
		fdLevelDown.left = new FormAttachment(wLevelUp, 24);
		fdLevelDown.width = (fdLevel.width) / 2;
		wLevelDown.setLayoutData(fdLevelDown);
		wLevelDown
				.setImage(new Image(
						new Device() {

							@Override
							public long internal_new_GC(GCData arg0) {
								// TODO Auto-generated method stub
								return 0;
							}

							@Override
							public void internal_dispose_GC(long arg0,
									GCData arg1) {
								// TODO Auto-generated method stub

							}
						},
						"C:/Users/Eduardo/Desktop/Estágio/eclipse/LogicDimension/fonte/src/uenp/kettle/logicdimension/images/seta_down.jpg"));
		Listener lsLevelDown = null;

		lsLevelDown = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				int tam = hierarchys.get(wHierarchy.getFocusIndex())
						.getLevels().size();
				if (wLevel.getFocusIndex() < (tam - 1)) {
					Level aux = hierarchys.get(wHierarchy.getFocusIndex())
							.getLevels().get(wLevel.getFocusIndex());
					hierarchys
							.get(wHierarchy.getFocusIndex())
							.getLevels()
							.set(wLevel.getFocusIndex(),
									hierarchys.get(wHierarchy.getFocusIndex())
											.getLevels()
											.get(wLevel.getFocusIndex() + 1));
					hierarchys.get(wHierarchy.getFocusIndex()).getLevels()
							.set(wLevel.getFocusIndex() + 1, aux);
					int levelAtual = wLevel.getFocusIndex() + 1;
					carregarLevels(levelAtual);
					carregarLevel();
				}
			}
		};
		wLevelDown.addListener(SWT.Selection, lsLevelDown);

		// new button new Hierarchy
		wNewHierarchy = new Button(shell, SWT.PUSH);
		props.setLook(wNewHierarchy);
		wNewHierarchy.setText(BaseMessages.getString(PKG, "Hierarchy.New"));
		fdNewHierarchy= new FormData();
		fdNewHierarchy.left= new FormAttachment(0,0);
		fdNewHierarchy.top = new FormAttachment(wHierarchyDown, 10);
		fdNewHierarchy.width= 154;
		wNewHierarchy.setLayoutData(fdNewHierarchy);
		Listener lsNewHierarchy;
		lsNewHierarchy = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Hierarchy h = new Hierarchy();
				h.setName(new ValueMetaAndData(new ValueMeta("Hierarchy.Name",
						ValueMetaInterface.TYPE_STRING), new String("Default"
						+ hierarchys.size())));
				h.setVisible(new ValueMetaAndData(new ValueMeta(
						"Hierarchy.Visible", ValueMetaInterface.TYPE_STRING),
						new String("true")));
				hierarchys.add(h);
				wHierarchy.add(String.valueOf(h.getName().getValueData()));
				wHierarchy.select(hierarchys.size() - 1);
				Level l = new Level();
				l.setName(new ValueMetaAndData(new ValueMeta("Level.Name",
						ValueMetaInterface.TYPE_STRING), new String("Level"
						+ h.getLevels().size())));
				l.setColumn(new ValueMetaAndData(new ValueMeta("Level.Column",
						ValueMetaInterface.TYPE_STRING),new String("")));
				l.setType(new ValueMetaAndData(new ValueMeta("Level.Type",
						ValueMetaInterface.TYPE_STRING), "String"));
				l.setTime(new ValueMetaAndData(new ValueMeta("Level.Time",
						ValueMetaInterface.TYPE_STRING), "TimeYears"));
				l.setVisible(new ValueMetaAndData(new ValueMeta(
						"Level.Visible", ValueMetaInterface.TYPE_STRING),
						new String("true")));
				h.getLevels().add(l);
				wLevel.select(hierarchys.get(wHierarchy.getFocusIndex())
						.getLevels().size() - 1);
				carregarHierarquia();
				carregarLevel();
			}
		};
		wNewHierarchy.addListener(SWT.Selection, lsNewHierarchy);

		// new button new Level
		wNewLevel = new Button(shell, SWT.PUSH);
		props.setLook(wNewLevel);
		wNewLevel.setText(BaseMessages.getString(PKG, "Level.New"));
		fdNewLevel= new FormData();
		fdNewLevel.left= new FormAttachment(wHierarchy, margin);
		fdNewLevel.top = new FormAttachment(wHierarchyDown, 10);
		fdNewLevel.width= 154;
		wNewLevel.setLayoutData(fdNewLevel);
		Listener lsNewLevel;
		lsNewLevel = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				int focus = wHierarchy.getFocusIndex();
				if (focus != -1) {
					Hierarchy h = hierarchys.get(focus);
					Level l = new Level();
					l.setName(new ValueMetaAndData(new ValueMeta("Level.Name",
							ValueMetaInterface.TYPE_STRING), new String("Level"
							+ h.getLevels().size())));
					l.setColumn(new ValueMetaAndData(new ValueMeta(
							"Level.Column", ValueMetaInterface.TYPE_STRING),
							new String("")));
					l.setType(new ValueMetaAndData(new ValueMeta("Level.Type",
							ValueMetaInterface.TYPE_STRING), new String(
							"String")));
					l.setTime(new ValueMetaAndData(new ValueMeta("Level.Time",
							ValueMetaInterface.TYPE_STRING), new String(
							"TimeYears")));
					l.setVisible(new ValueMetaAndData(new ValueMeta(
							"Level.Visible", ValueMetaInterface.TYPE_STRING),
							new String("true")));
					h.getLevels().add(l);
					carregarHierarquia();
					wLevel.select(hierarchys.get(wHierarchy.getFocusIndex())
							.getLevels().size() - 1);
					carregarLevel();
				}

			}
		};
		wNewLevel.addListener(SWT.Selection, lsNewLevel);
		
		// new button delete hierarchy
		wDeleteHierarchy = new Button(shell, SWT.PUSH);
		props.setLook(wDeleteHierarchy);
		wDeleteHierarchy.setText(BaseMessages.getString(PKG, "Hierarchy.Delete"));
		fdDeleteHierarchy= new FormData();
		fdDeleteHierarchy.left= new FormAttachment(0, 0);
		fdDeleteHierarchy.top = new FormAttachment(wNewHierarchy, 10);
		fdDeleteHierarchy.width= 154;
		wDeleteHierarchy.setLayoutData(fdDeleteHierarchy);
		Listener lsDeleteHierarchy;
		lsDeleteHierarchy= new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				if(hierarchys.size()>1){
					hierarchys.remove(wHierarchy.getFocusIndex());
					carregarHierarquias(0);
					carregarHierarquia();
				}
			}
		};
		wDeleteHierarchy.addListener(SWT.Selection,  lsDeleteHierarchy);
		
		//new button delete level
		wDeleteLevel = new Button(shell, SWT.PUSH);
		props.setLook(wDeleteLevel);
		wDeleteLevel.setText(BaseMessages.getString(PKG, "Level.Delete"));
		fdDeleteLevel= new FormData();
		fdDeleteLevel.left= new FormAttachment(wHierarchy, margin);
		fdDeleteLevel.top = new FormAttachment(wNewHierarchy, 10);
		fdDeleteLevel.width= 154;
		wDeleteLevel.setLayoutData(fdDeleteLevel);
		Listener lsDeleteLevel;
		lsDeleteLevel= new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				int tam=hierarchys.get(wHierarchy.getFocusIndex()).getLevels().size();
				if(tam>1){
					hierarchys.get(wHierarchy.getFocusIndex()).getLevels().remove(wLevel.getFocusIndex());
					carregarLevels(0);
					carregarLevel();
				}
			}
		};
		wDeleteLevel.addListener(SWT.Selection,  lsDeleteLevel);
		
		// new group hierarchy
		wgHierarchy = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(wgHierarchy);
		wgHierarchy.setText(BaseMessages.getString(PKG, "Hierarchy.Group"));
		flgHierarchy = new FormLayout();
		flgHierarchy.marginWidth = 10;
		flgHierarchy.marginHeight = 10;
		fdgHierarchy = new FormData();
		fdgHierarchy.top = new FormAttachment(wgDimension, margin);
		fdgHierarchy.left = new FormAttachment(wLevel, margin);
		fdgHierarchy.right = new FormAttachment(100, 0);
		wgHierarchy.setLayout(flgHierarchy);
		wgHierarchy.setLayoutData(fdgHierarchy);

		// new label hierarchy name

		wlHierarchyName = new Label(wgHierarchy, SWT.RIGHT);
		props.setLook(wlHierarchyName);
		wlHierarchyName.setText(BaseMessages.getString(PKG, "Hierarchy.Name"));
		fdlHierarchyName = new FormData();
		fdlHierarchyName.top = new FormAttachment(wgHierarchy, margin);
		fdlHierarchyName.left = new FormAttachment(0, 0);
		fdlHierarchyName.right = new FormAttachment(middle, -middle);
		wlHierarchyName.setLayoutData(fdlHierarchyName);

		// new text hierarchy name
		wHierarchyName = new Text(wgHierarchy, SWT.SINGLE | SWT.LEFT
				| SWT.BORDER);
		wHierarchyName.setText("");
		props.setLook(wHierarchyName);
		wHierarchyName.addModifyListener(lsMod);
		fdHierarchyName = new FormData();
		fdHierarchyName.left = new FormAttachment(middle, 0);
		fdHierarchyName.top = new FormAttachment(wgHierarchy, margin);
		fdHierarchyName.right = new FormAttachment(100, 0);
		wHierarchyName.setLayoutData(fdHierarchyName);
		FocusListener lsHierarchyName;
		lsHierarchyName = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				salvarHierarquia();
				carregarHierarquias(wHierarchy.getFocusIndex());
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		};
		wHierarchyName.addFocusListener(lsHierarchyName);

		// new label hierarchy visible
		wlVisibleHierarchy = new Label(wgHierarchy, SWT.RIGHT);
		wlVisibleHierarchy.setText(BaseMessages.getString(PKG,
				"Hierarchy.Visible"));
		props.setLook(wlVisibleHierarchy);
		fdlVisibleHierarchy = new FormData();
		fdlVisibleHierarchy.top = new FormAttachment(wHierarchyName, margin);
		fdlVisibleHierarchy.left = new FormAttachment(0, 0);
		fdlVisibleHierarchy.right = new FormAttachment(middle, -middle);
		wlVisibleHierarchy.setLayoutData(fdlVisibleHierarchy);

		// new check hierarchy visible
		wVisibleHierarchy = new Button(wgHierarchy, SWT.CHECK);
		props.setLook(wVisibleHierarchy);
		fdVisibleHierarchy = new FormData();
		fdVisibleHierarchy.top = new FormAttachment(wHierarchyName, margin);
		fdVisibleHierarchy.left = new FormAttachment(middle, margin);
		fdVisibleHierarchy.right = new FormAttachment(100, 0);
		wVisibleHierarchy.setLayoutData(fdVisibleHierarchy);
		FocusListener lsHierarchyVisible;
		lsHierarchyVisible = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				salvarHierarquia();
				carregarHierarquias(wHierarchy.getFocusIndex());
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		};
		wVisibleHierarchy.addFocusListener(lsHierarchyVisible);

		// new group level
		wgLevel = new Group(shell, SWT.SHADOW_NONE);
		props.setLook(wgLevel);
		wgLevel.setText(BaseMessages.getString(PKG, "Level.Group"));
		flgLevel = new FormLayout();
		flgLevel.marginWidth = 10;
		flgLevel.marginHeight = 10;
		fdgLevel = new FormData();
		fdgLevel.top = new FormAttachment(wgHierarchy, margin);
		fdgLevel.left = new FormAttachment(wLevel, margin);
		fdgLevel.right = new FormAttachment(100, 0);
		wgLevel.setLayout(flgLevel);
		wgLevel.setLayoutData(fdgLevel);

		// new label level name
		wlLevelName = new Label(wgLevel, SWT.RIGHT);
		props.setLook(wlLevelName);
		wlLevelName.setText(BaseMessages.getString(PKG, "Level.Name"));
		fdlLevelName = new FormData();
		fdlLevelName.top = new FormAttachment(wgLevel, margin);
		fdlLevelName.left = new FormAttachment(0, 0);
		fdlLevelName.right = new FormAttachment(middle, -middle);
		wlLevelName.setLayoutData(fdlLevelName);

		// new text level name
		wLevelName = new Text(wgLevel, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wLevelName.setText("");
		props.setLook(wLevelName);
		wLevelName.addModifyListener(lsMod);
		fdLevelName = new FormData();
		fdLevelName.left = new FormAttachment(middle, 0);
		fdLevelName.top = new FormAttachment(wgLevel, margin);
		fdLevelName.right = new FormAttachment(100, 0);
		wLevelName.setLayoutData(fdLevelName);
		FocusListener lsLevelName;
		lsLevelName = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				salvarLevel();
				carregarLevels(wLevel.getFocusIndex());

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		};
		wLevelName.addFocusListener(lsLevelName);

		// new label columnlevel
		wlColumnLevel = new Label(wgLevel, SWT.RIGHT);
		wlColumnLevel.setText(BaseMessages.getString(PKG, "Level.Column"));
		props.setLook(wlColumnLevel);
		fdlColumnLevel = new FormData();
		fdlColumnLevel.left = new FormAttachment(0, 0);
		fdlColumnLevel.right = new FormAttachment(middle, -middle);
		fdlColumnLevel.top = new FormAttachment(wLevelName, margin);
		wlColumnLevel.setLayoutData(fdlColumnLevel);

		// new combo columnlevel
		wColumnLevel = new Combo(wgLevel, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wColumnLevel);
		fdColumnLevel = new FormData();
		fdColumnLevel.left = new FormAttachment(middle, 0);
		fdColumnLevel.top = new FormAttachment(wLevelName, margin);
		fdColumnLevel.right = new FormAttachment(100, 0);
		fdColumnLevel.width = 200;
		wColumnLevel.setLayoutData(fdColumnLevel);
		FocusListener lsColumnLevel = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				salvarLevel();
				carregarLevels(wLevel.getFocusIndex());
				// TODO Auto-generated method stub

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (!wSelectDimension.getText().equals("")
						|| !wSelectDimension.getText().equals("")) {
					wColumnLevel.setItems(carregarColunas(wFileName.getText()));
				}

			}
		};
		wColumnLevel.addFocusListener(lsColumnLevel);

		// new label type level
		wlTypeLevel = new Label(wgLevel, SWT.RIGHT);
		wlTypeLevel.setText(BaseMessages.getString(PKG, "Level.Type"));
		props.setLook(wlTypeLevel);
		fdlTypeLevel = new FormData();
		fdlTypeLevel.left = new FormAttachment(0, 0);
		fdlTypeLevel.right = new FormAttachment(middle, -middle);
		fdlTypeLevel.top = new FormAttachment(wColumnLevel, margin);
		wlTypeLevel.setLayoutData(fdlTypeLevel);

		// new combo type level
		wTypeLevel = new Combo(wgLevel, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wTypeLevel);
		fdTypeLevel = new FormData();
		fdTypeLevel.left = new FormAttachment(middle, 0);
		fdTypeLevel.top = new FormAttachment(wColumnLevel, margin);
		fdTypeLevel.right = new FormAttachment(100, 0);
		fdTypeLevel.width = 200;
		wTypeLevel.setLayoutData(fdTypeLevel);
		wTypeLevel.add("String");
		wTypeLevel.add("Numeric");
		wTypeLevel.add("Integer");
		wTypeLevel.add("Boolean");
		wTypeLevel.add("Date");
		wTypeLevel.add("Time");
		wTypeLevel.add("Timestamp");
		FocusListener lsTypeLevel;
		lsTypeLevel = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				salvarLevel();
				carregarLevels(wLevel.getFocusIndex());
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		};
		wTypeLevel.addFocusListener(lsTypeLevel);

		// new label time level
				wlTimeLevel = new Label(wgLevel, SWT.RIGHT);
				wlTimeLevel.setText(BaseMessages.getString(PKG, "Level.Time"));
				props.setLook(wlTimeLevel);
				fdlTimeLevel = new FormData();
				fdlTimeLevel.left = new FormAttachment(0, 0);
				fdlTimeLevel.right = new FormAttachment(middle, -middle);
				fdlTimeLevel.top = new FormAttachment(wTypeLevel, margin);
				wlTimeLevel.setLayoutData(fdlTimeLevel);

				// new combo type level
				wTimeLevel = new Combo(wgLevel, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
				props.setLook(wTimeLevel);
				fdTimeLevel = new FormData();
				fdTimeLevel.left = new FormAttachment(middle, 0);
				fdTimeLevel.top = new FormAttachment(wTypeLevel, margin);
				fdTimeLevel.right = new FormAttachment(100, 0);
				fdTimeLevel.width = 200;
				wTimeLevel.setLayoutData(fdTimeLevel);
				wTimeLevel.add("TimeYears");
				wTimeLevel.add("TimeHalfYears");
				wTimeLevel.add("TimeHalfYear");
				wTimeLevel.add("TimeQuarters");
				wTimeLevel.add("TimeMonths");
				wTimeLevel.add("TimeWeeks");
				wTimeLevel.add("TimeDays");
				wTimeLevel.add("TimeHours");
				wTimeLevel.add("TimeMinutes");
				wTimeLevel.add("TimeSeconds");
				wTimeLevel.add("TimeUndefined");
				FocusListener lsTimeLevel;
				lsTimeLevel = new FocusListener() {

					@Override
					public void focusLost(FocusEvent arg0) {
						// TODO Auto-generated method stub
						salvarLevel();
						carregarLevels(wLevel.getFocusIndex());
					}

					@Override
					public void focusGained(FocusEvent arg0) {
						// TODO Auto-generated method stub

					}
				};
				wTimeLevel.addFocusListener(lsTimeLevel);

		
		// new label level visible
		wlVisibleLevel = new Label(wgLevel, SWT.RIGHT);
		wlVisibleLevel.setText(BaseMessages.getString(PKG, "Level.Visible"));
		props.setLook(wlVisibleLevel);
		fdlVisibleLevel = new FormData();
		fdlVisibleLevel.top = new FormAttachment(wTimeLevel, margin);
		fdlVisibleLevel.left = new FormAttachment(0, 0);
		fdlVisibleLevel.right = new FormAttachment(middle, -middle);
		wlVisibleLevel.setLayoutData(fdlVisibleLevel);

		// new check level visible
		wVisibleLevel = new Button(wgLevel, SWT.CHECK);
		props.setLook(wVisibleLevel);
		fdVisibleLevel = new FormData();
		fdVisibleLevel.top = new FormAttachment(wTimeLevel, margin);
		fdVisibleLevel.left = new FormAttachment(middle, margin);
		fdVisibleLevel.right = new FormAttachment(100, 0);
		wVisibleLevel.setLayoutData(fdVisibleLevel);
		FocusListener lsLevelVisible;
		lsLevelVisible = new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				salvarLevel();
				carregarLevels(wLevel.getFocusIndex());
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		};
		wVisibleLevel.addFocusListener(lsLevelVisible);

		// new button properties
		/*wProperties = new Button(wgLevel, SWT.PUSH);
		props.setLook(wProperties);
		wProperties.setText(BaseMessages.getString(PKG, "Level.Properties"));
		lddp = new LogicDimensionDialogProperties(shell, input, transMeta,
				stepname);
		Listener lsProperties = null;

		lsProperties = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				lddp.open();

			}
		};
		wProperties.addListener(SWT.Selection, lsProperties);
		BaseStepDialog.positionBottomButtons(wgLevel,
				new Button[] { wProperties }, margin, wVisibleLevel);
		wProperties.setVisible(false);
		*/
		
		// new button ok
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "Step.Ok"));
		lsOK = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				ok();

			}
		};
		wOK.addListener(SWT.Selection, lsOK);

		// new button cancel
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "Step.Cancel"));
		lsCancel = new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				cancel();

			}
		};
		wCancel.addListener(SWT.Selection, lsCancel);

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK,
				wCancel}, margin, wDeleteHierarchy);


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
		input.setSelectedDimension(new ValueMetaAndData(new ValueMeta(
				"selectedDimension", ValueMetaInterface.TYPE_STRING),
				new String(wSelectDimension.getText())));
		input.setHierarchys(hierarchys);
		input.setNameDimension(new ValueMetaAndData(new ValueMeta(
				"nameDimension", ValueMetaInterface.TYPE_STRING), new String(
				wNameDimension.getText())));
		input.setVisibleDimension(new ValueMetaAndData(new ValueMeta(
				"visibleDimension", ValueMetaInterface.TYPE_STRING),
				new String(String.valueOf(wVisibleDimension.getSelection()))));
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
		if (input.getSelectedDimension().getValueData() != null) {
			wSelectDimension.setText(String.valueOf(input
					.getSelectedDimension().getValueData()));
		}
		if (input.getNameDimension().getValueData() != null) {
			wNameDimension.setText(String.valueOf(input.getNameDimension()
					.getValueData()));
		}
		if (input.getNameDimension().getValueData() != null) {
			wVisibleDimension.setSelection(Boolean.valueOf(String.valueOf(input
					.getVisibleDimension().getValueData())));
		}
		if (input.getHierarchys() != null) {
			for (int i = 0; i < input.getHierarchys().size(); i++) {
				Hierarchy hTela = new Hierarchy();
				Hierarchy hMeta = input.getHierarchys().get(i);
				if (hMeta.getName().getValueData() != null) {
					hTela.setName(new ValueMetaAndData(hMeta.getName()
							.getValueMeta(), hMeta.getName().getValueData()));
				}
				if (hMeta.getVisible().getValueData() != null) {
					hTela.setVisible(new ValueMetaAndData(hMeta.getVisible()
							.getValueMeta(), hMeta.getVisible().getValueData()));
				}
				if (hMeta.getLevels() != null) {
					for (int j = 0; j < hMeta.getLevels().size(); j++) {
						Level lTela = new Level();
						Level lMeta = hMeta.getLevels().get(j);
						if (lMeta.getName().getValueData() != null) {
							lTela.setName(new ValueMetaAndData(lMeta.getName()
									.getValueMeta(), lMeta.getName()
									.getValueData()));
						}
						if (lMeta.getColumn().getValueData() != null) {
							lTela.setColumn(new ValueMetaAndData(lMeta
									.getColumn().getValueMeta(), lMeta
									.getColumn().getValueData()));
						}
						if (lMeta.getType().getValueData() != null) {
							lTela.setType(new ValueMetaAndData(lMeta.getType()
									.getValueMeta(), lMeta.getType()
									.getValueData()));
						}
						if(lMeta.getTime().getValueData() != null){
							lTela.setTime(new ValueMetaAndData(lMeta.getTime()
									.getValueMeta(), lMeta.getTime()
									.getValueData()));
						}
						if (lMeta.getVisible().getValueData() != null) {
							lTela.setVisible(new ValueMetaAndData(lMeta
									.getVisible().getValueMeta(), lMeta
									.getVisible().getValueData()));
						}
						hTela.getLevels().add(lTela);
					}

				}
				hierarchys.add(hTela);
			}

		}
		if (hierarchys.get(0) != null) {
			carregarHierarquias(0);
			carregarHierarquia();
			carregarLevels(0);
			carregarLevel();
		}
	}

	public String[] carregarDimens(String url) {
		File arquivo = new File(url);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(arquivo);
			Element raiz = doc.getDocumentElement();
			NodeList dimensions = raiz.getElementsByTagName("dimension");
			Element dimensão;
			String[] dimension = new String[dimensions.getLength()];
			for (int i = 0; i < dimensions.getLength(); i++) {
				dimensão = (Element) dimensions.item(i);
				dimension[i] = dimensão.getAttribute("name");
			}
			return dimension;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public String[] carregarColunas(String url) {
		File arquivo = new File(url);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(arquivo);
			Element raiz = doc.getDocumentElement();
			NodeList dimensions = raiz.getElementsByTagName("dimension");
			Element dimension = null;
			Element table = null;
			Element field = null;
			int cont = 0;
			// pegando dimensão
			for (int i = 0; i < dimensions.getLength(); i++) {
				dimension = (Element) dimensions.item(i);
				String nome = dimension.getAttribute("name");
				if (nome.equals(wSelectDimension.getText())) {
					i = dimensions.getLength();
				}
			}
			// pegando os campos
			NodeList tam = dimension.getElementsByTagName("field");
			String campos[] = new String[tam.getLength()];
			NodeList tables = dimension.getElementsByTagName("table");
			for (int i = 0; i < tables.getLength(); i++) {
				table = (Element) tables.item(i);
				NodeList fields = table.getElementsByTagName("field");
				for (int j = 0; j < fields.getLength(); j++) {
					field = (Element) fields.item(j);
					campos[cont] = table.getAttribute("name") + " - "
							+ field.getTextContent();
					cont++;

				}
			}
			return campos;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public void carregarHierarquia() {
		int selected = wHierarchy.getFocusIndex();
		wLevel.removeAll();
		for (int i = 0; i < hierarchys.get(selected).getLevels().size(); i++) {
			wLevel.add(String.valueOf(hierarchys.get(selected).getLevels()
					.get(i).getName().getValueData()));
		}
		if (hierarchys.get(selected).getName().getValueData() != null) {
			wHierarchyName.setText(String.valueOf(hierarchys.get(selected)
					.getName().getValueData()));
		} else {
			wHierarchyName.setText("");
		}
		if (hierarchys.get(selected).getVisible().getValueData() != null) {
			wVisibleHierarchy.setSelection(Boolean.valueOf(String
					.valueOf(hierarchys.get(selected).getVisible()
							.getValueData())));
		}
		if (wLevel.getItemCount() != -1) {
			wLevel.select(0);
		}
	}

	public void carregarLevel() {
		if (hierarchys.get(wHierarchy.getFocusIndex()).getLevels()
				.get(wLevel.getFocusIndex()).getName().getValueData() != null) {
			wLevelName.setText(String.valueOf(hierarchys
					.get(wHierarchy.getFocusIndex()).getLevels()
					.get(wLevel.getFocusIndex()).getName().getValueData()));
		} else {
			wLevelName.setText("");
		}
		if (hierarchys.get(wHierarchy.getFocusIndex()).getLevels()
				.get(wLevel.getFocusIndex()).getColumn().getValueData() != null) {
			wColumnLevel.setText(String.valueOf(hierarchys
					.get(wHierarchy.getFocusIndex()).getLevels()
					.get(wLevel.getFocusIndex()).getColumn().getValueData()));
		} else {
			wColumnLevel.setText("");
		}
		if (hierarchys.get(wHierarchy.getFocusIndex()).getLevels()
				.get(wLevel.getFocusIndex()).getType().getValueData() != null) {
			wTypeLevel.setText(String.valueOf(hierarchys
					.get(wHierarchy.getFocusIndex()).getLevels()
					.get(wLevel.getFocusIndex()).getType().getValueData()));
		} else {
			wTypeLevel.setText("");
		}
		if (hierarchys.get(wHierarchy.getFocusIndex()).getLevels()
				.get(wLevel.getFocusIndex()).getTime().getValueData() != null) {
			wTimeLevel.setText(String.valueOf(hierarchys
					.get(wHierarchy.getFocusIndex()).getLevels()
					.get(wLevel.getFocusIndex()).getTime().getValueData()));
		} else {
			wTimeLevel.setText("");
		}
		if (hierarchys.get(wHierarchy.getFocusIndex()).getLevels()
				.get(wLevel.getFocusIndex()).getVisible().getValueData() != null) {
			wVisibleLevel.setSelection(Boolean.valueOf(String
					.valueOf(hierarchys.get(wHierarchy.getFocusIndex())
							.getLevels().get(wLevel.getFocusIndex())
							.getVisible().getValueData())));
		}
	}

	public void carregarHierarquias(int atual) {
		wHierarchy.removeAll();
		for (int i = 0; i < hierarchys.size(); i++) {
			wHierarchy.add(String.valueOf(hierarchys.get(i).getName()
					.getValueData()));
		}
		wHierarchy.select(atual);
	}

	public void carregarLevels(int atual) {
		wLevel.removeAll();
		for (int i = 0; i < hierarchys.get(wHierarchy.getFocusIndex())
				.getLevels().size(); i++) {
			wLevel.add(String.valueOf(hierarchys
					.get(wHierarchy.getFocusIndex()).getLevels().get(i)
					.getName().getValueData()));
		}
		wLevel.select(atual);
	}

	public void salvarHierarquia() {
		int selected = wHierarchy.getFocusIndex();
		hierarchys.get(selected).setName(
				new ValueMetaAndData(new ValueMeta("Hierarchy.Name",
						ValueMetaInterface.TYPE_STRING), new String(
						wHierarchyName.getText())));
		hierarchys.get(selected).setVisible(
				new ValueMetaAndData(new ValueMeta("Hierarchy.Visible",
						ValueMetaInterface.TYPE_STRING), new String(String
						.valueOf(wVisibleHierarchy.getSelection()))));
		carregarHierarquia();
	}

	public void salvarLevel() {
		int selectedHierarchy = wHierarchy.getFocusIndex();
		int selectedLevel = wLevel.getFocusIndex();
		hierarchys
				.get(selectedHierarchy)
				.getLevels()
				.get(selectedLevel)
				.setName(
						new ValueMetaAndData(new ValueMeta("Level.Name",
								ValueMetaInterface.TYPE_STRING), new String(
								wLevelName.getText())));
		hierarchys
				.get(selectedHierarchy)
				.getLevels()
				.get(selectedLevel)
				.setColumn(
						new ValueMetaAndData(new ValueMeta("Level.Column",
								ValueMetaInterface.TYPE_STRING), new String(
								wColumnLevel.getText())));
		hierarchys
				.get(selectedHierarchy)
				.getLevels()
				.get(selectedLevel)
				.setType(
						new ValueMetaAndData(new ValueMeta("Level.Type",
								ValueMetaInterface.TYPE_STRING), new String(
								wTypeLevel.getText())));
		hierarchys
		.get(selectedHierarchy)
		.getLevels()
		.get(selectedLevel)
		.setTime(
				new ValueMetaAndData(new ValueMeta("Level.Time",
						ValueMetaInterface.TYPE_STRING), new String(
						wTimeLevel.getText())));
		hierarchys
				.get(selectedHierarchy)
				.getLevels()
				.get(selectedLevel)
				.setVisible(
						new ValueMetaAndData(new ValueMeta("Level.Visible",
								ValueMetaInterface.TYPE_STRING), new String(
								String.valueOf(wVisibleLevel.getSelection()))));
		carregarLevel();
	}
}
