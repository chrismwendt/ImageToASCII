import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ASCIIPanel extends JPanel {
	BufferedImage original = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	BufferedImage ascii = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	BufferedImage front = deepCopy(ascii);
	JLabel differenceLabel = new JLabel();
	Thread thread = null;
	ArrayList<String> text = new ArrayList<String>();
	Graphics2D g;
	int size = 10;
	
	public void paint(Rectangle2D b) {
		g.setColor(Color.WHITE);
		g.fillRect(0, (int)b.getMinY(), (int)Math.ceil(b.getMinX() + b.getWidth()), (int)Math.ceil(b.getHeight()));
		//System.out.println("clear " + b.getMinX() + " " + b.getMinY() + " " + b.getWidth() + " " + b.getHeight());
		
		g.setColor(Color.BLACK);
		//g.drawRect(2, (int)b.getMinY()+1, (int)Math.ceil(b.getMinX() + b.getWidth())-1, (int)Math.ceil(b.getHeight())-1);
		//drawStrings(g, text, 0, 0);
	}
	
	public void paint(Graphics graphics) {
		if (g == null) {
			return;
		}
		front = deepCopy(ascii);
		graphics.drawImage(front, 0, 0, null);
		//differenceLabel.setText(String.valueOf(difference(original, ascii)) + " " + System.currentTimeMillis());
	}

	public void setOriginal(BufferedImage image) {
		if (thread != null) {
			thread.interrupt();
			while (thread.isAlive()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		thread = new Thread(new Runner(this));
		original = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		original.getGraphics().drawImage(image, 0, 0, null);
		ascii = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
		front = deepCopy(ascii);
		g = (Graphics2D)ascii.getGraphics();
		g.setFont(new Font("Courier New", Font.PLAIN, size));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		setPreferredSize(new Dimension(original.getWidth(), original.getHeight()));
		((JFrame)getTopLevelAncestor()).pack();
		thread.start();
	}
	
	public void setDifferenceLabel(JLabel label) {
		differenceLabel = label;
	}
	
	public double difference() {
		return difference(original, ascii);
	}
	
	public double difference(BufferedImage a, BufferedImage b) {
		return difference(a, b, 0, 0, ascii.getWidth(), ascii.getHeight());
	}
	
	public int[] pixelToRGB(int pixel) {
		return new int[] {
			pixel >> 16 & 0x000000FF,
			pixel >>  8 & 0x000000FF,
			pixel >>  0 & 0x000000FF
		};
	}
	
	private void drawStrings(Graphics g, ArrayList<String> text, int x, int y) {
		y += g.getFontMetrics().getAscent();
        for (String line : text) {
            g.drawString(line, x, y);
            y += g.getFontMetrics().getHeight();
        }
    }

	public double difference(Rectangle2D stringBounds) {
		return difference(original, ascii, stringBounds.getMinX(), stringBounds.getMinY(), stringBounds.getWidth(), stringBounds.getHeight());
	}

	private double difference(BufferedImage a, BufferedImage b,
			double minXD, double minYD, double widthD, double heightD) {
		int minX = (int)minXD;
		int minY = (int)minYD;
		int width = (int)widthD;
		int height = (int)heightD;
		
		int[] aPixels = ((DataBufferInt)a.getRaster().getDataBuffer()).getData();
		int[] bPixels = ((DataBufferInt)b.getRaster().getDataBuffer()).getData();
		double d = 0;
		
		for (int y = minY; y < (minY+height); y++) {
			for (int x = minX; x < (minX+width); x++) {
				int i = y*a.getWidth() + x;
				if (i >= aPixels.length) {
					break;
				}
				int[] aRGB = pixelToRGB(aPixels[i]);
				int[] bRGB = pixelToRGB(bPixels[i]);
				for (int j = 0; j < aRGB.length; j++) {
					d += Math.abs(bRGB[j] - aRGB[j]);
				}
			}
		}
		
		return d/(widthD*heightD);
	}
	
	public BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public void changeSize() {
		setOriginal(original);
	}
}
