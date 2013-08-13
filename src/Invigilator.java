import java.util.ArrayList;
import java.util.Comparator;

/*
 * Created on Aug 7, 2013 5:57:01 PM
 */

/**
 * @author Olga Tsibulevskaya
 *
 */
public class Invigilator {
	
	private int id;
	private String name;
	private String email;
	private String phone;
	private int assignments;
	private ArrayList<String> availability = new ArrayList<String>();
	private ArrayList<Boolean> morning = new ArrayList<Boolean>();
	private ArrayList<Boolean> afternoon = new ArrayList<Boolean>();
	private ArrayList<Boolean> both = new ArrayList<Boolean>();
	private ArrayList<Boolean> second = new ArrayList<Boolean>();
	/**
	 * 
	 */
	public Invigilator() {
		assignments = 0;
	}
	public Invigilator(Invigilator one) {
		this.id = one.id;
		this.name = one.name;
		this.email = one.email;
		this.phone = one.phone;
	}
	public int getId() {
		return id;
	}
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getAssignments() {
		return assignments;
	}
	public void setAssignments(int ass) {
		assignments = ass;
	}
	public void incrementAssignments() {
		++assignments;
	}
	public ArrayList<String> getAvailability() {
		return availability;
	}
	public void setAvailability(ArrayList<String> availability) {
		this.availability = availability;
	}
	public ArrayList<Boolean> getMorning() {
		return morning;
	}
	public ArrayList<Boolean> getAfternoon() {
		return afternoon;
	}
	public ArrayList<Boolean> getBoth() {
		return both;
	}
	public ArrayList<Boolean> getSecond() {
		return second;
	}
	public void setArrays() {
		
	}
	public String toString() {
		String total = "";
		for (String s : availability)
			total += s + "\n";
		return name + "\n" + total;
	}
	public static class InvAssignsComparator implements Comparator<Invigilator> {
		public int compare(Invigilator inv1, Invigilator inv2) {
			Integer ass1 = inv1.getAssignments();
			Integer ass2 = inv2.getAssignments();
			return ass1.compareTo(ass2);  
		}
	}
}
