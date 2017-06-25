/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import c195application.C195Application;
import static c195application.C195Application.URL;
import static c195application.C195Application.PASSWORD;
import static c195application.C195Application.USER_NAME;
import static c195application.C195Application.getAppointments;
import static c195application.C195Application.lang;
import c195application.model.Appointment;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class CalendarMonthController  {

   private Stage stage;
	private Scene scene;

	private YearMonth month;
	private Appointment appointment;
	private ObservableList<Appointment> appointments = FXCollections.observableArrayList();

	@FXML
	private GridPane calendar;

	private List<Label> weekDays;
	@FXML
	private Label monthLabel;
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

	public CalendarMonthController() {}

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {
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

		setCalendarDates(YearMonth.now());
	}

	/**
	 * Set up the monthly view with a reference to the main scene
	 * @param scene
	 */
	public void setupMonthlyView(Stage stage, Scene scene) {
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
			setCalendarDates(month);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the dates on the calendar based on a month and year
	 * @param monthToSet the YearMonth to set the view to
	 */
	private void setCalendarDates(YearMonth monthToSet) {
		// get the first and last dates of the month
		LocalDate firstDate = monthToSet.atDay(1);
		LocalDate lastDate = monthToSet.atEndOfMonth();

		// set the month variable, get the start and end times for getting appointments
		month = monthToSet;
		LocalDateTime start = LocalDateTime.of(firstDate, LocalTime.MIDNIGHT);
		LocalDateTime end = start.plusMonths(1);
		appointments = getAppointments(start, end);

		// clear the previous calendar items and reset label for new month
		calendar.getChildren().clear();
		DateTimeFormatter monthYear = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
		monthLabel.setText(monthToSet.format(monthYear));

		// get the month offset integer for the month
		int monthOffset = getMonthOffset();

		// set up loop to go through all days of the month beginning with the Sunday on (or before) the first day of the month
		for (LocalDate date = firstDate; date.isBefore(lastDate.plusDays(1)); date = date.plusDays(1)) {
			int dayOfMonth = date.getDayOfMonth();

			BorderPane datePane = new BorderPane();
			datePane.getStyleClass().add("border-pane-month");

			Label dateLabel = new Label();
			dateLabel.getStyleClass().add("label-small");
			dateLabel.setText(Integer.toString(dayOfMonth));
			BorderPane.setAlignment(dateLabel, Pos.CENTER);
			datePane.setTop(dateLabel);

			final LocalDate checkDate = date;
			ObservableList<Appointment> filteredAppts = appointments.stream()
					.filter(a -> a.isOnDate(checkDate))
					.collect(Collectors.toCollection(FXCollections::observableArrayList));

			ListView<Appointment> listView = getListView(filteredAppts); 
			datePane.setCenter(listView);

			int index = dayOfMonth - monthOffset;
			calendar.add(datePane, index % 7, index / 7);
		}
	}

	/**
	 * Get an integer that represents the offset between the first day of the month
	 * and the first day of the week that contains the first day of the month. 
	 * @return
	 */
	private int getMonthOffset() {
		LocalDate firstDay = month.atDay(1);
		DayOfWeek day = firstDay.getDayOfWeek();
		int offset = (day.getValue() - 1)% 7;

		LocalDate firstSunday = firstDay.minusDays(offset);
		Period span = Period.between(firstDay, firstSunday);

		return span.getDays();
	}

	/**
	 * Create a ListView with a FocusedProperty listener and return it
	 * @param appts the appointments to put into the ListView
	 * @return A ListView of Appointment objects
	 */
	@SuppressWarnings("unchecked")
	private ListView<Appointment> getListView(ObservableList<Appointment> appts) {
		ListView<Appointment> listView = new ListView<>(appts); 

		// add a listener to the focused property
		listView.focusedProperty().addListener((obs, oldValue, newValue) -> {
			ListView<Appointment> lv = null;
			// if the currently focused item is a ListView, continue
			if(scene.getFocusOwner() instanceof ListView) {

				// initialize the ListView
				lv = (ListView<Appointment>)scene.getFocusOwner();

				// if there is only one appointment in the ListView, select it
				if(newValue && lv.getItems().size() == 1) lv.getSelectionModel().selectFirst();

				// get the selected appointment in the ListView
				Appointment appt = lv.getSelectionModel().getSelectedItem();

				// if the appointment is null, disable the buttons, enable otherwise
				editApptButton.setDisable(appt == null);
				delApptButton.setDisable(appt == null);

			}
		});

		listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> appointment = newSel);

		return listView;
	}

	@FXML
	private void handlePrev() {
		setCalendarDates(month.minusMonths(1));
	}
	@FXML
	private void handleNext() {
		setCalendarDates(month.plusMonths(1));
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
		setCalendarDates(month);
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
		showAppointmentInfoDialog(appointment);
	}
	/**
	 * Delete the selected appointment
	 */
	@FXML
	private void handleDelAppt() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		alert.setTitle("Delete Appointment");
		alert.setHeaderText(lang.getString("deleteAppointmentMessage"));
		alert.initOwner(stage);
		alert.showAndWait()
		.filter(answer -> answer == ButtonType.YES)
		.ifPresent(answer -> {
			deleteAppointment(appointment);
		});
	}
    
    
}
