//
//  DatabaseConnection.java
//  Operanter
//
//  Created by Robert Lachlan on 4/6/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.util.*;
import java.sql.*;
import java.io.*;


public class DatabaseConnection {
	
	boolean connected=false;
	Connection con=null;
	
	
	boolean writeText=false;
	UserInterface ui;
	int counter=0;
	
	public boolean doConnect(){
		
		try{	
			String url="jdbc:h2:database";

			ResultSet rs;
			try {
				Class.forName("org.h2.Driver"); 
			}
			catch(Exception e){e.printStackTrace();}
			con = DriverManager.getConnection(url, "sa", "");
			if (con!=null){
				connected=true;
			}
			
        }
        catch(SQLException ex ) {
			System.out.println("SQLException: " + ex.getMessage()); 
            System.out.println("SQLState: " + ex.getSQLState()); 
            System.out.println("VendorError: " + ex.getErrorCode());
			ex.printStackTrace();
        }
        //finally {}
		return connected;
    }
	
	public void doWriteText(UserInterface ui){
		writeText=true;
		System.out.println("ui logging switched on");
		this.ui=ui;
	}
	
	public synchronized void createDatabase(){
		
		Statement stmt = null; 
		String query="CREATE TABLE datalog(id INT IDENTITY PRIMARY KEY, time BIGINT, objectReg CHAR(30), actionReg CHAR(30), experimentType CHAR(30), channel INT, chamber INT)";
		try {
			
			stmt = con.createStatement(); 
			stmt.executeUpdate(query);;
		} 
		catch (Exception e){
			e.printStackTrace();
		}
		finally { 
			if (stmt != null) { 
				try { stmt.close();} catch (SQLException sqlEx) {} 
				stmt = null; 
			} 
		}
	}
	
	public synchronized void writeToDatabase(long time, String expt, String act, String obj, int chan,  int cham) {
		String insertStmt = "INSERT INTO datalog (time, objectReg, actionReg, experimentType, channel, chamber) values(?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(insertStmt);
			
			stmt.setLong(1, time);
			stmt.setString(2, obj);
			stmt.setString(3, act);
			stmt.setString(4, expt);
			stmt.setInt(5, chan);
			stmt.setInt(6, cham);

			stmt.executeUpdate();
		} 
		catch (Exception e){
			System.out.println("failed to write");
			e.printStackTrace();
		}
		finally { 
			
			if (stmt != null) { 
				try { stmt.close();} catch (SQLException sqlEx) {} 
				stmt = null; 
			} 
		}
	}
	
	public synchronized void writeToDatabase(LogEvent le) {
		counter++;
		String insertStmt = "INSERT INTO datalog (time, objectReg, actionReg, experimentType, channel, chamber) values(?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(insertStmt);
			
			stmt.setLong(1, le.time);
			stmt.setString(2, le.objectName);
			stmt.setString(3, le.actionName);
			stmt.setString(4, le.experimentName);
			stmt.setInt(5, le.channel);
			stmt.setInt(6, le.chamber);
			
			stmt.executeUpdate();
		} 
		catch (SQLException e){
			
			int co=e.getErrorCode();
			String st=e.getSQLState();
			
			try{
				createDatabase();
				writeToDatabase(le);
			}
			catch(Exception e2){}
			System.out.println("failed to write "+co+" "+st);
			e.printStackTrace();
		}
		finally { 
			
			if (stmt != null) { 
				try { stmt.close();} catch (SQLException sqlEx) {} 
				stmt = null; 
			} 
		}
		System.out.println(writeText+" "+counter);
		if (writeText){
			System.out.println("here");
			ui.writeLogToTextArea(le, counter);
		}
	}
	
	public synchronized void writeToDatabase(LogEvent le, UserInterface ui2) {
		counter++;
		String insertStmt = "INSERT INTO datalog (time, objectReg, actionReg, experimentType, channel, chamber) values(?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(insertStmt);
			
			stmt.setLong(1, le.time);
			stmt.setString(2, le.objectName);
			stmt.setString(3, le.actionName);
			stmt.setString(4, le.experimentName);
			stmt.setInt(5, le.channel);
			stmt.setInt(6, le.chamber);
			
			stmt.executeUpdate();
		} 
		catch (SQLException e){
			
			int co=e.getErrorCode();
			String st=e.getSQLState();
			
			try{
				createDatabase();
				writeToDatabase(le);
			}
			catch(Exception e2){}
			System.out.println("failed to write "+co+" "+st);
			e.printStackTrace();
		}
		finally { 
			
			if (stmt != null) { 
				try { stmt.close();} catch (SQLException sqlEx) {} 
				stmt = null; 
			} 
		}
		System.out.println(writeText+" "+counter);
		if (writeText){
			ui2.writeLogToTextArea(le, counter);
		}
	}
	
		
	public synchronized LogEvent[] readFromDatabase(long cutOffTime) {
		String selectStmt="SELECT time, objectReg, actionReg, experimentType, channel, chamber FROM datalog WHERE time>"+cutOffTime;
		
		PreparedStatement stmt = null; 
		ResultSet rs = null; 
		LinkedList ll=new LinkedList();
		try {
			stmt = con.prepareStatement(selectStmt);
			rs = stmt.executeQuery( );
			while( rs.next( ) ) {
				
				long time=rs.getLong("time");
				
				String obj=rs.getString("objectReg");
				String act=rs.getString("actionReg");
				String expt=rs.getString("experimentType");
				
				int chan=rs.getInt("channel");
				int cham=rs.getInt("chamber");
				
				LogEvent le=new LogEvent(time, obj, act, expt, chan, cham);
				
				ll.add(le);
			}
		}
		catch(Exception e){
			System.out.println("failed to read");
			e.printStackTrace();
		}
		finally { 
			if (rs !=null){
				try{rs.close();}catch (SQLException sqlEx) {} 
				rs = null; 
			}
			if (stmt != null) { 
				try { stmt.close();} catch (SQLException sqlEx) {} 
				stmt = null; 
			} 
		}	
		
		
		LogEvent[] le=new LogEvent[ll.size()];
		
		for (int i=0; i<ll.size(); i++){
			le[i]=(LogEvent)ll.get(i);
		}
		
		return le;
		
	}
	

	
	
	
	

}
