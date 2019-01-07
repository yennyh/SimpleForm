import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * To create a calendar model like the one in the phone. Models a calendar class
 * to load, view dates, create events, go to events, delete and save events.
 * (STORE DATA ONLY)
 * 
 * @author yen_my_huynh 11/20/2017
 */

public class MySimpleFormModel implements Serializable {
	// constants for months view.
	enum MONTHSVIEW {
		January, February, March, April, May, June, July, August, September, October, November, December;
	}

	// constants for event list view.
	enum DAYS {
		Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
	}

	private Calendar cal;
	private TreeMap<Calendar, TreeSet<SignIn>> signInList;
	private ArrayList<ChangeListener> listeners = new ArrayList<>();
	private int selectedDay;
	private int totalDays;
	private boolean monthChanged = false;

	/**
	 * Constructs a new calendar and a map to save all events.
	 */
	public MySimpleFormModel() {
		cal = new GregorianCalendar();
		cal = Calendar.getInstance();
		signInList = new TreeMap<>();
		selectedDay = cal.get(Calendar.DATE);
		totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// loads any files if any when calls on.
		try {
			load();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a change listener to the list of texts.
	 * @param listener the change listener to add
	 */
	public void attach(ChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Updates all ChangeListeners in arraylist.
	 */
	public void updateCal() {
		for (ChangeListener l : listeners) {
			l.stateChanged(new ChangeEvent(this));
		}
	}

	/**
	 * Resets the time to current time/date (month view).
	 */
	public void resetTime() {
		monthChanged = false;
	}

	/**
	 * Gets the current year.
	 * @return the current year
	 */
	public int getCurrentYear() {
		return cal.get(Calendar.YEAR);
	}

	/**
	 * Gets the current month.
	 * @return the current month
	 */
	public int getCurrentMonth() {
		return cal.get(Calendar.MONTH);
	}

	/**
	 * Gets the current day of week.
	 * @param month the month to get the day
	 * @return the current day of week
	 */
	public int getCurrentDayofWeek(int month) {
		cal.set(Calendar.DAY_OF_MONTH, month);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Gets the total days of entered month.
	 * @return the number of days of this month
	 */
	public int getMonthDays() {
		return totalDays;
	}

	/**
	 * Sets selected day of the month for highlighting.
	 * @param day the selected day
	 */
	public void setSelectedDay(int day) {
		selectedDay = day;
	}

	/**
	 * Gets the selected day of the month.
	 * @return the selected day
	 */
	public int getSelectedDay() {
		return selectedDay;
	}

	/**
	 * Checks if the month has changed or not. 
	 * @return true if month has changed, otherwise false
	 */
	public boolean isMonthChanged() {
		return monthChanged;
	}

	/**
	 * First time loading will create a new text file then saved data 
	 * using 'quit()' later. When user loads for a second time after using 'quit()', 
	 * pre-existing sign-ins will appear.
	 * @throws IOException throws IOException
	 * @throws FileNotFoundException throws if file not found
	 */
	public void load() throws FileNotFoundException, IOException {
		ObjectInputStream fis = null;
		try {
			fis = new ObjectInputStream(new FileInputStream("/Users/yen_my_huynh/Documents/SIWork/events.txt"));
		} catch (IOException e) {
			signInList.clear();
		}
		try {
			if (fis != null) {
				FileInputStream streamIn = new FileInputStream("/Users/yen_my_huynh/Documents/SIWork/events.txt");
				fis = new ObjectInputStream(streamIn);
				TreeMap<Calendar, TreeSet<SignIn>> list = (TreeMap<Calendar, TreeSet<SignIn>>) fis.readObject();
				signInList = list;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes and updates to the previous month.
	 */
	public void prevMonth() {
		cal.add(Calendar.MONTH, -1);
		totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		monthChanged = true;
		updateCal();
	}

	/**
	 * Changes and updates to the next month.
	 */
	public void nextMonth() {
		cal.add(Calendar.MONTH, 1);
		totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		monthChanged = true;
		updateCal();
	}

	/**
	 * Changes and updates to the previous day.
	 */
	public void prevDay() {
		selectedDay--;
		if (selectedDay < 1) {
			prevMonth();
			selectedDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		updateCal();
	}

	/**
	 * Changes and updates to the next day.
	 */
	public void nextDay() {
		selectedDay++;
		if (selectedDay > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			nextMonth();
			selectedDay = 1;
		}
		updateCal();
	}

	/**
	 * Creates a sign-in with or without existing sign-ins to a list.
	 * @param day the day of the event to be added
	 * @param sIn the existing sign-in in the map if any
	 */
	public void createSignIn(Calendar day, SignIn sIn) {
		if (this.signInList.get(day) == null) {
			TreeSet<SignIn> signInHolder = new TreeSet<>();
			signInHolder.add(sIn);
			signInList.put(day, signInHolder);
		}
		this.signInList.get(day).add(sIn);
	}

	/**
	 * Checks if there is a sign-in on a selected date.
	 * @return true if there's a sign-in, otherwise false
	 */
	public boolean hasSignIns(Calendar day) {
		return signInList.containsKey(day);
	}

	/**
	 * Views only events from this date.
	 * @param day the day to check if there are any events
	 */
	public String viewEventFromDay(Calendar day) {
		String events = "";
		if (signInList.get(day) != null) {
			for (SignIn e : signInList.get(day)) {
				events += e.toString() + "\n";
			}
		}
		return events;
	}

	/**
	 * Deletes sign-ins from the list on a selected day.
	 * @param day the day to delete existing sign-ins if any
	 */
	public void deleteSignIns(Calendar day) {
		signInList.remove(day);

	}

	/**
	 * Quits and saves "events.txt" to populate the calendar the first time.
	 * Quits and saves "event.txt" again when called and added in more events.
	 * @throws IOException throws IOException
	 * @throws FileNotFoundException throws if no file is found
	 */
	public void quit() throws FileNotFoundException, IOException {
		try (FileOutputStream f = new FileOutputStream("/Users/yen_my_huynh/Documents/SIWork/events.txt")) {
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(signInList);
			s.close();
			f.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
}
