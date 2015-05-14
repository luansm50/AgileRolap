package uenp.kettle.fdw;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class Fdw extends BaseStep implements StepInterface{
	private FdwData data;
	private FdwMeta meta;
	
	public Fdw(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis){
		super(s, stepDataInterface, c, t, dis);
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException{
		meta = (FdwMeta)smi;
		data = (FdwData)sdi;
		String p[] = null;
		
		if(first){
			first = false;
			data.outputRowMeta = (RowMetaInterface) new RowMeta();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, null);
			p = data.outputRowMeta.getFieldNames();
			data.outputRowMeta = (RowMetaInterface) new RowMeta();
			ValueMetaInterface v = new ValueMeta();
			v.setName("xml");
			v.setType(ValueMeta.TYPE_STRING);
			v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
			v.setOrigin(getStepname());
			data.outputRowMeta.addValueMeta(v);
		}
		Object o[] = new Object[1];
		
		for(int i = 0; i < p.length; i++){
			o[0] = String.valueOf(p[0]);
			putRow(data.outputRowMeta, o);
		}
		setOutputDone();
		return false;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi){
		meta = (FdwMeta)smi;
		data = (FdwData)sdi;
		
		return super.init(smi, sdi);
	}
	
	public void dispose(StepMetaInterface smi, StepDataInterface sdi){
		meta = (FdwMeta)smi;
		data = (FdwData)sdi;
		
		super.dispose(smi, sdi);
	}
	
	public void run(){
		try{
			while(processRow(meta, data) && !isStopped());
		}catch(Exception e){
			logError("Unexpected error: "+e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		}finally{
			dispose(meta, data);
			logBasic("Final");
			markStop();
		}
	}

}
