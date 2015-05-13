package uenp.kettle.XMLIn;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class XMLInData extends BaseStepData implements StepDataInterface {
	public RowMetaInterface outputRowMeta;
	
	public XMLInData(){
		super();
	}
}
