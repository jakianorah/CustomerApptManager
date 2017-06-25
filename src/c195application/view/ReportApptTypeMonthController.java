/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.view;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import static c195application.C195Application.URL;
import static c195application.C195Application.USER_NAME;
import static c195application.C195Application.PASSWORD;

/**
 * FXML Controller class
 *
 * @author jakianorah
 */
public class ReportApptTypeMonthController implements Initializable {

    @FXML
    private Button btnView;
    
    @FXML
    private ComboBox comboMonth;
    
    @FXML
    private ListView<String> lstReport;
    
    private ObservableList<String> months = FXCollections.observableArrayList();
    
    @FXML
    void onActionView(ActionEvent event){
        if(comboMonth.getSelectionModel().getSelectedItem().equals("May")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 05\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 05\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 05\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 05\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}
        else if(comboMonth.getSelectionModel().getSelectedItem().equals("June")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 06\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 06\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 06\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 06\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}

            else if(comboMonth.getSelectionModel().getSelectedItem().equals("July")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 07\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 07\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 07\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 07\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}
            else if(comboMonth.getSelectionModel().getSelectedItem().equals("August")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 08\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 08\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 08\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 08\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}
            else if(comboMonth.getSelectionModel().getSelectedItem().equals("September")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 09\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 09\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 09\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 09\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}

            else if(comboMonth.getSelectionModel().getSelectedItem().equals("October")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 10\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 10\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 10\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 10\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}

            else if(comboMonth.getSelectionModel().getSelectedItem().equals("November")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 11\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 11\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 11\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 11\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}

            else if(comboMonth.getSelectionModel().getSelectedItem().equals("December")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 12\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 12\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 12\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 12\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}
            else if(comboMonth.getSelectionModel().getSelectedItem().equals("January")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 01\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 01\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 01\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 01\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}
            else if(comboMonth.getSelectionModel().getSelectedItem().equals("February")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 02\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 02\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 02\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 02\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}
            else if(comboMonth.getSelectionModel().getSelectedItem().equals("March")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 03\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 03\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 03\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 03\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
        }
                    
             ObservableList<String> ids = FXCollections.observableArrayList(results);
            lstReport.setItems(ids);
           
            }
                
            
            catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
	}
            else if(comboMonth.getSelectionModel().getSelectedItem().equals("April")){
        
List<String> results = new ArrayList<>();
            String sql = "SELECT  (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='follow up' AND MONTH(start) = 04\n" +
"    ) AS follow_up,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='hand over responsibilities' AND MONTH(start) = 04\n" +
"    ) AS hand_over_responsibilities,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='customer requested' AND MONTH(start) = 04\n" +
"    ) AS customer_requested,\n" +
"    (\n" +
"    SELECT COUNT(description)\n" +
"    FROM   U03wsB.appointment where description='Sales'AND MONTH(start) = 04\n" +
"    ) AS Sales";

		try ( Connection conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD) ){


                    PreparedStatement prepstmt = conn.prepareStatement(sql);
            
                    ResultSet rs = conn.createStatement().executeQuery( sql );
             
                    while(rs.next()){
                 
                        results.add("Follow Up: "+rs.getString("follow_up"));
                        results.add("Hand Over Responsibilities: "+rs.getString("hand_over_responsibilities"));
                        results.add("Customer Requested: "+rs.getString("customer_requested"));
                        results.add("Sales: "+rs.getString("Sales"));
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
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         String[] monthNames = DateFormatSymbols.getInstance(Locale.getDefault()).getMonths();
		for (String month : monthNames) {
			if(!month.isEmpty()) months.add(month);
		}
		comboMonth.setItems(months);
    }    
        
    
}
