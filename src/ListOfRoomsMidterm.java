/*
 * Created on Jul 11, 2013 11:39:55 AM
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * @author Olga Tsibulevskaya
 * @see ListOfRooms
 */
public class ListOfRoomsMidterm extends ListOfRooms {

	final String filename = "F:\\Exams\\Files\\rooms_midterm.xlsx";
	InputStream fis;
	XSSFWorkbook wb;
	CellStyle styleDate;
	CellStyle styleWrap;
		
	public ListOfRoomsMidterm(File file) {
		super(file);
	}
	public void initRooms(Row r) {
		Room room = new RoomMidterm(r);
		listMain.add(room);
	}
	/**
	 * Finds a room which is not a lab and which has at least one place
	 * @return a room
	 * @throws FileNotFoundException 
	 */
	public RoomMidterm getRoom(StudentMidterm student, boolean write) throws FileNotFoundException {
		for (Room r : listMain) {
			if (! r.isLab() && ! r.isSmall()) {
				if (((RoomMidterm)r).hasPlace(student, wb, styleDate, styleWrap)) {
					if (write)
						((RoomMidterm)r).write(filename, wb);
					return (RoomMidterm)r;
				}
			}
		}
		return null;
	}
	/**
	 * Finds a room by the name, null if not exist
	 * @param name name of the room to find
	 * @return room, which name is specified in arguments, null if
	 * 		it doesn't exist
	 * @throws FileNotFoundException 
	 */
	public RoomMidterm getRoomByName(String name, StudentMidterm student, boolean write) throws FileNotFoundException {
		for (Room r : listMain) {
			if (r.getName().equals(name)) {
				if (((RoomMidterm)r).hasPlace(student, wb, styleDate, styleWrap)) {
					if (write)
						((RoomMidterm)r).write(filename, wb);
					return (RoomMidterm)r;
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds a small room (capacity <= 2)
	 * 
	 * @return a small room with if it's available
	 * @throws FileNotFoundException 
	 */
	public RoomMidterm getSmallRoom(StudentMidterm student, boolean write) throws FileNotFoundException {
		for (Room r : listMain) {
			if (r.isSmall()) {
				r.setCapacity(1);
				if (((RoomMidterm)r).hasPlace(student, wb, styleDate, styleWrap)) {
					if (write)
						((RoomMidterm)r).write(filename, wb);
					return (RoomMidterm)r;
				}
			}
		}
		return null;
	}
	/**
	 * Finds a lab with at least one place available
	 * @return
	 * @throws FileNotFoundException 
	 */
	public RoomMidterm getLab(StudentMidterm student, boolean write) throws FileNotFoundException {
		for (Room r : listMain) {
			if (r.isLab()) {
				if (((RoomMidterm)r).hasPlace(student, wb, styleDate, styleWrap)) {
					if (write)
						((RoomMidterm)r).write(filename, wb);
					return (RoomMidterm)r;
				}
			}
		}
		return null;
	}
	public void initializeBook() {
		try {
			InputStream fis = new FileInputStream(filename);
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			System.out.println("fis exception");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("wb exception");
			
		}
		styleDate = wb.createCellStyle();
		DataFormat df = wb.createDataFormat();
		styleDate.setDataFormat(df.getFormat("d-mmm"));
		
		styleWrap = wb.createCellStyle();
		styleWrap.setWrapText(true);
	}
	public void addLocation(ArrayList<StudentMidterm> list) throws FileNotFoundException {
		boolean write = false;
		for (int i = 0; i < list.size(); i++) {
			StudentMidterm s = list.get(i);
			if (s.getCampus().equalsIgnoreCase("Downtown")) {
				if (list.size() == 1) {
					initializeBook();
					write = true;
				}
				else if (i == 0) {
					initializeBook();
					write = false;
				}
				else if (i == list.size()-1)
					write = true;
		
				if (s.getComments() != null && (s.getComments().contains("rm alone") || s.getComments().contains("scribe"))) {
					RoomMidterm r = getSmallRoom(s, write);
					if (r != null) {
						s.setLocation(r.getId());
					}
					else {
						s.setLocation("small room not found");
					}
				}
				else if (s.getComments() != null && (s.getComments().contains("wynn") || s.getComments().contains("kurzweil"))) {
					RoomMidterm r = getRoomByName("OSD Lab", s, write);
					if (r != null && ! r.full()) {
						s.setLocation(r.getId());
					}
					else {
						s.setLocation("no places in OSD lab");
					}
				}
				else if (s.getComputer() != null && s.getComputer().equals("pc")) {
					RoomMidterm r = getLab(s, write);
					if (r != null) {
						s.setLocation(r.getId());
					}
					else {
						r = getRoom(s, write);
						if (r != null) {
							s.setLocation(r.getId());
						}
						else
							s.setLocation("no more places");
					}
				}
				// no special demands
				else {
					RoomMidterm r = getRoom(s, write);
					if (r != null) {
						s.setLocation(r.getId());
					}
					else {
						s.setLocation("no more places");
					}
				}
			}
		}
	}
}