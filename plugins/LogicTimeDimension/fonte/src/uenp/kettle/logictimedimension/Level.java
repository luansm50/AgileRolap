package uenp.kettle.logictimedimension;

import java.util.ArrayList;

import org.pentaho.di.core.row.ValueMetaAndData;

public class Level {
	private ValueMetaAndData name;
	private ValueMetaAndData column;
	private ValueMetaAndData type;
	private ValueMetaAndData time;
	private ValueMetaAndData visible;
	private ArrayList<Propertie> properties;

	public Level() {
		this.name = new ValueMetaAndData();
		this.column = new ValueMetaAndData();
		this.type = new ValueMetaAndData();
		this.time = new ValueMetaAndData();
		this.visible = new ValueMetaAndData();
		this.properties = new ArrayList<Propertie>();
	}

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

	public ValueMetaAndData getType() {
		return type;
	}

	public void setType(ValueMetaAndData type) {
		this.type = type;
	}

	public ValueMetaAndData getTime() {
		return time;
	}

	public void setTime(ValueMetaAndData time) {
		this.time = time;
	}

	public ValueMetaAndData getVisible() {
		return visible;
	}

	public void setVisible(ValueMetaAndData visible) {
		this.visible = visible;
	}

	public ArrayList<Propertie> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<Propertie> properties) {
		this.properties = properties;
	}

	public ArrayList<Propertie> getPropertie() {
		return properties;
	}

	public void setPropertie(ArrayList<Propertie> propertie) {
		this.properties = propertie;
	}
}
