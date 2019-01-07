import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * View and controller for Calendar. Extra methods for fun.
 * 
 * @author yen_my_huynh 1/5/2019
 */
public class MySimpleFormView implements ChangeListener {
	// constants for months view.
	enum MONTHSVIEW {
		January, February, March, April, May, June, July, August, September, October, November, December;
	}

	// constants for event list view.
	enum DAYS {
		Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
	}

	private MySimpleFormModel model;
	private DAYS[] arrayOfDays = DAYS.values();
	private MONTHSVIEW[] arrayOfMonths = MONTHSVIEW.values();
	private int prevHighlight = -1;
	private int maxDays;

	private JFrame frame = new JFrame("CS46A S.I. Session");
	private JPanel monthViewPanel = new JPanel();
	private JLabel monthLabel = new JLabel();
	private JButton create = new JButton("Sign In");
	private JButton nextDay = new JButton(">");
	private JButton prevDay = new JButton("<");
	private JTextPane dayTextPane = new JTextPane();
	private ArrayList<JButton> dayBtns = new ArrayList<JButton>();

	/**
	 * Constructs the calendar.
	 * @param model the model that stores and manipulates calendar data
	 */
	public MySimpleFormView(MySimpleFormModel model) {
		this.model = model;
		maxDays = model.getMonthDays();
		monthViewPanel.setLayout(new GridLayout(0, 7));
		dayTextPane.setPreferredSize(new Dimension(200, 150));
		dayTextPane.setEditable(false);

		// calendar month view buttons
		addBlankButtons();
		createDayButtons();
		addDayButtons();
		showDate(model.getSelectedDay());
		highlightSelectedDate(model.getSelectedDay() - 1);

		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSignInDialog();
			}
		});
		
		// for month views
		JButton prevMonth = new JButton("<<");
		prevMonth.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				model.prevMonth();
				create.setEnabled(false);
				nextDay.setEnabled(false);
				prevDay.setEnabled(false);
				dayTextPane.setText("");
			}
		});
		JButton nextMonth = new JButton(">>");
		nextMonth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextMonth();
				create.setEnabled(false);
				nextDay.setEnabled(false);
				prevDay.setEnabled(false);
				dayTextPane.setText("");
			}
		});

		JPanel monthContainer = new JPanel();
		monthContainer.setLayout(new BorderLayout());
		monthLabel.setText(arrayOfMonths[model.getCurrentMonth()] + " " + model.getCurrentYear());
		monthContainer.add(monthLabel, BorderLayout.NORTH);
		monthContainer.add(
				new JLabel(
						"        S                 M                 T                W                T                 F                 S"),
				BorderLayout.CENTER);
		monthContainer.add(monthViewPanel, BorderLayout.SOUTH);

		JPanel dayViewPanel = new JPanel();
		dayViewPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		JScrollPane dayScrollPane = new JScrollPane(dayTextPane);
		dayScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		dayViewPanel.add(dayScrollPane, c);
		JPanel btnsPanel = new JPanel();
		nextDay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextDay();
			}
		});
		prevDay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.prevDay();
			}
		});
		btnsPanel.add(prevDay);
		create.setBackground(Color.RED);
		create.setOpaque(true);
		create.setForeground(Color.WHITE);
		create.setBorderPainted(false);
		btnsPanel.add(nextDay);
		btnsPanel.add(create);
		btnsPanel.add(prevMonth);
		btnsPanel.add(nextMonth);
		c.gridx = 0;
		c.gridy = 1;
		dayViewPanel.add(btnsPanel, c);

		// creates a quit button to saves all events.
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		// creates a delete button to delete all sign-ins of a selected day.
		JButton deleteAll = new JButton("Delete All Sign-Ins");
		deleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteAll();
			}
		});

		frame.add(monthContainer);
		frame.add(dayViewPanel);
		frame.add(quit);
		frame.add(deleteAll);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Changes the state of day view after a button is clicked.
	 */
	public void stateChanged(ChangeEvent e) {
		if (model.isMonthChanged()) {
			maxDays = model.getMonthDays();
			dayBtns.clear();
			monthViewPanel.removeAll();
			monthLabel.setText(arrayOfMonths[model.getCurrentMonth()] + " " + model.getCurrentYear());
			createDayButtons();
			addBlankButtons();
			addDayButtons();
			prevHighlight = -1;
			model.resetTime();
			frame.pack();
			frame.repaint();
		} else {
			showDate(model.getSelectedDay());
			highlightSelectedDate(model.getSelectedDay() - 1);
		}
	}

	/**
	 * Creates an sign-in on the selected date through user input.
	 */
	private void createSignInDialog() {
		final JDialog signInDialog = new JDialog();
		signInDialog.setTitle("CS46A S.I. Session");
		signInDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		final JTextField nameText = new JTextField(35);
		final JTextField sIDText = new JTextField(10);
		JButton submit = new JButton("SUBMIT");
		submit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (nameText.getText().isEmpty()) {
					return;
				}
				String name = nameText.getText();
				String sid = sIDText.getText();
				Calendar day = new GregorianCalendar(model.getCurrentYear(), model.getCurrentMonth(),
						model.getSelectedDay());
				SignIn signIn = new SignIn(name, sid);

				// checks for correct format as specified
				if (checkFormat(name, sid)){
					signInDialog.dispose();
					model.createSignIn(day, signIn);
					showDate(model.getSelectedDay());
				}
			}
		});
		signInDialog.setLayout(new GridBagLayout());
		JLabel date = new JLabel();
		date.setText(model.getCurrentMonth() + 1 + "/" + model.getSelectedDay() + "/" + model.getCurrentYear());
		date.setBorder(BorderFactory.createEmptyBorder());

		// layers out the create box.
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		signInDialog.add(date, c);
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		signInDialog.add(new JLabel("Name:"), c);
		c.gridy = 2;
		signInDialog.add(nameText, c);
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		signInDialog.add(new JLabel("ID#"), c);
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		signInDialog.add(sIDText, c);
		c.anchor = GridBagConstraints.LINE_END;
		signInDialog.add(submit, c);
		signInDialog.pack();
		signInDialog.setVisible(true);
	}

	/**
	 * Shows the selected date and events on that date.
	 * @param date the selected date
	 */
	private void showDate(int date) {
		model.setSelectedDay(date);
		String dayOfWeek = arrayOfDays[model.getCurrentDayofWeek(date) - 1] + "";
		Calendar day = new GregorianCalendar(model.getCurrentYear(), model.getCurrentMonth(), date);
		String dateString = (model.getCurrentMonth() + 1) + "/" + date;
		String events = "";
		if (model.hasSignIns(day)) {
			events += model.viewEventFromDay(day);
		}
		dayTextPane.setText(dayOfWeek + " " + dateString + "\n" + events);
		dayTextPane.setCaretPosition(0);
	}

	/**
	 * Highlights the currently selected date.
	 * @param d the currently selected date
	 */
	private void highlightSelectedDate(int date) {
		Border border = new LineBorder(Color.ORANGE, 2);
		dayBtns.get(date).setBorder(border);
		if (prevHighlight != -1) {
			dayBtns.get(prevHighlight).setBorder(new JButton().getBorder());
		}
		prevHighlight = date;
	}

	/**
	 * Creates buttons representing days of the current month and adds them to
	 * an array list.
	 */
	private void createDayButtons() {
		for (int i = 1; i <= maxDays; i++) {
			final int d = i;
			JButton day = new JButton(Integer.toString(d));
			day.setBackground(Color.WHITE);

			day.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent ae) {
					showDate(d);
					highlightSelectedDate(d - 1);
					create.setEnabled(true);
					nextDay.setEnabled(true);
					prevDay.setEnabled(true);
				}
			});
			dayBtns.add(day);
		}
	}

	/**
	 * Adds days buttons of the month to the panel.
	 */
	private void addDayButtons() {
		for (JButton day : dayBtns) {
			monthViewPanel.add(day);
		}
	}

	/**
	 * Adds dummy buttons to align the date to correct day in the calendar.
	 */
	private void addBlankButtons() {
		for (int i = 1; i < model.getCurrentDayofWeek(1); i++) {
			JButton blank = new JButton();
			blank.setEnabled(false);
			monthViewPanel.add(blank);
		}
	}

	/**
	 * Checks for correct format as specified. 
	 * @param nameText the student's name
	 * @param sIDText the student's id
	 */
	public boolean checkFormat(String nameText, String sIDText) {
		if ((!nameText.isEmpty() && (sIDText.isEmpty())) || nameText.isEmpty() || sIDText.length() != 9
				|| !sIDText.matches("([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])")) {
			JDialog idErrorDialog = new JDialog();
			idErrorDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			idErrorDialog.setLayout(new GridLayout(2, 0));
			idErrorDialog.add(new JLabel("Please enter 9-digit student ID number."));
			JButton okButton = new JButton("Okay");
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					idErrorDialog.dispose();
				}
			});
			idErrorDialog.add(okButton);
			idErrorDialog.pack();
			idErrorDialog.setVisible(true);
			return false;
		}
		return true;
	}

	/**
	 * Quits and saves the program.
	 */
	public void quit() {
		try {
			model.quit();
			JDialog quitDialog = new JDialog();
			quitDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			quitDialog.setLayout(new GridLayout(2, 0));
			quitDialog.add(new JLabel("Everything is saved in 'events.txt.' file."));
			JButton okButton = new JButton("Okay");
			okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					quitDialog.dispose();
				}
			});
			quitDialog.add(okButton);
			quitDialog.pack();
			quitDialog.setVisible(true);
			System.exit(0);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Deletes all sign-ins for this day.
	 */
	public void deleteAll() {
		Calendar day = new GregorianCalendar(model.getCurrentYear(), model.getCurrentMonth(), model.getSelectedDay());
		model.deleteSignIns(day);
		JDialog deleteDialog = new JDialog();
		deleteDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		deleteDialog.setLayout(new GridLayout(1, 0));
		deleteDialog.add(new JLabel("Everything is clear for this day."));
		JButton okButton = new JButton("Okay");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				deleteDialog.dispose();
			}
		});
		deleteDialog.add(okButton);
		deleteDialog.pack();
		deleteDialog.setVisible(true);
	}
}