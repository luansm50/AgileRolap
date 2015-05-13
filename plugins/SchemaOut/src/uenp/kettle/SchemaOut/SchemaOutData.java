package uenp.kettle.SchemaOut;

import java.util.ArrayList;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class SchemaOutData extends BaseStepData implements StepDataInterface{
	public RowMetaInterface outputRowMeta;
	public ArrayList<String> dados = new ArrayList<String>();
	
	public SchemaOutData(){
		super();
	}
}
