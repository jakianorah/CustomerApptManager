/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import static c195application.C195Application.PASSWORD;
import static c195application.C195Application.URL;
import static c195application.C195Application.USER_NAME;
import static c195application.C195Application.getActiveUsers;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class ReportConsultantScheduleController {

   private DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
	private DateTimeFormatter timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

	private Stage stage;

	@FXML
	private ComboBox<String> consultantComboBox;
	@FXML
	private TextArea scheduleTextArea;

	public ReportConsultantScheduleController() {};

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {
		consultantComboBox.setItems(getActiveUsers());

		consultantComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> 
		scheduleTextArea.setText(getConsultantSchedule(newSel)));
	}

	private String getConsultantSchedule(String name) {
		String schedule = "";

		String sql = "SELECT title, start, end FROM appointment WHERE createdBy = ? ORDER BY start";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){

			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setString(1, name);

			ResultSet results = prepstmt.executeQuery(); 

			LocalDate prevDate = LocalDate.MIN;

			while (results.next()) {
				String title = results.getString("title");
				Timestamp startStamp = results.getTimestamp("start");
				Timestamp endStamp = results.getTimestamp("end");

				LocalDateTime startUTC = startStamp.toLocalDateTime();
				LocalDateTime endUTC = endStamp.toLocalDateTime();

				LocalDateTime startLocal = startUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
				LocalDateTime endLocal = endUTC.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

				LocalDate date = startLocal.toLocalDate();

				String dateString = date.format(dateFormat);
				String startString = startLocal.toLocalTime().format(timeFormat);
				String endString = endLocal.toLocalTime().format(timeFormat);

				if (!date.isEqual(prevDate)) {
					schedule += System.lineSeparator() + dateString+ System.lineSeparator();
					prevDate = date;
				}

				schedule += "     " + startString + " - " + endString + " --> " + title + System.lineSeparator();
			}

		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
		return schedule;
	}

	public void setupDialog(Stage stage) {
		this.stage = stage;
	}
  
    
}
