package op;

import java.awt.Dimension;
import javax.swing.JButton;

public class OButton extends JButton{
	
	public OButton(String s, Dimension dim){
		super(s);
		this.setPreferredSize(dim);
	}

}
