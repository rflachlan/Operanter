package op;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import devices.DigitalOutput;
import devices.MotorPWMOutput;

public class MotorButtonPanel extends JPanel{
	
	OButton button1, button2, button3, button4, button5;
	MotorPWMOutput mpo;
	Dimension dim=new Dimension(100, 100);
	
	public MotorButtonPanel(MotorPWMOutput mpo, ActionListener a){
		this.setLayout(new GridLayout(0,2));
		this.mpo=mpo;
		button1=new OButton(mpo.getName(), dim);
		button1.addActionListener(a);
		button2=new OButton("Open", dim);
		button2.addActionListener(a);
		button3=new OButton("Close", dim);
		button3.addActionListener(a);
		button4=new OButton("<html><center>" + "Shaping" + "<br>" + "Reward"+"</center></html>", dim);
		button4.addActionListener(a);
		button5=new OButton("<html><center>" + "Go/NoGo Shaping" + "<br>" + "Reward"+"</center></html>", dim);
		button5.addActionListener(a);
		
		this.add(button1);
		this.add(button2);
		this.add(button4);
		this.add(button3);
		this.add(button5);
		
	}

}

