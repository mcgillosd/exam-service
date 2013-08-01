/*
 * Room.java
 * 
 * Created on Jul 2, 2013 2:04:44 PM
 */

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Gathers info about exam rooms
 * 
 * @author Olga Tsibulevskaya
 */
public class Room implements Cloneable {
	protected String name;
	protected String id;
	/** the flag to distinguish labs from normal rooms */
	protected boolean lab = false;
	/** small rooms - capacity less than 3 */
	protected boolean small = false;
	protected int capacity;
	protected Date date;
	protected Date time;
	
	
	/**
	 * Creates a room by reading a row in the Excel file. 
	 * @param r row in the file
	 */
	public Room(Row r) {
		Cell c = r.getCell(0);
		if (c.getCellType() == Cell.CELL_TYPE_STRING)
			name = c.getStringCellValue();
		// some rooms written in numbers
		else if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			name = Integer.toString((int)c.getNumericCellValue());
		}
		else {
			// error
		}
		c = r.getCell(1);
		if (c.getCellType() == Cell.CELL_TYPE_STRING)
			id = c.getStringCellValue();
		// some rooms written in numbers
		else if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			id = Integer.toString((int)c.getNumericCellValue());
		}
		c = r.getCell(2);
		if (c != null && c.getCellType() == Cell.CELL_TYPE_STRING) { 
			if (c.getStringCellValue().equalsIgnoreCase("lab")) {
				lab = true;
			}
		}
		else {
			// error
		}
		c = r.getCell(4);
		if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			capacity = (int)c.getNumericCellValue();
			if (capacity < 3)
				small = true;
		}
		else {
			// error
		}
	}
	/**
	 * Create a room with any given name
	 * @param name name of the room to be created
	 */
	public Room(String name) {
		this.name = name;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true;
		if (! (obj instanceof Room))
			return false;
		Room r = (Room)obj;
		return (name.equals(r.name));
	}
	@Override
	public Object clone() {
		
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e){
			throw new InternalError(e.toString());
		}
	}
	public String getName() {
		return name;
	}
	public String getId() {
		return id;
	}
	public boolean isLab() {
		return lab;
	}
	public boolean isSmall() {
		return small;
	}
	public int getCapacity() {
		return capacity;
	}
	public void takePlace() {
		if (capacity > 0)
			capacity--;
		else
			throw new IllegalStateException();
	}
	public boolean full() {
		return capacity == 0;
	}
	@Override 
	public String toString() {
		return name + " " + capacity;
	}
}