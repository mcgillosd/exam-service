/*
 * Created on 2013-06-26 1:37:35 PM
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


/**
 * Searches professors emails on the mcgill.ca/directory/staff site
 *  
 * @author Olga Tsibulevskaya
 */
public class ProfMail {
	private String email;
	
	/**
	 * Gets a student and looks for email for his prof
	 * @param s a student whom prof to find
	 */
	public ProfMail(StudentFinal s) {
		email = getMailFromMidterms(s);
		if (email == null) {
			String surname = s.getNameProfLast();
			String name = s.getNameProfFirst();
			String html = web(surname, name);
			search(html);
		}
	}
	private String getMailFromMidterms(Student student) {
		String mail = null;
		String term = new Term().getTerm();
		
		String filename = "F:\\Exams\\Files\\" + term + " exam schedule.xlsx";
		File file = new File(filename);	
		
		if (! file.exists()) {
			return null;
		}
		try {
			FileInputStream fis = new FileInputStream(file);	
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			
			Row r = sheet.getRow(0);
			int last = sheet.getLastRowNum();
					
			
			// start reading the file from the 1st row (exclude the header)
			for (int rowNum = 1; rowNum <= last; rowNum++) {
				r = sheet.getRow(rowNum);
				if (r == null) {
					continue;
				}
				r = sheet.getRow(rowNum);
				Cell cell = r.getCell(4);
				if (cell != null) {
					
					String course = cell.getStringCellValue();
					if (course.equalsIgnoreCase(student.getCourse())) {
						cell = r.getCell(5);
						if (cell != null) {
							String section = cell.getStringCellValue();
							if (section.matches("[0-9]+") && student.getSection().matches("[0-9]+")) {
								int sec = Integer.parseInt(section);
								int secStud = Integer.parseInt(student.getSection());
								if (sec == secStud) {
									cell = r.getCell(11);
									if (cell != null) {
										mail = cell.getStringCellValue();
										break;
									}
								}
							}
						}
					}
				}
				
			}
			fis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		boolean correct = verifyEmail(student, mail);
		if (correct)
			return mail;
		else
			return null;
	}
	private boolean verifyEmail(Student student, String mail) {
		if (mail == null)
			return false;
		String prof = ((StudentFinal)student).getNameProfLast().toLowerCase();
		String emailLower = mail.toLowerCase();
		if (emailLower.contains(prof))
			return true;
		return false;
	}
	
	public String getEmail() {
		return email;
	}
	/**
	 * Gets the whole html page
	 * @param surname
	 * @param name
	 * @return a string with html data 
	 */
	private String web(String surname, String name) {
		String url = "http://www.mcgill.ca/directory/staff/";	
		HttpClient httpClient = new HttpClient();
	    PostMethod post = new PostMethod(url);
	  
	    post.addParameter("last", surname);
	    post.addParameter("first", name);
	  
	    post.addParameter("form_build_id", "form-7dff2c92dda505010006558560a0a8e8");
	    post.addParameter("form_id", "mcgill_directory_staff_form");
	
	    try {
	    	httpClient.executeMethod(post); 
	    } catch (HttpException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    if (post.getStatusCode() == HttpStatus.SC_OK) {
	       
	    } 
	    else {
	         post.getStatusLine();
	    }
	    post.releaseConnection();
	    
	    GetMethod httpget = new GetMethod(url);
        try {
           	httpClient.executeMethod(httpget); 
        }
        catch(Exception e) {
            e.printStackTrace();
        }
         
        String html = "";
        try {
        	InputStream in = httpget.getResponseBodyAsStream();
        	Scanner scanner = new Scanner(in);
        	scanner.useDelimiter("\\A");
        	html = scanner.hasNext() ? scanner.next() : "";
        	scanner.close();
        } catch (IOException e) {
			e.printStackTrace();
		}
        return html;	 
	}
	/**
	 * Extracts email form the html string and sets email
	 * @param html
	 */
	private void search(String html) {
		 Document doc = Jsoup.parse(html);
			
		 Elements span = doc.select("span.spamspan");
		 if (! span.isEmpty()) {
			 String spanS = span.html();
			 String stripped = spanS.replaceAll("<[^>]*>", "");
			 String subspan = stripped.substring(0,stripped.indexOf("("));
			 CharSequence s1 = "[dot]";
			 CharSequence s2 = ".";
			 spanS = subspan.replace(s1, s2);
			 s1 = "[at]";
			 s2 = "@";
			 subspan = spanS.replace(s1, s2);
			 spanS = subspan.replaceAll(" ", "");
			 email = spanS.toLowerCase();
		 }
	}
}