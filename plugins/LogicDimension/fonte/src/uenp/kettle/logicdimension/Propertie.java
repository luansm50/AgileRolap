package uenp.kettle.logicdimension;

import org.pentaho.di.core.row.ValueMetaAndData;

public class Propertie {
	private ValueMetaAndData name;
	private ValueMetaAndData column;
	
	
	public ValueMetaAndData getName() {
		return name;
	}
	public void setName(ValueMetaAndData name) {
		this.name = name;
	}
	public ValueMetaAndData getColumn() {
		return column;
	}
	public void setColumn(ValueMetaAndData column) {
		this.column = column;
	}
}
