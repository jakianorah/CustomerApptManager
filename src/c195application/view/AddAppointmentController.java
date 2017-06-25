/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import c195application.C195Application;
import static c195application.C195Application.addAppointment;
import static c195application.C195Application.getNextID;
import static c195application.C195Application.lang;
import static c195application.C195Application.updateAppointment;
import static c195application.C195Application.validateAppointmentTime;
import c195application.model.Appointment;
import c195application.model.Customer;
import c195application.model.Reminder;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.zone.ZoneRules;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class AddAppointmentController {

    private Stage stage;
	private Appointment appointment, newAppointment;
	private ObservableList<Customer> customers;
	private ObservableList<ZoneId> zones;
	private ObservableList<String> startTimes;
	private ObservableList<String> endTimes;
	private ObservableList<String> types;
	private ObservableList<Reminder> reminders;

	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<Customer> customerComboBox;
	@FXML
	private ComboBox<ZoneId> timeZoneComboBox;
	@FXML
	private ComboBox<String> startTimeComboBox;
	@FXML
	private ComboBox<String> endTimeComboBox;
	@FXML
	private ComboBox<String> typeComboBox;
	@FXML
	private TextField titleTextField;
	@FXML
	private TextField locationTextField;
	@FXML
	private TextField contactTextField;
	@FXML
	private TextField urlTextField;
	@FXML
	private ListView<Reminder> reminderListView;
	@FXML
	private Button addReminderButton;
	@FXML
	private Button delReminderButton;

	private DateTimeFormatter shortTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

	/**
	 * Construct the dialog and initialize array lists for combo boxes
	 */
	public AddAppointmentController() {
		startTimes = FXCollections.observableArrayList();
		endTimes = FXCollections.observableArrayList();
		LocalTime time = LocalTime.MIDNIGHT;
		do {
			startTimes.add(time.format(shortTime));
			endTimes.add(time.format(shortTime));
			time = time.plusMinutes(15);
		} while(!time.equals(LocalTime.MIDNIGHT));
		startTimes.remove(startTimes.size() - 1);
		endTimes.remove(0);

		zones = FXCollections.observableArrayList(
				ZoneId.of("Etc/UTC"),
				ZoneId.of("Europe/London"),
				ZoneId.of("US/Eastern"),
				ZoneId.of("US/Central"),
				ZoneId.of("US/Mountain"),
				ZoneId.of("US/Pacific"),
				ZoneId.of("Asia/Tokyo"));
		zones.sort((a, b) -> a.getId().compareTo(b.getId())); 

		types = FXCollections.observableArrayList(
				"Sales",
				"follow up",
				"customer requested",
				"hand over responsibilities");
		types.sort((a, b) -> a.compareTo(b));

		customers = C195Application.getAllCustomers();
		customers.sort((a, b) -> a.getLastName().compareTo(b.getLastName())); 

		reminders = FXCollections.observableArrayList();
	}

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {
		datePicker.setValue(LocalDate.now());

		startTimeComboBox.setItems(startTimes);
		endTimeComboBox.setItems(endTimes);
		startTimeComboBox.getSelectionModel().select(LocalTime.of(8, 0).format(shortTime));
		endTimeComboBox.getSelectionModel().select(LocalTime.of(9, 0).format(shortTime));

		timeZoneComboBox.setItems(zones);
		setTimeZone(ZoneId.systemDefault());

		typeComboBox.setItems(types);

		customerComboBox.setItems(customers);

		// add a listener to the time zone combo box
		timeZoneComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> 
		changeTimeZone(oldValue, newValue));

		// add a listener to the start time combo box
		startTimeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), shortTime);
			LocalTime startTime = LocalTime.parse(newValue, shortTime);
			// if the end time is not after the start time, adjust it
			if(!endTime.isAfter(startTime)) adjustEndTime(startTime, Duration.ofMinutes(15));
		});

		// add a listener to the end time combo box
		endTimeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), shortTime);
			LocalTime endTime = LocalTime.parse(newValue, shortTime);
			// if the start time is not before the end time, adjust it
			if(!startTime.isBefore(endTime)) adjustStartTime(endTime, Duration.ofMinutes(15));
		});

	}

	public void setupDialog(Stage stage, Appointment appt) {
		this.stage = stage;
		appointment = appt;

		initFields(appt);
	}

	/**
	 * Initialize all the fields with appointment information if one was selected
	 * @param appt
	 */
	private void initFields(Appointment appt) {
		if(appt != null) {
			titleTextField.setText(appt.getTitle());
			locationTextField.setText(appt.getLocation());
			contactTextField.setText(appt.getContact());
			urlTextField.setText(appt.getUrl());

			typeComboBox.getSelectionModel().select(appt.getDescription());
			startTimeComboBox.getSelectionModel().select(appt.getStart().toLocalTime().format(shortTime));
			endTimeComboBox.getSelectionModel().select(appt.getEnd().toLocalTime().format(shortTime));
			datePicker.setValue(appt.getStart().toLocalDate());

			Customer customer = Customer.getCustomer(appt.getCustomerId());
			customerComboBox.getSelectionModel().select(customer);

			delReminderButton.setDisable(true);

			reminders = C195Application.getReminders(appt); 
			reminderListView.setItems(reminders);
			reminderListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> 
			delReminderButton.setDisable(newSel == null));

		}			
	}

	/**
	 * Select the zone in the combo box to an equivalent zone if it
	 * already exists in the list. Otherwise, add it to the combo
	 * box and select it.
	 * @param thisZone
	 */
	private void setTimeZone(ZoneId thisZone) {
		ZoneRules thisRules = thisZone.getRules();

		boolean foundZone = false;
		for (ZoneId zone : zones) {
			ZoneRules rules = zone.getRules();
			if(rules.equals(thisRules)) {
				thisZone = zone;
				foundZone = true;
				break;
			}
		}
		if(!foundZone) {
			zones.add(thisZone);
			zones.sort((a, b) -> a.getId().compareTo(b.getId())); 
		}
		timeZoneComboBox.getSelectionModel().select(thisZone);
	}

	public Appointment getNewAppointment() {
		return newAppointment;
	}

	private void adjustStartTime(LocalTime endTime, Duration duration) {
		LocalTime startTime = endTime.minus(duration);
		startTimeComboBox.getSelectionModel().select(startTime.format(shortTime));
	}
	private void adjustEndTime(LocalTime startTime, Duration duration) {
		LocalTime endTime = startTime.plus(duration);
		endTimeComboBox.getSelectionModel().select(endTime.format(shortTime));
	}
	private void changeTimeZone(ZoneId oldZone, ZoneId newZone) {
		LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), shortTime);
		LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), shortTime);
		LocalDate day = datePicker.getValue();
		LocalDateTime startDT = LocalDateTime.of(day, startTime);
		LocalDateTime endDT = LocalDateTime.of(day, endTime);

		// get the zone offsets based on the zone ids and the start time
		ZoneOffset oldOffset = oldZone.getRules().getOffset(startDT);
		ZoneOffset newOffset = newZone.getRules().getOffset(startDT);

		// get the difference in seconds
		int offset = oldOffset.compareTo(newOffset);

		// adjust the start and end times
		startDT = startDT.plusSeconds(offset);
		endDT = endDT.plusSeconds(offset);

		// if the date is not the same, change it
		if(!day.equals(startDT.toLocalDate())) {
			datePicker.setValue(startDT.toLocalDate());
		}
		startTimeComboBox.getSelectionModel().select(startDT.toLocalTime().format(shortTime));
		endTimeComboBox.getSelectionModel().select(endDT.toLocalTime().format(shortTime));
	}

	@FXML
	private void handleAddReminder() {
		try {
			// set up the root for the add reminder dialog
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/AddReminder.fxml"));
			loader.setResources(lang);
			AnchorPane addReminderRoot = (AnchorPane) loader.load();

			// set up the stage for the add reminder dialog
			Stage addReminderStage = new Stage();
			addReminderStage.setTitle("Add Reminder");
			addReminderStage.initModality(Modality.WINDOW_MODAL);
			addReminderStage.initOwner(stage);

			// add a new scene with the root to the stage
			Scene scene = new Scene(addReminderRoot);
			addReminderStage.setScene(scene);

			// get the controller for the dialog and pass a reference to  
			// the add reminder dialog stage
			AddReminderController controller = loader.getController();
			int apptId = (appointment == null) ?
					getNextID("appointmentId", "appointment") : Appointment.getAppointmentId(appointment);
					LocalDateTime apptStart = LocalDateTime.of(datePicker.getValue(),
							LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), shortTime));
					controller.setupDialog(addReminderStage, apptId, apptStart);

					// show the add reminder dialog
					addReminderStage.showAndWait();

					// refresh the reminder list after an update
					Reminder newReminder = controller.getReminder();

					if (newReminder == null || reminderListView.getItems().contains(newReminder)) return;
					else {
						reminders.add(newReminder);
						reminderListView.setItems(reminders);
					}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void handleDeleteReminder() {
		reminders.remove(reminderListView.getSelectionModel().getSelectedItem());
		reminderListView.getSelectionModel().clearSelection();
	}

	@FXML
	private void handleSave() {
		Customer customer = customerComboBox.getSelectionModel().getSelectedItem();

		String title = titleTextField.getText();
		String description = typeComboBox.getSelectionModel().getSelectedItem();
		String location = locationTextField.getText();
		String contact = contactTextField.getText();
		String url = urlTextField.getText();

		LocalDate localDate = datePicker.getValue();
		LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), shortTime);
		LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), shortTime);
		LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
		LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

		ZoneId zone = timeZoneComboBox.getSelectionModel().getSelectedItem();
		LocalDateTime localStart = startDT.atZone(zone).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime localEnd = endDT.atZone(zone).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

		try {

			if(customer ==  null) throw new IllegalArgumentException("customer "  + lang.getString("missingInfoMessage"));

			int customerId = Customer.getCustomerId(customer);
			newAppointment = new Appointment(customerId, title, description, location, contact, url, localStart, localEnd);

			if (appointment ==  null) { // add an appointment
				validateAppointmentTime(newAppointment);
				addAppointment(newAppointment, reminders);
			} else { // update an appointment
				System.out.println("Replacing " + appointment);
				System.out.println("     with " + newAppointment);
				updateAppointment(appointment, newAppointment, reminders);
			}

		} catch (IllegalArgumentException e) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle(lang.getString("missingInfoTitle"));
			alert.setHeaderText(e.getMessage());
			alert.initOwner(stage);
			alert.showAndWait();
			return;
		}

		stage.close();
	}

	@FXML
	private void handleCancel() {
		stage.close();
	}
    
}
