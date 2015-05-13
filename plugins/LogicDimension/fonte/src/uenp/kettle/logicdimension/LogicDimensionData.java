package uenp.kettle.logicdimension;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class LogicDimensionData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;
	
	public LogicDimensionData(){
		super();
	}
}
