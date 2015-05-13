package uenp.kettle.XMLOut;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class XMLOut extends BaseStep implements StepInterface{

	private XMLOutData data;
	private XMLOutMeta meta;
	
	public XMLOut(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException{
		meta = (XMLOutMeta)smi;
		data = (XMLOutData)sdi;
		
		
		Object[] r = getRow();
		if(r!=null){
			for(int i = r.length-1; i >= 0; i--){
				if(r[i]!=null){
					boolean status = true;
					for(int j = 0; j < data.dados.size(); j++){
						if(r[i].equals(data.dados.get(j))){
							status = false;
						}
					}
					if(status){
						StringBuilder str = new StringBuilder(r[i].toString());
						int aux = str.indexOf("</conection>");
						if(aux!=-1){
							str.delete(0, aux+12);
						}
						data.dados.add(String.valueOf(str));
					}
				}
			}
		}
		
		if(r==null){
			gerarXML(data.dados, String.valueOf(meta.getUrl()));
			setOutputDone();
			return false;
		}
		
		
		return true;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi){
		meta = (XMLOutMeta)smi;
		data = (XMLOutData)sdi;
		
		return super.init(smi, sdi);
	}
	
	public void dispose(StepMetaInterface smi, StepDataInterface sdi){
		meta = (XMLOutMeta)smi;
		data = (XMLOutData)sdi;
		
		super.dispose(smi, sdi);
	}
	
	public void run(){
		try{
			while(processRow(meta, data) && !isStopped());
		}catch(Exception e){
			logError("Unexpected error:" + e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		}finally{
			dispose(meta, data);
			markStop();
		}
	}
	
	
	private void gerarXML(List<String> dados, String url){
		String xml = "<?xml version='1.0' encoding='UTF-8'?>";
		xml += "<rolap>";
		for(int i = 0; i < dados.size(); i++){
			xml+= dados.get(i);
		}
		xml += "</rolap>";
		
		if(!url.contains(".xml"))url += ".xml";
		
		try{
			File arquivo = new File(url);
			FileOutputStream fos = new FileOutputStream(arquivo);
			fos.write(xml.getBytes());
			fos.close();
		}catch(Exception e){
			logBasic("Error on save file");
		}
	}
}
