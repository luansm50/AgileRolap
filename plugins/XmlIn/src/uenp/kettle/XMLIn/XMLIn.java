package uenp.kettle.XMLIn;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
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


public class XMLIn extends BaseStep implements StepInterface{
	
	private XMLInData data;
	private XMLInMeta meta;
	
	public XMLIn(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleStepException{
		meta = (XMLInMeta)smi;
		data = (XMLInData)sdi;
		
		if(first){
			first = false;
			data.outputRowMeta = (RowMetaInterface) new RowMeta();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, null);
			meta.setDados(new ArrayList<String[]>());
			carregarXML(String.valueOf(meta.getUrl().getValueData()));
		}
		
		for(int i = 0; i < meta.getDados().size(); i++){
			putRow(data.outputRowMeta, meta.getDados(i));
		}
		
		setOutputDone();
		return false;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi){
		meta = (XMLInMeta)smi;
		data = (XMLInData)sdi;
		
		return super.init(smi, sdi);
	}
	
	public void dispose(StepMetaInterface smi, StepDataInterface sdi){
		meta = (XMLInMeta)smi;
		data = (XMLInData)sdi;
		
		super.dispose(smi, sdi);
	}
	
	public void run(){
		try {
			while(processRow(meta, data) && !isStopped());
		} catch (Exception e) {
			logError("Unexpected error:" + e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		} finally{
			dispose(meta, data);
			markStop();
		}
	}
	
	public void carregarXML(String url){
		File arquivo = new File(url);
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(arquivo);
			
			Element raiz = doc.getDocumentElement();
			NodeList dimensions = raiz.getElementsByTagName("dimension");
			
			for(int i = 0; i < dimensions.getLength(); i++){
				Element dimension = (Element) dimensions.item(i);
				NodeList tables = dimension.getElementsByTagName("table");
				for(int j = 0; j < tables.getLength(); j++){
					Element table = (Element) tables.item(j);
					NodeList fields = table.getElementsByTagName("field");
					for(int z = 0; z < fields.getLength(); z++){
						Element field = (Element) fields.item(z);
						if(field != null){
							Node no = field.getFirstChild();
							String[] line = new String[7];
							line[0] = dimension.getAttributeNode("name").getNodeValue();
							line[1] = dimension.getAttributeNode("main_table").getNodeValue();
							line[2] = table.getAttributeNode("name").getNodeValue();
							line[3] = table.getAttributeNode("primary_key").getNodeValue();
							line[4] = table.getAttributeNode("father_table").getNodeValue();
							line[5] = table.getAttributeNode("father_key").getNodeValue();
							line[6] = no.getNodeValue();
							meta.getDados().add(line);
						}
					}
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
