//
//  Defaults.java
//  Operanter
//
//  Created by Robert Lachlan on 4/5/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Properties.*;
import javax.swing.*;

public class Defaults {
	NumberFormat num;
	
	Properties props=new Properties();
	
	static LookAndFeel lnf;
	
	public Defaults(){
		num=NumberFormat.getNumberInstance();
		num.setMaximumFractionDigits(10);
		readProperties();
	}
	
	void readProperties(){
		String sConfigFile = "resources.properties";
		try{
			//InputStream in = Luscinia.class.getClassLoader().getResourceAsStream(sConfigFile);
			FileInputStream in = new FileInputStream("lusciniaproperties");
			if (in == null) {
				//writeProperties();
			}
			props = new Properties();
			props.load(in);
			in.close();
		}
		catch (Exception e){
			
		}
	}
	
	void writeProperties(){
		try{
			FileOutputStream out = new FileOutputStream("lusciniaproperties");
			props.store(out, "---No Comment---");
			out.close();
		}
		catch(Exception e){
			
		}
	}
	
	public int getIntProperty(String key){
		String s=props.getProperty(key, "0");
		int x=0;
		try{
			Number number=num.parse(s);
			x=number.intValue();
		}
		catch(ParseException e){}
		return x;
	}
	
	public int getIntProperty(String key, int defval){
		Integer a=new Integer(defval);
		String s=props.getProperty(key, a.toString());
		int x=0;
		try{
			Number number=num.parse(s);
			x=number.intValue();
		}
		catch(ParseException e){}
		return x;
	}
	
	public double getDoubleProperty(String key, double multiplier){
		int x=getIntProperty(key);
		return x/multiplier;
	}
	
	public double getDoubleProperty(String key, double multiplier, double defval){
		
		int defdoub=(int)Math.round(defval*multiplier);
		
		int x=getIntProperty(key, defdoub);
		return x/multiplier;
	}
	
	public LinkedList getStringList(String key){
		String s=props.getProperty(key);
		LinkedList LList=new LinkedList();
		if (s!=null){
			int ind=0;
			int ind2=0;
			ind2=s.indexOf("'|~|'", ind);
			ind=-4;
			while(ind2!=-1){
				String t=s.substring(ind+4, ind2);
				LList.add(t);
				ind=ind2+1;
				ind2=s.indexOf("'|~|'", ind);
			}
		}
		return LList;
	}
	
	public void setStringList(String key, LinkedList ll){
		
		StringBuffer sb=new StringBuffer();
		for(int i=0; i<ll.size(); i++){
			String s=(String)ll.get(i);
			sb.append(s);
			sb.append("'|~|'");
		}
		String s=sb.toString();
		props.setProperty(key, s);
	}
	
	public void setIntProperty(String key, int x){
		Integer y=new Integer(x);
		String s=y.toString();
		props.setProperty(key, s);
	}
	
	public void setDoubleProperty(String key, double x, int multiplier){
		int p=(int)Math.round(x*multiplier);
		setIntProperty(key, p);
	}
	
	public void setBooleanArray(String key, boolean[]data){
		StringBuffer sb=new StringBuffer();
		for (int i=0; i<data.length; i++){
			if (data[i]){sb.append("1");}
			else{sb.append("0");}
		}
		String s=sb.toString();
		props.setProperty(key, s);	
	}
	
	public boolean[] getBooleanArray(String key){
		String s=props.getProperty(key);
		boolean[] results=null;
		if (s!=null){
			int n=s.length();
			results=new boolean[n];
			for (int i=0; i<n; i++){
				char p=s.charAt(i);
				if (p=='0'){
					results[i]=false;
				}
				else{
					results[i]=true;
				}
			}
		}
		return results;
	}
		
	void setDefaultSoundFormat(int a){
		setIntProperty("sofo", a);
	}
	
	int getDefaultSoundFormat(){
		int p=getIntProperty("sofo", 0);
		return p;					 
	}
	
	void setDefaultImageFormat(int a){
		setIntProperty("imfo", a);
	}
	
	int getDefaultImageFormat(){
		int p=getIntProperty("imfo", 0);
		return p;					 
	}
	
	void setDefaultDocFormat(int a){
		setIntProperty("dofo", a);
	}
	
	int getDefaultDocFormat(){
		int p=getIntProperty("dofo", 0);
		return p;					 
	}
	
	
	
	void setStatisticsOutput(boolean[] chooserV, boolean[] chooserP, boolean[] chooserM, boolean[] chooserS, boolean[] chooserSy){
		
		setBooleanArray("vecOut", chooserV);
		setBooleanArray("parOut", chooserP);
		setBooleanArray("measOut", chooserM);
		setBooleanArray("scalOut", chooserS);
		setBooleanArray("sylOut", chooserSy);
	}
}
