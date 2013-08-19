/*
 * Created on Aug 6, 2013 3:31:20 PM
 */

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author Olga Tsibulevskaya
 *
 */
public class RoomFinal extends Room {

	private Date date = null;
	private String time = null;
		
	/**
	 * @param r
	 */
	public RoomFinal(Row r) {
		super(r);
		Cell c = r.getCell(5);
		if (c != null) {
			date = c.getDateCellValue();
		}
					
		c = r.getCell(6);
		if (c != null)
			time = c.getStringCellValue();
	}
	public RoomFinal(Room another) {
		super(another);
		RoomFinal room = (RoomFinal)another;
		date = room.date;
		time = room.time;
	}
	
	public Date getDate() {
		return date;
	}
	public String getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return name + " " + date + " " + time;
	}
	

}
