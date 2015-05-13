package uenp.kettle.cube;

import java.io.IOException;
import java.util.List;

import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
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





public class Cube extends BaseStep implements StepInterface {

	private CubeData data;
	private CubeMeta meta;
	
	public Cube(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		logBasic("Executando - cube");
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException{
		meta = (CubeMeta) smi;
		data = (CubeData) sdi;
		String p[] = null;

		Object[] r = getRow();
		if (r == null) {
			setOutputDone();
			return false;
		}

		data.outputRowMeta = (RowMetaInterface) new RowMeta();
		meta.getFields(data.outputRowMeta, getStepname(), null, null, null);
		p = data.outputRowMeta.getFieldNames();
		data.outputRowMeta = (RowMetaInterface) new RowMeta();
		ValueMetaInterface v = new ValueMeta();
		v.setName("xml_factor");
		v.setType(ValueMeta.TYPE_STRING);
		v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
		v.setOrigin(getStepname());
		data.outputRowMeta.addValueMeta(v);

		if (r != null) {
			for (int i = 0; i < r.length; i++) {
				if (r[i] != null) {
					Object o[] = new Object[2];
					o[0] = String.valueOf(r[i]);
					o[1] = String.valueOf(p[0]);
					putRow(data.outputRowMeta, o);
				}
			}
		}

		return true;

	}
	

	public boolean init(StepMetaInterface smi, StepDataInterface sdi){
		logBasic("Executando - init cube");
		meta = (CubeMeta)smi;
		data = (CubeData)sdi;
		
		return super.init(smi, sdi);
	}
	
	public void dispose(StepMetaInterface smi, StepDataInterface sdi){
		logBasic("Executando - dispose cube");
		meta = (CubeMeta)smi;
		data = (CubeData)sdi;
		
		super.dispose(smi, sdi);
	}
	
	public void run(){
		logBasic("Iniciando - cube");
		try{
			while(processRow(meta, data) && !isStopped());
		}catch(Exception e){
			logError("MeuPlugin3 encontrou um erro - "+e.toString());
			stopAll();
		}finally{
			logBasic("Finalizando - cube");
			dispose(meta, data);			
			markStop();
		}
	}
}
