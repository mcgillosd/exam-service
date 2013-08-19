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
public abstract class ListOfRooms implements Iterable<Room> {
	
	static ArrayList<Room> list = new ArrayList<Room>();
		
	protected ArrayList<Room> listMain = new ArrayList<Room>();
	protected ArrayList<Room> listTemp = new ArrayList<Room>();	
	
	protected ListOfRooms(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);	
			OPCPackage opcPackage = OPCPackage.open(fis);
			
			XSSFWorkbook wb = new XSSFWorkbook(opcPackage);
			XSSFSheet sheet = wb.getSheetAt(0);
	
			int i = 1;
			Row r = sheet.getRow(i); // 0 is a header
			while (r != null && r.getCell(0) != null) {
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
		list = listMain;
	}
	protected ListOfRooms() {
		
	}
	public abstract void initRooms(Row r);
	
	public ArrayList<Room> getRoomsList() {
		return list;
	}
	@Override
	public Iterator<Room> iterator() {
		return list.iterator();
	}
	
	public boolean empty() {
		for (Room r : list) {
			if (! r.full())
				return true;
		}
		return false;
	}
}