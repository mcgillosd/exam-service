/*
 * Created on Jul 2, 2013 2:33:07 PM
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * @author Olga Tsibulevskaya
 *
 */
public class ListOfRooms implements Iterable<Room>, Cloneable {
	private ArrayList<Room> list = new ArrayList<Room>();
	
	public ListOfRooms(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
	
			int i = 1;
			Row r = sheet.getRow(i); // 0 is a header
			while (r.getCell(0) != null) {
				Room room = new Room(r);
				list.add(room);
				r = sheet.getRow(++i);
			}
			fis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Room> getRoomsList() {
		return list;
	}
	@Override
	public Iterator<Room> iterator() {
		return list.iterator();
	}
	public boolean contains(String room) {
		Room r = new Room(room);
		return list.contains(r);
	}
	public boolean empty() {
		for (Room r : list) {
			if (! r.full())
				return true;
		}
		return false;
	}
	public Room getRoom() {
		for (Room r : list) {
			if (! r.isLab() && ! r.isSmall() && ! r.full())
				return r;
		}
		return null;
	}
	/**
	 * Finds a room by the name, null if not exist
	 * @param name name of the room to find
	 * @return room, which name is specified in arguments, null if
	 * it doesn't exist
	 */
	public Room getRoomByName(String name) {
		for (Room r : list) {
			if (r.getName().equals(name) && ! r.full())
				return r;
		}
		return null;
	}
	
	/**
	 * Looks for rooms with places up to the number sent in arguments
	 * 
	 * @param num maximum number of places in the room
	 * @return list of rooms with the maximum capacity indicated in arguments,
	 * 	which are not full yet
	 */
	public Room getSmallRoom() {
		for (Room r : list) {
			if (r.isSmall() && ! r.full())
				return r;
		}
		return null;
	}
	public Room getLab() {
		for (Room r : list) {
			if (r.isLab() && ! r.full())
				return r;
		}
		return null;
	}
	@Override
	public Object clone() {
		ListOfRooms lRooms = null;
		try {
			lRooms = (ListOfRooms)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
		ArrayList<Room> clone = new ArrayList<Room>(list.size());
		for (Room r : list)
			clone.add((Room)r.clone());
		lRooms.list = clone;
		return lRooms;
	}
}
// TODO: better to write a new column to indicate it's a computer lab or to use constant names to know it's a lab 
// the same for small rooms or just to define what they mean by a "small room" (max places), then can look up
// by capacities? but capacity reduced when places allocated
// Right in the file the same way it should be written in the file (Conference room = conf, etc)

// Midterms - all at once or as they register?