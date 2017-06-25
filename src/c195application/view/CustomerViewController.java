/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import c195application.C195Application;
import c195application.model.Customer;
import static c195application.C195Application.URL;
import static c195application.C195Application.PASSWORD;
import static c195application.C195Application.USER_NAME;
import static c195application.C195Application.lang;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class CustomerViewController  {
    private Stage stage;

	@FXML
	private TableView<Customer> customerTable;
	@FXML
	private TableColumn<Customer, String> firstNameColumn;
	@FXML
	private TableColumn<Customer, String> lastNameColumn;
	@FXML
	private TableColumn<Customer, String> address1Column;
	@FXML
	private TableColumn<Customer, String> address2Column;
	@FXML
	private TableColumn<Customer, String> postalCodeColumn;
	@FXML
	private TableColumn<Customer, String> phoneColumn;
	@FXML
	private TableColumn<Customer, String> cityColumn;
	@FXML
	private TableColumn<Customer, String> countryColumn;

	@FXML
	private Button delButton;
	@FXML
	private Button editButton;

	private ObservableList<Customer> customers = FXCollections.observableArrayList();

	public CustomerViewController() {}

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {

		firstNameColumn.setCellValueFactory(cell -> cell.getValue().firstNameProperty());
		lastNameColumn.setCellValueFactory(cell -> cell.getValue().lastNameProperty());
		address1Column.setCellValueFactory(cell -> cell.getValue().address1Property());
		address2Column.setCellValueFactory(cell -> cell.getValue().address2Property());
		postalCodeColumn.setCellValueFactory(cell -> cell.getValue().postalCodeProperty());
		phoneColumn.setCellValueFactory(cell -> cell.getValue().phoneProperty());
		cityColumn.setCellValueFactory(cell -> cell.getValue().cityProperty());
		countryColumn.setCellValueFactory(cell -> cell.getValue().countryProperty());

		customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
			editButton.setDisable(newSel == null);
			delButton.setDisable(newSel == null);
		});

	}

	/**
	 * Set up the customer view window with a reference to this controller's stage
	 * @param stage the stage for the Customer View Controller
	 */
	public void setupDialog(Stage stage) {
		this.stage = stage;

		customers = C195Application.getAllCustomers();

		customerTable.setItems(customers);
	}

	/**
	 * Set the customer information dialog appropriately based on one (or no) selection
	 */
	private void showCustomerInfoDialog(Customer customer) {
		try {
			// set up the root for the customer information dialog
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(C195Application.class.getResource("view/AddCustomer.fxml"));
			loader.setResources(lang);
			AnchorPane customerInfoRoot = (AnchorPane) loader.load();

			// set up the stage for the customer information dialog
			Stage customerInfoStage = new Stage();
			customerInfoStage.setTitle("");
			customerInfoStage.initModality(Modality.WINDOW_MODAL);
			customerInfoStage.initOwner(stage);

			// add a new scene with the root to the stage
			Scene scene = new Scene(customerInfoRoot);
			customerInfoStage.setScene(scene);

			// get the controller for the dialog and pass a reference  
			// the customer info dialog stage, and a customer, if one was selected
			AddCustomerController controller = loader.getController();
			controller.setupDialog(customerInfoStage, customer);

			// show the customer information dialog
			customerInfoStage.showAndWait();

			// refresh the customer list after an update
			Customer newCustomer = controller.getNewCustomer();
			if (newCustomer == null) return;

			if(customer == null) {
				customers.add(newCustomer);
			} else {
				customers.set(customers.indexOf(customer), newCustomer);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Delete a customer. I've made the assumption that you never wish to completely delete information.
	 * So, deleting a customer from the database will set the active field to zero effectively removing it
	 * while retaining data.
	 * @param customer
	 */
	public void deleteCustomer(Customer customer) {

		customers.remove(customer);

		String sql = "UPDATE customer SET active = 0 WHERE customerId = ?";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
				PreparedStatement prepstmt = conn.prepareStatement(sql); ) {

			int customerId = Customer.getCustomerId(customer);
			prepstmt.setInt(1, customerId);
			prepstmt.executeUpdate();

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}

	/**
	 * Show the customer information window with all text fields initially empty
	 */
	@FXML
	private void handleAdd() {
		showCustomerInfoDialog(null);	
	}
	/**
	 * Show the customer information window with all text fields set to a selected customer
	 */
	@FXML
	private void handleEdit() {
		Customer customer = customerTable.getSelectionModel().getSelectedItem();
		showCustomerInfoDialog(customer);
	}
	/**
	 * Delete the selected customer
	 */
	@FXML
	private void handleDelete() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		alert.setHeaderText(lang.getString("deleteCustomerMessage"));
		alert.initOwner(stage);
		alert.showAndWait()
		.filter(answer -> answer == ButtonType.YES)
		.ifPresent(answer -> {
			Customer customer = customerTable.getSelectionModel().getSelectedItem();
			deleteCustomer(customer);
		});
	}
    
}
