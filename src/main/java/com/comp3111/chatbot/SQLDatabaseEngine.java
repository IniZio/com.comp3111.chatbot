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
	/**
	 * Searches the opening hours of a restaurant and returns it.
	 * 
	 * @param text An index which refers to the restaurant queried. 
	 * @return A string which contains the opening hours of the restaurant.
	 * @throws Exception if the input number is out of range or invalid.
	 */
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
			try {
				try { rs.close(); } catch (Exception e) {}
				try { stmt.close(); }  catch (Exception e) {}
				try { connection.close(); } catch (Exception e) {}
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}

		if(result!=null)
			return result;
		throw new Exception("NOT FOUND");
	}
	 /**
	  * Searches a matched link for the information queried in the database and returns it.
	  * 
	  * @param text An index which refers to the information queried. 
	  * @return	A string which contains the matched link.
	  * @throws Exception if the input number is out of range or invalid.
	  */
	 String linkSearch(String text) throws Exception {
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			stmt = connection.prepareStatement("SELECT * FROM links WHERE index=?");
			int n = Integer.parseInt(text);
			stmt.setInt(1, n);
			rs = stmt.executeQuery();
			rs.next();
			result = "You can go to " + rs.getString(3);
		}catch(URISyntaxException e1){
			log.info("URISyntaxException: ", e1.toString());
		}catch(SQLException e2) {
			log.info("SQLException: ", e2.toString());
		} finally {
			try {
				try { rs.close(); } catch (Exception e) {}
				try { stmt.close(); }  catch (Exception e) {}
				try { connection.close(); } catch (Exception e) {}
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}

		if(result!=null)
			return result;
		throw new Exception("NOT FOUND");
	}
	 
	 
	 
	 /**
	  * Formulate a list of facilities or choices of suggested links for reply.
	  * 
	  * @param control Input can only be either ACTION.OPENINGHOUR_CHOOSE for displaying facilities or ACTION.LINK_CHOOSE for displaying choices of suggested links.
	  * @return A list of String of facilities or choices for suggested links.
	  * @throws Exception
	  */
	 String showChoice(String control) throws Exception {
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			if (control.equals(ACTION.OPENINGHOUR_CHOOSE))
				stmt = connection.prepareStatement("SELECT * FROM facilities");
			else if (control.equals(ACTION.LINK_CHOOSE))
				stmt = connection.prepareStatement("SELECT * FROM links");
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
			try {
				try { rs.close(); } catch (Exception e) {}
				try { stmt.close(); }  catch (Exception e) {}
				try { connection.close(); } catch (Exception e) {}
			}
			catch (Exception e) {
				log.info("Exception while disconnection: {}", e.toString());
			}
		}

		if(result!=null)
			return result;
		else
			return "NOT FOUND";
	}
	
	 /**
	  * Stores the current action and message input by the user in the database.
	  * 
	  * @param id A String which contains the Line User id.
	  * @param text	A String which contains the Line message input by the user.
	  * @param action A String which refers to an ACTION.
	  * @throws Exception
	  */
	public void storeAction(String id, String text, String action) throws Exception{
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {		
			connection = this.getConnection();

			stmt = connection.prepareStatement("INSERT INTO mainflow VALUES('"+ id + "'," + "'"+text +"', "+ "'"+action +"' ) ON CONFLICT (userid) DO UPDATE SET userinput='" + text + "', action='" + action + "';" );			

			// stmt = connection.prepareStatement("INSERT INTO mainflow VALUES('"+ id + "'," + "'"+text +"', "+ "'"+action +"' ) WHERE ;" );
			rs = stmt.executeQuery();
			connection.close();
		} catch (Exception e) {
			log.info("Exception while storing: {}", e.toString());
		} finally {
			try {		
				try { rs.close(); } catch (Exception e) {}
				try { stmt.close(); }  catch (Exception e) {}
				try { connection.close(); } catch (Exception e) {}
			} catch (Exception e) {
				log.info("Exception while storing: {}", e.toString());
			}
		}
	}		

	/**
	 * Searches the latest actions of the user by User Id in the database for deciding next route or action.
	 * 
	 * @param id A String which contains the Line User id.
	 * @return A String array which contains the latest action and message input by the user.
	 * @throws Exception
	 */
	
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
				try { rs.close(); } catch (Exception e) {}
				try { stmt.close(); }  catch (Exception e) {}
				try { connection.close(); } catch (Exception e) {}
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
