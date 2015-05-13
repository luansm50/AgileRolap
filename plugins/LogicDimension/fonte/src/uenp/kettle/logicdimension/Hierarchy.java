package uenp.kettle.logicdimension;

import java.util.ArrayList;

import org.pentaho.di.core.row.ValueMetaAndData;

public class Hierarchy {
	private ValueMetaAndData name;
	private ValueMetaAndData visible;
	private ArrayList <Level> levels;
	
	
	
	public Hierarchy() {
		this.name = new ValueMetaAndData();
		this.visible = new ValueMetaAndData();
		this.levels = new ArrayList<Level>();
	}
	public ValueMetaAndData getName() {
		return name;
	}
	public void setName(ValueMetaAndData name) {
		this.name = name;
	}
	public ValueMetaAndData getVisible() {
		return visible;
	}
	public void setVisible(ValueMetaAndData visible) {
		this.visible = visible;
	}
	public ArrayList<Level> getLevels() {
		return levels;
	}
	public void setLevels(ArrayList<Level> levels) {
		this.levels = levels;
	}
	
}
