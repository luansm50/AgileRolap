package uenp.kettle.logictimedimension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class LogicTimeDimension extends BaseStep implements StepInterface {

	private LogicTimeDimensionData data;
	private LogicTimeDimensionMeta meta;

	public LogicTimeDimension(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		logBasic("Executando - MeuPlugin3");
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		logBasic("Executando - processRow");
		meta = (LogicTimeDimensionMeta)smi;
		data = (LogicTimeDimensionData)sdi;
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

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		logBasic("Executando - init");
		meta = (LogicTimeDimensionMeta) smi;
		data = (LogicTimeDimensionData) sdi;

		return super.init(smi, sdi);
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		logBasic("Executando - dispose");
		meta = (LogicTimeDimensionMeta) smi;
		data = (LogicTimeDimensionData) sdi;

		super.dispose(smi, sdi);
	}

	public void run() {
		logBasic("Iniciando - MeuPlugin3");
		try {
			while (processRow(meta, data) && !isStopped())
				;
		} catch (Exception e) {
			logError("MeuPlugin3 encontrou um erro - " + e.toString());
			stopAll();
		} finally {
			logBasic("Finalizando - MeuPlugin3");
			dispose(meta, data);
			markStop();
		}
	}
}
