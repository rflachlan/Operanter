package op;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import schemes.Scheme;
import db.LogEvent;
import devices.DigitalOutput;
import devices.MotorPWMOutput;
import db.DatabaseConnection;

public class DirectControlPanel extends JPanel implements ActionListener{
	
	ButtonPanel[] pans;
	MotorButtonPanel[] motors;
	SoundPlaybackPanel[] sounds;
	OButton manEvent;
	DatabaseConnection dbc;
	
	public DirectControlPanel(Scheme scheme){
		
		this.setLayout(new FlowLayout());
		this.setBorder(new TitledBorder(scheme.getExperimentName()));
		this.setPreferredSize(new Dimension(700, 400));
		//this.setTitle(scheme.getExperimentName());
	//	System.out.println("DIRECT CONTROL SCHEME NAME: "+scheme.getExperimentName());

		
		pans=new ButtonPanel[scheme.dos.length];
		for (int i=0; i<scheme.dos.length; i++){
			pans[i]=new ButtonPanel(scheme.dos[i], this);
			this.add(pans[i]);
		}
		
		motors=new MotorButtonPanel[scheme.mps.length];
		for (int i=0; i<scheme.mps.length; i++){
			motors[i]=new MotorButtonPanel(scheme.mps[i], this);
			this.add(motors[i]);
		}
	//	System.out.println("NUMBER SOUND STIMULI: "+scheme.sss.length);
		sounds=new SoundPlaybackPanel[scheme.sss.length];
		for (int i=0; i<scheme.sss.length; i++){
			sounds[i]=new SoundPlaybackPanel(scheme.sss[i], this);
			this.add(sounds[i]);
		}
	}

	public void actionPerformed(ActionEvent e) {
		boolean found=false;

		for (int i=0; i<pans.length; i++){
			if (e.getSource()==pans[i].button){
				found=true;
				if (pans[i].main.isSelected()){
					pans[i].d.trigger();
				}
				else{
					pans[i].d.triggerAlt();
				}
				i=pans.length;
			}
		}
		if (!found){
			for (int i=0; i<motors.length; i++){
				if (e.getSource()==motors[i].button1){
					found=true;
					motors[i].mpo.trigger(2);
				}
				else if (e.getSource()==motors[i].button2){
					found=true;
					motors[i].mpo.trigger(0);
				}
				else if (e.getSource()==motors[i].button3){
					found=true;
					motors[i].mpo.trigger(1);
				}
				//Please, Rob, fix this hack job when you get a chance?
				else if (e.getSource()==motors[i].button4){
					found=true;
					for (int j=0; j<2; j++){
						found=true;
						pans[j].d.switchOff();
						}
					motors[i].mpo.trigger(2);
					try{
						Thread.sleep(motors[i].mpo.pauseLength + 2*motors[i].mpo.standardLength);
					}
					catch(InterruptedException ex){
						Thread.currentThread().interrupt();
					}
					for (int j=0; j<2; j++){
						found=true;
						pans[j].d.switchOn();
						}
				}
				else if (e.getSource()==motors[i].button5){
					found=true;
					pans[0].d.switchOff();
					pans[1].d.switchOff();
					motors[i].mpo.trigger(2);
					try{
						Thread.sleep(motors[i].mpo.pauseLength + 2*motors[i].mpo.standardLength);
					}
					catch(InterruptedException ex){
						Thread.currentThread().interrupt();
					}
					pans[0].d.switchOn();
		
				}
			}
		}
		if (!found){
			for (int i=0; i<sounds.length; i++){
				for (int j=0; j<sounds[i].stims.length; j++){
					if (e.getSource()==sounds[i].stims[j]){
						sounds[i].ss.playStim(j);
					}
				}
			}
		}
	}

}
