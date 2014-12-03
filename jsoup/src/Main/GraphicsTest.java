package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class GraphicsTest {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GraphicsTest();
			}
		});
	}

	JFrame frame = new JFrame("Hardware Acceleration Diagnostics");

	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice();
	GraphicsConfiguration gc = gd.getDefaultConfiguration();
	BufferCapabilities bufferCapabilities;
	BufferStrategy bufferStrategy;
	FrameStateListener frameStateListener = new FrameStateListener();
	FrameEscapeListener frameEscapeListener = new FrameEscapeListener();

	int y = 0;
	int delta = 1;
	boolean run = true;
	boolean acc = false;
	boolean fullscreen = false;
	boolean canoutputgraphics = true;
	float fps = 0;
	int ticks = 0;
	long time = 0;
	Font font = new Font("Arial", Font.PLAIN, 11);

	public GraphicsTest() {

		frame.setSize(480, 270);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowStateListener(frameStateListener);
		frame.addKeyListener(frameEscapeListener);

		frame.setVisible(true);

		frame.createBufferStrategy(3);
		bufferStrategy = frame.getBufferStrategy();
		bufferCapabilities = gc.getBufferCapabilities();
		acc = gc.getBufferCapabilities().getBackBufferCapabilities().isAccelerated();
		
		ActionListener actListner = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    ticks += 1;
			    if(ticks == 30){
			    	fps = 1e9f / time;
			    	ticks = 0;
			    }
			}
		};

		Timer timer = new Timer(1000/60, actListner);
		timer.start();
		
		new AnimationThread().start();
	}

    private String getDeviceConfigurationString(GraphicsConfiguration gc){
        return "Bounds: " + gc.getBounds() + "\n" + 
                "Buffer Capabilities: " + gc.getBufferCapabilities() + "\n" +
                "   -Back Buffer Capabilities: " + gc.getBufferCapabilities().getBackBufferCapabilities() + "\n" +
                "      -Accelerated: " + gc.getBufferCapabilities().getBackBufferCapabilities().isAccelerated() + "\n" + 
                "      -True Volatile: " + gc.getBufferCapabilities().getBackBufferCapabilities().isTrueVolatile() + "\n" +
                "   -Flip Contents: " + gc.getBufferCapabilities().getFlipContents() + "\n" +
                "   -Front Buffer Capabilities: " + gc.getBufferCapabilities().getFrontBufferCapabilities() + "\n" +
                "      -Accelerated: " + gc.getBufferCapabilities().getFrontBufferCapabilities().isAccelerated() + "\n" +
                "      -True Volatile: " + gc.getBufferCapabilities().getFrontBufferCapabilities().isTrueVolatile() + "\n" +
                "   -Is Full Screen Required: " + gc.getBufferCapabilities().isFullScreenRequired() + "\n" +
                "   -Is MultiBuffer Available: " + gc.getBufferCapabilities().isMultiBufferAvailable() + "\n" +
                "   -Is Page Flipping: " + gc.getBufferCapabilities().isPageFlipping() + "\n" +
                "Device: " + gc.getDevice() + "\n" +
                "   -Available Accelerated Memory: " + gc.getDevice().getAvailableAcceleratedMemory() + "\n" +
                "   -ID String: " + gc.getDevice().getIDstring() + "\n" +
                "   -Type: " + gc.getDevice().getType() + "\n" +
                "   -Display Mode: " + gc.getDevice().getDisplayMode() + "\n" +              
                "Image Capabilities: " + gc.getImageCapabilities() + "\n" + 
                "      -Accelerated: " + gc.getImageCapabilities().isAccelerated() + "\n" + 
                "      -True Volatile: " + gc.getImageCapabilities().isTrueVolatile() + "\n";        
    }
    
	class FrameStateListener implements WindowStateListener {
		@Override
		public void windowStateChanged(WindowEvent e) {
			if (e.getNewState() == JFrame.MAXIMIZED_BOTH) {
				fullscreen = true;
				run = false;

				frame.dispose();
				frame = new JFrame();
				frame.setUndecorated(true);
				frame.addWindowStateListener(frameStateListener);
				frame.addKeyListener(frameEscapeListener);

				gd.setFullScreenWindow(frame);
				
				frame.createBufferStrategy(3);
				bufferStrategy = frame.getBufferStrategy();
				bufferCapabilities = gc.getBufferCapabilities();

				run = true;
				new AnimationThread().start();
			}
		}
	}

	class FrameEscapeListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (fullscreen) {
					y = 0;
					fullscreen = false;
					run = false;

					frame.dispose();
					frame = new JFrame("Hardware Acceleration Diagnostics");
					frame.setSize(480, 270);
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					frame.addWindowStateListener(frameStateListener);
					frame.addKeyListener(frameEscapeListener);

					frame.setVisible(true);

					frame.createBufferStrategy(3);
					bufferStrategy = frame.getBufferStrategy();
					//bufferCapabilities = gc.getBufferCapabilities();

					run = true;
					new AnimationThread().start();
				}
			}
		}
	}

	class AnimationThread extends Thread {

		public void run() {
			while (run) {
				long start = System.nanoTime();
				drawGraphics();
				long stop = System.nanoTime();
				time = stop - start;
			}
		}

		public void drawGraphics() {
			try {
				Graphics2D g2 = null;
				try {
				g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
				draw(g2);
				if(canoutputgraphics){
					System.out.println("BackBuffer: "+((gc.getBufferCapabilities().getBackBufferCapabilities().isAccelerated()) ? "Yes" : "No"));
					System.out.println("FrontBuffer: "+((gc.getBufferCapabilities().getFrontBufferCapabilities().isAccelerated()) ? "Yes" : "No"));
			    	System.out.println("Image: "+((gc.getImageCapabilities().isAccelerated()) ? "Yes" : "No"));
			    	canoutputgraphics = false;
				}
			} finally {
				if (g2 != null)
					g2.dispose();
				}
			bufferStrategy.show();
			} catch (Exception err) {
			}
		}
	}

	public void draw(Graphics2D g2) {
		Color errorColor = new Color(173, 0, 8);
		g2.setFont(font);

		g2.setColor(Color.black);
		g2.fillRect(0, 0, frame.getWidth(), frame.getHeight());

		g2.setColor((bufferCapabilities.isPageFlipping() && bufferCapabilities
				.isMultiBufferAvailable()) ? Color.white : errorColor);
		g2.drawString(
				(bufferCapabilities.isPageFlipping() && bufferCapabilities
						.isMultiBufferAvailable()) ? "Hardware Acceleration is Working..."
						: "Hardware Acceleration is Not Supported...", 100, 60);

		g2.setColor(bufferCapabilities.isPageFlipping() ? Color.white
				: errorColor);
		g2.drawString("Page Flipping: "
				+ (bufferCapabilities.isPageFlipping() ? "Available"
						: "Not Supported"), 100, 80);

		g2.setColor(bufferCapabilities.isFullScreenRequired() ? errorColor
				: Color.white);
		g2.drawString(
				"Full Screen Required: "
						+ (bufferCapabilities.isFullScreenRequired() ? "Required"
								: "Not Required"), 100, 100);

		g2.setColor(bufferCapabilities.isMultiBufferAvailable() ? Color.white
				: errorColor);
		g2.drawString(
				"Multiple Buffer Capable: "
						+ (bufferCapabilities.isMultiBufferAvailable() ? "Yes"
								: "No"), 100, 120);

		g2.setColor(gd.isFullScreenSupported() ? Color.white : errorColor);
		g2.drawString("Full-Screen Supported: "
				+ (gd.isFullScreenSupported() ? "Yes" : "No"), 100, 140);

		g2.setColor((gc.getBufferCapabilities().getBackBufferCapabilities().isAccelerated() && gc.getBufferCapabilities().getFrontBufferCapabilities().isAccelerated() && gc.getImageCapabilities().isAccelerated()) ? Color.white : errorColor);
		g2.drawString("BackBuffer Accelerated:  "+ ((gc.getBufferCapabilities().getBackBufferCapabilities().isAccelerated()) ? "Yes" : "No"), 101, 160);
		g2.drawString("FrontBuffer Accelerated:  "+ ((gc.getBufferCapabilities().getFrontBufferCapabilities().isAccelerated()) ? "Yes" : "No"), 100, 170);
		g2.drawString("ImageBuffer Accelerated: "+ ((gc.getImageCapabilities().isAccelerated()) ? "Yes" : "No"), 100, 180);

		if (gd.isFullScreenSupported()) {
			g2.setColor(Color.gray);
			g2.drawString(
					fullscreen ? "Press ESC to exit full-screen mode."
							: "Maximize the window to enter Full-Screen Exclusive mode.",
					100, 220);
		} else {
			g2.setColor(new Color(173, 0, 8));
			g2.drawString("Full-screen exclusive mode is not supported...",
					100, 220);
		}

		g2.setColor(new Color(0, 173, 8));
		g2.drawString("FPS: "+(int)fps, 100, 200);
		
		y += delta;
		if ((y + 50) > frame.getHeight() || y < 0) {
			delta *= -1;
		}

		g2.setColor(Color.blue);
		g2.fillRect(frame.getWidth() - 50, y, 50, 50);
	}
}
