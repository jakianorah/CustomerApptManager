/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import c195application.C195Application;
import static c195application.C195Application.PASSWORD;
import static c195application.C195Application.USER_NAME;
import static c195application.C195Application.URL;
import static c195application.C195Application.lang;
import c195application.model.Appointment;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class CalendarWeekController  {

    private Stage stage;
	private Scene scene;

	private LocalDate firstDayOfWeek;
	private Appointment appointment;
	private ObservableList<Appointment> appointments = FXCollections.observableArrayList();

	@FXML
	private GridPane hourlyGrid;

	private List<Label> weekDates;
	private List<Label> weekDays;
	@FXML
	private Label sundayDate;
	@FXML
	private Label mondayDate;
	@FXML
	private Label tuesdayDate;
	@FXML
	private Label wednesdayDate;
	@FXML
	private Label thursdayDate;
	@FXML
	private Label fridayDate;
	@FXML
	private Label saturdayDate;
	@FXML
	private Label sundayLabel;
	@FXML
	private Label mondayLabel;
	@FXML
	private Label tuesdayLabel;
	@FXML
	private Label wednesdayLabel;
	@FXML
	private Label thursdayLabel;
	@FXML
	private Label fridayLabel;
	@FXML
	private Label saturdayLabel;


	@FXML
	private Button editApptButton;
	@FXML
	private Button delApptButton;

	private DateTimeFormatter shortTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

	public CalendarWeekController() {};

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {
		weekDates = new ArrayList<Label>();
		weekDates.add(sundayDate);
		weekDates.add(mondayDate);
		weekDates.add(tuesdayDate);
		weekDates.add(wednesdayDate);
		weekDates.add(thursdayDate);
		weekDates.add(fridayDate);
		weekDates.add(saturdayDate);

		weekDays = new ArrayList<Label>();
		weekDays.add(sundayLabel);
		weekDays.add(mondayLabel);
		weekDays.add(tuesdayLabel);
		weekDays.add(wednesdayLabel);
		weekDays.add(thursdayLabel);
		weekDays.add(fridayLabel);
		weekDays.add(saturdayLabel);

		for (int i = 1; i < 8; i++) {
			DayOfWeek day = DayOfWeek.of(i);
			String dayStr = day.getDisplayName(TextStyle.FULL, Locale.getDefault());
			weekDays.get(i % 7).setText(dayStr);
		}

		setWeekDays(LocalDate.now());

		setTimes();

	}

	/**
	 * Set up the weekly view with a reference to the main scene
	 * @param scene
	 */
	public void setupWeeklyView(Stage stage, Scene scene) {
		this.stage = stage;
		this.scene = scene;
	}

	/**
	 * Set the appointment information dialog appropriately based on one (or no) selection
	 */
	private void showAppointmentInfoDialog(Appointment appointment) {
		try {
			// set up the root for the appointment information dialog
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/AddAppointment.fxml"));
			loader.setResources(lang);
			AnchorPane appointmentInfoRoot = (AnchorPane) loader.load();

			// set up the stage for the appointment information dialog
			Stage appointmentInfoStage = new Stage();
			appointmentInfoStage.initModality(Modality.WINDOW_MODAL);
			appointmentInfoStage.initOwner(stage);

			// add a new scene with the root to the stage
			Scene scene = new Scene(appointmentInfoRoot);
			appointmentInfoStage.setScene(scene);

			// get the controller for the dialog and pass a reference to  
			// the appointment info dialog stage, and a appointment, if one was selected
			AddAppointmentController controller = loader.getController();
			controller.setupDialog(appointmentInfoStage, appointment);

			// show the appointment information dialog
			appointmentInfoStage.showAndWait();

			// refresh the appointment list after an update
			Appointment newAppointment = controller.getNewAppointment();
			if (newAppointment == null) return;

			if(appointment == null) {
				appointments.add(newAppointment);
			} else {
				appointments.set(appointments.indexOf(appointment), newAppointment);
			}
			setWeekDays(firstDayOfWeek);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the dates for all the days of the week
	 * @param dayInWeek a LocalDate in the week
	 */
	private void setWeekDays(LocalDate dayInWeek) {
		hourlyGrid.getChildren().removeIf(c -> c instanceof TextArea);

		DateTimeFormatter shortDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

		int offset = dayInWeek.getDayOfWeek().getValue() % 7;
		firstDayOfWeek = dayInWeek.minusDays(offset);

		for (int i = 0; i < weekDates.size(); i++) {
			Label dateLabel = weekDates.get(i);

			LocalDate setDate = firstDayOfWeek.plusDays(i);
			dateLabel.setText(setDate.format(shortDate));
		}

		LocalDateTime start = LocalDateTime.of(firstDayOfWeek, LocalTime.MIDNIGHT);
		LocalDateTime end = start.plusWeeks(1);
		appointments = C195Application.getAppointments(start, end);

		// set up loop to go through all the days of the week
		int columnIndex = 1;
		for (LocalDate date = start.toLocalDate(); date.isBefore(end.toLocalDate().plusDays(1)); date = date.plusDays(1)) {

			// get appointments on this day
			final LocalDate checkDate = date;
			ObservableList<Appointment> filteredAppts = appointments.stream()
					.filter(a -> a.isOnDate(checkDate))
					.collect(Collectors.toCollection(FXCollections::observableArrayList));

			for (Appointment appointment : filteredAppts) {
				LocalTime startTime = appointment.getStart().toLocalTime();

				// get row of start time and end time
				int minute = startTime.getMinute();
				int hour = startTime.getHour();
				int rowIndex = hour * 4 + minute / 15;
				long rowSpan = appointment.getDuration().toMinutes() / 15;

				// get the TextArea for this appointment
				TextArea apptBlock = getTextArea(appointment);

				hourlyGrid.add(apptBlock, columnIndex, rowIndex, 1, (int)rowSpan);
			}
			columnIndex++;
		}
	}

	/**
	 * Set up a TextArea object with all the properties needed to represent an appointment
	 * in the weekly view
	 * @param appointment
	 * @return
	 */
	private TextArea getTextArea(Appointment appointment) {

		// initialize the textArea with the appointment title
		TextArea apptBlock = new TextArea(appointment.getTitle());

		// set up some basic properties needed
		apptBlock.setEditable(false);
		apptBlock.setMaxHeight(Double.MAX_VALUE);
		apptBlock.setWrapText(true);

		// add a focused property listener to control if the buttons are disabled or enabled
		apptBlock.focusedProperty().addListener((obs, oldValue, newValue) -> {
			// if we are losing focus from a TextAre and the new focused item is not a button,
			// set the appointment to null
			if (oldValue &&	!(scene.getFocusOwner() instanceof Button)) this.appointment = null;

			// set the appointment to the appointment in the selected TextArea
			if (newValue) this.appointment = appointment;

			setButtonsToDisable(this.appointment == null);
		});

		return apptBlock;
	}

	/**
	 * Set the times for the weekly view
	 */
	private void setTimes() {

		RowConstraints rowConst = new RowConstraints(Region.USE_PREF_SIZE, 20, Region.USE_PREF_SIZE);
		rowConst.setValignment(VPos.TOP);

		int row = 0;

		LocalTime time = LocalTime.MIDNIGHT;
		do {
			Label timeLabel = new Label(time.format(shortTime));

			int rowCount = hourlyGrid.getRowConstraints().size();
			if(row > rowCount - 1) {
				hourlyGrid.getRowConstraints().add(rowConst);
			}

			if (time.getMinute() == 0 || time.getMinute() == 30) {
				hourlyGrid.add(timeLabel, 0, row);
			}

			row += 1;

			time = time.plusMinutes(15);
		} while(!time.equals(LocalTime.MIDNIGHT));

	}


	private void setButtonsToDisable(boolean isDisabled) {
		editApptButton.setDisable(isDisabled);
		delApptButton.setDisable(isDisabled);
	}

	@FXML
	private void handlePrev() {
		setWeekDays(firstDayOfWeek.minusWeeks(1));
	}
	@FXML
	private void handleNext() {
		setWeekDays(firstDayOfWeek.plusWeeks(1));
	}
	


	private void deleteAppointment(Appointment appointment) {
		int appointmentId = Appointment.getAppointmentId(appointment);

		appointments.remove(appointment);

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

			String sql = "DELETE FROM appointment WHERE appointmentId = ?";
			PreparedStatement prepstmt = conn.prepareStatement(sql); 
			prepstmt.setInt(1, appointmentId);
			prepstmt.executeUpdate();

			// delete reminders
			sql = "DELETE FROM reminder WHERE appointmentId = ?";
			prepstmt = conn.prepareStatement(sql); 
			prepstmt.setInt(1, appointmentId);
			prepstmt.executeUpdate();

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		setWeekDays(firstDayOfWeek);
	}


	/**
	 * Show the appointment information window with all fields initially empty
	 */
	@FXML
	private void handleNewAppt() {
		showAppointmentInfoDialog(null);
	}
	/**
	 * Show the appointment information window with all fields set to the selected appointment
	 */
	@FXML
	private void handleEditAppt() {
		setButtonsToDisable(true);
		showAppointmentInfoDialog(appointment);

	}
	/**
	 * Delete the selected appointment
	 */
	@FXML
	private void handleDelAppt() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		alert.setTitle(lang.getString("deleteAppointmentTitle"));
		alert.setHeaderText(lang.getString("deleteAppointmentMessage"));
		alert.initOwner(stage);
		alert.showAndWait()
		.filter(answer -> answer == ButtonType.YES)
		.ifPresent(answer -> {
			setButtonsToDisable(true);
			deleteAppointment(appointment);
		});
	}
 
    
}
