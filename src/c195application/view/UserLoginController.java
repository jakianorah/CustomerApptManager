/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import c195application.C195Application;
import static c195application.C195Application.lang;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class UserLoginController  {

        private C195Application mainApp;
        
	private Stage stage;

	@FXML
	private TextField userNameField;
	@FXML
	private PasswordField passwordField;

	public UserLoginController() {}

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * Set up the user login window
	 * @param mainApp the main application
	 * @param stage the stage for the User Login Controller
	 */
	public void setUpLogin(C195Application mainApp, Stage stage) {
		this.stage = stage;
		this.mainApp = mainApp;
	}

	@FXML
	private void handleLogin() {

		// do not allow an empty user name
		// an empty PASSWORD could be allowed for a guest account
		if (userNameField.getText() == null || userNameField.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setHeaderText(lang.getString("userNameBlankMessage"));
			alert.initOwner(stage);
			alert.showAndWait();
			return;
		}

		// attempt to login
		mainApp.login(userNameField.getText(), passwordField.getText());
	}

	@FXML
	private void handleCancel() {
		Platform.exit();
	}

    
}
