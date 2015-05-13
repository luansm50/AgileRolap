
package uenp.kettle.tempo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class TempoDialog extends BaseStepDialog implements StepDialogInterface {

	private TempoMeta input;

	private CCombo addConexao;
	private Label wlInicio;
	private Text wInicio;
	private FormData fdlInicio, fdInicio;

	private Label wlFinal;
	private Text wFinal;
	private FormData fdlFinal, fdFinal;

	private Label wlLinguage;
	private ComboVar wLinguage;
	private FormData fdlLinguage, fdLinguage;

	private Combo wCompareField;
	private FormData fdCompareField;
	private Label wlCompareField;
	private FormData fdlCompareField;
	private String actualLanguage;

	public TempoDialog(Shell parent, Object in, TransMeta transMeta,
			String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (TempoMeta) in;
	}

	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN
				| SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		changed = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("TempoDialog.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("TempoDialog.StepName.Label"));
		props.setLook(wlStepname);
		wlStepname.setLayoutData(fdlStepname);

		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.setLayoutData(fdStepname);

		addConexao = addConnectionLine(shell, wStepname, middle, margin);

		fdlInicio = new FormData();
		fdlInicio.left = new FormAttachment(0, 0);
		fdlInicio.right = new FormAttachment(middle, -margin);
		fdlInicio.top = new FormAttachment(addConexao, margin);
		wlInicio = new Label(shell, SWT.RIGHT);
		wlInicio.setText(Messages.getString("TempoDialog.DateStart.Label"));
		props.setLook(wlInicio);
		wlInicio.setLayoutData(fdlInicio);

		fdInicio = new FormData();
		fdInicio.left = new FormAttachment(middle, 0);
		fdInicio.right = new FormAttachment(100, 0);
		fdInicio.top = new FormAttachment(addConexao, margin);
		wInicio = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wInicio);
		wInicio.addModifyListener(lsMod);
		wInicio.setLayoutData(fdInicio);

		fdlFinal = new FormData();
		fdlFinal.left = new FormAttachment(0, 0);
		fdlFinal.right = new FormAttachment(middle, -margin);
		fdlFinal.top = new FormAttachment(wInicio, margin);
		wlFinal = new Label(shell, SWT.RIGHT);
		wlFinal.setText(Messages.getString("TempoDialog.DateStop.Label"));
		props.setLook(wlFinal);
		wlFinal.setLayoutData(fdlFinal);

		fdFinal = new FormData();
		fdFinal.left = new FormAttachment(middle, 0);
		fdFinal.right = new FormAttachment(100, 0);
		fdFinal.top = new FormAttachment(wInicio, margin);
		wFinal = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wFinal);
		wFinal.addModifyListener(lsMod);
		wFinal.setLayoutData(fdFinal);

		wlLinguage = new Label(shell, SWT.RIGHT);
		wlLinguage.setText(Messages.getString("TempoDialog.Language.Label")); //$NON-NLS-1$
		props.setLook(wlLinguage);
		fdlLinguage = new FormData();
		fdlLinguage.left = new FormAttachment(0, 0);
		fdlLinguage.right = new FormAttachment(middle, -margin);
		fdlLinguage.top = new FormAttachment(wFinal, margin);
		wlLinguage.setLayoutData(fdlLinguage);

		wLinguage = new ComboVar(transMeta, shell, SWT.BORDER | SWT.READ_ONLY);
		wLinguage.setEditable(true);
		props.setLook(wLinguage);
		wLinguage.addModifyListener(lsMod);
		fdLinguage = new FormData();
		fdLinguage.left = new FormAttachment(middle, 0);
		fdLinguage.top = new FormAttachment(wFinal, margin);
		fdLinguage.right = new FormAttachment(100, -margin);
		wLinguage.setLayoutData(fdLinguage);
		String[] itens = new String[2];
		itens[0] = Messages.getString("TempoDialog.Language.Itens.1");
		itens[1] = Messages.getString("TempoDialog.Language.Itens.2");
		wLinguage.setText(itens[1]);
		wLinguage.setItems(itens);
		actualLanguage = wLinguage.getText();
		FocusListener lsFocus = new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (!actualLanguage.equals(wLinguage.getText())) {
					actualLanguage = wLinguage.getText();
					preencheWCompareField();
				}
				logBasic("chamou o lost");
			}

			@Override
			public void focusGained(FocusEvent arg0) {

			}
		};
		wLinguage.addFocusListener(lsFocus);

		// new label Compare Key
		wlCompareField = new Label(shell, SWT.RIGHT);
		wlCompareField.setText(Messages.getString("TempoDialog.CompareField"));
		props.setLook(wlCompareField);
		fdlCompareField = new FormData();
		fdlCompareField.left = new FormAttachment(0, 0);
		fdlCompareField.right = new FormAttachment(middle, -margin);
		fdlCompareField.top = new FormAttachment(wLinguage, margin);
		wlCompareField.setLayoutData(fdlCompareField);
		
		// new combo Compare Key
		wCompareField = new Combo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wCompareField);
		fdCompareField = new FormData();
		fdCompareField.left = new FormAttachment(middle, 0);
		fdCompareField.top = new FormAttachment(wLinguage, margin);
		fdCompareField.right = new FormAttachment(100, -margin);
		wCompareField.setLayoutData(fdCompareField);
		wCompareField.addModifyListener(lsMod);
		preencheWCompareField();

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("TempoDialog.ButtonOk.Label"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("TempoDialog.ButtonCancel.Label"));

		BaseStepDialog.positionBottomButtons(shell,
				new Button[] { wOK, wCancel }, margin, wCompareField);

		lsOK = new Listener() {
			@Override
			public void handleEvent(Event e) {
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

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		wInicio.addSelectionListener(lsDef);
		wFinal.addSelectionListener(lsDef);

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
			if (!display.readAndDispatch())
				display.sleep();
		}

		return stepname;
	}

	public void getData() {
		wStepname.selectAll();
		logBasic("get data");
		if (input.getInicio().getValueMeta() != null) {
			wInicio.setText(String.valueOf(input.getInicio().getValueData()));
		}
		if (input.getFim().getValueMeta() != null) {
			wFinal.setText(String.valueOf(input.getFim().getValueData()));
		}
		if (input.getLanguage().getValueMeta() != null) {
			wLinguage.setText(String
					.valueOf(input.getLanguage().getValueData()));
		}
		if (input.getConection() != null) {
			addConexao.setText(input.getConection().getName());
		}
		if (input.getCompareField().getValueData() != null) {
			wCompareField.setText(String.valueOf(input.getCompareField()
					.getValueData()));
			logBasic("compareField não é null");
		}
	}

	public void ok() {
		stepname = wStepname.getText();
		input.setInicio(new ValueMetaAndData(new ValueMeta("inicio",
				ValueMetaInterface.TYPE_STRING), new String(wInicio.getText())));
		input.setFim(new ValueMetaAndData(new ValueMeta("fim",
				ValueMetaInterface.TYPE_STRING), new String(wFinal.getText())));
		input.setDimension(new ValueMetaAndData(new ValueMeta("dimension",
				ValueMetaInterface.TYPE_STRING),
				new String(wStepname.getText())));
		input.setLanguage(new ValueMetaAndData(new ValueMeta("language",
				ValueMetaInterface.TYPE_STRING),
				new String(wLinguage.getText())));
		input.setConection(transMeta.findDatabase(addConexao.getText()));
		input.setCompareField(new ValueMetaAndData(new ValueMeta(
				"compareField", ValueMetaInterface.TYPE_STRING), new String(
				wCompareField.getText())));
		dispose();
	}

	public void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	public void preencheWCompareField() {
		if (wLinguage.getText().equals("Português")
				|| wLinguage.getText().equals("Portuguese")) {
			logBasic("chamou preenche");
			wCompareField.setText("");
			wCompareField.removeAll();
			wCompareField.add("key");
			wCompareField.add("timestamp");
			wCompareField.add("date");
			wCompareField.add("data_curta");
			wCompareField.add("data_media");
			wCompareField.add("data_longa");
			wCompareField.add("data_completa");
			wCompareField.add("dia_semana");
			wCompareField.add("dia_semana_abrev");
			wCompareField.add("dia_mes");
			wCompareField.add("mes_abrev");
			wCompareField.add("mes");
			wCompareField.add("semana_ano");
			wCompareField.add("semana_mes");
			wCompareField.add("numero_mes");
			wCompareField.add("ano");
			wCompareField.add("semestre");
			wCompareField.add("semestre_ano");
			wCompareField.add("semestre_numero");
			wCompareField.add("trimestre");
			wCompareField.add("trimestre_ano");
			wCompareField.add("trimestre_numero");
			wCompareField.add("bimestre");
			wCompareField.add("bimestre_ano");
			wCompareField.add("bimestre_numero");
			wCompareField.add("feriado");
			wCompareField.add("feriado_descricao");
		} else if (wLinguage.getText().equals("Inglês")
				|| wLinguage.getText().equals("English")) {
			logBasic("chamou preenche");
			wCompareField.setText("");
			wCompareField.removeAll();
			wCompareField.add("key");
			wCompareField.add("timestamp");
			wCompareField.add("date");
			wCompareField.add("short_date");
			wCompareField.add("media_date");
			wCompareField.add("long");
			wCompareField.add("long_date");
			wCompareField.add("day_week");
			wCompareField.add("abbrev_day_week");
			wCompareField.add("day_month");
			wCompareField.add("abbrev_month");
			wCompareField.add("month");
			wCompareField.add("year_week");
			wCompareField.add("week");
			wCompareField.add("number_month");
			wCompareField.add("year");
			wCompareField.add("half");
			wCompareField.add("half_year");
			wCompareField.add("number_half");
			wCompareField.add("quarter");
			wCompareField.add("quarter_year");
			wCompareField.add("number_quarter");
			wCompareField.add("two_months");
			wCompareField.add("two_months_year");
			wCompareField.add("number_two_months");
			wCompareField.add("holiday");
			wCompareField.add("holiday_description");
		}
	}
}