package uenp.kettle.SchemaOut;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SchemaOut extends BaseStep implements StepInterface {

	private SchemaOutData data;
	private SchemaOutMeta meta;

	public SchemaOut(StepMeta s, StepDataInterface stepDataInterface, int c,
			TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		meta = (SchemaOutMeta) smi;
		data = (SchemaOutData) sdi;

		Object[] r = getRow();
		if (r != null) {
			logBasic("r!=null");
			for (int i = r.length - 1; i >= 0; i--) {
				if (r[i] != null) {
					logBasic("r[1]!=null");
					boolean status = true;
					for (int j = 0; j < data.dados.size(); j++) {
						if (r[i].equals(data.dados.get(j))) {
							status = false;
						}
					}
					if (status) {
						logBasic("status verdadeiro");
						StringBuilder str = new StringBuilder(r[i].toString());
						int aux = str.indexOf("</conection>");
						if (aux != -1) {
							str.delete(0, aux + 12);
						}
						data.dados.add(String.valueOf(str));
					}
				}
			}
		}

		if (r == null) {
			logBasic("r==null");
			gerarXML(data.dados, String.valueOf(meta.getUrl()),
					String.valueOf(meta.getSchemaName()));
			setOutputDone();
			return false;
		}

		return true;
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (SchemaOutMeta) smi;
		data = (SchemaOutData) sdi;

		return super.init(smi, sdi);
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (SchemaOutMeta) smi;
		data = (SchemaOutData) sdi;

		super.dispose(smi, sdi);
	}

	public void run() {
		try {
			while (processRow(meta, data) && !isStopped())
				;
		} catch (Exception e) {
			logError("Unexpected error:" + e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		} finally {
			dispose(meta, data);
			markStop();
		}
	}

	private void gerarXML(List<String> dados, String url, String schemaName) {
		String xml = "<?xml version='1.0' encoding='UTF-8'?>";
		xml += "<rolap>";
		for (int i = 0; i < dados.size(); i++) {
			xml += dados.get(i);
		}
		xml += "</rolap>";
		String schema = "";
		schema += "<Schema name='" + schemaName + "'>";
		File arquivoPath = new File(pegarPath(xml));// pegou path
		try {
			// lendo path
			DocumentBuilderFactory dbfPath = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dbPath = dbfPath.newDocumentBuilder();
			Document docPath = dbPath.parse(arquivoPath);
			Element raizPath = docPath.getDocumentElement();

			// lendo anterior
			DocumentBuilder dbAnterior = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource isAnterior = new InputSource();
			isAnterior.setCharacterStream(new StringReader(xml));
			Document docAnterior = dbAnterior.parse(isAnterior);
			Element raizAnterior = docAnterior.getDocumentElement();

			// construindo dimension
			NodeList logicsDimensionsAnteriores = raizAnterior
					.getElementsByTagName("logicDimension");
			NodeList dimensionsPath = raizPath
					.getElementsByTagName("dimension");
			logBasic("quantidade LogicDimensions: "
					+ logicsDimensionsAnteriores.getLength());
			for (int i = 0; i < logicsDimensionsAnteriores.getLength(); i++) {
				Element logicDimensionAnterior = (Element) logicsDimensionsAnteriores
						.item(i);
				NodeList visiblesDimensionsAnteriores = logicDimensionAnterior
						.getElementsByTagName("visibleDimension");
				NodeList namesDimensionsAnteriores = logicDimensionAnterior
						.getElementsByTagName("nameDimension");

				schema += "<Dimension type='StandardDimension' ";
				schema += "visible='"
						+ visiblesDimensionsAnteriores.item(0).getTextContent()
						+ "' ";
				schema += "highCardinality='false' ";
				schema += "name='"
						+ namesDimensionsAnteriores.item(0).getTextContent()
						+ "'>";

				// pegando hierarquias
				NodeList hierarchysAnteriores = logicDimensionAnterior
						.getElementsByTagName("hierarchy");
				for (int j = 0; j < hierarchysAnteriores.getLength(); j++) {
					Element hierarchyAnterior = (Element) hierarchysAnteriores
							.item(j);
					NodeList visiblesHierarchysAnteriores = hierarchyAnterior
							.getElementsByTagName("visibleHierarchy");
					NodeList namesHierarchysAnteriores = hierarchyAnterior
							.getElementsByTagName("nameHierarchy");
					NodeList selectedsDimensionsAnteriores = logicDimensionAnterior
							.getElementsByTagName("selectedDimension");
					Element dimensionPath = null;
					// selected dimension==dimension path
					for (int k = 0; k < dimensionsPath.getLength(); k++) {
						dimensionPath = (Element) dimensionsPath.item(k);
						if (dimensionPath.getAttribute("name").equals(
								logicDimensionAnterior
										.getElementsByTagName(
												"selectedDimension").item(0)
										.getTextContent())) {
							k = dimensionsPath.getLength();
						}
					}
					NodeList tablesPath = (NodeList) dimensionPath
							.getElementsByTagName("table");
					schema += "<Hierarchy ";
					schema += "visible='"
							+ visiblesHierarchysAnteriores.item(0)
									.getTextContent() + "' ";
					schema += "hasAll='true' ";
					schema += "primaryKey='"
							+ ((Element) tablesPath.item(0))
									.getAttribute("primary_key") + "' ";
					if (tablesPath.getLength() > 1) {
						schema += "primaryKeyTable='"
								+ ((Element) tablesPath.item(0))
										.getAttribute("name") + "' ";
					}
					schema += ">";
					if (tablesPath.getLength() > 1) {
						for (int k = 0; k < tablesPath.getLength() - 1; k++) {
							if (k != tablesPath.getLength() - 2) {
								schema += "<Join leftKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("father_key") + "' ";
								schema += "rightKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("primary_key") + "'>";
								schema += "<Table name='";
								schema += ((Element) tablesPath.item(k))
										.getAttribute("name") + "'>";
								schema += "</Table>";
							} else {
								schema += "<Join leftKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("father_key") + "' ";
								schema += "rightKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("primary_key") + "'>";
								schema += "<Table name='";
								schema += ((Element) tablesPath.item(k))
										.getAttribute("name") + "'>";
								schema += "</Table>";
								schema += "<Table name='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("name") + "'>";
								schema += "</Table>";
							}
						}
						for (int k = 0; k < tablesPath.getLength() - 1; k++) {
							schema += "</Join>";
						}
					} else {
						schema += "<Table name='";
						schema += ((Element) tablesPath.item(0))
								.getAttribute("name") + "'>";
						schema += "</Table>";
					}

					// pegando levels
					NodeList levelsAnteriores = hierarchyAnterior
							.getElementsByTagName("level");
					for (int k = 0; k < levelsAnteriores.getLength(); k++) {
						schema += "<level name='";
						schema += ((Element) levelsAnteriores.item(k))
								.getElementsByTagName("nameLevel").item(0)
								.getTextContent()
								+ "' ";

						// separando table e coluna
						String table_column[] = ((Element) levelsAnteriores
								.item(k)).getElementsByTagName("columnLevel")
								.item(0).getTextContent().split(" - ");
						schema += "visible='";
						schema += ((Element) levelsAnteriores.item(k))
								.getElementsByTagName("visibleLevel").item(0)
								.getTextContent()
								+ "' ";
						schema += "table='";
						schema += table_column[0] + "' ";
						schema += "column='";
						schema += table_column[1] + "' ";
						schema += "type='";
						schema += ((Element) levelsAnteriores.item(k))
								.getElementsByTagName("typeLevel").item(0)
								.getTextContent()
								+ "' ";
						schema += "uniqueMembers='false' ";
						schema += "levelType='Regular' ";
						schema += "hideMemberIf='Never'>";
						schema += "</level>";
					}

					schema += "</Hierarchy>";
				}
				schema += "</Dimension>";
			}

			// construindo time dimension
			NodeList logicsTimesDimensionsAnteriores = raizAnterior
					.getElementsByTagName("logicTimeDimension");
			dimensionsPath = raizPath.getElementsByTagName("dimension");
			logBasic("quantidade LogicTimeDimensions: "
					+ logicsTimesDimensionsAnteriores.getLength());
			for (int i = 0; i < logicsTimesDimensionsAnteriores.getLength(); i++) {
				Element logicDimensionAnterior = (Element) logicsTimesDimensionsAnteriores
						.item(i);
				NodeList visiblesDimensionsAnteriores = logicDimensionAnterior
						.getElementsByTagName("visibleDimension");
				NodeList namesDimensionsAnteriores = logicDimensionAnterior
						.getElementsByTagName("nameDimension");

				schema += "<Dimension type='TimeDimension' ";
				schema += "visible='"
						+ visiblesDimensionsAnteriores.item(0).getTextContent()
						+ "' ";
				schema += "highCardinality='false' ";
				schema += "name='"
						+ namesDimensionsAnteriores.item(0).getTextContent()
						+ "'>";

				// pegando hierarquias
				NodeList hierarchysAnteriores = logicDimensionAnterior
						.getElementsByTagName("hierarchy");
				for (int j = 0; j < hierarchysAnteriores.getLength(); j++) {
					Element hierarchyAnterior = (Element) hierarchysAnteriores
							.item(j);
					NodeList visiblesHierarchysAnteriores = hierarchyAnterior
							.getElementsByTagName("visibleHierarchy");
					NodeList namesHierarchysAnteriores = hierarchyAnterior
							.getElementsByTagName("nameHierarchy");
					NodeList selectedsDimensionsAnteriores = logicDimensionAnterior
							.getElementsByTagName("selectedDimension");
					Element dimensionPath = null;
					// selected dimension==dimension path
					for (int k = 0; k < dimensionsPath.getLength(); k++) {
						dimensionPath = (Element) dimensionsPath.item(k);
						if (dimensionPath.getAttribute("name").equals(
								logicDimensionAnterior
										.getElementsByTagName(
												"selectedDimension").item(0)
										.getTextContent())) {
							k = dimensionsPath.getLength();
						}
					}
					NodeList tablesPath = (NodeList) dimensionPath
							.getElementsByTagName("table");
					schema += "<Hierarchy ";
					schema += "visible='"
							+ visiblesHierarchysAnteriores.item(0)
									.getTextContent() + "' ";
					schema += "hasAll='true' ";
					schema += "primaryKey='"
							+ ((Element) tablesPath.item(0))
									.getAttribute("primary_key") + "' ";
					if (tablesPath.getLength() > 1) {
						schema += "primaryKeyTable='"
								+ ((Element) tablesPath.item(0))
										.getAttribute("name") + "' ";
					}
					schema += ">";
					if (tablesPath.getLength() > 1) {
						for (int k = 0; k < tablesPath.getLength() - 1; k++) {
							if (k != tablesPath.getLength() - 2) {
								schema += "<Join leftKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("father_key") + "' ";
								schema += "rightKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("primary_key") + "'>";
								schema += "<Table name='";
								schema += ((Element) tablesPath.item(k))
										.getAttribute("name") + "'>";
								schema += "</Table>";
							} else {
								schema += "<Join leftKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("father_key") + "' ";
								schema += "rightKey='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("primary_key") + "'>";
								schema += "<Table name='";
								schema += ((Element) tablesPath.item(k))
										.getAttribute("name") + "'>";
								schema += "</Table>";
								schema += "<Table name='";
								schema += ((Element) tablesPath.item(k + 1))
										.getAttribute("name") + "'>";
								schema += "</Table>";
							}
						}
						for (int k = 0; k < tablesPath.getLength() - 1; k++) {
							schema += "</Join>";
						}
					} else {
						schema += "<Table name='";
						schema += ((Element) tablesPath.item(0))
								.getAttribute("name") + "'>";
						schema += "</Table>";
					}

					// pegando levels
					NodeList levelsAnteriores = hierarchyAnterior
							.getElementsByTagName("level");
					for (int k = 0; k < levelsAnteriores.getLength(); k++) {
						schema += "<level name='";
						schema += ((Element) levelsAnteriores.item(k))
								.getElementsByTagName("nameLevel").item(0)
								.getTextContent()
								+ "' ";

						// separando table e coluna
						String table_column[] = ((Element) levelsAnteriores
								.item(k)).getElementsByTagName("columnLevel")
								.item(0).getTextContent().split(" - ");
						schema += "visible='";
						schema += ((Element) levelsAnteriores.item(k))
								.getElementsByTagName("visibleLevel").item(0)
								.getTextContent()
								+ "' ";
						schema += "table='";
						schema += table_column[0] + "' ";
						schema += "column='";
						schema += table_column[1] + "' ";
						schema += "type='";
						schema += ((Element) levelsAnteriores.item(k))
								.getElementsByTagName("typeLevel").item(0)
								.getTextContent()
								+ "' ";
						schema += "uniqueMembers='false' ";
						schema += "levelType='";
						schema += ((Element) levelsAnteriores.item(k))
								.getElementsByTagName("timeLevel").item(0)
								.getTextContent()
								+ "' ";
						schema += "hideMemberIf='Never'>";
						schema += "</level>";
					}

					schema += "</Hierarchy>";
				}
				schema += "</Dimension>";
			}

			// construindo cube
			NodeList cubesAnteriores = raizAnterior
					.getElementsByTagName("cube");
			NodeList cubesPath = raizPath.getElementsByTagName("fact");
			logBasic("quantos cubes: " + cubesAnteriores.getLength());
			for (int i = 0; i < cubesAnteriores.getLength(); i++) {
				schema += "<Cube name='";
				schema += ((Element) cubesAnteriores.item(i))
						.getElementsByTagName("cubeName").item(0)
						.getTextContent()
						+ "' ";
				schema += "visible='";
				schema += ((Element) cubesAnteriores.item(i))
						.getElementsByTagName("cubeVisible").item(0)
						.getTextContent()
						+ "' ";
				schema += "cache='true' enabled='true'>";

				// pegando cubePath
				Element cubePath = null;
				for (int j = 0; j < cubesPath.getLength(); j++) {
					cubePath = (Element) cubesPath.item(j);
					if (cubePath.getAttribute("table").equals(
							((Element) cubesAnteriores.item(i))
									.getElementsByTagName("cubeTable").item(0)
									.getTextContent())) {
						j = cubesPath.getLength();
					}
				}
				schema += "<Table name='";
				schema += cubePath.getAttribute("table") + "'>";
				schema += "</Table>";
				for (int j = 0; j < ((Element) cubesAnteriores.item(i))
						.getElementsByTagName("dimensionUsageName").getLength(); j++) {
					schema += "<DimensionUsage source='";
					schema += ((Element) cubesAnteriores.item(i))
							.getElementsByTagName("dimensionUsageSource")
							.item(j).getTextContent()
							+ "' ";
					schema += "name='";
					schema += ((Element) cubesAnteriores.item(i))
							.getElementsByTagName("dimensionUsageName").item(j)
							.getTextContent()
							+ "' ";
					schema += "visible='";
					schema += "true' " + "foreignKey='";
					NodeList dimensionsUsagesPath = cubePath
							.getElementsByTagName("dimension_usage");
					// pegando dimensionUsageAnterior==logicDimensionAnterior
					boolean correto = false;
					Element logicDimensionAnterior = null;
					for (int l = 0; l < logicsTimesDimensionsAnteriores
							.getLength(); l++) {
						logicDimensionAnterior = (Element) logicsTimesDimensionsAnteriores
								.item(l);
						if (logicDimensionAnterior
								.getElementsByTagName("nameDimension")
								.item(0)
								.getTextContent()
								.equals(((Element) cubesAnteriores.item(i))
										.getElementsByTagName(
												"dimensionUsageSource").item(j)
										.getTextContent())) {
							l = logicsTimesDimensionsAnteriores.getLength();
							correto = true;
						}

					}
					if (!correto) {
						for (int l = 0; l < logicsDimensionsAnteriores
								.getLength(); l++) {
							logicDimensionAnterior = (Element) logicsDimensionsAnteriores
									.item(l);
							if (logicDimensionAnterior
									.getElementsByTagName("nameDimension")
									.item(0)
									.getTextContent()
									.equals(((Element) cubesAnteriores.item(i))
											.getElementsByTagName(
													"dimensionUsageSource")
											.item(j).getTextContent())) {
								l = logicsDimensionsAnteriores.getLength();
							}

						}
					}

					// pegando a dimensionUsagePath ==
					// dimensionUsageSourceAnterior
					Element dimensionUsagePath = null;
					for (int l = 0; l < dimensionsUsagesPath.getLength(); l++) {
						dimensionUsagePath = (Element) dimensionsUsagesPath
								.item(l);
						if (dimensionUsagePath.getTextContent().equals(
								logicDimensionAnterior
										.getElementsByTagName(
												"selectedDimension").item(0)
										.getTextContent())) {
							l = dimensionsUsagesPath.getLength();
						}
					}
					schema += dimensionUsagePath.getAttribute("column") + "' ";
					schema += "highCardinality='false'>";
					schema += "</DimensionUsage>";
				}
				// construindo measures
				NodeList measuresAnteriores = ((Element) cubesAnteriores
						.item(i)).getElementsByTagName("measure");
				logBasic("quantidade measures: "
						+ measuresAnteriores.getLength());
				for (int j = 0; j < measuresAnteriores.getLength(); j++) {
					schema += "<Measure name='";
					schema += ((Element) measuresAnteriores.item(j))
							.getElementsByTagName("measureName").item(0)
							.getTextContent()
							+ "' ";
					schema += "column='";
					schema += ((Element) measuresAnteriores.item(j))
							.getElementsByTagName("measureColumn").item(0)
							.getTextContent()
							+ "' ";
					schema += "aggregator='";
					schema += ((Element) measuresAnteriores.item(j))
							.getElementsByTagName("measureAggregator").item(0)
							.getTextContent()
							+ "' ";
					schema += "visible='";
					schema += ((Element) measuresAnteriores.item(j))
							.getElementsByTagName("measureVisible").item(0)
							.getTextContent()
							+ "'>";
					schema += "</Measure>";
				}
				schema += "</Cube>";
			}
			schema += "</Schema>";
			logBasic(schema);
		} catch (Exception e) {
			logBasic("deu ruim");
			e.printStackTrace();
		}

		if (!url.contains(".xml"))
			url += ".xml";
		try {
			File saida = new File(url);
			FileOutputStream fos = new FileOutputStream(saida);
			fos.write(schema.getBytes());
			fos.close();
		} catch (Exception e) {
			logBasic("Error on save file");
		}
	}

	public String pegarPath(String xml) {
		try {
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
				Document doc = db.parse(is);

				Element raiz = doc.getDocumentElement();
				Element path;

				NodeList nodes = raiz.getElementsByTagName("path");
				path = (Element) nodes.item(0);
				return path.getTextContent();

			} catch (Exception e) {
				logBasic("nao achou o path");
			}
		} catch (Exception e) {

		}
		return "falhou";
	}
}
