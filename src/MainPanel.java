//
//  MainPanel.java
//  Operanter
//
//  Created by Robert Lachlan on 4/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;

public class MainPanel extends JPanel implements ActionListener {
	static String ADD_COMMAND="add";
	static String REMOVE_COMMAND="remove";
	static String SHUT_DOWN_COMMAND="shut down program";
	
	JTabbedPane tabPane=new JTabbedPane();
	
	JFrame f=new JFrame();
	
	Operanter op;
	
	public MainPanel(Operanter op){
		this.op=op;
		System.out.println("here");
		
		UserInterface ui=op.makeNewUI();
		String s="experiment 1";
		tabPane.add(ui, s);
		
		JPanel topPane=makeTopPane();
		
		JPanel contentPane=new JPanel(new BorderLayout());
		contentPane.add(topPane, BorderLayout.NORTH);
		contentPane.add(tabPane, BorderLayout.CENTER);
		
		
		f.setTitle("Operanter");
		f.getContentPane().add(contentPane);
		f.pack();
		f.setVisible(true);

	}
	
	
	public JPanel makeTopPane(){
		JPanel topPane=new JPanel(new BorderLayout());
		
		JButton add=new JButton("+");
		JButton remove=new JButton("-");
		JButton shutdown=new JButton("Close Operanter");
		
		
		JPanel leftPane=new JPanel();
		leftPane.add(add);
		leftPane.add(remove);
		
		topPane.add(leftPane, BorderLayout.WEST);
		topPane.add(shutdown, BorderLayout.EAST);
		
		add.setActionCommand(ADD_COMMAND);
		remove.setActionCommand(REMOVE_COMMAND);
		shutdown.setActionCommand(SHUT_DOWN_COMMAND);
		
		add.addActionListener(this);
		remove.addActionListener(this);
		shutdown.addActionListener(this);
		
		return topPane;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (ADD_COMMAND.equals(command)) {
			UserInterface ui=op.makeNewUI();
			String s="experiment "+Integer.toString(op.schemes.size());
			tabPane.add(ui, s);
		}
		else if (REMOVE_COMMAND.equals(command)){
			UserInterface ui=(UserInterface)tabPane.getSelectedComponent();
			//Scheme scheme=ui.scheme;
			op.removeScheme(ui);
			boolean cont=ui.shutDown();
			if (cont){tabPane.remove(ui);}
		}
		else if (SHUT_DOWN_COMMAND.equals(command)){
			boolean cont=true;
			for (int i=0; i<tabPane.getTabCount(); i++){
				UserInterface ui=(UserInterface)tabPane.getComponentAt(0);
				cont=ui.shutDown();
				if (!cont){
					i=tabPane.getTabCount()+1;
				}
			}
			if (cont){
				System.exit(1);
			}
		}
	}

}
