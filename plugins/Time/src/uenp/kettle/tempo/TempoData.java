package uenp.kettle.tempo;

import java.util.ArrayList;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class TempoData extends BaseStepData implements StepDataInterface{
	public RowMetaInterface outputRowMeta;
	public String inicio = "";
	public String fim = "";
	public int contador = 1;
	public int commit = 1000;
	public Database con = null;
	public ArrayList<String[]> holiday = new ArrayList<String[]>();
	
	public TempoData(){
		super();
	}
	
	public String[][] getHoliday(){
		String[][] holi = new String[holiday.size()][3];
		for(int i = 0; i < holi.length; i++){
			String aux[] = holiday.get(i);
			holi[i][0] = aux[0];
			holi[i][1] = aux[1];
			holi[i][2] = aux[2];
		}
		return holi;
	}
}
