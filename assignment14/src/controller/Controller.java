package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JTextField;


import model.Model;

public class Controller implements MouseListener, ActionListener {

	private Model model;

	public Controller(Model model) {

		this.model = model;
	}

	public void mouseClicked(MouseEvent e) {
		// We access the source and cast it as JList
		@SuppressWarnings("unchecked")
		JList<String> list = ((JList<String>) (e.getSource()));
		// We check the name and the clickCount and procees correspondingly
		if (e.getClickCount() == 2 && list.getName().equals("Reminders")) {
			// we get the index of the location where the mouse was clicked and
			// map it to an index
			int index = list.locationToIndex(e.getPoint());
			// We remove element with that index from our records
			model.removeElementFromReminders(index);

		}
		// The same applies if click happens in the events pane
		else if (e.getClickCount() == 2 && list.getName().equals("Events")) {
			int index = list.locationToIndex(e.getPoint());
			model.removeElementFromEvents(index);

		}
	}

	// We need to implement these method in order to implement the MouseListener
	// interface but we wont be using them
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Get the text that has been written into the JTextField if the user
		// pressed the return button
		String text = ((JTextField) (e.getSource())).getText();
		// pass it to the model
		model.processText(text);

	}

}
