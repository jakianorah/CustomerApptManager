/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import c195application.model.Reminder;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class AddReminderController  {

    private Stage stage;

	private Reminder reminder;
	private int appointmentId;
	private LocalDateTime start;

	@FXML
	private TextField amount;
	@FXML
	private ComboBox<ChronoUnit> unit;

	public AddReminderController() {}

	
	@FXML
	private void initialize() {
		ObservableList<ChronoUnit> units = FXCollections.observableArrayList();
		units.add(ChronoUnit.MINUTES);
		units.add(ChronoUnit.HOURS);
		units.add(ChronoUnit.DAYS);

		unit.setItems(units);
		unit.getSelectionModel().select(ChronoUnit.MINUTES);
	}

	public void setupDialog(Stage stage, int apptId, LocalDateTime apptTime) {
		this.stage = stage;
		appointmentId = apptId;
		start = apptTime;
	}

	public Reminder getReminder() {
		return reminder;
	}

	@FXML
	private void handleSave() {
		start = start.minus(Integer.parseInt(amount.getText()), unit.getValue());
		
		reminder = new Reminder(start, appointmentId, 15, ChronoUnit.MINUTES);
		stage.close();
	}

	@FXML
	private void handleCancel() {
		stage.close();
	}   
    
}
