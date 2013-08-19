/*
 * Created on Jul 11, 2013 12:26:29 PM
 */

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
/**
 * @author Olga Tsibulevskaya
 *
 */
public class ListOfRoomsFinal extends ListOfRooms {

	
	private Date noon;
	
	public ListOfRoomsFinal(File file) {
		super(file);
	}
	/* copy constructor */
	public ListOfRoomsFinal(ListOfRoomsFinal another) {
		for (int i = 0; i < another.listMain.size(); i++)
			listMain.add(i, new RoomFinal(another.listMain.get(i)));
		for (int i = 0; i < another.listTemp.size(); i++)
			listTemp.add(i, another.listTemp.get(i));
		noon = another.noon;
	}
	
	public void initRooms(Row r) {
		RoomFinal room = new RoomFinal(r);
		if (room.getDate() == null) {
			listMain.add(room);
		}
		else {
			listTemp.add(room);
		}
	}
	public void allocateRoom(Student s) {
		if (s.getComments() != null && (s.getComments().contains("rm alone") || s.getComments().contains("scribe"))) {
			Room r = getSmallRoom();
			if (r != null && ! r.getAlone()) {
				r.takePlace();
				r.setAlone();
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
			else { 
				r = getRoom();
				if (r != null) {
					r.takePlace();
					s.setLocation(r.getId());
				}
				else {
					r = getRoomTemp(s);
					if (r != null) {
						r.takePlace();
						s.setLocation(r.getId());
					}
					else
						s.setLocation("room not found");
				}
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
				r = getRoomTemp(s);
				if (r != null) {
					r.takePlace();
					s.setLocation(r.getId());
				}
				else 
					s.setLocation("room not found");
			}
		}
	}
	/**
	 * Finds a room which is not a lab and which has at least one place
	 * @return a room
	 */
	public Room getRoom() {
		for (Room r : listMain) {
			if (! r.isLab() && ! r.full()) {
				if (! r.isSmall())
					return r;
				if (r.isSmall() && ! r.getAlone())
					return r;
			}
		}
		return null;
	}
	private void setNoon(Date startTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 59);
		noon = cal.getTime();
	}
	public Room getRoomTemp(Student s) {
		if (noon == null)
			setNoon(s.getExamStartTime());
		
		for (Room r : listTemp) {
			if (! r.full() && ((RoomFinal)r).getDate().compareTo(s.getExamDate()) == 0) {
				if (s.getExamStartTime().before(noon) && ((RoomFinal)r).getTime().equalsIgnoreCase("morning"))
					return r;
				if (s.getExamStartTime().after(noon) && ((RoomFinal)r).getTime().equalsIgnoreCase("afternoon"))
					return r;
			}
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
		for (Room r : listMain) {
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
		for (Room r : listMain) {
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
		for (Room r : listMain) {
			if (r.isLab() && ! r.full())
				return r;
		}
		return null;
	}
}