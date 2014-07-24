//
//  DFileFilter.java
//  Operanter
//
//  Created by Robert Lachlan on 4/5/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class DFileFilter extends FileFilter {
    //String[] names ={"wav", "aif", "aiff"};
	
	String names="wav";
	
	int fileType=0;
	
	public DFileFilter(){
	}
	
	public DFileFilter(String n){
		this.names=n;
	}
	
	public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = getExtension(f);
		boolean found=false;
        if (extension != null) {
			//for (int i=0; i<names.length; i++){	
			//	if (extension.equals(names[i])) {found=true;}
			//}
			if (extension.equals(names)) {found=true;}
		}
		return found;
    }
	
    public String getDescription() {
		
		//StringBuffer sb=new StringBuffer();
		
		//for (int i=0; i<names.length; i++){	
		//	sb.append(names[i]);
		//	if (i<names.length-1){
		//		sb.append(", ");
		//	}
		//}
        //return sb.toString();
		return names;
    }
	
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
		
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}

