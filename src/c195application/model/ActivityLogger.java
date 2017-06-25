/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 *
 * @author jakianorah
 */
public class ActivityLogger {
   public static void recordLogin(String username){
        String logFilename = "loginActivity.txt";
        String logPath = "LoginTracker";
        File logFile = new File(logPath,logFilename);
        File logDir = new File(logPath);
        
        if (!logDir.exists()){
            logDir.mkdir();
            System.out.println("Successfully created logging directory");
        }
        
        try(FileWriter logWriter = new FileWriter(logFile,true)){
            logWriter.write("\n"+LocalDateTime.now() +": "+username+" logged in.");
            System.out.println("Successfully wrote log to: "+logFile.getAbsolutePath());
        }catch(IOException ex){
            System.out.println("Cannot write to log file");
            ex.printStackTrace();
        }
    }
     
}
