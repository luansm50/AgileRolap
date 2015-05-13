package uenp.kettle.XMLOut;

import java.util.ArrayList;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class XMLOutData extends BaseStepData implements StepDataInterface{
	public RowMetaInterface outputRowMeta;
	public ArrayList<String> dados = new ArrayList<String>();
	
	public XMLOutData(){
		super();
	}
}
