package uenp.kettle.tempo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

public class TempoMeta extends BaseStepMeta implements StepMetaInterface {

	private ValueMetaAndData inicio;
	private ValueMetaAndData fim;
	private ValueMetaAndData dimension;
	private ValueMetaAndData language;
	private DatabaseMeta conection;
	private ValueMetaAndData compareField;

	public TempoMeta() {
		super();
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transmeta,
			StepMeta stepMeta, RowMetaInterface prev, String[] input,
			String[] output, RowMetaInterface info) {
		CheckResult cr;
		if (prev == null || prev.size() == 0) {
			cr = new CheckResult(
					CheckResult.TYPE_RESULT_WARNING,
					"Não esta recebendo quaisquer campos de etapas anteriores!",
					stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK,
					"O passo esta ligado ao anterior, recebendo campos!",
					stepMeta);
			remarks.add(cr);
		}

		if (input.length > 0) {
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK,
					"O passo esta recebendo informações de outros passos!",
					stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR,
					"Sem informações recebidas de outros passos!", stepMeta);
			remarks.add(cr);
		}
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans disp) {
		return new Tempo(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	@Override
	public StepDataInterface getStepData() {
		return new TempoData();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases,
			Map<String, Counter> counters) throws KettleXMLException {
		try {
			readData(stepnode, databases);

			inicio = new ValueMetaAndData();
			fim = new ValueMetaAndData();
			dimension = new ValueMetaAndData();
			language = new ValueMetaAndData();
			compareField = new ValueMetaAndData();
			Node valnode = XMLHandler.getSubNode(stepnode, "values1", "value");
			if (valnode != null) {
				inicio.loadXML(valnode);
			}
			valnode = XMLHandler.getSubNode(stepnode, "values2", "value");
			if (valnode != null) {
				fim.loadXML(valnode);
			}
			valnode = XMLHandler.getSubNode(stepnode, "values3", "value");
			if (valnode != null) {
				dimension.loadXML(valnode);
			}
			valnode = XMLHandler.getSubNode(stepnode, "values4", "value");
			if (valnode != null) {
				language.loadXML(valnode);
			}
			logBasic("antes de problema");
			valnode = XMLHandler.getSubNode(stepnode, "compareField", "value");
			if (valnode != null) {
				compareField.loadXML(valnode);
				logBasic("depois de problema");
			}
		} catch (Exception e) {
			throw new KettleXMLException("Problema " + e);
		}
	}

	@Override
	public void readRep(Repository rep, ObjectId id_step,
			List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
	}

	@Override
	public void saveRep(Repository rep, ObjectId id_transformation,
			ObjectId id_step) throws KettleException {
	}

	private void readData(Node stepnode,
			List<? extends SharedObjectInterface> databases)
			throws KettleXMLException {
		try {
			String con = XMLHandler.getTagValue(stepnode, "connection");
			conection = DatabaseMeta.findDatabase(databases, con);
		} catch (Exception e) {
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}

	@Override
	public void setDefault() {
		inicio = new ValueMetaAndData(new ValueMeta("inicio",
				ValueMetaInterface.TYPE_STRING), new String("01/01/2000"));
		fim = new ValueMetaAndData(new ValueMeta("fim",
				ValueMetaInterface.TYPE_STRING), new String("01/01/2014"));
		dimension = new ValueMetaAndData();
		language = new ValueMetaAndData();
		compareField = new ValueMetaAndData();
	}

	public ValueMetaAndData getInicio() {
		return inicio;
	}

	public void setInicio(ValueMetaAndData inicio) {
		this.inicio = inicio;
	}

	public ValueMetaAndData getFim() {
		return fim;
	}

	public void setFim(ValueMetaAndData fim) {
		this.fim = fim;
	}

	public ValueMetaAndData getCompareField() {
		return compareField;
	}

	public void setCompareField(ValueMetaAndData compareField) {
		this.compareField = compareField;
	}

	public void getFields(RowMetaInterface r, String origin,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		ValueMetaInterface v = new ValueMeta();
		v.setName(xmlPutRow());
		v.setType(ValueMeta.TYPE_STRING);
		v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
		v.setOrigin(origin);
		r.addValueMeta(v);
	}

	public Object[] formatos(String entrada, int contador, String holiday[][])
			throws ParseException {
		Object formatos[] = new Object[27];
		if (String.valueOf(language.getValueData()).equals("Portugu�s")
				|| String.valueOf(language.getValueData()).equals("Portuguese")) {
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			Date data = new Date(formato.parse(entrada).getTime());
			formatos[0] = String.valueOf(contador);
			formatos[1] = new SimpleDateFormat("Y-MM-dd HH:mm:ss").format(data
					.getTime());
			formatos[2] = new SimpleDateFormat("Y-MM-dd")
					.format(data.getTime());
			formatos[3] = new SimpleDateFormat("dd/MM/yy").format(data
					.getTime());
			formatos[4] = new SimpleDateFormat("dd/MM/yyyy").format(data
					.getTime());
			formatos[5] = new SimpleDateFormat("dd' de 'MMMM' de 'yyyy")
					.format(data.getTime());
			formatos[6] = new SimpleDateFormat("EEEE', 'dd' de 'MMMM' de 'yyyy")
					.format(data.getTime());
			formatos[7] = new SimpleDateFormat("EEEE").format(data.getTime());
			formatos[8] = new SimpleDateFormat("E").format(data.getTime());
			formatos[9] = new SimpleDateFormat("d").format(data.getTime());
			formatos[10] = new SimpleDateFormat("MMM").format(data.getTime());
			formatos[11] = new SimpleDateFormat("MMMM").format(data.getTime());
			formatos[12] = new SimpleDateFormat("w").format(data.getTime());
			formatos[13] = new SimpleDateFormat("W").format(data.getTime());
			formatos[14] = new SimpleDateFormat("M").format(data.getTime());
			formatos[15] = new SimpleDateFormat("yyyy").format(data.getTime());
			if ((Integer.parseInt(new SimpleDateFormat("M").format(data
					.getTime())) / 6) <= 1) {
				formatos[16] = "1º Sem";
				formatos[17] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 1º Sem";
				formatos[18] = "1";
			} else {
				formatos[16] = "2º Sem";
				formatos[17] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 2º Sem";
				formatos[18] = "2";
			}
			switch (Integer.parseInt(new SimpleDateFormat("M").format(data
					.getTime()))) {
			case 1:
			case 2:
			case 3:
				formatos[19] = "1º Trim";
				formatos[20] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 1º Trim";
				formatos[21] = "1";
				break;
			case 4:
			case 5:
			case 6:
				formatos[19] = "2º Trim";
				formatos[20] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 2º Trim";
				formatos[21] = "2";
				break;
			case 7:
			case 8:
			case 9:
				formatos[19] = "3º Trim";
				formatos[20] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 3º Trim";
				formatos[21] = "3";
				break;
			case 10:
			case 11:
			case 12:
				formatos[19] = "4º Trim";
				formatos[20] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 4º Trim";
				formatos[21] = "4";
				break;
			}
			switch (Integer.parseInt(new SimpleDateFormat("M").format(data
					.getTime()))) {
			case 1:
			case 2:
				formatos[22] = "1º Bim";
				formatos[23] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 1º Bim";
				formatos[24] = "1";
				break;
			case 3:
			case 4:
				formatos[22] = "2º Bim";
				formatos[23] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 2º Bim";
				formatos[24] = "2";
				break;
			case 5:
			case 6:
				formatos[22] = "3º Bim";
				formatos[23] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 3º Bim";
				formatos[24] = "3";
				break;
			case 7:
			case 8:
				formatos[22] = "4º Bim";
				formatos[23] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 4º Bim";
				formatos[24] = "4";
				break;
			case 9:
			case 10:
				formatos[22] = "5º Bim";
				formatos[23] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 5º Bim";
				formatos[24] = "5";
				break;
			case 11:
			case 12:
				formatos[22] = "6º Bim";
				formatos[23] = new SimpleDateFormat("yyyy").format(data
						.getTime()) + " - 6º Bim";
				formatos[24] = "6";
				break;
			}
			formatos[25] = "f";
			formatos[26] = "";
			for (int i = 0; i < holiday.length; i++) {
				if (formatos[11].equals(holiday[i][1])) {
					if (formatos[9].equals(holiday[i][0])) {
						formatos[25] = "t";
						formatos[26] = holiday[i][2];
						break;
					}
				}
			}
		} else {
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy",
					Locale.US);
			Date data = new Date(formato.parse(entrada).getTime());
			formatos[0] = String.valueOf(contador);
			formatos[1] = new SimpleDateFormat("Y-MM-dd HH:mm:ss", Locale.US)
					.format(data.getTime());
			formatos[2] = new SimpleDateFormat("Y-MM-dd", Locale.US)
					.format(data.getTime());
			formatos[3] = new SimpleDateFormat("dd/MM/yy", Locale.US)
					.format(data.getTime());
			formatos[4] = new SimpleDateFormat("dd/MM/yyyy", Locale.US)
					.format(data.getTime());
			formatos[5] = new SimpleDateFormat("dd' 'MMMM' 'yyyy", Locale.US)
					.format(data.getTime());
			formatos[6] = new SimpleDateFormat("EEEE', 'dd' 'MMMM' 'yyyy",
					Locale.US).format(data.getTime());
			formatos[7] = new SimpleDateFormat("EEEE", Locale.US).format(data
					.getTime());
			formatos[8] = new SimpleDateFormat("E", Locale.US).format(data
					.getTime());
			formatos[9] = new SimpleDateFormat("d", Locale.US).format(data
					.getTime());
			formatos[10] = new SimpleDateFormat("MMM", Locale.US).format(data
					.getTime());
			formatos[11] = new SimpleDateFormat("MMMM", Locale.US).format(data
					.getTime());
			formatos[12] = new SimpleDateFormat("w", Locale.US).format(data
					.getTime());
			formatos[13] = new SimpleDateFormat("W", Locale.US).format(data
					.getTime());
			formatos[14] = new SimpleDateFormat("M", Locale.US).format(data
					.getTime());
			formatos[15] = new SimpleDateFormat("yyyy", Locale.US).format(data
					.getTime());
			if ((Integer.parseInt(new SimpleDateFormat("M", Locale.US)
					.format(data.getTime())) / 6) <= 1) {
				formatos[16] = "1º Sem";
				formatos[17] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 1º Sem";
				formatos[18] = "1";
			} else {
				formatos[16] = "2º Sem";
				formatos[17] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 2º Sem";
				formatos[18] = "2";
			}
			switch (Integer.parseInt(new SimpleDateFormat("M", Locale.US)
					.format(data.getTime()))) {
			case 1:
			case 2:
			case 3:
				formatos[19] = "1º Quar";
				formatos[20] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 1º Trim";
				formatos[21] = "1";
				break;
			case 4:
			case 5:
			case 6:
				formatos[19] = "2º Quar";
				formatos[20] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 2º Trim";
				formatos[21] = "2";
				break;
			case 7:
			case 8:
			case 9:
				formatos[19] = "3º Quar";
				formatos[20] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 3º Trim";
				formatos[21] = "3";
				break;
			case 10:
			case 11:
			case 12:
				formatos[19] = "4º Quar";
				formatos[20] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 4º Trim";
				formatos[21] = "4";
				break;
			}
			switch (Integer.parseInt(new SimpleDateFormat("M", Locale.US)
					.format(data.getTime()))) {
			case 1:
			case 2:
				formatos[22] = "1º Bim";
				formatos[23] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 1º Bim";
				formatos[24] = "1";
				break;
			case 3:
			case 4:
				formatos[22] = "2º Bim";
				formatos[23] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 2º Bim";
				formatos[24] = "2";
				break;
			case 5:
			case 6:
				formatos[22] = "3º Bim";
				formatos[23] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 3º Bim";
				formatos[24] = "3";
				break;
			case 7:
			case 8:
				formatos[22] = "4º Bim";
				formatos[23] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 4º Bim";
				formatos[24] = "4";
				break;
			case 9:
			case 10:
				formatos[22] = "5º Bim";
				formatos[23] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 5º Bim";
				formatos[24] = "5";
				break;
			case 11:
			case 12:
				formatos[22] = "6º Bim";
				formatos[23] = new SimpleDateFormat("yyyy", Locale.US)
						.format(data.getTime()) + " - 6º Bim";
				formatos[24] = "6";
				break;
			}
			formatos[25] = "f";
			formatos[26] = "";
			for (int i = 0; i < holiday.length; i++) {
				if (formatos[11].equals(holiday[i][1])) {
					if (formatos[9].equals(holiday[i][0])) {
						formatos[25] = "t";
						formatos[26] = holiday[i][2];
						break;
					}
				}
			}
		}
		return formatos;
	}

	public String getXML() throws KettleException {
		String retval = "";
		StringBuilder retva = new StringBuilder();

		retva.append("    "
				+ XMLHandler.addTagValue("connection", conection == null ? ""
						: conection.getName()));
		retval += retva.toString();

		retval += "<values1>" + Const.CR;
		if (inicio != null) {
			retval += inicio.getXML();
		}
		retval += "</values1>" + Const.CR;
		retval += "<values2>" + Const.CR;
		if (fim != null) {
			retval += fim.getXML();
		}
		retval += "</values2>" + Const.CR;
		retval += "<values3>" + Const.CR;
		if (dimension != null) {
			retval += dimension.getXML();
		}
		retval += "</values3>" + Const.CR;
		retval += "<values4>" + Const.CR;
		if (language != null) {
			retval += language.getXML();
		}
		retval += "</values4>" + Const.CR;
		retval += "<compareField>";
		if (compareField != null) {
			retval += compareField.getXML();
		}
		retval += "</compareField>" + Const.CR;
		return retval;
	}

	public String getInicioText() throws KettleException {
		String xml = this.getXML();
		String saida[] = xml.split("<text>");
		return saida[1];
	}

	public ValueMetaAndData getDimension() {
		return dimension;
	}

	public void setDimension(ValueMetaAndData dimension) {
		this.dimension = dimension;
	}

	public ValueMetaAndData getLanguage() {
		return language;
	}

	public void setLanguage(ValueMetaAndData language) {
		this.language = language;
	}

	public DatabaseMeta getConection() {
		return conection;
	}

	public void setConection(DatabaseMeta conection) {
		this.conection = conection;
	}

	public String xmlPutRow() {
		String xml = "";
		if (String.valueOf(language.getValueData()).equals("Portugu�s")
				|| String.valueOf(language.getValueData()).equals("Portuguese")) {
			xml += "<conection>" + conection.getName() + "</conection>"
					+ "<dimension style='time' name='"
					+ String.valueOf(dimension.getValueData()).toLowerCase()
					+ "' " + "main_table='"
					+ String.valueOf(dimension.getValueData()).toLowerCase()
					+ "'>" + "<table name='"
					+ String.valueOf(dimension.getValueData()).toLowerCase()
					+ "' " + "primary_key='" + compareField.getValueData()
					+ "' father_table='' father_key=''>" + "<field>key</field>"
					+ "<field>timestamp</field>" + "<field>date</field>"
					+ "<field>data_curta</field>" + "<field>data_media</field>"
					+ "<field>data_longa</field>"
					+ "<field>data_completa</field>"
					+ "<field>dia_semana</field>"
					+ "<field>dia_semana_abrev</field>"
					+ "<field>dia_mes</field>" + "<field>mes_abrev</field>"
					+ "<field>mes</field>" + "<field>semana_ano</field>"
					+ "<field>semana_mes</field>" + "<field>numero_mes</field>"
					+ "<field>ano</field>" + "<field>semestre</field>"
					+ "<field>semestre_ano</field>"
					+ "<field>semestre_numero</field>"
					+ "<field>trimestre</field>"
					+ "<field>trimestre_ano</field>"
					+ "<field>trimestre_numero</field>"
					+ "<field>bimestre</field>" + "<field>bimestre_ano</field>"
					+ "<field>bimestre_numero</field>"
					+ "<field>feriado</field>"
					+ "<field>feriado_descricao</field>" + "</table>"
					+ "</dimension>";
		} else {
			xml += "<conection>" + conection.getName() + "</conection>"
					+ "<dimension style='time' name='"
					+ String.valueOf(dimension.getValueData()).toLowerCase()
					+ "' " + "main_table='"
					+ String.valueOf(dimension.getValueData()).toLowerCase()
					+ "'>" + "<table name='"
					+ String.valueOf(dimension.getValueData()).toLowerCase()
					+ "' " + "primary_key='" + compareField.getValueData()
					+ "' father_table='' father_key=''>" + "<field>key</field>"
					+ "<field>timestamp</field>" + "<field>date</field>"
					+ "<field>short_date</field>" + "<field>media_date</field>"
					+ "<field>long</field>" + "<field>long_date</field>"
					+ "<field>day_week</field>"
					+ "<field>abbrev_day_week</field>"
					+ "<field>day_month</field>"
					+ "<field>abbrev_month</field>" + "<field>month</field>"
					+ "<field>year_week</field>" + "<field>week</field>"
					+ "<field>number_month</field>" + "<field>year</field>"
					+ "<field>half</field>" + "<field>half_year</field>"
					+ "<field>number_half</field>" + "<field>quarter</field>"
					+ "<field>quarter_year</field>"
					+ "<field>number_quarter</field>"
					+ "<field>two_months</field>"
					+ "<field>two_months_year</field>"
					+ "<field>number_two_months</field>"
					+ "<field>holiday</field>"
					+ "<field>holiday_description</field>" + "</table>"
					+ "</dimension>";
		}
		return xml;
	}

	public String[] Fields() {
		String fields[] = new String[27];
		logBasic("Linguagem: "+String.valueOf(language.getValueData()));
		logBasic("Linguagem teste1: Portugu�s");
		if (String.valueOf(language.getValueData()).equals("Portugu�s")
				|| String.valueOf(language.getValueData()).equals("Portuguese")) {
			logBasic("caiu no if");
			fields[0] = "key";
			fields[1] = "timestamp";
			fields[2] = "date";
			fields[3] = "data_curta";
			fields[4] = "data_media";
			fields[5] = "data_longa";
			fields[6] = "data_completa";
			fields[7] = "dia_semana";
			fields[8] = "dia_semana_abrev";
			fields[9] = "dia_mes";
			fields[10] = "mes_abrev";
			fields[11] = "mes";
			fields[12] = "semana_ano";
			fields[13] = "semana_mes";
			fields[14] = "numero_mes";
			fields[15] = "ano";
			fields[16] = "semestre";
			fields[17] = "semestre_ano";
			fields[18] = "semestre_numero";
			fields[19] = "trimestre";
			fields[20] = "trimestre_ano";
			fields[21] = "trimestre_numero";
			fields[22] = "bimestre";
			fields[23] = "bimestre_ano";
			fields[24] = "bimestre_numero";
			fields[25] = "feriado";
			fields[26] = "feriado_descricao";
		} else {
			logBasic("caiu no else");
			fields[0] = "key";
			fields[1] = "timestamp";
			fields[2] = "date";
			fields[3] = "short_date";
			fields[4] = "media_date";
			fields[5] = "long";
			fields[6] = "long_date";
			fields[7] = "day_week";
			fields[8] = "abbrev_day_week";
			fields[9] = "day_month";
			fields[10] = "abbrev_month";
			fields[11] = "month";
			fields[12] = "year_week";
			fields[13] = "week";
			fields[14] = "number_month";
			fields[15] = "year";
			fields[16] = "half";
			fields[17] = "half_year";
			fields[18] = "number_half";
			fields[19] = "quarter";
			fields[20] = "quarter_year";
			fields[21] = "number_quarter";
			fields[22] = "two_months";
			fields[23] = "two_months_year";
			fields[24] = "number_two_months";
			fields[25] = "holiday";
			fields[26] = "holiday_description";
		}
		return fields;
	}
}