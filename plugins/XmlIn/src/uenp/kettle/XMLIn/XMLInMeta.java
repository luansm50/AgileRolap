package uenp.kettle.XMLIn;

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


public class XMLInMeta extends BaseStepMeta implements StepMetaInterface{
	
	private ValueMetaAndData url;
	private ArrayList<String[]> dados;

	
	public XMLInMeta(){
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
		return new XMLIn(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	@Override
	public StepDataInterface getStepData() {
		return new XMLInData();
	}

	public String getXML() throws KettleException{
		String retval = "";
		retval += "<url>"+Const.CR;
		if(url!=null)retval += url.getXML();
		retval += "</url>"+Const.CR;
		return retval;
	}
	
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		url = new ValueMetaAndData();
		Node valnode = XMLHandler.getSubNode(stepnode, "url", "value");
		if(valnode != null){
			url.loadXML(valnode);
		}	
	}

	@Override
	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {}

	@Override
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {}

	@Override
	public void setDefault() {
		url = new ValueMetaAndData(new ValueMeta("url", ValueMetaInterface.TYPE_STRING), new String(""));
	}

	public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space){
		ValueMetaInterface v[] = new ValueMetaInterface[7];
		for(int i = 0; i < v.length; i++){
			v[i] = new ValueMeta();
		}
		v[0].setName("NameDimension");
		v[1].setName("MainTable");
		v[2].setName("Table");
		v[3].setName("PrimaryKey");
		v[4].setName("FatherTable");
		v[5].setName("FatherKey");
		v[6].setName("Atribute");
		for(int i = 0; i < v.length; i++){
			v[i].setType(ValueMeta.TYPE_STRING);
			v[i].setTrimType(ValueMeta.TRIM_TYPE_BOTH);
			v[i].setOrigin(origin);
			r.addValueMeta(v[i]);
		}
	}
	
	public Object[] getDados(int indice){
		return (Object[]) dados.get(indice);
	}
	
	public ValueMetaAndData getUrl() {
		return url;
	}

	public void setUrl(ValueMetaAndData url) {
		this.url = url;
	}

	public ArrayList<String[]> getDados() {
		return dados;
	}

	public void setDados(ArrayList<String[]> dados) {
		this.dados = dados;
	}

	

}
