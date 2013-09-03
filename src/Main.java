import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class Main extends JFrame implements FileDrop.Listener, DocumentListener {
	JLabel filenameLabel = new JLabel("none");
	JLabel image;
	ASCIIPanel ascii = new ASCIIPanel();
	JLabel difference = new JLabel();
	String defaultFilename = "C:\\Users\\Chris\\Downloads\\horse_line_drawing.png";
	
	public Main() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		JPanel north = new JPanel(new FlowLayout());
		north.add(new JLabel("Current Image:"));
		north.add(filenameLabel);
		JTextField textbox = new JTextField(String.valueOf(ascii.size));
		textbox.setPreferredSize(new Dimension(50, 20));
		textbox.getDocument().addDocumentListener(this);
		north.add(textbox);
		add(north, BorderLayout.NORTH);
		
		BufferedImage defaultImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		image = new JLabel(new ImageIcon(defaultImage));
		new FileDrop(image, this);
		add(image, BorderLayout.WEST);
		
		JTextArea text = new JTextArea("hey\nthere");
		text.setForeground(Color.BLACK);
		//add(text);
		
		ascii.setDifferenceLabel(difference);
		add(ascii, BorderLayout.EAST);
		
		JPanel south = new JPanel(new FlowLayout());
		south.add(new JLabel("Difference:"));
		south.add(difference);
		add(south, BorderLayout.SOUTH);
		
		setTitle("Image to ASCII");
		
		pack();
		setVisible(true);
		
		EventQueue.invokeLater(new Runnable() {
		    public void run() {
		    	filesDropped(new File[] {new File(defaultFilename)});
		    }
		});
	}
	
	public static void main(String[] args) {
		new Main();
	}

	public void filesDropped(File[] files) {
		File f = files[0];
		filenameLabel.setText(f.getAbsolutePath());
		image.setIcon(new ImageIcon(f.getAbsolutePath()));
		try {
			ascii.setOriginal(ImageIO.read(new File(f.getAbsolutePath())));
		} catch (Exception e) {e.printStackTrace();}
		ascii.repaint();
		pack();
	}
	
	public void thing(DocumentEvent e) {
		try {
			ascii.size = Integer.parseInt(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (Exception ex) {
			ascii.size = 10;
		}
		ascii.changeSize();
	}

	public void insertUpdate(DocumentEvent e) {
		thing(e);
	}

	public void removeUpdate(DocumentEvent e) {
		thing(e);
	}

	public void changedUpdate(DocumentEvent e) {
		thing(e);
	}
}