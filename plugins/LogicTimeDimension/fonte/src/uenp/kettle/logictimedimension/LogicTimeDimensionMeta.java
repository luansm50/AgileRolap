package uenp.kettle.logictimedimension;

import java.util.ArrayList;
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

public class LogicTimeDimensionMeta extends BaseStepMeta implements
		StepMetaInterface {

	private ValueMetaAndData path;
	private ValueMetaAndData selectedDimension;
	private ValueMetaAndData nameDimension;
	private ValueMetaAndData visibleDimension;
	private ArrayList<Hierarchy> hierarchys;

	public ValueMetaAndData getNameDimension() {
		return nameDimension;
	}

	public void setNameDimension(ValueMetaAndData nameDimension) {
		this.nameDimension = nameDimension;
	}

	public ValueMetaAndData getVisibleDimension() {
		return visibleDimension;
	}

	public void setVisibleDimension(ValueMetaAndData visibleDimension) {
		this.visibleDimension = visibleDimension;
	}

	public ArrayList<Hierarchy> getHierarchys() {
		return hierarchys;
	}

	public void setHierarchys(ArrayList<Hierarchy> hierarchys) {
		this.hierarchys = hierarchys;
	}

	public ValueMetaAndData getSelectedDimension() {
		return selectedDimension;
	}

	public void setSelectedDimension(ValueMetaAndData selectedDimension) {
		this.selectedDimension = selectedDimension;
	}

	private ArrayList<String[]> dados;

	public LogicTimeDimensionMeta() {
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

	// método que envia dados
	public void getFields(RowMetaInterface r, String origin,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
		ValueMetaInterface v = new ValueMeta();
		String xml = "<logicTimeDimension>";

		// path

		xml += "<path>";
		xml += String.valueOf(this.path.getValueData());
		xml += "</path>";

		// selected dimension

		xml += "<selectedDimension>";
		xml += String.valueOf(this.selectedDimension.getValueData());
		xml += "</selectedDimension>";

		// name dimension

		xml += "<nameDimension>";
		xml += String.valueOf(this.nameDimension.getValueData());
		xml += "</nameDimension>";

		// visible dimension

		xml += "<visibleDimension>";
		xml += String.valueOf(this.visibleDimension.getValueData());
		xml += "</visibleDimension>";

		for (int i = 0; i < hierarchys.size(); i++) {
			xml += "<hierarchy>";

			// name hierarchy

			xml += "<nameHierarchy>";
			xml += String.valueOf(this.hierarchys.get(i).getName()
					.getValueData());
			xml += "</nameHierarchy>";

			// visible hierarchy

			xml += "<visibleHierarchy>";
			xml += String.valueOf(this.hierarchys.get(i).getVisible()
					.getValueData());
			xml += "</visibleHierarchy>";

			for (int j = 0; j < hierarchys.get(i).getLevels().size(); j++) {
				xml += "<level>";

				// name level

				xml += "<nameLevel>";
				xml += String.valueOf(this.hierarchys.get(i).getLevels().get(j)
						.getName().getValueData());
				xml += "</nameLevel>";

				// column level

				xml += "<columnLevel>";
				xml += String.valueOf(this.hierarchys.get(i).getLevels().get(j)
						.getColumn().getValueData());
				xml += "</columnLevel>";

				// type level

				xml += "<typeLevel>";
				xml += String.valueOf(this.hierarchys.get(i).getLevels().get(j)
						.getType().getValueData());
				xml += "</typeLevel>";

				// time level

				xml += "<timeLevel>";
				xml += String.valueOf(this.hierarchys.get(i).getLevels().get(j)
						.getTime().getValueData());
				xml += "</timeLevel>";

				// visible level

				xml += "<visibleLevel>";
				xml += String.valueOf(this.hierarchys.get(i).getLevels().get(j)
						.getVisible().getValueData());
				xml += "</visibleLevel>";
				xml += "</level>";
			}
			xml += "</hierarchy>";
		}
		xml += "</logicTimeDimension>";
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
		return new LogicTimeDimension(stepMeta, stepDataInterface, cnr,
				transMeta, disp);
	}

	@Override
	public StepDataInterface getStepData() {
		return new LogicTimeDimensionData();
	}

	// método que lê
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases,
			Map<String, Counter> counters) throws KettleXMLException {
		path = new ValueMetaAndData();
		selectedDimension = new ValueMetaAndData();
		hierarchys = new ArrayList<Hierarchy>();
		nameDimension = new ValueMetaAndData();
		visibleDimension = new ValueMetaAndData();
		Node node = XMLHandler.getSubNode(stepnode, "path", "value");
		if (node != null) {
			path.loadXML(node);
		}
		node = XMLHandler.getSubNode(stepnode, "selectedDimension", "value");
		if (node != null) {
			selectedDimension.loadXML(node);
		}
		node = XMLHandler.getSubNode(stepnode, "nameDimension", "value");
		if (node != null) {
			nameDimension.loadXML(node);
			logBasic("não foi nulo");
		} else
			logBasic("foi nulo");
		node = XMLHandler.getSubNode(stepnode, "visibleDimension", "value");
		if (node != null) {
			visibleDimension.loadXML(node);
		}
		Level l;
		Hierarchy h;
		node = XMLHandler.getSubNode(stepnode, "hierarchys");
		List<Node> hierarquias = XMLHandler.getNodes(node, "hierarchy");
		for (int i = 0; i < hierarquias.size(); i++) {
			h = new Hierarchy();
			h.getName().loadXML(
					XMLHandler.getSubNode(hierarquias.get(i), "name", "value"));
			h.getVisible().loadXML(
					XMLHandler.getSubNode(hierarquias.get(i), "visible",
							"value"));
			node = XMLHandler.getSubNode(hierarquias.get(i), "levels");
			List<Node> levels = XMLHandler.getNodes(node, "level");
			for (int j = 0; j < levels.size(); j++) {
				l = new Level();
				l.getName().loadXML(
						XMLHandler.getSubNode(levels.get(j), "name", "value"));
				l.getColumn()
						.loadXML(
								XMLHandler.getSubNode(levels.get(j), "column",
										"value"));
				l.getType().loadXML(
						XMLHandler.getSubNode(levels.get(j), "type", "value"));
				l.getTime().loadXML(
						XMLHandler.getSubNode(levels.get(j), "time", "value"));
				l.getVisible()
						.loadXML(
								XMLHandler.getSubNode(levels.get(j), "visible",
										"value"));
				h.getLevels().add(l);
			}
			hierarchys.add(h);
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
		selectedDimension = new ValueMetaAndData();
		nameDimension = new ValueMetaAndData();
		visibleDimension = new ValueMetaAndData();
		hierarchys = new ArrayList<Hierarchy>();
		setPath(new ValueMetaAndData(new ValueMeta("path",
				ValueMetaInterface.TYPE_STRING), new String("")));
		setSelectedDimension(new ValueMetaAndData(new ValueMeta(
				"selectedDimension", ValueMetaInterface.TYPE_STRING),
				new String("")));
		setNameDimension(new ValueMetaAndData(new ValueMeta("nameDimension",
				ValueMetaInterface.TYPE_STRING), new String("")));
		setVisibleDimension(new ValueMetaAndData(new ValueMeta(
				"visibleDimension", ValueMetaInterface.TYPE_STRING),
				new String("true")));
		Hierarchy h = new Hierarchy();
		Level l = new Level();
		h.setName(new ValueMetaAndData(new ValueMeta("Hierarchy.Name",
				ValueMetaInterface.TYPE_STRING), new String("Default0")));
		h.setVisible(new ValueMetaAndData(new ValueMeta("Hierarchy.Visible",
				ValueMetaInterface.TYPE_STRING), new String("true")));
		l.setName(new ValueMetaAndData(new ValueMeta("Level.Name",
				ValueMetaInterface.TYPE_STRING), new String("Level0")));
		l.setColumn(new ValueMetaAndData(new ValueMeta("Level.Column",
				ValueMetaInterface.TYPE_STRING), new String("")));
		l.setType(new ValueMetaAndData(new ValueMeta("Level.Type",
				ValueMetaInterface.TYPE_STRING), new String("String")));
		l.setTime(new ValueMetaAndData(new ValueMeta("Level.Time",
				ValueMetaInterface.TYPE_STRING), new String("TimeYears")));
		l.setVisible(new ValueMetaAndData(new ValueMeta("Level.Visible",
				ValueMetaInterface.TYPE_STRING), new String("true")));
		h.getLevels().add(l);
		hierarchys.add(h);

		logBasic("DEFAULT");
	}

	// método que salva
	public String getXML() throws KettleException {
		String xml = "";
		xml += Const.CR+"<path>";
		if (path.getValueData() != null) {
			xml += path.getXML();
		}
		xml += "</path>"+Const.CR;
		xml += "<selectedDimension>";
		if (selectedDimension.getValueData() != null) {
			xml += selectedDimension.getXML();
		}
		xml += "</selectedDimension>"+Const.CR;
		xml += "<nameDimension>";
		if (nameDimension.getValueData() != null) {
			xml += nameDimension.getXML();
		}
		xml += "</nameDimension>"+Const.CR;
		xml += "<visibleDimension>";
		if (visibleDimension.getValueData() != null) {
			xml += visibleDimension.getXML();
		}
		xml += "</visibleDimension>"+Const.CR;
		xml += "<hierarchys>";
		for (int i = 0; i < hierarchys.size(); i++) {
			xml += "<hierarchy>";
			xml += "<name>";
			if (hierarchys.get(i).getName().getValueData() != null) {
				xml += hierarchys.get(i).getName().getXML();
			}
			xml += "</name>"+Const.CR;
			xml += "<visible>";
			if (hierarchys.get(i).getVisible().getValueData() != null) {
				xml += hierarchys.get(i).getVisible().getXML();
			}
			xml += "</visible>"+Const.CR;
			xml += "<levels>";
			for (int j = 0; j < hierarchys.get(i).getLevels().size(); j++) {
				xml += "<level>";
				xml += "<name>";
				if (hierarchys.get(i).getLevels().get(j).getName()
						.getValueData() != null) {
					xml += hierarchys.get(i).getLevels().get(j).getName()
							.getXML();
				}
				xml += "</name>"+Const.CR;
				xml += "<column>";
				if (hierarchys.get(i).getLevels().get(j).getColumn()
						.getValueData() != null) {
					xml += hierarchys.get(i).getLevels().get(j).getColumn()
							.getXML();
				}
				xml += "</column>"+Const.CR;
				xml += "<type>";
				if (hierarchys.get(i).getLevels().get(j).getType()
						.getValueData() != null) {
					xml += hierarchys.get(i).getLevels().get(j).getType()
							.getXML();
				}
				xml += "</type>"+Const.CR;
				xml += "<time>";
				if (hierarchys.get(i).getLevels().get(j).getTime()
						.getValueData() != null) {
					xml += hierarchys.get(i).getLevels().get(j).getTime()
							.getXML();
				}
				xml += "</time>"+Const.CR;
				xml += "<visible>";
				if (hierarchys.get(i).getLevels().get(j).getVisible()
						.getValueData() != null) {
					xml += hierarchys.get(i).getLevels().get(j).getVisible()
							.getXML();
				}
				xml += "</visible>"+Const.CR;
				xml += "</level>"+Const.CR;
			}
			xml += "</levels>"+Const.CR;
			xml += "</hierarchy>"+Const.CR;
		}
		xml += "</hierarchys>"+Const.CR;
		logBasic("salvando: " + xml);
		return xml;
	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	public ValueMetaAndData getPath() {
		return path;
	}

	public void setPath(ValueMetaAndData path) {
		this.path = path;
	}

	public ArrayList<String[]> getDados() {
		return dados;
	}

	public void setDados(ArrayList<String[]> dados) {
		this.dados = dados;
	}

	public Object[] getDados(int indice) {
		return (Object[]) dados.get(indice);
	}
}
