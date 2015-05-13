package uenp.kettle.tempo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
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

public class Tempo extends BaseStep implements StepInterface{

	private TempoData data;
	private TempoMeta meta;
	
	public Tempo(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}
	
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException{
		meta = (TempoMeta)smi;
		data = (TempoData)sdi;

		Object[] r=getRow();
		
		if(first){
			first = false;
			data.outputRowMeta = (RowMetaInterface) new RowMeta();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, null);
			data.inicio = String.valueOf(meta.getInicio().getValueData());
			data.fim = String.valueOf(meta.getFim().getValueData());
			data.con = new Database(meta.getConection());
			data.con.connect();
			String tables[] = data.con.getTablenames();
			boolean status = false;
			for(int i = 0; i < tables.length; i++){
				if(String.valueOf(meta.getDimension().getValueData()).toLowerCase().equals(tables[i])){
					status = true;
				}
			}
			if(!status){
				String filds[] = meta.Fields();
				String sql = "create table "+String.valueOf(meta.getDimension().getValueData()).toLowerCase()+" ("+
						filds[0]+" serial not null, "+
						filds[1]+" timestamp without time zone, "+
						filds[2]+" text not null, "+
						filds[3]+" text not null, "+
						filds[4]+" text not null, "+
						filds[5]+" text not null, "+
						filds[6]+" text not null, "+
						filds[7]+" text not null, "+
						filds[8]+" text not null, "+
						filds[9]+" integer not null, "+
						filds[10]+" text not null, "+
						filds[11]+" text not null, "+
						filds[12]+" integer not null, "+
						filds[13]+" integer not null, "+
						filds[14]+" integer not null, "+
						filds[15]+" integer not null, "+
						filds[16]+" text not null, "+
						filds[17]+" text not null, "+
						filds[18]+" integer not null, "+
						filds[19]+" text not null, "+
						filds[20]+" text not null, "+
						filds[21]+" integer not null, "+
						filds[22]+" text not null, "+
						filds[23]+" text not null, "+
						filds[24]+" integer not null,"+
						filds[25]+" boolean not null,"+
						filds[26]+" text);";
				data.con.execStatement(sql);
			}
			if(status){
				data.con.execStatement("delete from "+String.valueOf(meta.getDimension().getValueData()).toLowerCase()+";");
			}
		}
		
		if(r!=null){
			String holi[] = new String[3]; 
			for(int i = 0; i < r.length; i++){
				if(r[i]!=null){
					holi[i] = String.valueOf(r[i]);
				}
			}
			data.holiday.add(holi);
		}
		
		if(r==null){
			while(data.inicio.equals(data.fim)){
				try{
					Object dados[] = meta.formatos(data.inicio, data.contador, data.getHoliday());
					data.con.execStatement("insert into "+
							String.valueOf(meta.getDimension().getValueData()).toLowerCase()+
							" values("+
							"'"+String.valueOf(dados[0])+"', "+
							"'"+String.valueOf(dados[1])+"', "+
							"'"+String.valueOf(dados[2])+"', "+
							"'"+String.valueOf(dados[3])+"', "+
							"'"+String.valueOf(dados[4])+"', "+
							"'"+String.valueOf(dados[5])+"', "+
							"'"+String.valueOf(dados[6])+"', "+
							"'"+String.valueOf(dados[7])+"', "+
							"'"+String.valueOf(dados[8])+"', "+
							"'"+String.valueOf(dados[9])+"', "+
							"'"+String.valueOf(dados[10])+"', "+
							"'"+String.valueOf(dados[11])+"', "+
							"'"+String.valueOf(dados[12])+"', "+
							"'"+String.valueOf(dados[13])+"', "+
							"'"+String.valueOf(dados[14])+"', "+
							"'"+String.valueOf(dados[15])+"', "+
							"'"+String.valueOf(dados[16])+"', "+
							"'"+String.valueOf(dados[17])+"', "+
							"'"+String.valueOf(dados[18])+"', "+
							"'"+String.valueOf(dados[19])+"', "+
							"'"+String.valueOf(dados[20])+"', "+
							"'"+String.valueOf(dados[21])+"', "+
							"'"+String.valueOf(dados[22])+"', "+
							"'"+String.valueOf(dados[23])+"', "+
							"'"+String.valueOf(dados[24])+"', "+
							"'"+String.valueOf(dados[25])+"', "+
							"'"+String.valueOf(dados[26])+"'"+
							");");
				}catch(Exception e){}
				ValueMetaInterface vri = new ValueMeta();
				vri.setName(String.valueOf(meta.getDimension().getValueData()).toLowerCase());
				vri.setType(ValueMeta.TYPE_STRING);
				vri.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
				vri.setOrigin("");
				
				Object[] obj= new Object[1];
				obj[0] = meta.xmlPutRow().replace("style='time' ", "");
				data.outputRowMeta = new RowMeta();
				data.outputRowMeta.addValueMeta(vri);
				putRow(data.outputRowMeta, obj);
				setOutputDone();
				return false;
			}
			
			try{
				SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy");
				Object dados[] = meta.formatos(data.inicio, data.contador, data.getHoliday());
				data.con.execStatement("insert into "+
						String.valueOf(meta.getDimension().getValueData()).toLowerCase()+
						" values("+
						"'"+String.valueOf(dados[0])+"', "+
						"'"+String.valueOf(dados[1])+"', "+
						"'"+String.valueOf(dados[2])+"', "+
						"'"+String.valueOf(dados[3])+"', "+
						"'"+String.valueOf(dados[4])+"', "+
						"'"+String.valueOf(dados[5])+"', "+
						"'"+String.valueOf(dados[6])+"', "+
						"'"+String.valueOf(dados[7])+"', "+
						"'"+String.valueOf(dados[8])+"', "+
						"'"+String.valueOf(dados[9])+"', "+
						"'"+String.valueOf(dados[10])+"', "+
						"'"+String.valueOf(dados[11])+"', "+
						"'"+String.valueOf(dados[12])+"', "+
						"'"+String.valueOf(dados[13])+"', "+
						"'"+String.valueOf(dados[14])+"', "+
						"'"+String.valueOf(dados[15])+"', "+
						"'"+String.valueOf(dados[16])+"', "+
						"'"+String.valueOf(dados[17])+"', "+
						"'"+String.valueOf(dados[18])+"', "+
						"'"+String.valueOf(dados[19])+"', "+
						"'"+String.valueOf(dados[20])+"', "+
						"'"+String.valueOf(dados[21])+"', "+
						"'"+String.valueOf(dados[22])+"', "+
						"'"+String.valueOf(dados[23])+"', "+
						"'"+String.valueOf(dados[24])+"', "+
						"'"+String.valueOf(dados[25])+"', "+
						"'"+String.valueOf(dados[26])+"'"+
						");");
				Calendar calendario = Calendar.getInstance();
				calendario.setTime(new Date(formatar.parse(data.inicio).getTime()));
				calendario.add(Calendar.DATE, 1);
				data.inicio = new SimpleDateFormat("dd/MM/yyyy").format(calendario.getTime());
				data.contador++;
			}catch(Exception e){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi){
		meta = (TempoMeta)smi;
		data = (TempoData)sdi;
		
		return super.init(smi, sdi);
	}
	
	public void dispose(StepMetaInterface smi, StepDataInterface sdi){
		meta = (TempoMeta)smi;
		data = (TempoData)sdi;
		
		super.dispose(smi, sdi);
	}
	
	public void run(){
		try{
			while(processRow(meta, data) && !isStopped());
		}catch(Exception e){
			logError("Erro: "+e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		}finally{
			dispose(meta, data);
			markStop();
			
		}
	}

}
