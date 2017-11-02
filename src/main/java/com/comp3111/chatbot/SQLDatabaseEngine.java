package com.comp3111.chatbot;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine {
	
	String searchSuggestedLinks(String text) throws Exception {
		//Write your code here...
		String result = null;
		
		Connection connection = this.getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM chatbot");
		ResultSet rs = stmt.executeQuery();
		
		try {
			
			String sCurrentLine;
			while (rs.next()) {

				// To do 
				
			}
		}
		catch (Exception e) {
			log.info("Exception while connection: {}", e.toString());
		}
		finally {
			try {
				rs.close();
				stmt.close();
				connection.close();
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}
		
		if (result != null)
			return result;
		
		throw new Exception("NOT FOUND");
	
	}
	
	
	public void storeAction(String id, String text, Action act){
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO mainflow VALUES('"+ id + "'," + "'"+text +"', "+ "'"+act.name() +"' );" );
			ResultSet rs = stmt.executeQuery();
		
			rs.close();
			stmt.close();
			connection.close();
			
			}
			catch (Exception e) {
				log.info("Exception while storing: {}", e.toString());
			}
		}		
	
	
	public Action nextAction(String id){
		Action next= Action.valueOf("MAIN");
		
		Connection connection = this.getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM mainflow");
		ResultSet rs = stmt.executeQuery();
		
		try {
			
			String sCurrentLine;
			while (rs.next()) {

				sCurrentLine = rs.getString(1) + ":" +  rs.getString(2) + rs.getString(3) ;
				String[] parts = sCurrentLine.split(":");
				
				if (id.equals(parts[0])) {
					next = Action.valueOf(parts[2]);
				}
				
			}
		}
		catch (Exception e) {
			log.info("Exception while connection: {}", e.toString());
		}
		finally {
			try {
				rs.close();
				stmt.close();
				connection.close();
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}				
		
		return next;
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
