package op;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import devices.SoundStimulus;

public class SoundPlaybackPanel extends JPanel{
	
	OButton[] stims;
	SoundStimulus ss;
	Dimension dim=new Dimension(100, 100);
	
	public SoundPlaybackPanel(SoundStimulus ss, ActionListener a){
		this.setLayout(new GridLayout(0,1));
		this.ss=ss;
		stims=new OButton[ss.numColls];
		for (int i=0; i<ss.numColls; i++){
			stims[i]=new OButton(ss.sc[i].name, dim);
			stims[i].addActionListener(a);
			this.add(stims[i]);
		}
		
	}

}