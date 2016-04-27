package db;
//
//  DocumentSave.java
//  Operanter
//
//  Created by Robert Lachlan on 4/5/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//



import java.util.*;
import java.io.*;

//import javax.swing.*;

//import java.awt.*;

import op.Defaults;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;


public class DocumentSave {
	
	//Component host;
	Defaults defaults;
	//String thpath=null;
	//String name=null;
	//JFileChooser fc;
	int fileType=0;
	File file;
	HSSFWorkbook wb;
	HSSFSheet sheet;
	HSSFRow row;
	CreationHelper createHelper;
	PrintWriter Results;
	int rownum=0;
	int colnum=0;
	//Cursor c;
	//boolean suppressChoice=false;
	boolean readyToWrite=false;
	
	public DocumentSave(File file, int fileType){
		this.file=file;
		this.fileType=fileType;
		
		try{
			//if (!suppressChoice){host.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));}
			
			if (fileType==0){
				wb = new HSSFWorkbook();
				createHelper = wb.getCreationHelper();
				sheet = wb.createSheet("Results");
				row = sheet.createRow((short)0);
			}
			else{
				Results = new PrintWriter(new FileWriter(file));
			}
			
			
			readyToWrite=true;
		}
		catch(IOException e2){
			//if (!suppressChoice){
				//host.setCursor(c);
				//cont= JOptionPane.showConfirmDialog(host,"Couldn't save the file","Try again?", JOptionPane.YES_NO_OPTION);
				//if (cont==0){
					//SaveDocument sd=new SaveDocument(data, host);
				//}
			//}
			//else{System.exit(3);}
		}
		
		
		
		
	}
	
	/*
	public DocumentSave(Component host, Defaults defaults){
		this.defaults=defaults;
		this.host=host;
		c=host.getCursor();
		thpath=defaults.props.getProperty("path");
		name=defaults.props.getProperty("name");
		//file=new File(thpath, name);
	}
	
	public DocumentSave(String path, String name){
		thpath=path;
		file=new File(thpath, name);
		suppressChoice=true;
		fileType=2;
	}
	*/
	
	
	public void writeSheet(String s){
		if (fileType==0){
			sheet=wb.createSheet(s);
			rownum=0;
			row = sheet.createRow((short)0);
		}
		else if (fileType==1){
			Results.println(csvEntry(s));
		}
		else if (fileType==2){
			Results.println(textEntry(s));
		}
	}
	
	public void writeBoolean(boolean b){
		if (b){writeString("true");}
		else{writeString("false");}
	}
	
	public void writeString(String s){
		try{
			if (fileType==0){
				row.createCell(colnum).setCellValue(s);
				colnum++;
			}
			else if (fileType==1){
				Results.print(csvEntry(s));
			}
			else if (fileType==2){
				Results.print(textEntry(s));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeInt(int i){
		try{
			if (fileType==0){
				row.createCell(colnum).setCellValue(i);
				colnum++;
			}
			else if (fileType==1){
				Results.print(i+",");
			}
			else if (fileType==2){
				Results.print(i+"\u0009");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	public void writeLong(long i){
		try{
			if (fileType==0){
				row.createCell(colnum).setCellValue(i);
				colnum++;
			}
			else if (fileType==1){
				Results.print(i+",");
			}
			else if (fileType==2){
				Results.print(i+"\u0009");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	
	public void writeFloat(float i){
		try{
			if (fileType==0){
				row.createCell(colnum).setCellValue(i);
				colnum++;
			}
			else if (fileType==1){
				Results.print(i+",");
			}
			else if (fileType==2){
				Results.print(i+"\u0009");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeDouble(double i){
		try{
			if (fileType==0){
				row.createCell(colnum).setCellValue(i);
				colnum++;
			}
			else if (fileType==1){
				Results.print(i+",");
			}
			else if (fileType==2){
				Results.print(i+"\u0009");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeDate(long i){
		try{
			
			
			if (fileType==0){
				CellStyle cellStyle = wb.createCellStyle();
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
				Calendar cal=Calendar.getInstance();
				cal.setTimeInMillis(i);
				Cell cell=row.createCell(colnum);
				cell.setCellValue(cal);
				cell.setCellStyle(cellStyle);
				//row.createCell(colnum).setCellValue(cal);
				colnum++;
			}
			else if (fileType==1){
				Results.print(i+",");
			}
			else if (fileType==2){
				Results.print(i+"\u0009");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeLine(){
		if (fileType==0){
			row = sheet.createRow(++rownum);
			colnum=0;
		}
		else {
			Results.println();
		}
	}	
	
	
	
	public void finishWriting(){
		if (fileType==0){
			try{
				FileOutputStream fileOut = new FileOutputStream(file);
				wb.write(fileOut);
				fileOut.close();
				//if (defaults!=null){
					//defaults.props.setProperty("path", thpath);
					//defaults.props.setProperty("filename", name);
				//}
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
		else{
			Results.close();
		}
		//if (!suppressChoice){host.setCursor(c);}
	}
	
	public String csvEntry(String s){
		char quot='"';
		StringBuffer sb=new StringBuffer(s);
		boolean encap=true;
		int i=s.lastIndexOf(',');
		int j=s.lastIndexOf(' ');
		if ((i>-1)||(j>-1)){
			sb.insert(0, quot);
			sb.append(quot);
			encap=true;
		}
		j=0; 
		int k=sb.length();
		if (encap){
			j++;
			k--;
		}
		for (i=j; i<k; i++){
			if (sb.charAt(i)==quot){
				sb.insert(i, quot);
				i+=2;
				sb.insert(i, quot);
				k+=2;
			}
		}
		sb.append(",");
		return sb.toString();
	}
	
	public String textEntry(String s){
		String t="\u0009";
		StringBuffer sb=new StringBuffer(s);
		sb.append(t);
		return sb.toString();
	}
	
}
