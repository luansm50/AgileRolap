package uenp.kettle.cube;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class CubeData extends BaseStepData implements StepDataInterface {
	
	public RowMetaInterface outputRowMeta;
	
	public CubeData(){
		super();
	}
}
