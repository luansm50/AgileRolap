package uenp.kettle.Fato;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.compatibility.Value;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleEOFException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class FatoMeta extends BaseStepMeta implements StepMetaInterface{

	private ValueMetaAndData sql;
	private ValueMetaAndData xml;
	private ValueMetaAndData factor;
	private ValueMetaAndData novoSQL[][];
	private ValueMetaAndData[][] dimensions;
	private ValueMetaAndData[][] time_dimensions;
	private ValueMetaAndData[][] measure;	
	private DatabaseMeta databaseMeta;
	
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info) {
		CheckResult cr;
		if(prev == null || prev.size() == 0){
			cr = new CheckResult(CheckResult.TYPE_RESULT_WARNING, "Não esta recebendo quaisquer campos de etapas anteriores!", stepMeta);
			remarks.add(cr);
		}else{
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "O passo esta ligado ao anterior, recebendo campos!", stepMeta);
			remarks.add(cr);
		}
		
		if(input.length>0){
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "O passo esta recebendo informações de outros passos!", stepMeta);
			remarks.add(cr);
		}else{
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, "Sem informações recebidas de outros passos!", stepMeta);
			remarks.add(cr);
		}
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans disp) {
		return new Fato(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	@Override
	public StepDataInterface getStepData() {
		return new FatoData();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
				
		readData(stepnode, databases);
		
		Node valnode = XMLHandler.getSubNode(stepnode, "xml", "value");
		if(valnode != null){
			xml = new ValueMetaAndData();
			xml.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "sql", "value");
		if(valnode != null){
			sql = new ValueMetaAndData();
			sql.loadXML(valnode);
		}
		valnode = XMLHandler.getSubNode(stepnode, "factor", "value");
		if(valnode != null){
			factor = new ValueMetaAndData();
			factor.loadXML(valnode);
		}
		valnode = XMLHandler.getSubNode(stepnode, "dimensions");
		dimensions = new ValueMetaAndData[XMLHandler.countNodes(valnode, "attribute")][2];
		List<Node> tAttribute = XMLHandler.getNodes(valnode, "attribute");
		List<Node> tDimension = XMLHandler.getNodes(valnode, "dimension");
		for(int i = 0; i < dimensions.length; i++){
			dimensions[i][0] = new ValueMetaAndData();
			dimensions[i][1] = new ValueMetaAndData();
			dimensions[i][0].loadXML(XMLHandler.getSubNode(tAttribute.get(i), "value"));
			dimensions[i][1].loadXML(XMLHandler.getSubNode(tDimension.get(i), "value"));
		}
		valnode = XMLHandler.getSubNode(stepnode, "time_dimensions");
		time_dimensions = new ValueMetaAndData[XMLHandler.countNodes(valnode, "attribute")][3];
		List<Node> tdAttribute = XMLHandler.getNodes(valnode, "attribute");
		List<Node> tdComparison = XMLHandler.getNodes(valnode, "comparison");
		List<Node> tdName = XMLHandler.getNodes(valnode, "name");
		for(int i = 0; i < time_dimensions.length; i++){
			time_dimensions[i][0] = new ValueMetaAndData();
			time_dimensions[i][1] = new ValueMetaAndData();
			time_dimensions[i][2] = new ValueMetaAndData();
			time_dimensions[i][0].loadXML(XMLHandler.getSubNode(tdAttribute.get(i), "value"));
			time_dimensions[i][1].loadXML(XMLHandler.getSubNode(tdComparison.get(i), "value"));
			time_dimensions[i][2].loadXML(XMLHandler.getSubNode(tdName.get(i), "value"));
		}
		valnode = XMLHandler.getSubNode(stepnode, "measure");
		measure = new ValueMetaAndData[XMLHandler.countNodes(valnode, "attribute")][2];
		List<Node> tmAttribute = XMLHandler.getNodes(valnode, "attribute");
		List<Node> tmName = XMLHandler.getNodes(valnode, "name");
		for(int i = 0; i < measure.length; i++){
			measure[i][0] = new ValueMetaAndData();
			measure[i][1] = new ValueMetaAndData();
			measure[i][0].loadXML(XMLHandler.getSubNode(tmAttribute.get(i), "value"));
			measure[i][1].loadXML(XMLHandler.getSubNode(tmName.get(i), "value"));
		}
		valnode = XMLHandler.getSubNode(stepnode, "novoSQL");
		novoSQL = new ValueMetaAndData[XMLHandler.countNodes(valnode, "name")][4];
		List<Node> tmNovoSQLName = XMLHandler.getNodes(valnode, "name");
		List<Node> tmNovoSQLNickName = XMLHandler.getNodes(valnode, "nickname");
		List<Node> tmNovoSQLTables = XMLHandler.getNodes(valnode, "tables");
		List<Node> tmNovoSQLConditions = XMLHandler.getNodes(valnode, "conditions");
		for(int i = 0; i < novoSQL.length; i++){
			novoSQL[i][0] = new ValueMetaAndData();
			novoSQL[i][1] = new ValueMetaAndData();
			novoSQL[i][2] = new ValueMetaAndData();
			novoSQL[i][3] = new ValueMetaAndData();
			novoSQL[i][0].loadXML(XMLHandler.getSubNode(tmNovoSQLName.get(i), "value"));
			novoSQL[i][1].loadXML(XMLHandler.getSubNode(tmNovoSQLNickName.get(i), "value"));
			novoSQL[i][2].loadXML(XMLHandler.getSubNode(tmNovoSQLTables.get(i), "value"));
			novoSQL[i][3].loadXML(XMLHandler.getSubNode(tmNovoSQLConditions.get(i), "value"));
		}
	}
	
	public String getXML() throws KettleException{
		String retval = "";
		StringBuilder retva=new StringBuilder();
		
		retva.append("    "+XMLHandler.addTagValue("connection",     databaseMeta==null?"":databaseMeta.getName()));		
		retval+=retva.toString();
		
		if (xml!=null){
			retval+="<xml>"+Const.CR;
			retval+=xml.getXML();
			retval+="</xml>"+Const.CR;
		}
		
		if (sql!=null){
			retval+="<sql>"+Const.CR;
			retval+=sql.getXML();
			retval+="</sql>"+Const.CR;
		}
		if(factor != null){
			retval+="<factor>"+Const.CR;
			retval+=factor.getXML();
			retval+="</factor>"+Const.CR;
		}
		if(dimensions != null){
			retval+="<dimensions>"+Const.CR;
			for(int i = 0; i < dimensions.length; i++){
				retval+="<attribute>"+Const.CR;
				retval+=dimensions[i][0].getXML();
				retval+="</attribute>"+Const.CR;
				retval+="<dimension>"+Const.CR;
				retval+=dimensions[i][1].getXML();
				retval+="</dimension>"+Const.CR;
			}
			retval+="</dimensions>"+Const.CR;
		}
		if(time_dimensions != null){
			retval+="<time_dimensions>"+Const.CR;
			for(int i = 0; i < time_dimensions.length; i++){
				retval+="<attribute>"+Const.CR;
				retval+=time_dimensions[i][0].getXML();
				retval+="</attribute>"+Const.CR;
				retval+="<comparison>"+Const.CR;
				retval+=time_dimensions[i][1].getXML();
				retval+="</comparison>"+Const.CR;
				retval+="<name>"+Const.CR;
				retval+=time_dimensions[i][2].getXML();
				retval+="</name>"+Const.CR;
			}
			retval+="</time_dimensions>"+Const.CR;
		}
		if(measure != null){
			retval+="<measure>"+Const.CR;
			for(int i = 0; i < measure.length; i++){
				retval+="<attribute>"+Const.CR;
				retval+=measure[i][0].getXML();
				retval+="</attribute>"+Const.CR;
				retval+="<name>"+Const.CR;
				retval+=measure[i][1].getXML();
				retval+="</name>"+Const.CR;
			}
			retval+="</measure>"+Const.CR;
		}
		if(novoSQL != null){
			retval+="<novoSQL>"+Const.CR;
			for(int i = 0; i < novoSQL.length; i++){
				retval+="<name>"+Const.CR;
				retval+=novoSQL[i][0].getXML();
				retval+="</name>"+Const.CR;
				retval+="<nickname>"+Const.CR;
				retval+=novoSQL[i][1].getXML();
				retval+="</nickname>"+Const.CR;
				retval+="<tables>"+Const.CR;
				retval+=novoSQL[i][2].getXML();
				retval+="</tables>"+Const.CR;
				retval+="<conditions>"+Const.CR;
				retval+=novoSQL[i][3].getXML();
				retval+="</conditions>"+Const.CR;
			}
			retval+="</novoSQL>"+Const.CR;
		}
		return retval;
	}
	
	public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space){
		ValueMetaInterface v = new ValueMeta();
		if(xml != null){
			if(String.valueOf(xml.getValueData()).equals("null")){
				xml = new ValueMetaAndData(new ValueMeta("xml", ValueMetaInterface.TYPE_STRING), new String("<rolap></rolap>"));		
			}
		}else{
			xml = new ValueMetaAndData(new ValueMeta("xml", ValueMetaInterface.TYPE_STRING), new String("<rolap></rolap>"));
		}
		String xml_externo = String.valueOf(xml.getValueData());
		DocumentBuilder dbo;
		try {
			dbo = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml_externo));
			Document doc = dbo.parse(is);
			Element raiz = doc.getDocumentElement();
			NodeList dimension = raiz.getElementsByTagName("dimension");
			String xml = "";
			if(factor != null){
				if(factor.getValueData()!= null){
					xml += "<fact table='"+String.valueOf(factor.getValueData())+"'>";
					if(dimensions!=null){
						for(int i = 0; i < dimensions.length; i++){
							if(dimensions[i][1].getValueData()!=null && dimensions[i][1].getValueData()!="" && dimensions[i][0].getValueData()!=null){
								for(int j = 0; j < dimension.getLength(); j++){
									Element dimen = (Element) dimension.item(j);
									if(dimen.getAttributeNode("name").getNodeValue().equals(String.valueOf(dimensions[i][1].getValueData()))){ 
										xml+= "<dimension_usage column='"+String.valueOf(dimen.getAttributeNode("main_table").getNodeValue())+"'>"+String.valueOf(dimensions[i][1].getValueData())+"</dimension_usage>"; 
									}
								}
								
								
							}
						}
					}
					if(time_dimensions!=null){
						for(int i = 0; i < time_dimensions.length; i++){
							if(time_dimensions[i][2].getValueData()!=null && time_dimensions[i][2].getValueData()!="" && time_dimensions[i][0].getValueData()!=null && time_dimensions[i][1].getValueData()!=null){
								for(int j = 0; j < dimension.getLength(); j++){
									Element dimen = (Element) dimension.item(j);
									if(dimen.getAttributeNode("name").getNodeValue().equals(String.valueOf(time_dimensions[i][2].getValueData()))){ 
										xml+= "<dimension_usage column='"+String.valueOf(dimen.getAttributeNode("main_table").getNodeValue())+"'>"+String.valueOf(time_dimensions[i][2].getValueData())+"</dimension_usage>"; 
									}
								}
								
							}
						}
					}
					if(measure!=null){
						for(int i = 0; i < measure.length; i++){
							if(measure[i][1].getValueData()!=null && measure[i][1].getValueData()!="" && measure[i][0].getValueData()!=null){
								xml+= "<measure>"+String.valueOf(measure[i][1].getValueData())+"</measure>";
							}
						}
					}
					xml+="</fact>";
				}
			}
			
			v.setName(xml);
			v.setType(ValueMeta.TYPE_STRING);
			v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
			v.setOrigin(origin);
			r.addValueMeta(v);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readData(Node stepnode, List<? extends SharedObjectInterface> databases) throws KettleXMLException
	{
		try
		{
			String con     = XMLHandler.getTagValue(stepnode, "connection");
			databaseMeta   = DatabaseMeta.findDatabase(databases, con);
        }
		catch(Exception e)
		{
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}
	 
	
	@Override
	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {}

	@Override
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {}

	@Override
	public void setDefault() {
		// TODO Auto-generated method stub
		
	}
	
	public Object clone(){
		return super.clone();
	}
	
	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name){
		return new FatoDialog(shell, meta, transMeta, name);
	}

	public DatabaseMeta getDatabaseMeta() {
		return databaseMeta;
	}

	public void setDatabaseMeta(DatabaseMeta databaseMeta) {
		this.databaseMeta = databaseMeta;
	}

	public ValueMetaAndData getSql() {
		return sql;
	}

	public void setSql(ValueMetaAndData sql) {
		this.sql = sql;
	}

	public ValueMetaAndData[][] getDimensions() {
		return dimensions;
	}

	public void setDimensions(ValueMetaAndData[][] dimensions) {
		this.dimensions = dimensions;
	}

	public ValueMetaAndData[][] getTime_dimensions() {
		return time_dimensions;
	}

	public void setTime_dimensions(ValueMetaAndData[][] time_dimensions) {
		this.time_dimensions = time_dimensions;
	}

	public ValueMetaAndData[][] getMeasure() {
		return measure;
	}

	public void setMeasure(ValueMetaAndData[][] measure) {
		this.measure = measure;
	}

	public ValueMetaAndData getFactor() {
		return factor;
	}

	public void setFactor(ValueMetaAndData factor) {
		this.factor = factor;
	}

	public ValueMetaAndData getXml() {
		return xml;
	}

	public void setXml(ValueMetaAndData xml) {
		this.xml = xml;
	}

	public ValueMetaAndData[][] getNovoSQL() {
		return novoSQL;
	}

	public void setNovoSQL(ValueMetaAndData[][] novoSQL) {
		this.novoSQL = novoSQL;
	}

	
	
}
