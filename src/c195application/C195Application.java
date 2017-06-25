/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application;

import c195application.model.Appointment;
import c195application.model.Customer;
import c195application.model.Reminder;
import c195application.model.ActivityLogger;
import c195application.view.MainScreenController;
import c195application.view.UserLoginController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author jakianorah
 */
public class C195Application extends Application {
    
   // database info
	private final static String DRIVER = "com.mysql.jdbc.Driver";
	public final static String URL = "jdbc:mysql://52.206.157.109/U03wsB";
	public final static String USER_NAME = "U03wsB";
	public final static String PASSWORD = "53688105932";

	// static variables used throughout the application
	public static ResourceBundle lang = ResourceBundle.getBundle("resources/lang", Locale.getDefault());
	public static String loggedInUser;
	public static boolean isLoggedIn = false;

	// JavaFX variables
	private Stage primaryStage;
	private AnchorPane root;

	private final static Map<String, LocalDateTime> REMINDER_QUEUE = new LinkedHashMap<>();


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException {
		Class.forName(DRIVER);

		ScheduledExecutorService reminderService = null;

		try {
			// create a reminder service schedule thread pool
			reminderService = Executors.newScheduledThreadPool(5);

			// add the check reminders task to the thread pool
			Runnable checkRemindersTask = () -> addRemindersToQueue();
			@SuppressWarnings("unused") // not sure if I will need the result in the future so leaving it in unused
			ScheduledFuture<?> checkRemindersTaskResult = reminderService.scheduleWithFixedDelay(checkRemindersTask, 0, 2, TimeUnit.MINUTES);

			// add the show reminder alert task to the thread pool
			Runnable showReminderAlertTask = () -> showReminderAlert();
			@SuppressWarnings("unused") // not sure if I will need the result in the future so leaving it in unused
			ScheduledFuture<?> showReminderAlertTaskResult = reminderService.scheduleWithFixedDelay(showReminderAlertTask, 0, 1, TimeUnit.MINUTES);

			launch(args);

		} finally {
			if(reminderService != null) reminderService.shutdown();
		}
	}

	/**
	 * Set up the primary stage and show the login screen
	 */
	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;

		showLoginScreen();
	}

	/**
	 * Set up the login screen on the primary stage and show the dialog.
	 */
	private void showLoginScreen() {
		try {
			// Load root node from main FXML file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/UserLogin.fxml"));
			loader.setResources(lang);
			AnchorPane loginRoot = (AnchorPane)loader.load();

			// Set up a scene with the root node and load that onto the stage
			Scene scene = new Scene(loginRoot);
			primaryStage.setScene(scene);

			// Initialize the main controller
			UserLoginController controller = loader.getController();
			// Give the login screen a reference to the main application 
			controller.setUpLogin(this, primaryStage);

			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize and show the main screen
	 */
	private void showMainScreen() {

		this.primaryStage.setTitle("Appointment Scheduler");

		try {		
			// Load root node from main FXML file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/MainScreen.fxml"));
			loader.setResources(lang);
			root = (AnchorPane)loader.load();

			// Set up a scene with the root node and load that onto the primary stage
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);

			// Initialize the main controller
			MainScreenController controller = loader.getController();
			// Give the main screen a reference to the stage 
			controller.setupMainScreen(primaryStage);

			primaryStage.centerOnScreen();
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

		addRemindersToQueue();
	}

	/**
	 * Get all the active customers in the database
	 * @return an observable array list of active customers
	 */
	public static ObservableList<Customer> getAllCustomers() {
		ObservableList<Customer> customers = FXCollections.observableArrayList();

		String sql = "SELECT co.countryId, country, ci.cityId, city, "
				+ "ad.addressId, address, address2, postalCode, phone, "
				+ "cu.customerId, customerName "
				+ "FROM customer AS cu, address AS ad, city AS ci, country AS co "
				+ "WHERE cu.addressId = ad.addressId "
				+ "AND ad.cityId = ci.cityId "
				+ "AND ci.countryId = co.countryId "
				+ "AND active = 1";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				Statement stmt = conn.createStatement(); 
				ResultSet results = stmt.executeQuery(sql); ){

			while (results.next()) {
				String country = results.getString("country");
				String city = results.getString("city");
				String address1 = results.getString("address");
				String address2 = results.getString("address2");
				String postalCode = results.getString("postalCode");
				String phone = results.getString("phone");
				String customerName = results.getString("customerName");

				Customer customer = new Customer(customerName, address1, address2, postalCode, phone, city, country);

				customers.add(customer);
			}

		} catch (IllegalArgumentException e) {
			System.out.println("Exception getting all customers: " + e.getMessage());
		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}

		return customers;
	}
	/**
	 * Get a list of the countries available to be assigned to customers from the database
	 * @return an array list of strings of all the country names in the database
	 */
	public static List<String> getAllCountries() {
		List<String> countries = new ArrayList<>();

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery("SELECT country FROM country"); ){
			while (results.next()) {
				countries.add(results.getString("country"));
			}
		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return countries;
	}
	/**
	 * Get a list of all appointments in the database between two LocalDateTimes. Time stamps
	 * stored in the database are in UTC. This method will use your default time zone to transform
	 * the times supplied into the appropriate UTC times.
	 * @param startLocal
	 * @param endLocal
	 * @return a list of appointments that occur solely between the times provided
	 */
	public static ObservableList<Appointment> getAppointments(LocalDateTime startLocal, LocalDateTime endLocal) {
		ZoneId zone = ZoneId.systemDefault();

		ObservableList<Appointment> appointments = FXCollections.observableArrayList();

		LocalDateTime startUTC = startLocal.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
		LocalDateTime endUTC = endLocal.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

		String sql = "SELECT * FROM appointment WHERE start > ? AND end < ? AND createdBy = ?";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				PreparedStatement prepstmt = conn.prepareStatement(sql); ){

			prepstmt.setTimestamp(1, Timestamp.valueOf(startUTC));
			prepstmt.setTimestamp(2, Timestamp.valueOf(endUTC));
			prepstmt.setString(3, loggedInUser);
			ResultSet results = prepstmt.executeQuery();

			while (results.next()) {

				int customerId = results.getInt("customerId");
				String title = results.getString("title");
				String description = results.getString("description");
				String location = results.getString("location");
				String contact = results.getString("contact");
				String apptUrl = results.getString("url");

				startUTC = results.getTimestamp("start").toLocalDateTime();
				endUTC = results.getTimestamp("end").toLocalDateTime();
				startLocal = startUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
				endLocal = endUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();

				Appointment appt = new Appointment(customerId, title, description, location, contact, apptUrl, startLocal, endLocal);

				appointments.add(appt);
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return appointments;
	}
	/**
	 * Get a list of all reminders in the database associated with a specific appointment.
	 * @param appointment the appointment to find reminders for
	 * @return a list of reminders for the appointment specified
	 */
	public static ObservableList<Reminder> getReminders(Appointment appointment) {

		ObservableList<Reminder> reminders = FXCollections.observableArrayList();

		String sql = "SELECT reminderDate, snoozeIncrement, snoozeIncrementTypeId, remindercol, r.appointmentId "
				+ "FROM reminder AS r, appointment AS a "
				+ "WHERE r.appointmentId = a.appointmentId "
				+ "AND a.title = ? "
				+ "AND a.description = ? "
				+ "AND a.location = ? "
				+ "AND a.contact = ? "
				+ "AND a.url = ? "
				+ "AND a.start = ? "
				+ "AND a.end = ?";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				PreparedStatement prepstmt = conn.prepareStatement(sql); ){
			ZonedDateTime startZoned = appointment.getStart().atZone(ZoneId.systemDefault());
			ZonedDateTime endZoned = appointment.getEnd().atZone(ZoneId.systemDefault());
			LocalDateTime startUTC = startZoned.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
			LocalDateTime endUTC = endZoned.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

			prepstmt.setString(1, appointment.getTitle());
			prepstmt.setString(2, appointment.getDescription());
			prepstmt.setString(3, appointment.getLocation());
			prepstmt.setString(4, appointment.getContact());
			prepstmt.setString(5, appointment.getUrl());
			prepstmt.setTimestamp(6, Timestamp.valueOf(startUTC));
			prepstmt.setTimestamp(7, Timestamp.valueOf(endUTC));
			ResultSet result = prepstmt.executeQuery();

			while (result.next()) {
				LocalDateTime reminderUTC = result.getTimestamp("reminderDate").toLocalDateTime();
				int snoozeInc = result.getInt("snoozeIncrement");
				int incType = result.getInt("snoozeIncrementTypeId");
				int apptId = result.getInt("appointmentId");

				Instant instantUTC = reminderUTC.toInstant(ZoneOffset.UTC);
				LocalDateTime reminderDT = LocalDateTime.ofInstant(instantUTC, ZoneId.systemDefault());

				ChronoUnit snoozeUnit;
				switch (incType) {
				case 1:
					snoozeUnit = ChronoUnit.MINUTES;
					break;
				case 2:
					snoozeUnit = ChronoUnit.HOURS;
					break;
				case 3:
					snoozeUnit = ChronoUnit.DAYS;
					break;
				default:
					snoozeUnit = ChronoUnit.MINUTES;
				}

				Reminder reminder = new Reminder(reminderDT, apptId, snoozeInc, snoozeUnit);

				reminders.add(reminder);
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return reminders;
	}
	/**
	 * Get a list of all the types (descriptions) of appointments in the database
	 * @return
	 */
	public static ObservableList<String> getApptTypes() {
		ObservableList<String> types = FXCollections.observableArrayList();

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery("SELECT DISTINCT description FROM appointment"); ){

			while (results.next()) {
				types.add(results.getString(1));
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return types;
	}
	/**
	 * Get a map of all of the cities and how many active customers are in each city
	 * @return
	 */
	public static Map<String, Double> getCustomerLocationData() {
		Map<String, Double> data = new HashMap<>();

		String sql = "SELECT city, COUNT(city) AS customerCount "
				+ "FROM customer AS cu, address AS ad, city AS ci "
				+ "WHERE cu.addressId = ad.addressId "
				+ "AND ad.cityId = ci.cityId "
				+ "GROUP BY city";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery(sql); ){

			while (results.next()) {
				String city = results.getString("city");
				double count = results.getInt("customerCount");
				data.put(city, count);
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return data;
	}

	/**
	 * Validate an appointment time. This checks for conflicts with existing appointments, appointments
	 * occurring in the past, or appointment not during business hours.
	 * @param appointment
	 */
	public static void validateAppointmentTime(Appointment appointment) {
		ZoneId zone = ZoneId.systemDefault();

		LocalDateTime start = appointment.getStart();
		LocalDateTime end = appointment.getEnd();
		LocalDate day = start.toLocalDate();

		// check if start or end time occurs in the past
		if(start.atZone(zone).isBefore(ZonedDateTime.now(ZoneOffset.UTC)) || 
				end.atZone(zone).isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
			throw new IllegalArgumentException(lang.getString("apptOccursInPast"));
		}

		LocalDateTime businessStart = LocalDateTime.of(day, LocalTime.of(8, 0));
		LocalDateTime businessEnd = LocalDateTime.of(day, LocalTime.of(17, 0));

		if(start.isBefore(businessStart) || // check if the start time is after 8 AM
				end.isAfter(businessEnd) || // check if the end time is before 5 PM
				!day.equals(end.toLocalDate()) || // check if the start time and end time occur on the same day
				day.getDayOfWeek() == DayOfWeek.SATURDAY || // check if the start time is on a Saturday
				day.getDayOfWeek() == DayOfWeek.SUNDAY) { // check if the start time is on a Sunday
			String startTime = LocalTime.of(8, 0).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
			String endTime = LocalTime.of(17, 0).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
			String startDay = DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.getDefault());
			String endDay = DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL, Locale.getDefault());
			throw new IllegalArgumentException(lang.getString("apptDuringBusinessHours") + System.lineSeparator() + 
					"(" + startDay + "-" + endDay + "; " + startTime + "-" + endTime + ")");
		}

		// check for conflicts with existing appointments in the database
		String sql = "SELECT * FROM appointment "
				+ "WHERE (? BETWEEN start AND end OR ? BETWEEN start AND end) "
				+ "AND createdBy = ?";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				PreparedStatement prepstmt = conn.prepareStatement(sql); ) {

			LocalDateTime startUTC = start.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
			LocalDateTime endUTC = end.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

			prepstmt.setTimestamp(1, Timestamp.valueOf(startUTC));
			prepstmt.setTimestamp(2, Timestamp.valueOf(endUTC));
			prepstmt.setString(3, loggedInUser);

			ResultSet result = prepstmt.executeQuery();

			if(result.next()) {
				throw new IllegalArgumentException(lang.getString("apptConflict"));
			}
		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	/**
	 * Add the country to the database. Returns the id of the new country, or an
	 * existing id if the country was already in the database.
	 * @param country the name of the country to add
	 * @return the id of the country in the database
	 */
	public static int addCountry(String country) {
		int countryId = -1;

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {

			String sql = "SELECT countryId FROM country WHERE country = ?";
			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setString(1, country);
			ResultSet result = prepstmt.executeQuery();

			if(result.next()) {
				countryId = result.getInt("countryId");
			} else {
				countryId = getNextID("countryId", "country");
				sql = "INSERT INTO country VALUES (?, ?, NOW(), ?, NOW(), ?)";
				prepstmt = conn.prepareStatement(sql);
				prepstmt.setInt(1, countryId);
				prepstmt.setString(2, country);
				prepstmt.setString(3, loggedInUser);
				prepstmt.setString(4, loggedInUser);
				prepstmt.executeUpdate();

			}
		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return countryId;
	}
	/**
	 * Add the city to the database. Returns the id of the new city, or an
	 * existing id if the city was already in the database.
	 * @param city the name of the city to add
	 * @param country the name of the country that the city is in
	 * @return the id of the city in the database
	 */
	public static int addCity(String city, String country) {
		int cityId = -1;
		int countryId = addCountry(country);

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {

			String sql = "SELECT cityId FROM city "
					+ "WHERE city = ? AND countryId = ?";
			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setString(1, city);
			prepstmt.setInt(2, countryId);
			ResultSet result = prepstmt.executeQuery();

			if(result.next()) {
				cityId = result.getInt("cityId");
			} else {
				cityId = getNextID("cityId", "city");
				sql = "INSERT INTO city VALUES (?, ?, ?, NOW(), ?, NOW(), ?)";
				prepstmt = conn.prepareStatement(sql);
				prepstmt.setInt(1, cityId);
				prepstmt.setString(2, city);
				prepstmt.setInt(3, countryId);
				prepstmt.setString(4, loggedInUser);
				prepstmt.setString(5, loggedInUser);
				prepstmt.executeUpdate();

			}
		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return cityId;
	}
	/**
	 * Add the address to the database. Returns the id of the new address, or an
	 * existing id if the address was already in the database.
	 * @param address the first line of the address to add
	 * @param address2 the second line of the address to add
	 * @param postalCode the postal code of the address to add
	 * @param phone the phone number of the address to add
	 * @param city the name of the city that the address is in
	 * @param country the name of the country that the address is in
	 * @return the id of the address in the database
	 */
	public static int addAddress(String address, String address2, String postalCode, String phone, String city, String country) {
		int addressId = -1;
		int cityId = addCity(city, country);

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {

			String sql = "SELECT addressId FROM address "
					+ "WHERE address = ? AND address2 = ? "
					+ "AND cityId = ? AND postalCode = ? AND phone = ?";
			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setString(1, address);
			prepstmt.setString(2, address2);
			prepstmt.setInt(3, cityId);
			prepstmt.setString(4, postalCode);
			prepstmt.setString(5, phone);
			ResultSet result = prepstmt.executeQuery();

			if(result.next()) {
				addressId = result.getInt("addressId");
			} else {
				addressId = getNextID("addressId", "address");
				sql = "INSERT INTO address VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";
				prepstmt = conn.prepareStatement(sql);
				prepstmt.setInt(1, addressId);
				prepstmt.setString(2, address);
				prepstmt.setString(3, address2);
				prepstmt.setInt(4, cityId);
				prepstmt.setString(5, postalCode);
				prepstmt.setString(6, phone);
				prepstmt.setString(7, loggedInUser);
				prepstmt.setString(8, loggedInUser);
				prepstmt.executeUpdate();

			}
		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return addressId;
	}
	/**
	 * Add a customer to the database.
	 * @param customer the customer to add
	 * @return true if the customer was added successfully
	 */
	public static boolean addCustomer(Customer customer) {
		String name = customer.getName();
		int customerId = getNextID("customerId", "customer");
		int addressId = addAddress(customer.getAddress1(), customer.getAddress2(), customer.getPostalCode(), customer.getPhone(), customer.getCity(), customer.getCountry());

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {

			String sql = "INSERT INTO customer VALUES (?, ?, ?, 1, NOW(), ?, NOW(), ?)";
			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setInt(1, customerId);
			prepstmt.setString(2, name);
			prepstmt.setInt(3, addressId);
			prepstmt.setString(4, loggedInUser);
			prepstmt.setString(5, loggedInUser);
			prepstmt.executeUpdate();

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			return false;
		}
		return true;
	}
	/**
	 * Update a customer in the database
	 * @param oldCustomer the customer in the database to update
	 * @param newCustomer the customer information to update the old customer to
	 * @return true if the update was successful, false if not
	 */
	public static boolean updateCustomer(Customer oldCustomer, Customer newCustomer) {
		int customerId = Customer.getCustomerId(oldCustomer);

		String oldName = oldCustomer.getName();
		String newName = newCustomer.getName();

		//		int oldAddressId = addAddress(oldCustomer.getAddress1(), oldCustomer.getAddress2(), oldCustomer.getPostalCode(),
		//				oldCustomer.getPhone(), oldCustomer.getCity(), oldCustomer.getCountry());
		int newAddressId = addAddress(newCustomer.getAddress1(), newCustomer.getAddress2(), newCustomer.getPostalCode(),
				newCustomer.getPhone(), newCustomer.getCity(), newCustomer.getCountry());

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {

			//			String sql = "SELECT customerId FROM customer "
			//					+ "WHERE customerName = ? AND addressId = ? AND active = 1";
			//			PreparedStatement prepstmt = conn.prepareStatement(sql);
			//			prepstmt.setString(1, oldName);
			//			prepstmt.setInt(2, oldAddressId);
			//			ResultSet result = prepstmt.executeQuery();
			//			
			//			if(result.next()) customerId = result.getInt("customerId");

			String sql = "UPDATE customer SET customerName=?, addressId=?, lastUpdate=NOW(), lastUpdateBy=? "
					+ "WHERE customerId = ?";
			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setString(1, newName);
			prepstmt.setInt(2, newAddressId);
			prepstmt.setString(3, loggedInUser);
			prepstmt.setInt(4, customerId);
			prepstmt.executeUpdate();

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			return false;
		}
		return true;
	}
	/**
	 * Add an appointment to the database.
	 * @param appointment the appointment to add
	 * @param reminders the reminders for the appointment
	 * @return true if the appointment was added successfully
	 */
	public static boolean addAppointment(Appointment appointment, ObservableList<Reminder> reminders) {
		int appointmentId = getNextID("appointmentId", "appointment");
		String title = appointment.getTitle();

		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime startUTC = appointment.getStart().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
		LocalDateTime endUTC = appointment.getEnd().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

		String sql = "INSERT INTO appointment VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {
			PreparedStatement prepstmt = conn.prepareStatement(sql);

			prepstmt.setInt(1, appointmentId);
			prepstmt.setInt(2, appointment.getCustomerId());
			prepstmt.setString(3, title);
			prepstmt.setString(4, appointment.getDescription());
			prepstmt.setString(5, appointment.getLocation());
			prepstmt.setString(6, appointment.getContact());
			prepstmt.setString(7, appointment.getUrl());
			prepstmt.setTimestamp(8, Timestamp.valueOf(startUTC));
			prepstmt.setTimestamp(9, Timestamp.valueOf(endUTC));
			prepstmt.setString(10, loggedInUser);
			prepstmt.setString(11, loggedInUser);
			prepstmt.executeUpdate();

			// add reminders
			int reminderId = getNextID("reminderId", "reminder");
			sql = "INSERT INTO reminder VALUES (?, ?, ?, ?, ?, null,  NOW(), ?)";
			prepstmt = conn.prepareStatement(sql);
			for (Reminder reminder : reminders) {
				LocalDateTime reminderTime = reminder.getLocalDT().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

				ChronoUnit snoozeUnit = reminder.getSnoozeUnit();
				int snoozeIncType = (snoozeUnit == ChronoUnit.HOURS) ? 2 : ((snoozeUnit == ChronoUnit.DAYS) ? 3 : 1);

				prepstmt.setInt(1, reminderId);
				prepstmt.setTimestamp(2, Timestamp.valueOf(reminderTime));
				prepstmt.setInt(3, reminder.getSnoozeIncrement());
				prepstmt.setInt(4, snoozeIncType);
				prepstmt.setInt(5, reminder.getAppointmentId());
				prepstmt.setString(6, loggedInUser);

				prepstmt.executeUpdate();

				reminderId++;
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			return false;
		}
		return true;
	}
	/**
	 * Update an appointment in the database
	 * @param oldAppointment the appointment in the database to update
	 * @param newAppointment the appointment information to update the old customer to
         * @param reminders the list of reminders for the updated appointment
	 * @return true if the update was successful, false if not
	 */
	public static boolean updateAppointment(Appointment oldAppointment, Appointment newAppointment, ObservableList<Reminder> reminders) {
		int appointmentId = Appointment.getAppointmentId(oldAppointment);
		String oldTitle = oldAppointment.getTitle();
		String newTitle = newAppointment.getTitle();

		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime startUTC = newAppointment.getStart().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
		LocalDateTime endUTC = newAppointment.getEnd().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {

			String sql = "UPDATE appointment SET customerId=?, title=?, description=?, "
					+ "location=?, contact=?, url=?, start=?, end=?, "
					+ "lastUpdate=NOW(), lastUpdateBy=? "
					+ "WHERE appointmentId = ?";
			PreparedStatement prepstmt = conn.prepareStatement(sql);

			prepstmt.setInt(1, newAppointment.getCustomerId());
			prepstmt.setString(2, newTitle);
			prepstmt.setString(3, newAppointment.getDescription());
			prepstmt.setString(4, newAppointment.getLocation());
			prepstmt.setString(5, newAppointment.getContact());
			prepstmt.setString(6, newAppointment.getUrl());
			prepstmt.setTimestamp(7, Timestamp.valueOf(startUTC));
			prepstmt.setTimestamp(8, Timestamp.valueOf(endUTC));
			prepstmt.setString(9, loggedInUser);
			prepstmt.setInt(10, appointmentId);
			prepstmt.executeUpdate();

			// remove the old reminders
			sql = "DELETE FROM reminder WHERE appointmentId = ?";
			prepstmt = conn.prepareStatement(sql);
			prepstmt.setInt(1, appointmentId);
			prepstmt.executeUpdate();

			// add the new reminders
			int reminderId = getNextID("reminderId", "reminder");
			sql = "INSERT INTO reminder VALUES (?, ?, ?, ?, ?, null, NOW(), ?)";
			prepstmt = conn.prepareStatement(sql);
			for (Reminder reminder : reminders) {
				LocalDateTime reminderTime = reminder.getLocalDT().atZone(zone).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

				ChronoUnit snoozeUnit = reminder.getSnoozeUnit();
				int snoozeIncType = (snoozeUnit == ChronoUnit.HOURS) ? 2 : ((snoozeUnit == ChronoUnit.DAYS) ? 3 : 1);

				prepstmt.setInt(1, reminderId);
				prepstmt.setTimestamp(2, Timestamp.valueOf(reminderTime));
				prepstmt.setInt(3, reminder.getSnoozeIncrement());
				prepstmt.setInt(4, snoozeIncType);
				prepstmt.setInt(5, appointmentId);
				prepstmt.setString(6, loggedInUser);

				prepstmt.executeUpdate();

				reminderId++;
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			return false;
		}
		return true;
	}

	/**
	 * get the next available ID number in a table from the database
	 * @param idColumn the name of the column for the id
	 * @param table the name of the table
	 * @return an integer that is 1 plus the maximum id number found, -1 if there was an database issue
	 */
	public static int getNextID(String idColumn, String table) {
		int nextID = -1;

		String sql = "SELECT MAX(" + idColumn + ") FROM " + table;

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				Statement stmt = conn.createStatement(); 
				ResultSet result = stmt.executeQuery(sql); ) {

			if(result.next()) {
				nextID = result.getInt(1) + 1;
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}

		assert nextID > 0;
		return nextID;
	}

	/**
	 * Log in to the application checking against user names and passwords stored in the database
	 * @param loginUser user name
	 * @param loginPass PASSWORD
	 */
	public void login(String loginUser, String loginPass) {
		// initialize the SQL query to retrieve a matching user name and PASSWORD
		// from a user with an active status as 1. I am assuming here that an active
		// status of 0 can be treated as inactive and should not be able to login.
		String sql = "SELECT * FROM user WHERE active = 1 "
				+ "AND userName = ? AND password = ?";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ) {

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, loginUser);
			stmt.setString(2, loginPass);
			ResultSet results = stmt.executeQuery();

			// if there is a result, show the main screen, otherwise throw the exception
			if (results.next()) {
				isLoggedIn = true;
				loggedInUser = loginUser;
                                ActivityLogger.recordLogin(loginUser);
				showMainScreen();
			} else throw new IllegalArgumentException("username password mismatch");

		} catch (SQLException e) {

			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		} catch (IllegalArgumentException e) {

			if(e.getMessage().equals("username password mismatch")) {

				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle(lang.getString("badLoginTitle"));
				alert.setHeaderText(lang.getString("badLoginMessage"));
				alert.initOwner(primaryStage);
				alert.showAndWait();

	
			} else {
				throw e;
			}
		}
	}
	/**
	 * Get a list of the active users from the database
	 * @return a list of all the active users in the database
	 */
	public static ObservableList<String> getActiveUsers() {

		ObservableList<String> users = FXCollections.observableArrayList();

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet results = stmt.executeQuery("SELECT userName FROM user WHERE active = 1"); ) {

			while (results.next()) users.add(results.getString(1));

		} catch (SQLException e) {

			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());

		}
		return users;
	}

	
	
	/**
	 * Add reminders to the reminder queue for any appointment from now until one hour from now
	 */
	private static void addRemindersToQueue() {
		// if the user is not yet logged in, go no further
		if(!isLoggedIn) return;

		String sql = "SELECT title, start FROM appointment "
				+ "WHERE createdBy = ? "
				+ "AND start BETWEEN ? AND ? "
				+ "ORDER BY start";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){

			LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(14);
			LocalDateTime nowPlusOneHour = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);

			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setString(1, loggedInUser);
			prepstmt.setTimestamp(2, Timestamp.valueOf(now));
			prepstmt.setTimestamp(3, Timestamp.valueOf(nowPlusOneHour));

			ResultSet results = prepstmt.executeQuery();

			while (results.next()) {
				String title = results.getString("title");
				LocalDateTime apptTimeUTC = results.getTimestamp("start").toLocalDateTime();
				LocalDateTime apptTime = apptTimeUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

				REMINDER_QUEUE.putIfAbsent(title, apptTime);
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Look are the reminder queue and show any alerts as needed based on the time of the appointment.
	 * NOTE: The task requirements did not say to show alerts with reminders. In fact, in spite of any reminders, the task
	 * requirements state that alerts should be shown 15 minutes before appointments. That is what is done in this method.
	 * In practice, this method would only look at reminder times and not appointment times.
	 */
	private static void showReminderAlert() {
		try {
			// if there are no reminders in the queue, go no further
			if (REMINDER_QUEUE.isEmpty()) return;

			// loop through all reminders in the queue
			for(Iterator<Map.Entry<String, LocalDateTime>> iter = REMINDER_QUEUE.entrySet().iterator(); iter.hasNext(); ) {

				Map.Entry<String, LocalDateTime> entry = iter.next();
				String key = entry.getKey();
				LocalDateTime value = entry.getValue();

				// if the reminder time has passed 15 minutes from now
				if(!value.isAfter(LocalDateTime.now().plusMinutes(15))) {
					// show an alert window to be run on the JavaFX thread
					// this must be done since the showReminderAlert method may not be run in the JavaFx thread
					Platform.runLater(() -> runReminderAlertOnFxThread(key, value));

					iter.remove(); // remove the reminder that was just shown
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Show the alert window for an appointment reminder
	 * @param title the title of the appointment
	 * @param time the time of the appointment
	 */
	private static void runReminderAlertOnFxThread(String title, LocalDateTime time) {
		Duration timeUntil = Duration.between(LocalDateTime.now(), time);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Reminder");
		alert.setHeaderText(title + " " + "is in" + " " +
				(timeUntil.toMinutes() + 1) + " " + "minutes" + ".");
		alert.show();
	}
}
