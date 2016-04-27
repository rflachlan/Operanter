package op;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;

import SoundPlayback.PannedDataLine;
import SoundPlayback.SoundCollection;
import devices.SoundStimulus;

public class SoundStimulusOptionPanel extends JPanel implements ActionListener{

	SoundStimulus ss;
	
	
	
	
	JButton[] setSoundLocs;
	JLabel[] locs;
	String[] locsC;
	String[] soundOuts;
	JSpinner[] transFields;
	JComboBox<String> soundOutputs;
	NumberFormat num;
	
	Font font=new Font("Sans-Serif", Font.PLAIN, 10);
	
	public SoundStimulusOptionPanel(SoundStimulus ss){
	//	System.out.println("Making sound stimulus options pane");
		this.ss=ss;
		num=NumberFormat.getNumberInstance();
		num.setMaximumFractionDigits(5);
		
		this.setLayout(new GridLayout(0,1));
		
		this.setFont(font);
		this.setBorder(new TitledBorder(ss.getName()));
		
		
		
		soundOuts=ss.sconfig.getMixerNames();
		soundOutputs=new JComboBox<String>(soundOuts);
		if (ss.pdlid<soundOuts.length){soundOutputs.setSelectedIndex(ss.pdlid);}
		JLabel soundLabel=new JLabel("Sound output channel: ");
		JPanel channelPanel=new JPanel(new FlowLayout());
		channelPanel.add(soundLabel);
		channelPanel.add(soundOutputs);
		this.add(channelPanel);
		JPanel[] xps=new JPanel[ss.numColls];
		setSoundLocs=new JButton[ss.numColls];
		locs=new JLabel[ss.numColls];
		locsC=new String[ss.numColls];
		transFields=new JSpinner[ss.numColls];
		for (int i=0; i<ss.numColls; i++){
			xps[i]=createPanel(i);
			this.add(xps[i]);
		}	
		changeFont(font, this);
	}
	
	public static void changeFont (Font font, JPanel pane){
		
	    for (Component child : pane.getComponents()){
	            child.setFont(font);
	    }
	}
	
	public JPanel createPanel(int a){
		
		JPanel pane=new JPanel(new FlowLayout());
		String s=" ";
		if (ss.sc[a]!=null){
			s=ss.sc[a].name;
		}
		else{
			s=ss.scNames[a];
		}
		pane.setBorder(new TitledBorder(s));
		
		setSoundLocs[a]=new JButton("Sound Location");
		setSoundLocs[a].addActionListener(this);
		pane.add(setSoundLocs[a]);
		
		String t="Sound location not set yet";
		if (ss.sc[a]!=null){
			t=ss.sc[a].fileLoc;
		}
		locs[a]=new JLabel(t);
		pane.add(locs[a]);
		
//		transFields[a]=new JFormattedTextField(num);
//		transFields[a].setColumns(10);
//		transFields[a].setValue(new Double(ss.transitions[a]));
//		pane.add(transFields[a]);
		
		//MM update
		SpinnerModel model = new SpinnerNumberModel(1,0,1,0.05);
		transFields[a]=new JSpinner(model);
		transFields[a].setPreferredSize(new Dimension(50,50));
		setJSpinnerButtonSize(transFields[a]);
		JComponent editor = transFields[a].getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		tf.setColumns(3);
		transFields[a].setValue(new Double(ss.transitions[a]));
		pane.add(transFields[a]);
		
		return pane;
	}
	
	
	
	public void updateSoundStimulus(){
		System.out.println("UPDATING SS");
		int x=soundOutputs.getSelectedIndex();
		
		
		for (int i=0; i<ss.numColls; i++){
			ss.sc[i].fileLoc=locsC[i];
			System.out.println("File loc: "+locsC[i]);
			double p1=(double)((Number)transFields[i].getValue()).doubleValue();
			ss.transitions[i]=p1;
		}
		ss.setPDL(x);
		ss.calculateCumulativeTransitions();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		for (int i=0; i<setSoundLocs.length; i++){
			if (e.getSource().equals(setSoundLocs[i])){
				String s=getFile();
				locsC[i]=s;
				locs[i].setText(s);		
			}
		}
		
	}
	
	public String getFile(){
		JFileChooser fc=new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		File file=null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file  = fc.getSelectedFile();
		
		}
		return file.getPath();
	}
	
	public void setJSpinnerButtonSize(JSpinner spinner){
		Dimension d = spinner.getPreferredSize();
		d.width = 80;
		spinner.setPreferredSize(d);
		
		spinner.setUI(new BasicSpinnerUI(){
			protected Component createPreviousButton(){
				Component b=super.createPreviousButton();
				JPanel wrap=new JPanel(new BorderLayout());
				wrap.add(b);
				wrap.setPreferredSize(new Dimension(20, 50));
				return wrap;
			}
		});
	
	}
	
	
	
}
