package uenp.kettle.dimensao;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class DimensaoData extends BaseStepData implements StepDataInterface{
	public RowMetaInterface outputRowMeta;
	
	
	public DimensaoData(){
		super();
	}
}
