import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Runner implements Runnable {
	ASCIIPanel panel;
	String characters = " !\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

	public Runner(ASCIIPanel c) {
		this.panel = c;
	}

	public void run() {
		//characters = " !()[]ij";
		System.out.println((double)panel.getHeight() / panel.g.getFontMetrics().getHeight());
		panel.text.clear();
		try {
			panel.g.setColor(Color.WHITE);
			panel.g.fillRect(0, 0, panel.ascii.getWidth(), panel.ascii.getHeight());
			for (int y = 0; y < (double)panel.getHeight() / panel.g.getFontMetrics().getHeight() && !Thread.currentThread().isInterrupted(); y++) {
				panel.text.add("");
				for (int x = 0; panel.g.getFontMetrics().stringWidth(panel.text.get(y)) < panel.getWidth() && !Thread.currentThread().isInterrupted(); x++) {
					double d = Double.POSITIVE_INFINITY;
					String old = panel.text.get(panel.text.size()-1);
					char bestChar = ' ';
					Rectangle2D bounds, bestBounds;
					for (char c : characters.toCharArray()) {
						if (Thread.currentThread().isInterrupted()) {
							break;
						}
						panel.text.set(y, old+c);
						Rectangle2D bounds1 = panel.g.getFontMetrics().getStringBounds(old+c, 0, old.length(), panel.g);
						bounds = panel.g.getFontMetrics().getStringBounds(old+c, old.length(), old.length()+1, panel.g);
						bounds.setRect(bounds1.getWidth(), panel.g.getFontMetrics().getHeight() * y, bounds.getWidth(), bounds.getHeight());
						panel.paint(bounds);
						panel.g.drawString(String.valueOf(c), (int)bounds.getMinX(), y*panel.g.getFontMetrics().getHeight() + panel.g.getFontMetrics().getAscent());
						double thisDiff = panel.difference(bounds);
						//System.out.println(bounds);
						if (thisDiff < d) {
							bestChar = c;
							d = thisDiff;
							//System.out.println(old+c + " " + thisDiff);
							bestBounds = bounds;
						}
						panel.repaint();

						//Thread.sleep(10);
					}
					panel.text.set(y, old+bestChar);
					bounds = panel.g.getFontMetrics().getStringBounds(old+bestChar, 0, old.length()+1, panel.g);
					bounds.setRect(bounds.getMinX(), panel.g.getFontMetrics().getHeight() * y, bounds.getWidth(), bounds.getHeight());
					panel.paint(bounds);
					panel.g.drawString(old+bestChar, (int)bounds.getMinX(), y*panel.g.getFontMetrics().getHeight() + panel.g.getFontMetrics().getAscent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter("out.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (String s : panel.text) {
			pw.println(s);
		}
		pw.close();
	}
}
