/* 
 * WebConnect.java
 * 
 * Created on 2013-06-11 11:21:09 AM
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JTextArea;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Connects to the web page and gets the data from it.
 * Called by the <code>StudentsList</code> class, when the event is done 
 * (successful login), will call <code>StudentsList.start()</code>
 * 
 * @author Olga Tsibulevskaya
 */
public class WebConnect {

	private JTextArea label;
	
	private String url;
	
	final String urlInv = "https://www.mcgill.ca/osd/node/884/webform-results/table?results=0";
	
	final String urlMidterm = "https://www.mcgill.ca/osd/node/879/webform-results/table?results=0";
	private HttpClient httpclient; 
    /**
	 * Creates a new panel to login, sets <code>StudentsMidterm</code> data
	 * to be used for calling after ActionListeners events
	 * 
	 * @param midterm if <code>true</code>, then loads students sign-ups,
	 * else invigilators
	 */
	public WebConnect(boolean midterm) {
		httpclient = new HttpClient();
        httpclient.getHttpConnectionManager().
                getParams().setConnectionTimeout(30000);
        httpclient.getHttpConnectionManager().getParams().setSoTimeout(90000);
        if (midterm) {
        	setUrl(urlMidterm);
        	setLabel(PanelMidterms.label);
        }
        else {
        	setUrl(urlInv);
        	setLabel(PanelFinals.label);
        }
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setLabel(JTextArea label) {
		this.label = label;
	}
	/**
	 * Connects to the web page
	 * 
	 * @param user
	 * @param password
	 * @return http reponse number
	 */
	public int connect(String user, char[] password) {
		
		label.append("-- Authentication\n");
		label.paintImmediately(label.getVisibleRect());
	
		PostMethod httppost = new PostMethod(url);
		
        httppost.addParameter("name", user);
        String pass = new String(password);
        httppost.addParameter("pass", pass);
        httppost.addParameter("form_build_id", "form-ITnhneCtAHbLg9ss6u3tUe3y-NY0XJV6d8FeDa2ncX4");
        httppost.addParameter("form_id", "user_login");
        
        int result = 0;
        try {
            result = httpclient.executeMethod(httppost);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        httppost.releaseConnection();
        return result;
	}
	public String getContent() {
		label.append("-- Accessing the database\n");
    	label.paintImmediately(label.getVisibleRect());
		
    	GetMethod httpget = new GetMethod(url);
        int result = 0;	
        try {
           	result = httpclient.executeMethod(httpget);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
         
        if (result == 200) {
        	label.append("-- Downloading data\n");
        	label.paintImmediately(label.getVisibleRect());
        }
        else {
        	label.append("-- Connection failed\n");
        	label.paintImmediately(label.getVisibleRect());
        }
        
        String html = "";
        try {
        	InputStream in = httpget.getResponseBodyAsStream();
        	Scanner s = new Scanner(in);
        	s.useDelimiter("\\A");
            html = s.hasNext() ? s.next() : "";
            s.close();
        } catch (IOException e) {
        	e.printStackTrace();
		}
       
        httpget.releaseConnection();
        return html;		 
	}
	public String getInvigilatorsPage() {
		String html = "";
		label.append("-- Accessing the database\n");
    	label.paintImmediately(label.getVisibleRect());
		
    	GetMethod httpget = new GetMethod(url);
        int result = 0;	
        try {
           	result = httpclient.executeMethod(httpget);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
         
        if (result == 200) {
        	label.append("-- Getting invigilators info\n");
        	label.paintImmediately(label.getVisibleRect());
        }
        else {
        	label.append("-- Connection failed\n");
        	label.paintImmediately(label.getVisibleRect());
        }
        
        try {
        	InputStream in = httpget.getResponseBodyAsStream();
        	Scanner s = new Scanner(in);
        	s.useDelimiter("\\A");
        	html = s.hasNext() ? s.next() : "";
        	s.close();
        } catch (IOException e) {
        	e.printStackTrace();
		}
       
        httpget.releaseConnection();
		return html;
	}
}