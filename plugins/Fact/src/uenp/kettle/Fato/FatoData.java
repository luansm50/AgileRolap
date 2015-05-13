package uenp.kettle.Fato;

import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class FatoData extends BaseStepData implements StepDataInterface{
	
	public RowMetaInterface outputRowMeta;
	public int row;
	
	public  Database db;
	public  int      warnings;
	public  String   tableName;
	public  int      valuenrs[];    // Stream valuename nrs to prevent searches.
    
    /**
     * Mapping between the SQL and the actual prepared statement.
     * Normally this is only one, but in case we have more then one, it's convenient to have this.
     */
    public  Map<String, PreparedStatement>      preparedStatements;
    
    public  int      indexOfPartitioningField;
    
    /** Cache of the data formatter object */
    public SimpleDateFormat dateFormater;

    /** Use batch mode or not? */
    public boolean batchMode;
    public int indexOfTableNameField;
    
    public List<Object[]> batchBuffer;
    public boolean sendToErrorRow;
    public RowMetaInterface insertRowMeta;
	public boolean useSafePoints;
	public Savepoint savepoint;
	public boolean releaseSavepoint;
// 
	public DatabaseMeta databaseMeta;
	
	public Map<String, Integer> commitCounterMap;
    
	public int commitSize;
	
	public FatoData(){
		super();
		
		db=null;
		warnings=0;
		tableName=null;
        
        preparedStatements = new Hashtable<String, PreparedStatement>(); 
        
        indexOfPartitioningField = -1;
        indexOfTableNameField = -1;
        
        batchBuffer = new ArrayList<Object[]>();
        commitCounterMap = new HashMap<String, Integer>();
        
        releaseSavepoint = true;
	}
}
