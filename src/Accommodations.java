/*
 * Accommodations.java
 * 
 * Created on 2013-06-18 12:46:50 PM
 */
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Reads from the accommodations.xlsx file and saves info
 * 
 * @author Olga Tsibulevskaya
 *
 */
public class Accommodations {
	private String id;
	private String emailAcc;
	private ArrayList<String> listAcc;
	private String other;
		
	public Accommodations(Row r) {
		id = r.getCell(0).getStringCellValue();
		Cell cell = r.getCell(4);
		if (cell != null)
			emailAcc = r.getCell(4).getStringCellValue();
		else
			emailAcc = "";
		listAcc = new ArrayList<String>();
		
		int i = 5;
		while (r.getCell(i) != null) { 
			listAcc.add(r.getCell(i).getStringCellValue());
			i+=3;
		}
		cell = r.getCell(37);
		if (cell != null)
			other = cell.getStringCellValue();
		else
			other = null;
	}
	public String getId() {
		return id;
	}
	public String getEmailAcc() {
		return emailAcc;
	}
	public ArrayList<String> getList() {
		return listAcc;
	}
	public String getOther() {
		return other;
	}
	public static class IdAccComparator implements Comparator<Accommodations> {
		public int compare(Accommodations acc1, Accommodations acc2) {
			return acc1.getId().compareTo(acc2.getId());
		}
	}
}
