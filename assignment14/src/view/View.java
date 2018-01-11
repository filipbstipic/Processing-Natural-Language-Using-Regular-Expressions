package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import controller.Controller;
import model.Model;

public class View extends JFrame implements Observer {

	// We create the necessary fields
	private Model model;

	private DefaultListModel<String> remindersListModel;
	private DefaultListModel<String> eventsListModel;
	private JPanel jpReminders;
	private JPanel jpCalender;

	public View(Model model, Controller controller) {

		// we call the super constructor
		super();
		this.model = model;
		// We want the application to exit on close
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(500, 500));
		setVisible(true);
		// the observer of the model is this class
		model.addObserver(this);

		initWidgets(controller);
	}

	private void initWidgets(Controller controller) {

		// Creating the tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane);
		// Creating panels
		jpCalender = new JPanel();
		jpReminders = new JPanel();

		// setting the layout
		jpCalender.setLayout(new BorderLayout());
		jpReminders.setLayout(new BorderLayout());

		// we will use the DefaultListModel for the JList because it has a lot
		// of useful methods
		remindersListModel = new DefaultListModel<String>();
		// We reach into the reminders list and then add all the elements of
		// it to the remindersListModel
		if (model.getReminders() != null) {

			for (String s : model.getReminders()) {
				remindersListModel.addElement(s);

			}
		}
		// We create a new JList and pass it the remindersListModel
		JList<String> jlReminders = new JList<String>(remindersListModel);

		// We name the JList because we might need to access it later on
		jlReminders.setName("Reminders");
		// We add the Mouse listener which is the controller class- we need this
		// to detect double clicking
		jlReminders.addMouseListener(controller);

		// We can only select one option at a time
		jlReminders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// We add the scroll pane to the JList
		JScrollPane scrollPane1 = new JScrollPane(jlReminders);
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jpReminders.add(scrollPane1);

		// add the jtextfiled
		JTextField jtfInputReminder = new JTextField();
		jpReminders.add(jlReminders, BorderLayout.CENTER);
		jpReminders.add(jtfInputReminder, BorderLayout.SOUTH);

		// From here on everything is symmetrical
		eventsListModel = new DefaultListModel<String>();
		if (!model.getEvents().isEmpty()) {

			for (String s : model.getEvents()) {
				eventsListModel.addElement(s);

			}
		}
		JList<String> jlEvents = new JList<String>(eventsListModel);
		jlEvents.addMouseListener(controller);
		jlEvents.setName("Events");

		jlEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// add the scrollPane
		JScrollPane scrollPane2 = new JScrollPane(jlEvents);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jpCalender.add(scrollPane2);

		// Add jtext field
		JTextField jtfInputEvent = new JTextField();
		jpCalender.add(jlEvents, BorderLayout.CENTER);
		jpCalender.add(jtfInputEvent, BorderLayout.SOUTH);

		// Add listeners
		jtfInputReminder.addActionListener(controller);
		jtfInputEvent.addActionListener(controller);

		// Add panels as tabs
		tabbedPane.addTab("Reminders", jpReminders);
		tabbedPane.addTab("Calender", jpCalender);

		pack();
	}

	// Updates the JList models which update the JLists we see on the screen
	public void update(Observable o, Object arg) {
		boolean isReminder = (boolean) arg;
		// A lot of potential optimisation as every time an update occurs we are
		// overwriting everything from the listModels. However it does not break
		// the mvc as we are only reading from the core ArrayLists: the
		// reminders and the events
		if (isReminder) {

			remindersListModel.clear();
			for (String s : model.getReminders()) {
				remindersListModel.addElement(s);
			}
		} else {
			eventsListModel.clear();
			for (String s : model.getEvents()) {
				eventsListModel.addElement(s);
			}
		}
		// updateUIs
		jpReminders.updateUI();
		jpCalender.updateUI();
	}

}
