package uenp.kettle.fdw;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

public class FdwMeta extends BaseStepMeta implements StepMetaInterface{

	private ValueMetaAndData value;
	private ValueMetaAndData conection;
	private ValueMetaAndData connection_fdw;
	private ValueMetaAndData nameDimension;
	private ValueMetaAndData nameUser;
	private ValueMetaAndData nameServer;
	private ValueMetaAndData nameHost;
	private ValueMetaAndData nameDatabase;
	private ValueMetaAndData nameUserFdw;
	private ValueMetaAndData password;
	

	private ValueMetaAndData[][] tables;
	private ValueMetaAndData[][] attributes;
	
	private DatabaseMeta databaseMeta;
	
	public FdwMeta(){
		super();
	}
	
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
		return new Fdw(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}
	
	@Override
	public StepDataInterface getStepData() {
		return new FdwData();
	}
	
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		conection = new ValueMetaAndData();
		connection_fdw = new ValueMetaAndData();
		nameDimension = new ValueMetaAndData();
		nameUser = new ValueMetaAndData();
		nameServer = new ValueMetaAndData();
		nameHost = new ValueMetaAndData();
		nameDatabase = new ValueMetaAndData();
		nameUserFdw = new ValueMetaAndData();
		password  = new ValueMetaAndData();
		
		Node valnode = XMLHandler.getSubNode(stepnode, "conection", "value");
		if(valnode != null){
			conection.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "connection_fdw", "value");
		if(valnode != null){
			connection_fdw.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "nameDimension", "value");
		if(valnode != null){
			nameDimension.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "nameFdw", "value");
		if(valnode != null){
			nameUser.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "nameServer", "value");
		if(valnode != null){
			nameServer.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "nameHost", "value");
		if(valnode != null){
			nameHost.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "nameDatabase", "value");
		if(valnode != null){
			nameDatabase.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "nameUserFdw", "value");
		if(valnode != null){
			nameUserFdw.loadXML(valnode);
		}
		
		valnode = XMLHandler.getSubNode(stepnode, "password", "value");
		if(valnode != null){
			password.loadXML(valnode);
		}
		
//		valnode = XMLHandler.getSubNode(stepnode, "nameTable", "value");
//		if(valnode != null){
//			nameTable.loadXML(valnode);
//		}
		
		valnode = XMLHandler.getSubNode(stepnode, "tables");
		tables = new ValueMetaAndData[XMLHandler.countNodes(valnode, "name")][3];
		List<Node> tName = XMLHandler.getNodes(valnode, "name");
		List<Node> tPrimaryKey = XMLHandler.getNodes(valnode, "primaryKey");
		List<Node> tForeignKey = XMLHandler.getNodes(valnode, "foreignKey");
		for(int i = 0; i < tables.length; i++){
			tables[i][0] = new ValueMetaAndData();
			tables[i][1] = new ValueMetaAndData();
			tables[i][2] = new ValueMetaAndData();
			tables[i][0].loadXML(XMLHandler.getSubNode(tName.get(i), "value"));
			tables[i][1].loadXML(XMLHandler.getSubNode(tPrimaryKey.get(i), "value"));
			tables[i][2].loadXML(XMLHandler.getSubNode(tForeignKey.get(i), "value"));
		}
		valnode = XMLHandler.getSubNode(stepnode, "attributes");
		attributes = new ValueMetaAndData[XMLHandler.countNodes(valnode, "table")][2];
		List<Node> aTable = XMLHandler.getNodes(valnode, "table");
		List<Node> aName = XMLHandler.getNodes(valnode, "name");
		for(int i = 0; i < attributes.length; i++){
			attributes[i][0] = new ValueMetaAndData();
			attributes[i][1] = new ValueMetaAndData();
			attributes[i][0].loadXML(XMLHandler.getSubNode(aTable.get(i), "value"));
			attributes[i][1].loadXML(XMLHandler.getSubNode(aName.get(i), "value"));
		}
	}
	
	@Override
	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {}
	
	@Override
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {}
	
	@Override
	public void setDefault() {
		conection = new ValueMetaAndData(new ValueMeta("conection", ValueMetaInterface.TYPE_STRING), new String(""));
	}
	
	public ValueMetaAndData getValue(){
		return value;
	}
	
	public void setValue(ValueMetaAndData value){
		this.value = value;
	}
	
	public String getXML() throws KettleException{
		String retval = "";
		retval+="<conection>"+Const.CR;
		if (conection!=null)retval+=conection.getXML();
		retval+="</conection>"+Const.CR;
		
		retval+="<connection_fdw>"+Const.CR;
		if (connection_fdw!=null)retval+=connection_fdw.getXML();
		retval+="</connection_fdw>"+Const.CR;
		
		retval+="<nameDimension>"+Const.CR;
		if (nameDimension!=null)retval+=nameDimension.getXML();
		retval+="</nameDimension>"+Const.CR;

		retval+="<nameUser>"+Const.CR;
		if (nameUser!=null)retval+=nameUser.getXML();
		retval+="</nameUser>"+Const.CR;
		
		retval+="<nameServer>"+Const.CR;
		if (nameServer!=null)retval+=nameServer.getXML();
		retval+="</nameServer>"+Const.CR;
		
		retval+="<nameHost>"+Const.CR;
		if (nameHost!=null)retval+=nameHost.getXML();
		retval+="</nameHost>"+Const.CR;
		
		retval+="<nameDatabase>"+Const.CR;
		if (nameDatabase!=null)retval+=nameDatabase.getXML();
		retval+="</nameDatabase>"+Const.CR;

		retval+="<nameUserFdw>"+Const.CR;
		if (nameUserFdw!=null)retval+=nameUserFdw.getXML();
		retval+="</nameUserFdw>"+Const.CR;
		
		retval+="<password>"+Const.CR;
		if (password!=null)retval+=password.getXML();
		retval+="</password>"+Const.CR;
			
		retval+="<tables>"+Const.CR;
		if(tables != null || true){
			for(int i = 0; i < tables.length; i++){
				retval+="<name>"+Const.CR;
				retval+=tables[i][0].getXML();
				retval+="</name>"+Const.CR;
				retval+="<primaryKey>"+Const.CR;
				retval+=tables[i][1].getXML();
				retval+="</primaryKey>"+Const.CR;
				retval+="<foreignKey>"+Const.CR;
				retval+=tables[i][2].getXML();
				retval+="</foreignKey>"+Const.CR;
			}
		}
		retval+="</tables>"+Const.CR;
		retval+="<attributes>"+Const.CR;
		if(attributes != null){
			for(int i = 0; i < attributes.length; i++){
				retval+="<table>"+Const.CR;
				retval+=attributes[i][0].getXML();
				retval+="</table>"+Const.CR;
				retval+="<name>"+Const.CR;
				retval+=attributes[i][1].getXML();
				retval+="</name>"+Const.CR;
			}
		}
		retval+="</attributes>"+Const.CR;
		return retval;

	}
	
	public DatabaseMeta getDatabaseMeta() {
		return databaseMeta;
	}

	public void setDatabaseMeta(DatabaseMeta databaseMeta) {
		this.databaseMeta = databaseMeta;
	}

	public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space){
		ValueMetaInterface v = new ValueMeta();
		ArrayList<String[]> array= new ArrayList<String[]>();
		String dimensionName = "";
		String tableName = "";
		boolean status = false;
		String xml = "";
		if(getConection()!=null){
			if(getConection().getValueData()!=null){
				//xml = "<conection>"+getConection().getValueData()+"</conection>";
			}
		}
		for(int i = 0; i < getAttributes().length; i++){
			array.add(getDados(i));
		}
		
		while(array.size()!=0){
			if(array.get(0)[0].equals("")){
				array.remove(0);
			}else{
				status = true;
				if(!dimensionName.equals(array.get(0)[0]) && !dimensionName.equals("")){
					//xml += "</dimension>";
					dimensionName = "";
				}
				if(dimensionName.equals("")){
					dimensionName = array.get(0)[0];
					//xml += "<dimension name='"+dimensionName+"' main_table='"+array.get(0)[1]+"'>";
				}
				tableName = array.get(0)[2];
				if(!tableName.equals("")){
					//xml += "<table name='"+tableName+"' primary_key='"+array.get(0)[3]+"' father_table='"+array.get(0)[4]+"' father_key='"+array.get(0)[5]+"'>";
					for(int i = 0; i < array.size(); i++){
						if(array.get(i)[0].equals(dimensionName) && array.get(i)[2].equals(tableName)){
							if(!array.get(i)[6].equals("")){
								xml += "<field>"+array.get(i)[6]+"</field>";
								
								
							}
							array.remove(i);
							i--;
						}
					}
					xml += "</table>";
				}else{
					array.remove(0);
				}
			}
		}
		if(status)xml += "</dimension>";
		v.setName(xml);
		v.setType(ValueMeta.TYPE_INTEGER);
		//System.out.println(v.getType()+ " Eu sou retardado, eu nao sei fazer as coisas");
		
		v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
		v.setOrigin(origin);
		r.addValueMeta(v);
	}
	
	public Object clone(){
		return super.clone();
	}

	
	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name){
		return new FdwDialog(shell, meta, transMeta, name);
	}
	
	
	public ValueMetaAndData[][] getAttributes() {
		return attributes;
	}

	public void setAttributes(ValueMetaAndData[][] attributes) {
		this.attributes = attributes;
	}

	public ValueMetaAndData[][] getTables() {
		return tables;
	}

	public void setTables(ValueMetaAndData[][] tables) {
		this.tables = tables;
	}

	public ValueMetaAndData getConection() {
		return conection;
	}

	public void setConection(ValueMetaAndData conection) {
		this.conection = conection;
	}
	
	public ValueMetaAndData getConnection_fdw() {
		return connection_fdw;
	}

	public void setConnection_fdw(ValueMetaAndData connection_fdw) {
		this.connection_fdw = connection_fdw;
	}

	public ValueMetaAndData getNameDimension() {
		return nameDimension;
	}

	public void setNameUser(ValueMetaAndData NameUser) {
		this.nameUser = NameUser;
	}

	public ValueMetaAndData getNameUser() {
		return nameUser;
	}
	
	public ValueMetaAndData getNameServer() {
		return nameServer;
	}

	public void setNameServer(ValueMetaAndData nameServer) {
		this.nameServer = nameServer;
	}

	public void setNameDimension(ValueMetaAndData nameDimension) {
		this.nameDimension = nameDimension;
	}
	
	public ValueMetaAndData getNameHost() {
		return nameHost;
	}

	public void setNameHost(ValueMetaAndData nameHost) {
		this.nameHost = nameHost;
	}

	public ValueMetaAndData getNameDatabase() {
		return nameDatabase;
	}

	public void setNameDatabase(ValueMetaAndData nameDatabase) {
		this.nameDatabase = nameDatabase;
	}

	public ValueMetaAndData getNameUserFdw() {
		return nameUserFdw;
	}

	public void setNameUserFdw(ValueMetaAndData nameUserFdw) {
		this.nameUserFdw = nameUserFdw;
	}

	public ValueMetaAndData getPassword() {
		return password;
	}

	public void setPassword(ValueMetaAndData password) {
		this.password = password;
	}

	
	public String[] getDados(int indice){
		String[] dados = new String[8];
		int aux = -1;
		for(int i = 0; i < tables.length; i++){
			if(String.valueOf(attributes[indice][0].getValueData()).equals(String.valueOf(tables[i][0].getValueData()))){
				aux = i;
				break;
			}
		}
		String fatherTable = "";
		if(aux-1 >= 0){
			fatherTable = String.valueOf(tables[aux-1][0].getValueData());
		}
		if(aux != -1){
			if(nameDimension.getValueData()!= null){dados[0] = String.valueOf(nameDimension.getValueData());}else{dados[0] = new String("");}
			if(tables[0][0].getValueData() != null){dados[1] = String.valueOf(tables[0][0].getValueData());}else{dados[1] = new String("");}
			if(attributes[indice][0].getValueData() != null){dados[2] = String.valueOf(attributes[indice][0].getValueData());}else{dados[2] = new String("");}
			if(tables[aux][1].getValueData() != null){dados[3] = String.valueOf(tables[aux][1].getValueData());}else{dados[3] = new String("");}
			dados[4] = fatherTable;
			if(tables[aux][2].getValueData() != null){dados[5] = String.valueOf(tables[aux][2].getValueData());}else{dados[5] = new String("");}
			if(attributes[indice][1].getValueData() != null){dados[6] = String.valueOf(attributes[indice][1].getValueData());}else{dados[6] = new String("");
			}
		}
		return dados;
	}
}
