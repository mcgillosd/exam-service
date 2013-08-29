/*
 * Worker.java
 * 
 * Created on 2013-08-28 1:23:47 PM
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * A thread which looks for profs' emails. Can be interrupted.
 * 
 * @author Olga Tsibulevskaya
 * @see <code>StringWorker</code>
 */
public class Worker extends SwingWorker<Void, String> {
	
	private ArrayList<StudentFinal> list = new ArrayList<StudentFinal>();
	private JTextArea labelFinal;
	private AtomicBoolean stopWork = new AtomicBoolean();
	
	public Worker(ArrayList<StudentFinal> list, JTextArea label) {
		this.list = list;
		labelFinal = label;
		stopWork.set(false);
	}
	@Override
	protected Void doInBackground() throws Exception {
		String filename = "F:\\Exams\\Lists for profs.xlsx";
		File file = new File(filename);
		if (file.exists()) {
			int result = JOptionPane.showConfirmDialog(
					null,"The file already exists, overwrite it?", 
					"Warning",JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// go on
			}
			else
				return null;
		}
				
		Collections.sort(list, new StudentFinal.ProfComparator());
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		String[] headers = {"Prof name", "Prof email", "List of students" };
					
		final int NB_COL = 3;
					
		/* creating the first header row */
		Row row = sheet.createRow((short) 0);
		int colXL = 0;
		while (colXL < NB_COL) {
			Cell cell = row.createCell(colXL);
			cell.setCellValue(headers[colXL++]);
		}
		
		CellStyle styleVertical = wb.createCellStyle();
		styleVertical.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		CellStyle styleWrap = wb.createCellStyle();
		styleWrap.setWrapText(true);
		
		int rowXL = 1;
		int i = 0;
		
		while (i < list.size() && ! stopWork.get()) {
			boolean noProf = false;
			StudentFinal student = list.get(i);
			row = sheet.createRow((short) rowXL++);
			Cell cell = row.createCell(0);
			if (student.getNameProfLast() != "")
				cell.setCellValue(student.getNameProfFirst() + " " + student.getNameProfLast());
			else {
				cell.setCellValue("No prof info");
				noProf = true;
			}
			cell.setCellStyle(styleVertical);
			
			
			cell = row.createCell(1);
			if (! noProf) {
				String email = new ProfMail(student).getEmail();
				if (email != null) {
					publish("-- " + student.getNameProfLast() + ": " + email + "\n");
					cell.setCellValue(email);
				}
				else {
					publish("-- " + student.getNameProfLast() + ": not found\n");
					cell.setCellValue("");
				}
			}
			else { // do not need it since profs should be defined, if no info about profs - "", for sorting purposes 
				publish(student.getNameProfLast() + ": no prof info"); 
				cell.setCellValue("email not found");
			}
			
			cell.setCellStyle(styleVertical);
			
			DateFormat df = new SimpleDateFormat("dd-MMM");	
			String date = df.format(student.getExamDate());
			
			cell = row.createCell(2);
			cell.setCellValue(student.getNameLast() + " " + student.getNameFirst() + " (" + student.getCourse() + ")");
			int count = 1;
			
			if (noProf) {
				while (++i < list.size() && student.getCourse().equals(list.get(i).getCourse())) {
					count++;
					date = df.format(list.get(i).getExamDate());
					cell.setCellValue(cell.getStringCellValue() + "\n" + list.get(i).getNameLast() + " " + 
							list.get(i).getNameFirst() + " (" + list.get(i).getCourse() + ", " + date + ")");
				}
			}
			else {
				while (++i < list.size() && student.equalProf(list.get(i))) {
					count++;
					date = df.format(list.get(i).getExamDate());
					cell.setCellValue(cell.getStringCellValue() + "\n" + list.get(i).getNameLast() + " " + 
							list.get(i).getNameFirst() + " (" + list.get(i).getCourse() + ", " + date + ")");
				}
			}
			cell.setCellStyle(styleWrap);
			row.setHeight((short)(count*300));
		}
		if (stopWork.get()) {
			publish("-- The process interrupted\n");
			return null;
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.close();
			labelFinal.append("-- File " + filename + " has been created\n");
			labelFinal.paintImmediately(labelFinal.getVisibleRect());
			
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return null;
	}
	@Override
	  protected void process(List<String> chunks) {
	    // Updates the messages text area
	    for (final String string : chunks) {
	      labelFinal.setText(string);
	    }
	}
	
	public void stop() {
		stopWork.set(true);
	}
}

