package op;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import db.DFileFilter;
import db.LogEvent;

public class LogPane extends JPanel implements ActionListener{
	
	Defaults defaults;
	Dimension dim=new Dimension(150, 75);
	NumberFormat nf;
	Calendar cal=Calendar.getInstance();
	
	JTextArea output=new JTextArea(15, 50);
	JScrollPane scrollPane;
	
	OButton download, wipe;
	
	public LogPane(Defaults defaults){
		super();
		this.defaults=defaults;
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(700, 400));
		
		nf=NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);
		
	    
	    output.setTabSize(12);
	    output.setLineWrap(false); //changed from true to false
		scrollPane=new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel topPanel=new JPanel(new FlowLayout());
		
		download=new OButton("<html><center>" + "Download results" + "</html></center>", dim);
		download.addActionListener(this);
		wipe=new OButton("Empty database", dim);
		wipe.addActionListener(this);
		
		topPanel.add(download);
		topPanel.add(wipe);
		
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);
	}
	
	public void writeLogToTextArea(LogEvent le, int counter){
		cal.setTimeInMillis(le.time);
		java.util.Date date=cal.getTime();
		DateFormat df=DateFormat.getInstance();
		String s=df.format(date);
		//System.out.println("trying to write stuff on the textarea");
		String t=Integer.toString(counter);
		
		String x="\t";
		
		int sec=cal.get(Calendar.SECOND);
		int msec=cal.get(Calendar.MILLISECOND);
		
		double q=sec+(0.001*msec);
		String y=nf.format(q);
		
		
		output.append(t+"  ");
		output.append(s+x);
		output.append(y+x);
		output.append(le.objectName+x);
		output.append(le.actionName+x);
		output.append(le.experimentName+x);
		
		
		//DefaultEditorKit dek=new DefaultEditorKit();
		//output.append(dek.insertBreakAction);
		output.append("\n");
		
		JViewport vp=scrollPane.getViewport();
		
		Dimension dim1=scrollPane.getSize();
		Dimension dim2=output.getSize();
		Point p=new Point(0, (int)(dim2.getHeight()-dim1.getHeight()));
		
		vp.setViewPosition(p);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource()==download){
			makeFile();
		}
		else if(e.getSource()==wipe){
			int a=JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently clear the database?");
			if (a==JOptionPane.OK_OPTION){
				defaults.dbc.wipeDatabase();
				output.setText(null);
			}
		}
	}
	
	public void makeFile(){
		JFileChooser fc;
		String thpath;
		File file=null;
		int returnVal = JFileChooser.APPROVE_OPTION;
		boolean readyToWrite=false;
		try{
			//thpath=defaults.props.getProperty("path");
			//MM fix because "path" always seems to revert to /root.
			thpath=defaults.props.getProperty("parentpath");
			fc=new JFileChooser(thpath);
		}
		catch(Exception e){
			fc=new JFileChooser();
		}
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		
		Date today = new Date(Calendar.getInstance().getTime().getTime());
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
		String today2 = new String();
		today2 = df.format(today);
		
		File defaultFileName = new File(today2);
		fc.setSelectedFile(defaultFileName);
		
		fc.setAcceptAllFileFilterUsed(false);
						
		DFileFilter sffxls=new DFileFilter("xls");
		sffxls.fileType=0;
		fc.addChoosableFileFilter(sffxls);
		DFileFilter sffcsv=new DFileFilter("csv");
		sffcsv.fileType=1;
		fc.addChoosableFileFilter(sffcsv);
		DFileFilter sfftxt=new DFileFilter("txt");
		sfftxt.fileType=2;
		fc.addChoosableFileFilter(sfftxt);
			
		int p=defaults.getDefaultDocFormat();
		if (p==0){
			fc.setFileFilter(sffxls);
		}
		else if (p==1){
			fc.setFileFilter(sffcsv);
		}
		else if (p==2){
			fc.setFileFilter(sfftxt);
		}
			
		returnVal = fc.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			int cont=0;
			
			file = fc.getSelectedFile();
			thpath=file.getPath();
			String parent=file.getParent();
			String name=file.getName();
			DFileFilter sf2=(DFileFilter) fc.getFileFilter();
			file=new File(thpath+"."+sf2.names);
			if (defaults!=null){
				defaults.props.setProperty("path", parent);
				defaults.props.setProperty("filename", name);
			}
			if (!thpath.endsWith("."+sf2.names)){file=new File(thpath+"."+sf2.names);}
			else{file=new File(thpath);}
			int fileType=sf2.fileType;
				
			
			try{
				defaults.setDefaultDocFormat(fileType);
				defaults.props.setProperty("path", thpath);
				defaults.props.setProperty("name", name);
				//MM added line below
				defaults.props.setProperty("parentpath", parent);
				defaults.writeProperties();
			}
			catch(Exception e){}
			if (file.exists()){
				cont= JOptionPane.showConfirmDialog(this,"Do you really want to overwrite this file?\n"+"(It will be deleted permanently)","Confirm Overwrite", JOptionPane.YES_NO_OPTION);
			}
			
			
			defaults.dbc.downloadData(file, fileType);
			
		}
	}

}
