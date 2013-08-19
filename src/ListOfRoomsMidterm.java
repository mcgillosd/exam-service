/*
 * Created on Jul 11, 2013 11:39:55 AM
 */

import java.io.File;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
/**
 * @author Olga Tsibulevskaya
 * @see ListOfRooms
 */
public class ListOfRoomsMidterm extends ListOfRooms {

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
	 */
	public RoomMidterm getRoom(Date date, Date timeStart, Date timeFinish) {
		for (Room r : listMain) {
			if (! r.isLab() && ! r.isSmall()) {
				if (((RoomMidterm)r).hasPlace(date, timeStart, timeFinish))
					return (RoomMidterm)r;
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
	public RoomMidterm getRoomByName(String name, Date date, Date timeStart, Date timeFinish) {
		for (Room r : listMain) {
			if (r.getName().equals(name) && ! r.full())
				return (RoomMidterm)r;
		}
		return null;
	}
	
	/**
	 * Finds a small room (capacity <= 2)
	 * 
	 * @return a small room with if it's available
	 */
	public RoomMidterm getSmallRoom(Date date, Date timeStart, Date timeFinish) {
		for (Room r : listMain) {
			if (r.isSmall()) {
				r.setCapacity(1);
				if (((RoomMidterm)r).hasPlace(date, timeStart, timeFinish))
					return (RoomMidterm)r;
			}
		}
		return null;
	}
	/**
	 * Finds a lab with at least one place available
	 * @return
	 */
	public RoomMidterm getLab(Date date, Date timeStart, Date timeFinish) {
		for (Room r : listMain) {
			if (r.isLab()) {
				if (((RoomMidterm)r).hasPlace(date, timeStart, timeFinish))
					return (RoomMidterm)r;
			}
		}
		return null;
	}
}