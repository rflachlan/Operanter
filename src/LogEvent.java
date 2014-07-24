//
//  LogEvent.java
//  Operanter
//
//  Created by Robert Lachlan on 4/5/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.util.*;
import java.text.*;

public class LogEvent {
	
	long time;
	int channel;
	int chamber;
	String actionName;
	String objectName;
	String experimentName;
	NumberFormat nf=NumberFormat.getInstance();
	
	public LogEvent(String s){
		
		actionName=s;
		time=System.currentTimeMillis();
	}
	
	public LogEvent(String s, int p){
		actionName=s;
		channel=p;
		time=System.currentTimeMillis();
		
	}
	
	public LogEvent(String s, String t, String u, int p, int q){
		objectName=s;
		actionName=t;
		experimentName=u;
		channel=p;
		chamber=q;
		time=System.currentTimeMillis();
		
	}
	
	public LogEvent(long t2, String s, String t, String u, int p, int q){
		objectName=s;
		actionName=t;
		experimentName=u;
		channel=p;
		chamber=q;
		time=t2;
		
	}
	
	
	public void writeDoc(DocumentSave ds){
		
		Calendar cal=Calendar.getInstance();
				
		cal.setTimeInMillis(time);

		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		int day=cal.get(Calendar.DATE);
		int hour=cal.get(Calendar.HOUR);
		int minute=cal.get(Calendar.MINUTE);
		int second=cal.get(Calendar.SECOND);
		int milli=cal.get(Calendar.MILLISECOND);
		
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);;
		double q=second+(0.001*milli);
		String y=nf.format(q);
	
		ds.writeInt(year);
		ds.writeInt(month);
		ds.writeInt(day);
		ds.writeInt(hour);
		ds.writeInt(minute);
		ds.writeString(y);
		
		
		ds.writeString(experimentName);
		ds.writeString(objectName);
		ds.writeString(actionName);
		ds.writeInt(channel);
		ds.writeInt(chamber);
		ds.writeLine();
		
	}
	
	

}
