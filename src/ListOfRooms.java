/*
 * Created on Jul 2, 2013 2:33:07 PM
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Gets all available rooms together and controls their availability and allocation
 * 
 * @author Olga Tsibulevskaya
 */
public abstract class ListOfRooms implements Iterable<Room>, Cloneable {
	protected ArrayList<Room> list = new ArrayList<Room>();
		
	protected ListOfRooms(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);	
			OPCPackage opcPackage = OPCPackage.open(fis);
			
			XSSFWorkbook wb = new XSSFWorkbook(opcPackage);
			XSSFSheet sheet = wb.getSheetAt(0);
	
			int i = 1;
			Row r = sheet.getRow(i); // 0 is a header
			while (r.getCell(0) != null) {
				initRooms(r);
				r = sheet.getRow(++i);
			}
			fis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (InvalidFormatException e) {
			e.printStackTrace();
		} 
	}
	public abstract void initRooms(Row r);
	
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
	public void allocateRoom(Student s) {
		if (s.getComments() != null && (s.getComments().contains("rm alone") || s.getComments().contains("scribe"))) {
			Room r = getSmallRoom();
			if (r != null) {
				r.takePlace();
				s.setLocation(r.getId());
			}
			else {
				s.setLocation("room not found");
			}
		}
		else if (s.getComments() != null && (s.getComments().contains("wynn") || s.getComments().contains("kurzweil"))) {
			Room r = getRoomByName("OSD Lab");
			if (r != null && ! r.full()) {
				r.takePlace();
				s.setLocation(r.getId());
			}
			else {
				s.setLocation("room not found");
			}
		}
		else if (s.getComputer() != null && s.getComputer().equals("pc")) {
			Room r = getLab();
			if (r != null) {
				r.takePlace();
				s.setLocation(r.getId());
			}
			else { // there are laptops TODO: limited qty of laptops - how many? should students be in the OSD office?
				r = getRoom();
				if (r != null) {
					r.takePlace();
					s.setLocation(r.getId());
				}
				else
					s.setLocation("room not found");
			}
		}
		// no special demands
		else {
			Room r = getRoom();
			if (r != null) {
				r.takePlace();
				s.setLocation(r.getId());
			}
			else {
				s.setLocation("room not found");
			}
		}
	}
}