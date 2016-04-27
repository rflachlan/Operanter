package op;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;

import devices.PAUSELinker;

public class PAUSELinkerOptionPanel extends JPanel{

	PAUSELinker diout;
	String[] types={"Fixed", "Uniform Distribution"};
	
	JComboBox<String> pauseType=new JComboBox<String>(types);
	JSpinner fixedLengthField, lowerLimitField, upperLimitField;
	
	Font font=new Font("Sans-Serif", Font.PLAIN, 10);
	
	public PAUSELinkerOptionPanel(PAUSELinker diout){
		this.diout=diout;
		NumberFormat num=NumberFormat.getNumberInstance();
		num.setMaximumFractionDigits(0);
		
		this.setLayout(new GridLayout(0,2));
		this.setFont(font);
		this.setBorder(new TitledBorder(diout.getName()));
		
		JLabel stlab=new JLabel("Type of pause: ");
		this.add(stlab);
		pauseType.setSelectedIndex(diout.pauseType);
		this.add(pauseType);
		
		JLabel sllab=new JLabel("Fixed pause length (ms): ");
		this.add(sllab);
		/*fixedLengthField=new JFormattedTextField(num);
		fixedLengthField.setColumns(10);
		fixedLengthField.setValue(new Integer(diout.pauseLength));
		this.add(fixedLengthField);*/
		SpinnerModel model = new SpinnerNumberModel(10000,0,30000,1000);
		fixedLengthField=new JSpinner(model);
		setJSpinnerButtonSize(fixedLengthField);
		fixedLengthField.setValue(new Integer(diout.pauseLength));
		this.add(fixedLengthField);
		
		JLabel lllab=new JLabel("Lower pause limit (ms): ");
		this.add(lllab);
		/*lowerLimitField=new JFormattedTextField(num);
		lowerLimitField.setColumns(10);
		lowerLimitField.setValue(new Integer(diout.pauseBoundaries[0]));
		this.add(lowerLimitField);*/
		SpinnerModel model2 = new SpinnerNumberModel(1000,0,30000,1000);
		lowerLimitField=new JSpinner(model2);
		setJSpinnerButtonSize(lowerLimitField);
		lowerLimitField.setValue(new Integer(diout.pauseBoundaries[0]));
		this.add(lowerLimitField);
		
		JLabel ullab=new JLabel("Upper pause limit (ms): ");
		this.add(ullab);
/*		upperLimitField=new JFormattedTextField(num);
		upperLimitField.setColumns(10);
		upperLimitField.setValue(new Integer(diout.pauseBoundaries[1]));
		this.add(upperLimitField);*/
		SpinnerModel model3 = new SpinnerNumberModel(15000,0,30000,1000);
		upperLimitField=new JSpinner(model3);
		setJSpinnerButtonSize(upperLimitField);
		upperLimitField.setValue(new Integer(diout.pauseBoundaries[1]));
		this.add(upperLimitField);
		
		changeFont(font, this);
	}
	
	public static void changeFont (Font font, JPanel pane){
		
	    for (Component child : pane.getComponents()){
	            child.setFont(font);
	    }
	}
	
	public void updatePAUSELinker(){
		int st=pauseType.getSelectedIndex();
		if (st>=0){diout.pauseType=st;}
		
		int p1=(int)((Number)fixedLengthField.getValue()).intValue();
		diout.pauseLength=p1;
		
		int p2=(int)((Number)lowerLimitField.getValue()).intValue();
		diout.pauseBoundaries[0]=p2;
		
		int p3=(int)((Number)upperLimitField.getValue()).intValue();
		diout.pauseBoundaries[1]=p3;
	
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
