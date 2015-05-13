package uenp.kettle.Fato;

import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.ui.core.database.dialog.SQLEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Fato extends BaseStep implements StepInterface {

	private FatoData data;
	private FatoMeta meta;

	public Fato(StepMeta s, StepDataInterface stepDataInterface, int c,
			TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		meta = (FatoMeta) smi;
		data = (FatoData) sdi;
		String p[] = null;

		Object[] r = getRow();
		if (r == null) {
			setOutputDone();
			// createView();
			// createViewNovo();
			createViewNovo2();
			return false;
		}

		data.outputRowMeta = (RowMetaInterface) new RowMeta();
		meta.getFields(data.outputRowMeta, getStepname(), null, null, null);
		p = data.outputRowMeta.getFieldNames();
		data.outputRowMeta = (RowMetaInterface) new RowMeta();
		ValueMetaInterface v = new ValueMeta();
		v.setName("xml_factor");
		v.setType(ValueMeta.TYPE_STRING);
		v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
		v.setOrigin(getStepname());
		data.outputRowMeta.addValueMeta(v);

		if (r != null) {
			for (int i = 0; i < r.length; i++) {
				if (r[i] != null) {
					Object o[] = new Object[2];
					o[0] = String.valueOf(r[i]);
					o[1] = String.valueOf(p[0]);
					putRow(data.outputRowMeta, o);
				}
			}
		}

		return true;
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (FatoMeta) smi;
		data = (FatoData) sdi;

		return super.init(smi, sdi);
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (FatoMeta) smi;
		data = (FatoData) sdi;

		super.dispose(smi, sdi);
	}

	public void run() {
		try {
			while (processRow(meta, data) && !isStopped())
				;
		} catch (Exception e) {
			logError("Unexpected error :" + e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		} finally {
			dispose(meta, data);
			markStop();
		}
	}

	// create view meu

	private void createViewNovo() throws KettleException {
		String xml = String.valueOf(meta.getXml().getValueData());
		ValueMetaAndData novoSQL[][] = meta.getNovoSQL();
		ValueMetaAndData dimensions[][] = meta.getDimensions();
		ValueMetaAndData time_dimensions[][] = meta.getTime_dimensions();
		ValueMetaAndData measure[][] = meta.getMeasure();
		String campos, tabelas, tabelasAdicionais, condições, condiçõesAdicionais = new String();
		campos = "select \n";
		tabelas = "\n from ";
		tabelasAdicionais = "";
		condições = "\n where ";
		condiçõesAdicionais = "";
		int qntCampos = 0, qntTabelas = 0, qntTabelasAdicionais = 0, qntCondições = 0, qntCondiçõesAdicionais = 0;

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			Document docAnterior = db.parse(is);
			Element raiz = docAnterior.getDocumentElement();

			for (int i = 0; i < novoSQL.length; i++) {

				// campos
				if (qntCampos != 0) {
					if (novoSQL[i][0].getValueData() != null) {
						campos += "\n, " + novoSQL[i][0].getValueData();
						qntCampos++;
					}
				} else {
					if (novoSQL[i][0].getValueData() != null) {
						campos += novoSQL[i][0].getValueData();
						qntCampos++;
					}
				}

				// apelidos
				if (novoSQL[i][1].getValueData() != null) {
					// medida
					boolean medida = false;
					for (int j = 0; j < measure.length; j++) {
						logBasic("quantidade de medidas:" + measure.length);
						if (measure[j][0].getValueData() != null) {

							logBasic((String) novoSQL[i][1].getValueData()
									+ " é igual a "
									+ measure[j][1].getValueData() + " ?");
							if (measure[j][0].getValueData().equals(
									novoSQL[i][1].getValueData())) {
								logBasic("sim");
								medida = true;
								campos += " " + measure[j][1].getValueData();
								j = measure.length;
							}

						}
					}

					// tempo
					boolean tempo = false;
					if (!medida) {
						for (int j = 0; j < time_dimensions.length; j++) {
							if (time_dimensions[j][0].getValueData() != null) {
								if (time_dimensions[j][0].getValueData()
										.equals(novoSQL[i][1].getValueData())) {
									tempo = true;
									campos += " "
											+ time_dimensions[j][2]
													.getValueData();
									NodeList dimensões = raiz
											.getElementsByTagName("dimension");
									String tabela = "";
									for (int k = 0; k < dimensões.getLength(); k++) {
										if (((Element) dimensões.item(k))
												.getAttribute("name")
												.equals(time_dimensions[j][2]
														.getValueData())) {
											tabela = ((Element) dimensões
													.item(k))
													.getAttribute("main_table");
										}
									}

									// tabelas adicionais
									tabelasAdicionais += "\n";
									if (qntTabelasAdicionais > 0) {
										tabelasAdicionais += ",";
									}
									tabelasAdicionais += tabela;
									qntTabelasAdicionais++;

									// condições adicionais
									condiçõesAdicionais += "\n";
									if (qntCondiçõesAdicionais > 0)
										condiçõesAdicionais += "and ";
									condiçõesAdicionais += novoSQL[i][0]
											.getValueData()
											+ "="
											+ tabela
											+ "." + time_dimensions[j][1];
									qntCondiçõesAdicionais++;

									j = time_dimensions.length;
								}
							}

						}
					}
					if (!medida && !tempo) {
						campos += " " + novoSQL[i][1].getValueData();
					}
				}
				// tabelas
				if (qntTabelas != 0) {
					if (novoSQL[i][2].getValueData() != null) {
						tabelas += "\n, " + novoSQL[i][2].getValueData();
						qntTabelas++;
					}
				} else {
					if (novoSQL[i][2].getValueData() != null) {
						tabelas += "\n" + novoSQL[i][2].getValueData();
						qntTabelas++;
					}
				}

				// condições
				if (novoSQL[i][3].getValueData() != null) {
					qntCondições++;
					condições += " \n" + novoSQL[i][3].getValueData();
				}
			}
			String sql = campos + tabelas;
			if (qntTabelas > 0) {
				sql += ",";
			}
			sql += tabelasAdicionais;
			sql += condições;
			if (qntCondições > 0) {
				sql += " and";
			}
			sql += condiçõesAdicionais;
			Database database = new Database(meta.getDatabaseMeta());
			sql = "CREATE MATERIALIZED VIEW "
					+ String.valueOf(meta.getFactor().getValueData()) + " AS "
					+ sql;

			JOptionPane.showMessageDialog(null,
					"A seguinte query serÃ¡ executada:\n" + sql,
					Messages.getString("FatoDialog.Shell.Title"),
					JOptionPane.INFORMATION_MESSAGE);
			database.connect();
			database.execStatement(sql);
			logBasic(sql);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Ocorreu um erro:\n" + ex.toString(),
					Messages.getString("FatoDialog.Shell.Title"),
					JOptionPane.INFORMATION_MESSAGE);
			ex.printStackTrace();
		}
	}

	// create view meu

	private void createViewNovo2() throws KettleException {
		String xml = String.valueOf(meta.getXml().getValueData());
		String sql = String.valueOf(meta.getSql().getValueData());
		ValueMetaAndData dimensions[][] = meta.getDimensions();
		ValueMetaAndData time_dimensions[][] = meta.getTime_dimensions();
		ValueMetaAndData measure[][] = meta.getMeasure();
		Database db = new Database(meta.getDatabaseMeta());
		DocumentBuilder dbo;
		try {
			db.connect();
			RowMetaInterface apelidos = db.getQueryFields(sql, false);
			DocumentBuilder dbf = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			Document docAnterior = dbf.parse(is);
			Element raiz = docAnterior.getDocumentElement();
			for (int i = 0; i < apelidos.size(); i++) {
				ValueMetaInterface apelido = apelidos.getValueMeta(i);
				String tabela = "";
				// medida
				boolean medida = false;
				logBasic("quantidade de medidas:" + measure.length);
				for (int j = 0; j < measure.length; j++) {
					if (measure[j][0].getValueData() != null) {
						logBasic(apelido.getName() + " é igual a "
								+ measure[j][1].getValueData() + " ?");
						if (measure[j][0].getValueData().equals(
								apelido.getName())) {
							logBasic("sim medida");
							medida = true;
							sql = sql.replaceAll(apelido.getName(),
									(String) measure[j][1].getValueData());
							apelidos.getValueMeta(i).setName(
									(String) measure[j][1].getValueData());
							j = measure.length;
						}

					}
				}

				// tempo
				boolean tempo = false;
				if (!medida) {
					logBasic("quantidade de tempos:" + time_dimensions.length);
					for (int j = 0; j < time_dimensions.length; j++) {
						if (time_dimensions[j][0].getValueData() != null) {
							logBasic(apelido.getName() + " é igual a "
									+ time_dimensions[j][2].getValueData()
									+ " ?");
							if (time_dimensions[j][0].getValueData().equals(
									apelido.getName())) {
								tempo = true;
								logBasic("sim tempo");
								NodeList dimensões = raiz
										.getElementsByTagName("dimension");

								for (int k = 0; k < dimensões.getLength(); k++) {
									if (((Element) dimensões.item(k))
											.getAttribute("name").equals(
													time_dimensions[j][2]
															.getValueData())) {
										tabela = ((Element) dimensões.item(k))
												.getAttribute("main_table");
										k=dimensões.getLength();
									}
								}

								// tabelas adicionais
								String ultimoApelido = ((ValueMetaInterface) apelidos
										.getValueMeta(apelidos.size() - 1))
										.getName();
								int posicao = sql.indexOf(ultimoApelido)
										+ (ultimoApelido.length());
								String auxDiv = sql.substring(posicao);
								logBasic("div: " + auxDiv);
								String auxFrom[] = auxDiv.split(" from ");
								logBasic("tabela: " + tabela);
								auxFrom[0] += " from " + tabela + ", ";
								logBasic("parte 0: " + auxFrom[0]);
								String auxJuntando = auxFrom[0] + auxFrom[1];
								logBasic("juntando: " + auxFrom[0] + auxFrom[1]);
								logBasic("contem? "
										+ String.valueOf(sql.contains(auxDiv)));
								sql = sql.replace(auxDiv, auxJuntando);
								logBasic("sql Juntada: " + sql);
								// condições adicionais
								String auxWhere[] = sql.split(" where ");
								String campoReal = "";
								if (i > 0) {
									String apelidoPassado = ((ValueMetaInterface) apelidos
											.getValueMeta(i - 1)).getName();
									int k = sql.indexOf(apelidoPassado)
											+ apelidoPassado.length();
									logBasic("apelidoPassado: "
											+ apelidoPassado);
									logBasic("k começou em: "
											+ sql.substring(k, k + 10));
									for (; k < auxWhere[0].indexOf(apelido
											.getName()); k++) {
										campoReal += sql.charAt(k);

									}
								} else {
									for (int k = 0; k < auxWhere[0]
											.indexOf(apelido.getName()); k++) {
										campoReal += sql.charAt(k);
									}
								}
								logBasic("campo real: " + campoReal);
								campoReal = campoReal.substring(2);
								auxWhere[0] += " where " + campoReal + "="
										+ tabela + "." + time_dimensions[j][1];
								sql = auxWhere[0];
								if (auxWhere.length > 1) {
									sql += " and " + auxWhere[1] + "\n";
								}
								sql = sql.replaceAll(apelido.getName(), tabela);
								apelidos.getValueMeta(i).setName(tabela);
								j = time_dimensions.length;
							}
						}

					}
				}
				if (!medida && !tempo) {
					for (int j = 0; j < dimensions.length; j++) {
						if (dimensions[j][0].getValueData() != null) {
							logBasic(apelido.getName() + " é igual a "
									+ dimensions[j][1].getValueData()
									+ " ?");
							if (dimensions[j][0].getValueData().equals(
									apelido.getName())) {
								tempo = true;
								logBasic("sim dimensão");
								NodeList dimensões = raiz
										.getElementsByTagName("dimension");

								for (int k = 0; k < dimensões.getLength(); k++) {
									if (((Element) dimensões.item(k))
											.getAttribute("name").equals(
													dimensions[j][1]
															.getValueData())) {
										tabela = ((Element) dimensões.item(k))
												.getAttribute("main_table");
										k=dimensões.getLength();
										sql = sql.replaceAll(apelido.getName(), tabela);
										apelidos.getValueMeta(i).setName(tabela);
										j = dimensions.length;
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception ex) {

		}
		try {
			sql = "CREATE MATERIALIZED VIEW "
					+ String.valueOf(meta.getFactor().getValueData()) + " AS "
					+ sql;

			JOptionPane.showMessageDialog(null,
					"A seguinte query serÃ¡ executada:\n" + sql,
					Messages.getString("FatoDialog.Shell.Title"),
					JOptionPane.INFORMATION_MESSAGE);
			db.execStatement(sql);
			logBasic(sql);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Ocorreu um erro:\n" + ex.toString(),
					Messages.getString("FatoDialog.Shell.Title"),
					JOptionPane.INFORMATION_MESSAGE);
			ex.printStackTrace();
		}

	}

	// create view ellielson

	private void createView() throws KettleException {
		String sql = String.valueOf(meta.getSql().getValueData());
		String xml = String.valueOf(meta.getXml().getValueData());
		logBasic("SQL: " + sql);
		logBasic("XML: " + xml);
		ValueMetaAndData dimensions[][] = meta.getDimensions();
		ValueMetaAndData time_dimensions[][] = meta.getTime_dimensions();
		ValueMetaAndData measure[][] = meta.getMeasure();
		Database db = new Database(meta.getDatabaseMeta());
		DocumentBuilder dbo;
		try {
			db.connect();
			int f = 7;
			RowMetaInterface fields = db.getQueryFields(sql, false);
			for (int i = 0; i < fields.size(); i++) {
				ValueMetaInterface field = fields.getValueMeta(i);
				for (int j = 0; j < time_dimensions.length; j++) {
					if (String.valueOf(time_dimensions[j][0].getValueData())
							.equals(field.getName())) {
						StringBuilder sb = new StringBuilder(sql);
						String aux = sb.substring(f, (sql.indexOf(field
								.getName()) + field.getName().length()));
						aux = aux.replaceAll("\n,", "");
						sql = sql.replaceAll(Pattern.quote(aux),
								" " + field.getName());
						sql = sql + "\nAND" + aux;
						break;
					}
				}
				f = sql.indexOf(field.getName()) + field.getName().length();
			}
			f = 7;
			for (int i = 0; i < fields.size(); i++) {
				boolean status = true;
				ValueMetaInterface field = fields.getValueMeta(i);
				for (int j = 0; j < dimensions.length; j++) {
					if (status
							&& String.valueOf(dimensions[j][0].getValueData())
									.equals(field.getName())) {
						status = false;
						break;
					}
				}
				for (int j = 0; j < time_dimensions.length; j++) {
					if (status
							&& String.valueOf(
									time_dimensions[j][0].getValueData())
									.equals(field.getName())) {
						status = false;
						break;
					}
				}
				for (int j = 0; j < measure.length; j++) {
					if (status
							&& String.valueOf(measure[j][0].getValueData())
									.equals(field.getName())) {
						status = false;
						break;
					}
					if (status && (j + 1) == measure.length) {
						status = false;
						StringBuilder sb = new StringBuilder(sql);
						if (i != 0) {
							sql = sb.delete(
									f,
									(sql.indexOf(field.getName()) + field
											.getName().length())).toString();
						} else {
							sql = sb.delete(
									f,
									(sql.indexOf(field.getName()) + field
											.getName().length()) + 2)
									.toString();
						}
					}
				}
				f = sql.indexOf(field.getName()) + field.getName().length();
			}
			f = 7;
			for (int i = 0; i < fields.size(); i++) {
				int aux = 0;
				ValueMetaInterface field = fields.getValueMeta(i);
				for (int j = 0; j < measure.length; j++) {
					if (String.valueOf(measure[j][0].getValueData()).equals(
							field.getName())) {
						aux++;
						if (aux > 1) {
							StringBuilder sb = new StringBuilder(sql);
							String bux = sb.substring(f, (sql.indexOf(field
									.getName()) + field.getName().length()));
							sql = sql.replaceAll(Pattern.quote(bux), bux + bux);
						}
					}
				}
				f = sql.indexOf(field.getName()) + field.getName().length();
			}

			dbo = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			Document doc = dbo.parse(is);
			Element raiz = doc.getDocumentElement();
			NodeList dimension = raiz.getElementsByTagName("dimension");
			for (int i = 0; i < dimensions.length; i++) {
				if (!String.valueOf(dimensions[i][0].getValueData()).equals(
						"null")
						&& !String.valueOf(dimensions[i][1].getValueData())
								.equals("null")) {
					for (int j = 0; j < dimension.getLength(); j++) {
						Element dimen = (Element) dimension.item(j);
						if (dimen
								.getAttributeNode("name")
								.getNodeValue()
								.equals(String.valueOf(dimensions[i][1]
										.getValueData()))) {
							sql = sql.replaceAll(String
									.valueOf(dimensions[i][0].getValueData()),
									dimen.getAttributeNode("main_table")
											.getNodeValue());
						}
					}
				}
			}
			for (int i = 0; i < time_dimensions.length; i++) {
				if (!String.valueOf(time_dimensions[i][0].getValueData())
						.equals("null")
						&& !String
								.valueOf(time_dimensions[i][1].getValueData())
								.equals("null")
						&& !String
								.valueOf(time_dimensions[i][2].getValueData())
								.equals("null")) {
					for (int j = 0; j < dimension.getLength(); j++) {
						Element dimen = (Element) dimension.item(j);
						if (dimen
								.getAttributeNode("name")
								.getNodeValue()
								.equals(String.valueOf(time_dimensions[i][2]
										.getValueData()))) {
							sql = sql.replaceFirst(
									String.valueOf(time_dimensions[i][0]
											.getValueData()),
									dimen.getAttributeNode("main_table")
											.getNodeValue()
											+ ".key "
											+ dimen.getAttributeNode(
													"main_table")
													.getNodeValue());
							sql = sql
									.replaceFirst(
											String.valueOf(time_dimensions[i][0]
													.getValueData()),
											"= "
													+ dimen.getAttributeNode(
															"main_table")
															.getNodeValue()
													+ "."
													+ String.valueOf(time_dimensions[i][1]
															.getValueData()));
							sql = sql.replaceFirst("FROM ", "FROM "
									+ dimen.getAttributeNode("main_table")
											.getNodeValue() + ", ");
						}
					}
				}
			}
			for (int i = 0; i < measure.length; i++) {
				if (!String.valueOf(measure[i][0].getValueData())
						.equals("null")
						&& !String.valueOf(measure[i][1].getValueData())
								.equals("null")) {
					sql = sql.replaceFirst(
							String.valueOf(measure[i][0].getValueData()),
							String.valueOf(measure[i][1].getValueData()));
				}
			}
			sql = "CREATE MATERIALIZED VIEW "
					+ String.valueOf(meta.getFactor().getValueData()) + " AS "
					+ sql;

			JOptionPane.showMessageDialog(null,
					"A seguinte query serÃ¡ executada:\n" + sql,
					Messages.getString("FatoDialog.Shell.Title"),
					JOptionPane.INFORMATION_MESSAGE);
			db.connect();
			db.execStatement(sql);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Ocorreu um erro:\n" + e.toString(),
					Messages.getString("FatoDialog.Shell.Title"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
