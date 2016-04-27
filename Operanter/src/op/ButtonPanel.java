package op;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import devices.DigitalOutput;

public class ButtonPanel extends JPanel{
	
	OButton button;
	ButtonGroup bg=new ButtonGroup();
	JRadioButton main=new JRadioButton("main");
	JRadioButton alt=new JRadioButton("alt");
	DigitalOutput d;
	
	public ButtonPanel(DigitalOutput d, ActionListener a){
		this.setLayout(new BorderLayout());
		this.d=d;
		button=new OButton(d.getName(), new Dimension(100, 100));
		button.addActionListener(a);
		
		this.add(button, BorderLayout.CENTER);
		bg.add(main);
		bg.add(alt);

		JPanel subPanel=new JPanel(new GridLayout(0,1));
		subPanel.add(main);
		subPanel.add(alt);
		this.add(subPanel, BorderLayout.SOUTH);
	}

}
