package uenp.kettle.feriado;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class Feriado extends BaseStep implements StepInterface{
	private FeriadoData data;
	private FeriadoMeta meta;
	
	public Feriado(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}
	
	public boolean processRow (StepMetaInterface smi, StepDataInterface sdi) throws KettleException{
		meta = (FeriadoMeta)smi;
		data = (FeriadoData)sdi;
		
		data.outputRowMeta = (RowMetaInterface) new RowMeta();
		meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
		
		ValueMetaAndData holiday[][] = meta.getHoliday();
		
		for(int i = 0; i < holiday.length; i++){
			if(!String.valueOf(holiday[i][0].getValueData()).equals("null") && !String.valueOf(holiday[i][1].getValueData()).equals("null")){
				Object extraValue[] = new Object[3];
				extraValue[0] = holiday[i][0].getValueData();
				extraValue[1] = holiday[i][1].getValueData();
				extraValue[2] = holiday[i][2].getValueData();
				putRow(data.outputRowMeta, extraValue);
			}
		}
		
		setOutputDone();
		return false;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi){
		meta = (FeriadoMeta)smi;
		data = (FeriadoData)sdi;
		
		return super.init(smi, sdi);
	}
	
	public void dispose(StepMetaInterface smi, StepDataInterface sdi){
		meta = (FeriadoMeta)smi;
		data = (FeriadoData)sdi;
		
		super.dispose(smi, sdi);
	}
	
	public void run(){
		logBasic("Starting to run...");
		try{
			while(processRow(meta, data) && !isStopped());
		}catch(Exception e){
			logError("Unexpected error: "+e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		}finally{
			dispose(meta, data);
			logBasic("Finished, precessing: "+linesRead+" rows");
			markStop();
		}
	}

}
