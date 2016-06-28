package op;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import schemes.*;

public class SchemeOptions extends JPanel implements ActionListener{

	Scheme scheme;
	Defaults defaults;
	
	String[] schemeTypes={"Go-No-Go Scheme", "First Scheme", "Sound Tester"}; //add "SoundTester"
	JComboBox<String> schemas=new JComboBox<String>(schemeTypes);
	JTextField schemeName;
	LinkedList<String> schemes;
	DefaultComboBoxModel<String> schemeModel=new DefaultComboBoxModel<String>();
	JComboBox<String> schemeTypeBox=new JComboBox<String>(schemeModel);
	
	JButton add=new JButton("+");
	JButton save=new JButton("Save");
//	JButton delete=new JButton("Delete");
	
	DigitalOutputOptionPanel[] doop;
	MotorPWMOptionPanel[] mpop;
	DigitalSwitchOptionPanel[]dsop;
	PAUSELinkerOptionPanel[] plop;
	SoundStimulusOptionPanel[]ssop;
	JScrollPane mainPanel;
	Operanter op;
	
	public SchemeOptions(Defaults defaults, Operanter op){
		this.defaults=defaults;
		this.op=op;
		this.scheme=op.getScheme();
		this.setPreferredSize(new Dimension(700, 400));
		
		schemes=defaults.getSchemeIDs();
		for (int i=0; i<schemes.size(); i++){
			System.out.println(" schemes: "+i+" "+schemes.get(i));
			schemeModel.addElement(schemes.get(i));
		}
		
		this.setLayout(new BorderLayout());
		
		JPanel topPanel=new JPanel(new FlowLayout());
		
		schemeTypeBox.setSelectedItem(defaults.getStringProperty("defaultscheme"));
		schemeTypeBox.addActionListener(this);
		topPanel.add(schemeTypeBox);
		
		add.addActionListener(this);
		topPanel.add(add);
		
		save.addActionListener(this);
		topPanel.add(save);
		
//		delete.addActionListener(this);
//		topPanel.add(delete);
		
		this.add(topPanel, BorderLayout.NORTH);
		
		createAndAddSchemePanel();
		
	}
	
	public Scheme getScheme(){
		return scheme;
	}
	
	public void createAndAddSchemePanel(){
		if(mainPanel!=null){
			this.remove(mainPanel);
		}
		this.validate();
		mainPanel=createPanel();
		this.add(mainPanel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}
	
	public JScrollPane createPanel(){
		//String qt=defaults.getStringProperty(s+"type");
		//if (qt.equals("GoNoGo")){
			//scheme=new GoNoGoSchema(defaults);
		//}
		//scheme.setExperimentName(s);
		//scheme.loadFromDefaults();
		
		JTabbedPane mainPanel=new JTabbedPane();
		mainPanel.setTabPlacement(2);
		String s=scheme.getExperimentName();
		//JPanel dospane=new JPanel(new FlowLayout());
		doop=new DigitalOutputOptionPanel[scheme.dos.length];
		for (int i=0; i<scheme.dos.length; i++){
			 scheme.dos[i].getProperties(s);
			 doop[i]=new DigitalOutputOptionPanel(scheme.dos[i]);
		//	 System.out.println("ADDING TAB: "+scheme.dos[i].getName());
			 mainPanel.addTab(scheme.dos[i].getName(), doop[i]);
			 //dospane.add(doop[i]);
		}
		
		mpop=new MotorPWMOptionPanel[scheme.dos.length];
		for (int i=0; i<scheme.mps.length; i++){
			 scheme.mps[i].getProperties(s);
			 mpop[i]=new MotorPWMOptionPanel(scheme.mps[i]);
			 mainPanel.addTab(scheme.mps[i].getName(), mpop[i]);
		}
		//JPanel dsspane=new JPanel(new FlowLayout());
		dsop=new DigitalSwitchOptionPanel[scheme.dss.length];
		for (int i=0; i<scheme.dss.length; i++){
			scheme.dss[i].getProperties(s);
			dsop[i]=new DigitalSwitchOptionPanel(scheme.dss[i]);
			mainPanel.addTab(scheme.dss[i].getName(), dsop[i]);
			//dsspane.add(dsop[i]);
		}
		//JPanel plspane=new JPanel(new FlowLayout());
		plop=new PAUSELinkerOptionPanel[scheme.pls.length];
		for (int i=0; i<scheme.pls.length; i++){
			scheme.pls[i].getProperties(s);
			plop[i]=new PAUSELinkerOptionPanel(scheme.pls[i]);
			mainPanel.addTab(scheme.pls[i].getName(), plop[i]);
			//plspane.add(plop[i]);
		}
		//JPanel ssspane=new JPanel(new FlowLayout());
		ssop=new SoundStimulusOptionPanel[scheme.sss.length];
		for (int i=0; i<scheme.sss.length; i++){
			scheme.sss[i].getProperties(s);
			ssop[i]=new SoundStimulusOptionPanel(scheme.sss[i]);
			mainPanel.addTab(scheme.sss[i].getName(), ssop[i]);
			//ssspane.add(ssop[i]);
		}
		/*
		JPanel mp=new JPanel(new FlowLayout());
		mp.add(dospane);
		mp.add(dsspane);
		mp.add(plspane);
		mp.add(ssspane);*/
		
		JScrollPane sp=new JScrollPane(mainPanel);
		
		return sp;
	}
	
	JPanel createAddPanel(){
		JPanel pane=new JPanel(new GridLayout(0,1));
		
		pane.add(schemas);
		
		JPanel bpane=new JPanel(new BorderLayout());
		
		JLabel t=new JLabel("Name for scheme: ");
		bpane.add(t, BorderLayout.WEST);
		schemeName=new JTextField();
		schemeName.setColumns(20);
		bpane.add(schemeName, BorderLayout.CENTER);
		pane.add(bpane);
		return pane;
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==schemeTypeBox){
			String s=(String) schemeTypeBox.getSelectedItem();
			op.unloadScheme();
			op.loadScheme(s);
			op.updateUI();
			this.scheme=op.getScheme();
			createAndAddSchemePanel();
			defaults.setStringProperty("defaultscheme", s);
			System.out.println("Default scheme is: " + defaults.getStringProperty("defaultscheme"));
		}
		if (e.getSource()==add){
			addScheme();
		}
		if (e.getSource()==save){
			updateScheme();
		}
//		if (e.getSource()==delete){
//			deleteScheme();
//		}
	}
	
	public void addScheme(){
		JPanel ap=createAddPanel();
		int a=JOptionPane.showConfirmDialog(this, ap, "Add a new scheme", JOptionPane.OK_CANCEL_OPTION);
		if (a==JOptionPane.OK_OPTION){
			if (schemas.getSelectedIndex()==0){
				op.unloadScheme();
				String s=schemeName.getText();
				schemes.add(s);
				defaults.setStringList("schemes", schemes);
				defaults.setStringProperty(s+"type", "GoNoGo");
				GoNoGoSchema g=new GoNoGoSchema(defaults, s);
				g.setExperimentName(s);
				g.writeToDefaults();
				schemeModel.addElement(s);
				op.loadScheme(s);
				op.updateUI();
				this.scheme=op.getScheme();
				createPanel();
			}
			if (schemas.getSelectedIndex()==1){
				op.unloadScheme();
				String s=schemeName.getText();
				schemes.add(s);
				defaults.setStringList("schemes", schemes);
				defaults.setStringProperty(s+"type", "FirstSch");
				FirstSchema g=new FirstSchema(defaults, s);
				g.setExperimentName(s);
				g.writeToDefaults();
				schemeModel.addElement(s);
				op.loadScheme(s);
				op.updateUI();
				this.scheme=op.getScheme();
				createPanel();
			}
			if (schemas.getSelectedIndex()==2){
				op.unloadScheme();
				String s=schemeName.getText();
				schemes.add(s);
				defaults.setStringList("schemes", schemes);
				defaults.setStringProperty(s+"type", "SoundTest");
				SoundTester g=new SoundTester(defaults, s);
				g.setExperimentName(s);
				g.writeToDefaults();
				schemeModel.addElement(s);
				op.loadScheme(s);
				op.updateUI();
				this.scheme=op.getScheme();
				createPanel();
			}
		}
	}
	
	public void updateScheme(){
		System.out.println("UPDATING SCHEME");
		for (int i=0; i<scheme.dos.length; i++){
			doop[i].updateDigitalOutput();
		}
		for (int i=0; i<scheme.mps.length; i++){
			mpop[i].updateMotorPWM();
		}
		for (int i=0; i<scheme.dss.length; i++){
			dsop[i].updateDigitalSwitch();
		}
		for (int i=0; i<scheme.pls.length; i++){
			plop[i].updatePAUSELinker();
		}
		for (int i=0; i<scheme.sss.length; i++){
			ssop[i].updateSoundStimulus();
		}
		scheme.writeToDefaults();
		defaults.writeProperties();
		
		op.updateUI();
	}
	
/*	public void deleteScheme(){
		
		//DOES NOT WORK YET!

	//	String s = schemeName.getText();
		String s=(String) schemeTypeBox.getSelectedItem();
		System.out.println("schemeName we're going to delete: " + s);
		schemes.remove(s);
		defaults.setStringList("schemes", schemes);
		System.out.println("default schemelist: "+ defaults.getStringList("schemes"));
		System.out.println("Deleting scheme: " + s);
		op.unloadScheme();
		op.updateUI();
		
	}*/
	
}
