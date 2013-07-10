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
 * Gets all available rooms together and controls their availability and allocation
 * 
 * @author Olga Tsibulevskaya
 */
public class ListOfRooms implements Iterable<Room>, Cloneable {
	private ArrayList<Room> list = new ArrayList<Room>();
	
	public ListOfRooms(File file, boolean finals) {
		try {
			FileInputStream fis = new FileInputStream(file);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
	
			int i = 1;
			Row r = sheet.getRow(i); // 0 is a header
			while (r.getCell(0) != null) {
				Room room;
				if (finals)
					room = new Room(r);
				else
					room = new RoomMidterm(r);
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
	/**
	 * Finds a room which is not a lab and which has at least one place
	 * @return a room
	 */
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
	 * 		it doesn't exist
	 */
	public Room getRoomByName(String name) {
		for (Room r : list) {
			if (r.getName().equals(name) && ! r.full())
				return r;
		}
		return null;
	}
	
	/**
	 * Finds a small room (capacity <= 2)
	 * 
	 * @return a small room with at least one place available
	 */
	public Room getSmallRoom() {
		for (Room r : list) {
			if (r.isSmall() && ! r.full())
				return r;
		}
		return null;
	}
	/**
	 * Finds a lab with at least one place available
	 * @return
	 */
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