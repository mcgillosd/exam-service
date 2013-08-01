

/*
 * Created on Jul 11, 2013 12:26:29 PM
 */
import java.io.File;

import org.apache.poi.ss.usermodel.Row;
/**
 * @author Olga Tsibulevskaya
 *
 */
public class ListOfRoomsFinal extends ListOfRooms {

	public ListOfRoomsFinal(File file) {
		super(file);
	}
	
	public void initRooms(Row r) {
		Room room = new Room(r);
		list.add(room);
	}
}
