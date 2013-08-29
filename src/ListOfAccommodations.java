import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * Created on Jul 18, 2013 1:05:49 PM
 */

/**
 * @author Olga Tsibulevskaya
 *
 */
public class ListOfAccommodations {

	private ArrayList<Accommodations> listAcc = new ArrayList<Accommodations>();
		
	public ListOfAccommodations() throws FileNotFoundException {
				
		final String accommodations = "F:\\Exams\\Files\\accommodations.xlsx";
		File fileAccommodations = new File(accommodations);
		
		if (! fileAccommodations.exists()) {
			new Message("File " + accommodations + " not found");
			throw new FileNotFoundException();
		}
		try {
			FileInputStream fis = new FileInputStream(fileAccommodations);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
	
			Cell cell = sheet.getRow(0).getCell(0);
			int rowNum = 0;
			while (! cell.getStringCellValue().equalsIgnoreCase("ID")) {
				cell = sheet.getRow(rowNum++).getCell(0);
				if (cell == null)
					cell = sheet.getRow(rowNum++).getCell(0);
			}
						
			Row r = sheet.getRow(rowNum);
			cell = r.getCell(0);
			while (cell != null) {
				Accommodations acc = new Accommodations(r);
				listAcc.add(acc);
				r = sheet.getRow(++rowNum);
				cell = r.getCell(0);
			}
			fis.close();
			Collections.sort(listAcc, new Accommodations.IdAccComparator());
		}
		catch (FileNotFoundException e1) {
			new Message("File " + accommodations + " is in use.\nPlease try later when it is available");
			throw e1;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void addAccommodations(ArrayList<? extends Student> list) {
		Collections.sort(list);
		int i = 0;
		for (Student s : list) {
			String id = s.getSid();
			while (id.compareTo(listAcc.get(i).getId()) > 0) {
				i++;
			}
			if (id.compareTo(listAcc.get(i).getId()) == 0) {
				Accommodations acc = listAcc.get(i);
				setAccommodations(s, acc);
			}
			else { //id < idAcc
				setAccommodations(s, null);
			}
		}
	}
	public void findAccommodations(ArrayList<StudentMidterm> list) {
		for (Student stud : list) {
			for (Accommodations acc : listAcc) {
				if (acc.getId().equals(stud.getSid()))
					setAccommodations(stud, acc);
			}
		}
	}
	public void findAccommodations(Student stud) {
		for (Accommodations acc : listAcc) {
			if (acc.getId().equals(stud.getSid()))
				setAccommodations(stud, acc);
		}
	}
	private void setAccommodations(Student s, Accommodations acc) {
		if (acc != null) {
			s.setEmail(acc.getEmailAcc());
			ArrayList<String> listCodes = acc.getList();
			for (String code : listCodes) {
				if (code.equals("XA")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("rm alone");
					else
						s.setComments(comment + ", " + "rm alone");
				}
				else if (code.equals("XB")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("braille");
					else
						s.setComments(comment + ", " + "braille");	
				}
				else if (code.equals("XC"))
					s.setComputer("Yes");
				else if (code.equals("XD")) {
					s.setExtraTime("2x");
				//	s.setExamLength(true);
				}
				else if (code.equals("XE")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("enlarge");
					else
						s.setComments(comment + ", " + "enlarge");
				}			
				else if (code.equals("XF")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("formula sheet");
					else
						s.setComments(comment + ", " + "formula sheet");	
				}
				else if (code.equals("XG")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("proof");
					else
						s.setComments(comment + ", " + "proof");	
				}
				else if (code.equals("XH") && s.getExtraTime() == null) {
					s.setExtraTime("T1/2");
					//s.setExamLength(true);
				}
				else if (code.equals("XL")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("calculator");
					else
						s.setComments(comment + ", " + "calculator");	
				}
				else if (code.equals("XM")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("small room");
					else
						s.setComments(comment + ", " + "small room");	
				}
				else if (code.equals("XR") && s.getExtraTime() == null) {
					s.setExtraTime(""); // regular time
					//s.setExamLength(true);
				}
				else if (code.equals("XS")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("scribe");
					else
						s.setComments(comment + ", " + "scribe");	
				}
				else if (code.equals("XT")) {
					String comment = s.getComments();
					if (comment == null)
						s.setComments("on tape");
					else
						s.setComments(comment + ", " + "on tape");	
				}
				else if (code.equals("XW"))
					s.setStopwatch("Yes");
				else if (code.equals("XX")) {
					// nothing
				}
				else {
					s.setWarning("Unknown code: " + code);
				}
			}
			String otherAcc = acc.getOther();
			if (otherAcc != null) {
				if ((otherAcc.contains("1/3") || otherAcc.contains("1/4")) && s.getExtraTime() == null) {
					String sub = null;
					if (otherAcc.contains("1/3")) {
						sub = "1/3";
						s.setExtraTime("T1/3");
						//s.setExamLength(true);
					}
					else {
						sub = "1/4";
						s.setExtraTime("T1/4");
						//s.setExamLength(true);
					}
					if (otherAcc.contains("on all")) {
						s.setComments(otherAcc);
						s.setWarning("Please check extra time");
					}
					else {
						int index = otherAcc.indexOf(sub);
						int len = otherAcc.length();
						if (index+4 < len) {
							String comment = otherAcc.substring(index+4);
							s.setComments(comment);
						}
					}
				}
			}
			else {
				//
			}
		}
		else {
			s.setWarning("No accommodations data");
		}
		s.setExamLength();  // check if I need to set it before
	}
}
