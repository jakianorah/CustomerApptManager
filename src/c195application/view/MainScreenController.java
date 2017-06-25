/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import c195application.C195Application;
import static c195application.C195Application.lang;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class MainScreenController  {
private Stage stage;

	@FXML
	private BorderPane rootPane;

	@FXML
	private RadioMenuItem monthlyView;
	@FXML
	private RadioMenuItem weeklyView;

	public MainScreenController() {};

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {

		// Create a new toggle group
		ToggleGroup radioGroup = new ToggleGroup();

		// add the radio buttons to the toggle group
		monthlyView.setToggleGroup(radioGroup);
		weeklyView.setToggleGroup(radioGroup);

		// set up the listener for the radio buttons
		radioGroup.selectedToggleProperty().addListener((obs, prevView, newView) -> switchCalendarView(newView));
	}

	/**
	 * Set up the main screen with a reference to this controller's stage
	 * @param mainStage
	 */
	public void setupMainScreen(Stage mainStage) {

		stage = mainStage;

		switchCalendarView(monthlyView);
	}

	/**
	 * Switch the calendar view to monthly or weekly views based on which radio menu item was selected
	 * @param selectedView the radio menu item that was selected
	 */
	private void switchCalendarView(Toggle selectedView) {

		if (selectedView == null) return;

		// initialize the FXML loader with resources
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(lang);

		try {

			if (selectedView.equals(monthlyView)) {

				loader.setLocation(C195Application.class.getResource("view/CalendarMonth.fxml"));
				rootPane.setCenter(loader.load());
				CalendarMonthController controller = loader.getController();
				controller.setupMonthlyView(stage, stage.getScene());

			} else if (selectedView.equals(weeklyView)) {

				loader.setLocation(C195Application.class.getResource("view/CalendarWeek.fxml"));
				rootPane.setCenter(loader.load());
				CalendarWeekController controller = loader.getController();
				controller.setupWeeklyView(stage, stage.getScene());

			} else {
				throw new IllegalArgumentException("There was a problem selcting which view is desired.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show the customer view window
	 */
	@FXML
	private void handleViewAllCustomers() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/CustomerView.fxml"));
			loader.setResources(lang);
			AnchorPane viewCustomersRoot = (AnchorPane) loader.load();

			Stage viewCustomerStage = new Stage();
			viewCustomerStage.initModality(Modality.WINDOW_MODAL);
			viewCustomerStage.initOwner(stage);

			Scene scene = new Scene(viewCustomersRoot);
			viewCustomerStage.setScene(scene);

			CustomerViewController controller = loader.getController();
			controller.setupDialog(viewCustomerStage);

			viewCustomerStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show a dialog that allows access to any consultant's schedule
	 */
	@FXML
	private void handleGetSchedules() {
		try {
			// initialize the FXML loader with resources
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/ReportConsultantSchedule.fxml"));
			loader.setResources(lang);
			AnchorPane schedulesRoot = (AnchorPane) loader.load();

			// set up the stage for the schedules dialog
			Stage schedulesStage = new Stage();
			schedulesStage.initModality(Modality.WINDOW_MODAL);
			schedulesStage.initOwner(stage);

			Scene scene = new Scene(schedulesRoot);
			schedulesStage.setScene(scene);

			// get the controller for the dialog
			ReportConsultantScheduleController controller = loader.getController();
			controller.setupDialog(schedulesStage);

			schedulesStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show a bar chart of the number of appointments by type and month
	 */
	@FXML
	private void handleApptSummary() {
		try {
			// initialize the FXML loader with resources
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/ReportApptTypeMonth.fxml"));
			loader.setResources(lang);
			AnchorPane summaryRoot = (AnchorPane) loader.load();

			// set up the stage for the summary dialog
			Stage summaryStage = new Stage();
			summaryStage.initModality(Modality.WINDOW_MODAL);
			summaryStage.initOwner(stage);

			Scene scene = new Scene(summaryRoot);
			summaryStage.setScene(scene);

			// get the controller for the dialog and pass all appointments
			ReportApptTypeMonthController controller = loader.getController();

//			LocalDateTime firstDay = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0, 0);
//			LocalDateTime lastDay = firstDay.plusYears(1);
//			controller.setAppointmentData(getAppointments(firstDay, lastDay));

			summaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show a pie chart of the percentages of which locations customers are from
	 */
	@FXML
	private void handleCustomerLocationView() {
		try {
			// initialize the FXML loader with resources
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/ReportApptsInNextSevDays.fxml"));
			loader.setResources(lang);
			AnchorPane customerLocationRoot = (AnchorPane) loader.load();

			// set up the stage for the customer location dialog
			Stage customerLocationStage = new Stage();
			customerLocationStage.initModality(Modality.WINDOW_MODAL);
			customerLocationStage.initOwner(stage);

			Scene scene = new Scene(customerLocationRoot);
			customerLocationStage.setScene(scene);

			// get the controller for the dialog and pass all appointments
			ReportApptsInNextSevDaysController controller = loader.getController();

//			controller.setData(getCustomerLocationData());

			customerLocationStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
}
