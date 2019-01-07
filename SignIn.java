import java.io.Serializable;
/**
 * Models an Event class to create events and save events, with few attributes
 * of name, day and times.
 * 
 * @author yen_my_huynh 1/5/2019
 */
public class SignIn implements Comparable<SignIn>, Serializable {
	private String name;
	private String sId;

	// constants for months view.
	enum MONTHSVIEW {
		January, February, March, April, May, June, July, August, September, October, November, December;
	}

	// constants for days view.
	enum DAYS {
		Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
	}

	/**
	 * Constructs an event with a name, day, and times.
	 * @param name the name of the student
	 * @param sId the student id
	 */
	public SignIn(String name, String sId) {
		this.name = name;
		this.sId = sId;

	}

	/**
	 * Gets the student's name.
	 * @return the student's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the student's id.
	 * @return the student's id
	 */
	public String getId() {
		return sId;
	}

	/**
	 * Compares the order of the sign-ins by 
	 * @param x the other sign-in
	 * @return the order of sign-ins in the map
	 */
	public int compareTo(SignIn x) {
		SignIn other = (SignIn) x;
		int nameOrder = this.name.compareTo(other.name);
		if (nameOrder != 0){
				return this.sId.compareTo(other.sId);
		}
		return nameOrder;		
	}

	/**
	 * Check if two sign-ins are equal to each other.
	 * @param x the other sign-in
	 * @return true if two sign-ins are equal, if not, false
	 */
	public boolean equals(Object x) {
		SignIn other = (SignIn) x;
		return this.name.compareTo(other.name) == 0;
	}

	/**
	 * Formats out the name and id for each sign-in. If any
	 */
	public String toString() {
		return String.format("%-35s %s", name, sId);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
