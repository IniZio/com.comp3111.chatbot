package com.comp3111.chatbot;

import lombok.extern.slf4j.Slf4j;
import java.net.URISyntaxException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbutils.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SQLDatabaseEngine {
	 String openingHourSearch(String text) throws Exception {
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			stmt = connection.prepareStatement("SELECT * FROM facilities WHERE index=?");
			int n = Integer.parseInt(text);
			stmt.setInt(1, n);
			rs = stmt.executeQuery();
			rs.next();
			result = "The opening hour of " + rs.getString(2) + " is:\n" + rs.getString(4);
		}catch(URISyntaxException e1){
			log.info("URISyntaxException: ", e1.toString());
		}catch(SQLException e2) {
			log.info("SQLException: ", e2.toString());
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(connection);
		}

		if(result!=null)
			return result;
		throw new Exception("NOT FOUND");
	}
	 
	 String showFacilitiesChoices() throws Exception {
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			stmt = connection.prepareStatement("SELECT * FROM facilities");
			rs = stmt.executeQuery();

			while(rs.next())
			{
				if(result == null)
					result = rs.getInt(1) + ". " + rs.getString(2) + "\n";
				else
					result += rs.getInt(1) + ". " + rs.getString(2) + "\n";
				}
		} catch(URISyntaxException e1) {
			log.info("URISyntaxException: ", e1.toString());
		} catch(SQLException e2) {
			log.info("SQLException: ", e2.toString());
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(connection);
		}

		if(result!=null)
			return result;
		else
			return "NOT FOUND";
	}
	
	
	public void storeAction(String id, String text, String action) throws Exception{
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {		
			connection = this.getConnection();
			stmt = connection.prepareStatement("SELECT count(*) FROM mainflow WHERE userid=" + "'" + id + "';");
			rs = stmt.executeQuery();
			rs.next();
			log.info("int = {}", rs.getInt(1));
			////check if the userid aready exit, if yes, update it, else inset a new entry
			if(rs.getInt(1)==0)
				stmt = connection.prepareStatement("INSERT INTO mainflow VALUES('"+ id + "'," + "'"+text +"', "+ "'"+action +"' );" );
			else
				stmt = connection.prepareStatement("UPDATE mainflow SET userinput=" + "'"+text +"',"+ "action=" + "'"+action +"'" + " where userid=''"+id+"';" );
			rs = stmt.executeQuery();
			connection.close();
		} catch (Exception e) {
			log.info("Exception while storing: {}", e.toString());
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(connection);
		}
	}		

	
	
	public String[] nextAction(String id) throws Exception{
		String[] next= new String [2];
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = this.getConnection();
			log.info("userid: {}", id);
			stmt = connection.prepareStatement("SELECT * FROM mainflow where userid = '" + id + "'");
			
			rs = stmt.executeQuery();
			String sCurrentLine;

			// Stupid way to loop til latest match record
			if(rs.next()) {
				do {
					sCurrentLine = rs.getString(1) + ":" + rs.getString(2) +":"+ rs.getString(3) ;
					String[] parts = sCurrentLine.split(":");
					
					if (id.equals(parts[0])) {
						next[0] = parts[1];
						next[1] = parts[2];
					}
				} while (rs.next());
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
