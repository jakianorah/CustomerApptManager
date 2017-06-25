/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import static c195application.C195Application.PASSWORD;
import static c195application.C195Application.USER_NAME;
import static c195application.C195Application.URL;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class ReportApptsInNextSevDaysController  {

    @FXML
    public ListView<String> lstReport;

	

	/**
	 * The initialize method is called after the constructor by JavaFX
	 */
	@FXML
	private void initialize() {
		
        List<String> results = new ArrayList<>();
            String sql = "SELECT COUNT(appointmentId) as 'No. of Appointments' FROM U03wsB.appointment where START BETWEEN NOW() AND DATE_ADD(NOW(), interval 7 DAY);";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add(rs.getString("No. of Appointments"));
        }
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}  
    
}
