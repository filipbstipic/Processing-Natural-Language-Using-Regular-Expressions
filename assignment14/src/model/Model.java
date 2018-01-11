package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model extends Observable {

	// We initialize the necessary fields
	private Pattern dayPattern1;
	private Pattern dayPattern2;
	private Pattern dayPattern3;
	private String input;

	private Pattern datePattern1;
	private Pattern datePattern2;
	private Pattern datePattern3;

	private Pattern timePattern1;
	private Pattern timePattern2;

	private File remindersFile;
	private File eventsFile;
	private Pattern locationPattern;

	private PrintWriter remindersWriter;
	private PrintWriter eventsWriter;

	private ArrayList<String> reminders;
	private ArrayList<String> events;

	public Model() {
		input = "";
		// Here we compile all the patterns that we will use afterwards
		String dayRegex = "([Mm]onday|[Tt]uesday|[Ww]ednesday|[Tt]hursday|[Ff]riday|[Ss]aturday|[Ss]unday)";
		dayPattern1 = Pattern.compile("\\b[Oo]n\\s*" + dayRegex);
		dayPattern2 = Pattern.compile(dayRegex);
		dayPattern3 = Pattern.compile("\\b[Nn]ext\\s*" + dayRegex);

		datePattern1 = Pattern.compile("(\\d{1,2}/){2}\\d{1,4}");
		// a date might be 0th!
		datePattern2 = Pattern.compile(dayRegex
				+ "\\s*(([12]?[04-9]th)|30th|([123]?1st)|([12]?2nd)|([12]?3rd))(\\s+)([Jj]anuary|[Mm]arch|[Mm]ay|[Jj]uly|[Aa]ugust|[Oo]ctober|[Dd]ecember)");
		datePattern3 = Pattern.compile(dayRegex
				+ "\\s*((([12]?[04-9]th)|(30th)|([12]?1st)|([12]?2nd)|([12]?3rd)(\\s+))([Aa]pril|[Jj]une|[Ss]eptember|[Nn]ovember))|((([12]?[04-9]th)|([12]?1st)|([12]?2nd)|([12]?3rd))[Ff]ebruary)");
		timePattern1 = Pattern.compile("\\s+((0\\d)|(1\\d)|(2[0123])):([0-5]\\d)\\s+");
		timePattern2 = Pattern.compile("\\b((\\d)|(1[012]))[ap]m\\b");
		locationPattern = Pattern.compile("\\bat(\\s)+[a-zA-Z']+");

		// these arrayLists will store all the end products -when a reminder or
		// an event has been created it is added to either one of these lists
		reminders = new ArrayList<String>();
		events = new ArrayList<String>();
		try {
			// we open new files and Create bufferedReaders
			remindersFile = new File("src/model/Reminders.txt");
			eventsFile = new File("src/model/Events.txt");

			FileReader remindersReader = new FileReader(remindersFile);
			FileReader eventsReader = new FileReader(eventsFile);

			BufferedReader remindersBr = new BufferedReader(remindersReader);
			BufferedReader eventsBr = new BufferedReader(eventsReader);
			String line;
			// We read each line in the reminder class and store it in the
			// reminders ArrayList
			while ((line = remindersBr.readLine()) != null) {
				reminders.add(line);

			}
			// we do the same thing for the events file
			while ((line = eventsBr.readLine()) != null) {
				events.add(line);

			}
			// I decided to use the printWriter class because it is the most
			// convenient one to use. However, the downside
			remindersWriter = new PrintWriter(remindersFile);
			eventsWriter = new PrintWriter(eventsFile);

			for (String s : reminders) {
				remindersWriter.println(s);
			}
			remindersWriter.flush();

			for (String s : events) {
				eventsWriter.println(s);
			}
			eventsWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void processText(String input) {
		boolean isReminder;
		this.input = input;
		String output = "";
		if (this.input.contains("Remind me to") || this.input.contains("remind me to")) {
			// TODO
			this.input = this.input.replaceFirst("Remind me to", "");
			this.input = this.input.replaceFirst("remind me to", "");

			output = " Date: " + processDate() + " | " + " Time: " + processTime() + " | " + " Location: "
					+ processLocation();
			// If there is no additional inforation to parse just print out the
			// event without the unnecessary hyphens
			if (output.equals(" Date: " + "-" + " | " + " Time: " + "-" + " | " + " Location: " + "-")) {

				output = "Event: " + this.input.replaceAll("\\s{2,}", " ");
			} else {
				// Replace all the additional spaces with monospaces. Also trim
				// the output just in case
				output = "Event: " + this.input.replaceAll("\\s{2,}", " ") + " | " + output.trim();
			}
			// add the output to reminders
			reminders.add(output);
			// print to the file
			remindersWriter.println(output);
			isReminder = true;
			// flush the changes to the file
			remindersWriter.flush();

		} else {
			// everything is the same here except for replacing the remind me
			// part
			output = " Date: " + processDate() + " | " + " Time: " + processTime() + " | " + " Location: "
					+ processLocation();
			output = "Event: " + this.input.replaceAll("\\s{2,}", " ") + " | " + output.trim();

			events.add(output);
			eventsWriter.println(output);
			isReminder = false;
			eventsWriter.flush();
		}
		// Notify observers with the additional parameter of isReminder which
		// will tell the View is it dealing with a reminder or an event
		setChanged();
		notifyObservers(isReminder);

	}

	/**
	 * this method is removing the element from the reminders list and then
	 * calling the writetofile so the changes can be commited. It also notifies
	 * the observers
	 * 
	 * @param int
	 *            index n
	 */
	public void removeElementFromReminders(int n) {
		if (n >= 0 && n < reminders.size()) {
			reminders.remove(n);
			writeToFile(remindersFile);
			setChanged();
			notifyObservers(true);
		}
	}

	/**
	 * this method is removing the element from the reminders list and then
	 * calling the writetofile so the changes can be commited. It also notifies
	 * the observers
	 * 
	 * @param int
	 *            index n
	 */
	public void removeElementFromEvents(int n) {
		if (n >= 0 && n < events.size()) {
			events.remove(n);
			writeToFile(eventsFile);
			setChanged();
			notifyObservers(false);
		}
	}

	private void writeToFile(File file) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(file);
			if (file == remindersFile) {
				for (String s : reminders)
					pw.println(s);
			} else {
				for (String s : events)
					pw.println(s);
			}
			pw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns the reminders
	 * 
	 * @return reminders
	 */
	public ArrayList<String> getReminders() {

		return reminders;
	}

	/**
	 * Returns the events
	 * 
	 * @return events
	 */
	public ArrayList<String> getEvents() {

		return events;
	}

	/**
	 * Extracts the date in the input string and returns it. If it is not found
	 * it returns "-"
	 * 
	 * @return date
	 */
	private String processDate() {

		String date = "";

		Matcher dayMatcher1 = dayPattern1.matcher(input);
		Matcher dayMatcher3 = dayPattern3.matcher(input);

		Matcher dateMatcher1 = datePattern1.matcher(input);
		Matcher dateMatcher2 = datePattern2.matcher(input);
		Matcher dateMatcher3 = datePattern3.matcher(input);

		if (dateMatcher1.find()) {
			date += turnToDesirableFormat(dateMatcher1.group(), false);
			input = dateMatcher1.replaceFirst("");
		}

		else if (dateMatcher2.find()) {
			date += dateMatcher2.group();
			input = dateMatcher2.replaceFirst("");
		}

		else if (dateMatcher3.find()) {
			date += dateMatcher3.group();
			input = dateMatcher3.replaceFirst("");
		}

		else if (dayMatcher1.find()) {
			date += getDateFromDay(dayMatcher1.group(), false);
			input = dayMatcher1.replaceFirst("");
		} else if (dayMatcher3.find()) {
			date += getDateFromDay(dayMatcher3.group(), true);
			input = dayMatcher3.replaceFirst("");
		} else {
			date = "-";
		}
		return date;

	}

	/**
	 * This method turns the expression next (specify day) or on (specify day)
	 * to a valid date
	 * 
	 * @param s
	 * @param isNext
	 * @return
	 */
	private String getDateFromDay(String s, boolean isNext) {
		// We create the necessary mathcers
		Matcher dayMatcher2 = dayPattern2.matcher(s);
		String day = "";
		LocalDate localDate = LocalDate.now();
		DayOfWeek currentDayOfWeek = localDate.getDayOfWeek();

		if (dayMatcher2.find()) {

			day = dayMatcher2.group();
			day = day.trim();
			// we turn the day that we found in the string to a day using the
			// whichDay Method
			int ordinalDay = whichDay(day);
			// If the ordinal day of the current day of the week is less than
			// the ordinal input day and we are not looking a week ahead then
			// just subtract the values
			if (currentDayOfWeek.getValue() < ordinalDay && isNext == false) {
				localDate = localDate.plusDays(ordinalDay - currentDayOfWeek.getValue());

				// In a similar case if the value of the current day of the week
				// is bigger than the value of the ordinalDay add seven and
				// subtract ordinalday to currentDay
			} else if (currentDayOfWeek.getValue() > ordinalDay && isNext == false) {
				localDate = localDate.plusDays(7 + (ordinalDay - currentDayOfWeek.getValue()));
				// Further on everything is the same, only the flag has changed
				// indicating that we should skip 7 days into the next week
			} else if (currentDayOfWeek.getValue() < ordinalDay && isNext == true) {
				localDate = localDate.plusDays(7 + ordinalDay - currentDayOfWeek.getValue());

			} else if (currentDayOfWeek.getValue() > ordinalDay && isNext == true) {
				localDate = localDate.plusDays(14 + (ordinalDay - currentDayOfWeek.getValue()));

			}

		}
		// Before we actually return the String we need to turn it into a
		// desirable format, as specified by Martin
		return turnToDesirableFormat(localDate.toString(), true);

	}

	/**
	 * Turns the String into the format of [Day][Date][Month]. returns "-" if
	 * the String is not a valid date. If the date is in the format
	 * YEAR/MONTH/DAY put the boolean flag is reverse to true
	 * 
	 * @param s
	 * @return date
	 */
	private String turnToDesirableFormat(String s, boolean isReverse) throws DateTimeParseException {
		LocalDate localDate;
		String returnString;
		if (isReverse) {

			localDate = LocalDate.parse(s);
			returnString = localDate.getDayOfWeek().toString() + " " + addSuffix(localDate.getDayOfMonth()) + " "
					+ localDate.getMonth();
		} else {
			String[] dateArray = s.split("/");
			try {
				localDate = LocalDate.parse(dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0]);
				returnString = localDate.getDayOfWeek().toString() + " " + addSuffix(localDate.getDayOfMonth()) + " "
						+ localDate.getMonth();
			} catch (DateTimeParseException e) {
				returnString = "-";
			}
		}
		return returnString;

	}

	/**
	 * Creates a string form the number that is given as a parameter that
	 * represents how that number is suffixed as an ordinal number (works only
	 * for 1 ore 2 digits numbers)
	 * 
	 * @param n
	 * @return
	 */
	private String addSuffix(int n) {
		if (n % 10 == 1) {
			return n + "st";
		} else if (n % 10 == 2) {
			return n + "nd";
		} else if (n % 10 == 3) {
			return n + "rd";
		} else {
			return n + "th";
		}

	}

	/**
	 * Compiles the necessary patterns and uses the Matcher objects to find a
	 * valid time
	 * 
	 * @return time
	 */
	private String processTime() {

		String time = "";
		Matcher timeMatcher1 = timePattern1.matcher(input);
		Matcher timeMatcher2 = timePattern2.matcher(input);
		Matcher eveningMatcher = Pattern.compile("\\b(in\\s+the\\s+)?[Ee]vening").matcher(input);
		Matcher morningMatcher = Pattern.compile("\\b(in\\s+the\\s+)?[Mm]orning").matcher(input);
		// The first catches patttern that are in the pm/am format. We invoke
		// the turnTo24hFormat method to make it 24 hour format
		if (timeMatcher1.find()) {
			time += timeMatcher1.group();
			input = timeMatcher1.replaceFirst("");
		}
		// We check the rest of the patterns
		else if (timeMatcher2.find()) {
			time += turnTo24hFormat(timeMatcher2.group());
			input = timeMatcher2.replaceFirst("");
		} else if (eveningMatcher.find()) {
			time += "20:00";
			input = eveningMatcher.replaceFirst("");
		} else if (morningMatcher.find()) {
			time += "09:00";
			input = morningMatcher.replaceFirst("");
		} else {
			time = "-";
		}
		// And then return time in the end
		return time;
	}

	/**
	 * Turns the input that is in the pm/am format to a 24 hour format. might
	 * throw errors
	 * 
	 * @param s
	 * @return
	 */
	private String turnTo24hFormat(String s) {

		if (s.contains("am")) {

			return s.replace("am", "") + ":00";
		} else {
			try {
				int n = Integer.parseInt(s.replace("pm", "")) + 12;
				return n + ":00";
			} catch (NumberFormatException e) {
				return "";
			}
		}

	}

	/**
	 * Tries to find the location at specified in the constructor of the
	 * locationPattern. If it succeeds it returns the location as a String. If
	 * it fails it returns "-"
	 * 
	 * @return location
	 */
	private String processLocation() {

		String location = "";
		Matcher locationMatcher = locationPattern.matcher(input);

		if (locationMatcher.find()) {
			location = locationMatcher.group();
			// replace the at with the blank, we don't need it
			location = location.replaceFirst("\\s*at\\s+", "");
			input = locationMatcher.replaceFirst("");
		} else {
			location = "-";
		}
		return location;

	}

	/**
	 * This method turns a day in the week into an integer: 1 if it's monday, 2
	 * if it's Tuesday , etc. if the String is not a valid day of the week, it
	 * returns -1
	 * 
	 * @param day
	 * @return int
	 */
	private int whichDay(String day) {

		if (day.equals("Sunday") | day.equals("sunday")) {

			return 7;

		} else if (day.equals("Monday") | day.equals("monday")) {

			return 1;

		} else if (day.equals("Tuesday") | day.equals("tuesday")) {

			return 2;

		} else if (day.equals("Wednesday") | day.equals("wednesday")) {

			return 3;

		} else if (day.equals("Thursday") | day.equals("thursday")) {

			return 4;

		} else if (day.equals("Friday") | day.equals("friday")) {

			return 5;

		} else if (day.equals("Saturday") | day.equals("saturday")) {

			return 6;

		} else {

			return -1;
		}

	}

}
