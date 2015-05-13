package uenp.kettle.feriado;

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

public class FeriadoMeta extends BaseStepMeta implements StepMetaInterface{
	
	private ValueMetaAndData holiday[][];
	
	public FeriadoMeta(){
		super();
	}
	
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		CheckResult cr;
		if(prev == null || prev.size() == 0){
			cr = new CheckResult(CheckResult.TYPE_RESULT_WARNING, "Não esta recebendo quaisquer campos de etapas anteriores!", stepMeta);
			remarks.add(cr);
		}else{
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "O passo esta ligado ao anterior, recebendo campos!", stepMeta);
			remarks.add(cr);
		}
		
		if(input.length>0){
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "O passo esta recebendo informações de outros passos!", stepMeta);
			remarks.add(cr);
		}else{
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, "Sem informações recebidas de outros passos!", stepMeta);
			remarks.add(cr);
		}
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans disp) {
		return new Feriado(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	@Override
	public StepDataInterface getStepData() {
		return new FeriadoData();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		Node valNode = XMLHandler.getSubNode(stepnode, "holiday");
		holiday = new ValueMetaAndData[XMLHandler.countNodes(valNode, "day")][3];
		List<Node> lDay = XMLHandler.getNodes(valNode, "day");
		List<Node> lMonth = XMLHandler.getNodes(valNode, "month");
		List<Node> lDescription = XMLHandler.getNodes(valNode, "description");
		for(int i = 0; i < holiday.length; i++){
			holiday[i][0] = new ValueMetaAndData();
			holiday[i][1] = new ValueMetaAndData();
			holiday[i][2] = new ValueMetaAndData();
			holiday[i][0].loadXML(XMLHandler.getSubNode(lDay.get(i), "value"));
			holiday[i][1].loadXML(XMLHandler.getSubNode(lMonth.get(i), "value"));
			holiday[i][2].loadXML(XMLHandler.getSubNode(lDescription.get(i), "value"));
		}
	}
	
	public String getXML() throws KettleException{
		String retval = "";
		
		if(holiday != null){
			retval+="<holiday>"+Const.CR;
			for(int i = 0; i < holiday.length; i++){
				retval+="<day>"+Const.CR;
				retval+=holiday[i][0].getXML();
				retval+="</day>"+Const.CR;
				retval+="<month>"+Const.CR;
				retval+=holiday[i][1].getXML();
				retval+="</month>"+Const.CR;
				retval+="<description>"+Const.CR;
				retval+=holiday[i][2].getXML();
				retval+="</description>"+Const.CR;
			}
			retval+="</holiday>"+Const.CR;
		}
		
		return retval;
	}
	
	public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space){
		ValueMetaInterface v[] = new ValueMeta[3];
		v[0] = new ValueMeta();
		v[1] = new ValueMeta();
		v[2] = new ValueMeta();
		
		v[0].setName("day");
		v[1].setName("Month");
		v[2].setName("description");
		
		for(int i = 0; i < v.length; i++){
			v[i].setType(ValueMeta.TYPE_STRING);
			v[i].setTrimType(ValueMeta.TRIM_TYPE_BOTH);
			v[i].setOrigin(origin);
			r.addValueMeta(v[i]);
		}	
	}

	@Override
	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {}

	@Override
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {}

	@Override
	public void setDefault() {}

	public ValueMetaAndData[][] getHoliday() {
		return holiday;
	}

	public void setHoliday(ValueMetaAndData[][] holiday) {
		this.holiday = holiday;
	}

}
