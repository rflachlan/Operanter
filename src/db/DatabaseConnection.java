package db;
//
//  DatabaseConnection.java
//  Operanter
//
//  Created by Robert Lachlan on 4/6/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.sql.Date;
import java.io.*;

import op.Defaults;
import op.UserInterface;


public class DatabaseConnection {
	
	boolean connected=false;
	Connection con=null;
	
	
	boolean writeText=false;
	UserInterface ui;
	int counter=0;
	Defaults defaults;
	
	public boolean doConnect(Defaults defaults){
		this.defaults=defaults;
		
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
		return connected;
    }
	
	public void doWriteText(UserInterface ui){
		writeText=true;
		System.out.println("ui logging switched on");
		this.ui=ui;
	}
	
	public synchronized void createDatabase(){
		
		Statement stmt = null; 
		String query="CREATE TABLE datalog(id INT IDENTITY PRIMARY KEY, time BIGINT, objectReg CHAR(30), actionReg CHAR(30), experimentName CHAR(30), experimentType CHAR(30), pin CHAR(30))";
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
	
	public synchronized void wipeDatabase(){
		Statement stmt = null; 
		String query="DELETE FROM datalog";
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
	
	public synchronized void writeToDatabase(long time, String expt, String type, String act, String obj, String pin) {
		String insertStmt = "INSERT INTO datalog (time, objectReg, actionReg, experimentName, experimentType, pin) values(?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(insertStmt);
			
			stmt.setLong(1, time);
			stmt.setString(2, obj);
			stmt.setString(3, act);
			stmt.setString(4, expt);
			stmt.setString(5, type);
			stmt.setString(6, pin);

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
		String insertStmt = "INSERT INTO datalog (time, objectReg, actionReg, experimentName, experimentType, pin) values(?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(insertStmt);
			
			stmt.setLong(1, le.time);
			stmt.setString(2, le.objectName);
			stmt.setString(3, le.actionName);
			stmt.setString(4, le.experimentName);
			stmt.setString(5, le.experimentType);
			stmt.setString(6, le.pin);
			
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
		String insertStmt = "INSERT INTO datalog (time, objectReg, actionReg, experimentName, experimentType, pin) values(?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(insertStmt);
			
			stmt.setLong(1, le.time);
			stmt.setString(2, le.objectName);
			stmt.setString(3, le.actionName);
			stmt.setString(4, le.experimentName);
			stmt.setString(5, le.experimentType);
			stmt.setString(6, le.pin);
			
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
		String selectStmt="SELECT time, objectReg, actionReg, experimentName, experimentType, pin FROM datalog WHERE time>"+cutOffTime;
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
				String expt=rs.getString("experimentName");
				String type=rs.getString("experimentType");
				String pin=rs.getString("pin");
				
				LogEvent le=new LogEvent(time, obj, act, expt, type, pin);
				
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
	
	public void dumpData(){

		Date today = new Date(Calendar.getInstance().getTime().getTime());
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
		String today2 = new String();
		today2 = df.format(today);
		
		File f=new File(defaults.getStringProperty("parentpath") + "/" + today2 + ".xls");
		int fileType=0; //changed from 1
		downloadData(f, fileType);
	}
	
	public void downloadData(File f, int fileType){
		DocumentSave ds=new DocumentSave(f, fileType);
			
		ds.writeString("Time");
		ds.writeString("Time(ms)");
		ds.writeString("Device");
		ds.writeString("Action");
		ds.writeString("ExperimentType");
		ds.writeString("ExperimentName");
		ds.writeString("Pin");
		ds.writeLine();
		LogEvent[] le=readFromDatabase(0);
			
		for (int i=0; i<le.length; i++){
			ds.writeDate(le[i].time);
			ds.writeLong(le[i].time);
			ds.writeString(le[i].objectName);
			ds.writeString(le[i].actionName);
			ds.writeString(le[i].experimentType);
			ds.writeString(le[i].experimentName);
			ds.writeString(le[i].pin);
			ds.writeLine();	
		}
		ds.finishWriting();	
	}

}
