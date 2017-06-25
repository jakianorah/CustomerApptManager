/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195application.model;

import c195application.C195Application;
import static c195application.C195Application.PASSWORD;
import static c195application.C195Application.USER_NAME;
import static c195application.C195Application.lang;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 *
 * @author jakianorah
 */
public class Appointment {
  private int customerId;
	private String title;
	private String description;
	private String location;
	private String contact;
	private String url;
	private LocalDateTime start;
	private LocalDateTime end;

	public Appointment(int customerId, String title, String description, String location, String contact, String url, LocalDateTime start, LocalDateTime end) {
		this.customerId = customerId;
		setTitle(title);
		setDescription(description);
		setLocation(location);
		setContact(contact);
		setUrl(url);
		setStart(start);
		setEnd(end);
	}

	@Override
	public String toString() {
		DateTimeFormatter shortTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
		String startTime = start.toLocalTime().format(shortTime);
		return startTime + " - " + title;
	}

	/**
	 * Get the ID of a appointment in the database
	 * @param appointment the appointment to find in the database
	 * @return the id number of the appointment in the database, -1 if the appointment was not found
	 */
	public static int getAppointmentId(Appointment appointment) {
		int appointmentId = -1;
		String sql = "SELECT appointmentId FROM appointment "
				+ "WHERE customerId = ? "
				+ "AND title = ? "
				+ "AND description = ? "
				+ "AND location = ? "
				+ "AND contact = ? "
				+ "AND url = ? "
				+ "AND start = ? "
				+ "AND end = ?";

		try ( Connection conn = DriverManager.getConnection(C195Application.URL, USER_NAME, PASSWORD);
				PreparedStatement prepstmt = conn.prepareStatement(sql); ) {

			ZonedDateTime startZoned = appointment.getStart().atZone(ZoneId.systemDefault());
			ZonedDateTime endZoned = appointment.getEnd().atZone(ZoneId.systemDefault());
			LocalDateTime startUTC = startZoned.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
			LocalDateTime endUTC = endZoned.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

			prepstmt.setInt(1, appointment.getCustomerId());
			prepstmt.setString(2, appointment.getTitle());
			prepstmt.setString(3, appointment.getDescription());
			prepstmt.setString(4, appointment.getLocation());
			prepstmt.setString(5, appointment.getContact());
			prepstmt.setString(6, appointment.getUrl());
			prepstmt.setTimestamp(7, Timestamp.valueOf(startUTC));
			prepstmt.setTimestamp(8, Timestamp.valueOf(endUTC));
			ResultSet result = prepstmt.executeQuery();

			if(result.next()) {
				appointmentId = result.getInt("appointmentId");
			}
		} catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}

		return appointmentId;
	}

	public Duration getDuration() {
		return Duration.between(start, end);
	}
	public int getCustomerId() {
		return customerId;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public String getLocation() {
		return location;
	}
	public String getContact() {
		return contact;
	}
	public String getUrl() {
		return url;
	}
	public LocalDateTime getStart() {
		return start;
	}
	public LocalDateTime getEnd() {
		return end;
	}

	public void setCustomerId(int id) {
		if(id < 0) {
			throw new IllegalArgumentException("Customer ID " + lang.getString("lessThanOneMessage"));
		} else {
			customerId = id;
		}
	}
	public void setTitle(String title) {
		if(title == null || title.isEmpty()) {
			throw new IllegalArgumentException("Title"+ lang.getString("missingInfoMessage"));
		} else {
			this.title = title;
		}
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setStart(LocalDateTime start) {
		if(start == null) {
			throw new IllegalArgumentException("startTime" + " " + lang.getString("missingInfoMessage"));
		} else {
			this.start = start;
		}
	}
	public void setEnd(LocalDateTime end) {
		if(end == null) {
			throw new IllegalArgumentException("endTime" + " " + lang.getString("missingInfoMessage"));
		} else {
			this.end = end;
		}
	}

	public boolean isOnDate(LocalDate date) {
		return start.toLocalDate().equals(date);
	}  
}
