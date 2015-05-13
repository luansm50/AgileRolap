package uenp.kettle.cube;

import java.util.List;
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
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

public class CubeMeta extends BaseStepMeta implements StepMetaInterface {

	private ValueMetaAndData path;
	private ValueMetaAndData cubeName;
	private ValueMetaAndData cubeTable;
	private ValueMetaAndData cubeVisible;
	private ValueMetaAndData dimensionsUsages[][];
	private ValueMetaAndData measures[][];

	public CubeMeta() {
		super();
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transmeta,
			StepMeta stepMeta, RowMetaInterface prev, String[] input,
			String[] output, RowMetaInterface info) {
		CheckResult cr;
		if (prev == null || prev.size() == 0) {
			cr = new CheckResult(CheckResult.TYPE_RESULT_WARNING, "warning",
					stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "Ok", stepMeta);
			remarks.add(cr);
		}
		if (input.length > 0) {
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "OK", stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, "Erro",
					stepMeta);
			remarks.add(cr);
		}

	}

	public void getFields(RowMetaInterface r, String origin,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		ValueMetaInterface v = new ValueMeta();
		String xml = "";
		xml += "<cube>";

		// cube name
		xml += "<cubeName>";
		if (getCubeName().getValueData() != null) {
			xml += String.valueOf(getCubeName().getValueData());
		}
		xml += "</cubeName>";

		// cube table
		xml += "<cubeTable>";
		if (getCubeTable().getValueData() != null) {
			xml += String.valueOf(getCubeTable().getValueData());
		}
		xml += "</cubeTable>";

		// cube visible
		xml += "<cubeVisible>";
		if (getCubeVisible().getValueData() != null) {
			xml += String.valueOf(getCubeVisible().getValueData());
		}
		xml += "</cubeVisible>";
		for (int i = 0; i < getDimensionsUsages().length; i++) {
			// dimension usage name
			if (getDimensionsUsages()[i][0].getValueData() != null) {
				xml += "<dimensionUsageName>";
				xml += String.valueOf(getDimensionsUsages()[i][0]
						.getValueData());
				xml += "</dimensionUsageName>";
			}

			// dimension usage source
			if (getDimensionsUsages()[i][1].getValueData() != null) {
				xml += "<dimensionUsageSource>";
				xml += String.valueOf(getDimensionsUsages()[i][1]
						.getValueData());
				xml += "</dimensionUsageSource>";
			}

		}

		for (int i = 0; i < getMeasures().length; i++) {
			if(getMeasures()[i][0].getValueData()!=null && getMeasures()[i][1].getValueData()!=null && getMeasures()[i][2].getValueData()!=null && getMeasures()[i][3].getValueData()!=null){
				xml += "<measure>";
			// measure name
			xml += "<measureName>";
			xml += String.valueOf(getMeasures()[i][0].getValueData());
			xml += "</measureName>";

			// measure aggregator
			xml += "<measureAggregator>";
			xml += String.valueOf(getMeasures()[i][1].getValueData());
			xml += "</measureAggregator>";

			// measure column
			xml += "<measureColumn>";
			xml += String.valueOf(getMeasures()[i][2].getValueData());
			xml += "</measureColumn>";

			// measure visible
			xml += "<measureVisible>";
			xml += String.valueOf(getMeasures()[i][3].getValueData());
			xml += "</measureVisible>";
			xml += "</measure>";
			}
		}
		xml += "</cube>";
		logBasic(xml);
		v.setName(xml);
		v.setType(ValueMeta.TYPE_STRING);
		v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
		v.setOrigin(origin);
		r.addValueMeta(v);
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans disp) {
		return new Cube(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	@Override
	public StepDataInterface getStepData() {
		return new CubeData();
	}

	@Override
	// método que lê
	public void loadXML(Node stepnode, List<DatabaseMeta> databases,
			Map<String, Counter> counters) throws KettleXMLException {
		path = new ValueMetaAndData();
		Node node = XMLHandler.getSubNode(stepnode, "path", "value");
		if (node != null) {
			path.loadXML(node);
		}
		Node valnode = XMLHandler.getSubNode(stepnode, "cubeName", "value");
		if (valnode != null) {
			cubeName = new ValueMetaAndData();
			cubeName.loadXML(valnode);
		}
		valnode = XMLHandler.getSubNode(stepnode, "cubeTable", "value");
		if (valnode != null) {
			cubeTable = new ValueMetaAndData();
			cubeTable.loadXML(valnode);
		}
		valnode = XMLHandler.getSubNode(stepnode, "cubeVisible", "value");
		if (valnode != null) {
			cubeVisible = new ValueMetaAndData();
			cubeVisible.loadXML(valnode);
		}
		valnode = XMLHandler.getSubNode(stepnode, "dimensionsUsages");
		dimensionsUsages = new ValueMetaAndData[XMLHandler.countNodes(valnode,
				"name")][2];
		List<Node> name = XMLHandler.getNodes(valnode, "name");
		List<Node> source = XMLHandler.getNodes(valnode, "source");
		for (int i = 0; i < dimensionsUsages.length; i++) {
			dimensionsUsages[i][0] = new ValueMetaAndData();
			dimensionsUsages[i][1] = new ValueMetaAndData();
			dimensionsUsages[i][0].loadXML(XMLHandler.getSubNode(name.get(i),
					"value"));
			dimensionsUsages[i][1].loadXML(XMLHandler.getSubNode(source.get(i),
					"value"));
		}
		valnode = XMLHandler.getSubNode(stepnode, "measures");
		measures = new ValueMetaAndData[XMLHandler.countNodes(valnode, "name")][4];
		name = XMLHandler.getNodes(valnode, "name");
		List<Node> aggregator = XMLHandler.getNodes(valnode, "aggregator");
		List<Node> column = XMLHandler.getNodes(valnode, "column");
		List<Node> visible = XMLHandler.getNodes(valnode, "visible");
		for (int i = 0; i < measures.length; i++) {
			measures[i][0] = new ValueMetaAndData();
			measures[i][1] = new ValueMetaAndData();
			measures[i][2] = new ValueMetaAndData();
			measures[i][3] = new ValueMetaAndData();
			measures[i][0].loadXML(XMLHandler.getSubNode(name.get(i), "value"));
			measures[i][1].loadXML(XMLHandler.getSubNode(aggregator.get(i),
					"value"));
			measures[i][2]
					.loadXML(XMLHandler.getSubNode(column.get(i), "value"));
			measures[i][3].loadXML(XMLHandler.getSubNode(visible.get(i),
					"value"));

		}
	}

	@Override
	public void readRep(Repository arg0, ObjectId arg1,
			List<DatabaseMeta> arg2, Map<String, Counter> arg3)
			throws KettleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveRep(Repository arg0, ObjectId arg1, ObjectId arg2)
			throws KettleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefault() {
		path = new ValueMetaAndData();
	    cubeName= new ValueMetaAndData();
	    cubeTable= new ValueMetaAndData();
	    cubeVisible = new ValueMetaAndData();
		setPath(new ValueMetaAndData(new ValueMeta("path",
				ValueMetaInterface.TYPE_STRING), new String("")));
		setCubeName(new ValueMetaAndData(new ValueMeta("cubeName",
				ValueMetaInterface.TYPE_STRING), new String("")));
		setCubeTable(new ValueMetaAndData(new ValueMeta("cubeTable",
				ValueMetaInterface.TYPE_STRING), new String("")));
		setCubeVisible(new ValueMetaAndData(new ValueMeta("cubeVisible",
				ValueMetaInterface.TYPE_STRING), new String("true")));
		
	}

	// método que salva
	public String getXML() throws KettleException {
		String xml = "";
		xml += "<path>";
		if (path != null) {
			xml += path.getXML();
		}
		xml += "</path>";
		xml += "<cubeName>";
		if (cubeName != null) {
			xml += cubeName.getXML();
		}
		xml += "</cubeName>";
		xml += "<cubeTable>";
		if (cubeTable != null) {
			xml += cubeTable.getXML();
		}
		xml += "</cubeTable>";
		xml += "<cubeVisible>";
		if (cubeVisible != null) {
			xml += cubeVisible.getXML();
		}
		xml += "</cubeVisible>";
		xml += "<dimensionsUsages>";
		if (dimensionsUsages != null) {
			for (int i = 0; i < dimensionsUsages.length; i++) {

				xml += "<name>";
				xml += dimensionsUsages[i][0].getXML();
				xml += "</name>";
				xml += "<source>";
				xml += dimensionsUsages[i][1].getXML();
				xml += "</source>";

			}
		}
		xml += "</dimensionsUsages>";
		xml += "<measures>";
		if (measures != null) {
			for (int i = 0; i < measures.length; i++) {
				xml += "<name>";
				xml += measures[i][0].getXML();
				xml += "</name>";
				xml += "<aggregator>";
				xml += measures[i][1].getXML();
				xml += "</aggregator>";
				xml += "<column>";
				xml += measures[i][2].getXML();
				xml += "</column>";
				xml += "<visible>";
				xml += measures[i][3].getXML();
				xml += "</visible>";
			}
		}
		xml += "</measures>";
		return xml;
	}

	public ValueMetaAndData getCubeName() {
		return cubeName;
	}

	public void setCubeName(ValueMetaAndData cubeName) {
		this.cubeName = cubeName;
	}

	public ValueMetaAndData getCubeTable() {
		return cubeTable;
	}

	public void setCubeTable(ValueMetaAndData cubeTable) {
		this.cubeTable = cubeTable;
	}

	public ValueMetaAndData getCubeVisible() {
		return cubeVisible;
	}

	public void setCubeVisible(ValueMetaAndData cubeVisible) {
		this.cubeVisible = cubeVisible;
	}

	public ValueMetaAndData[][] getDimensionsUsages() {
		return dimensionsUsages;
	}

	public void setDimensionsUsages(ValueMetaAndData[][] dimensionsUsages) {
		this.dimensionsUsages = dimensionsUsages;
	}

	public ValueMetaAndData getPath() {
		return path;
	}

	public void setPath(ValueMetaAndData path) {
		this.path = path;
	}

	public ValueMetaAndData[][] getMeasures() {
		return measures;
	}

	public void setMeasures(ValueMetaAndData[][] measures) {
		this.measures = measures;
	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

}
