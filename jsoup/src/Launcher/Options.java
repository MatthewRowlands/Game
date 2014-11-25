package Launcher;

import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Main.Display;

public class Options extends JFrame{
	private static final long serialVersionUID = 1L;

	private Choice resolution = new Choice();
	
	private int width = 550;
	private int height = 450;
	private int button_width = 80;
	private int button_height = 40;
	
	int w = 0;
	int h = 0;
	
	private JButton ok, fullscreen;
	private JTextField twidth, theight;
	private JTextField tjump, tmove, tmouse;
	private JLabel ljump, lmove, lmouse;
	private JLabel lwidth, lheight;
	private Rectangle rok, rresolution, rfullscreen;
	
	JPanel window = new JPanel();
	Configuration config = new Configuration();
	Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	
	public Options(){
		setTitle("Options");
		setSize(new Dimension(width, height));
		getContentPane().add(window);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		window.setLayout(null);
		
		drawButtons();
		window.repaint();
	}
	
	private void drawButtons(){
		ok = new JButton("OK");
		rok = new Rectangle((width-100), (height - 70), button_width, button_height - 10);
		ok.setBounds(rok);
		window.add(ok);
		
		fullscreen = new JButton("Fullscreen");
		rfullscreen = new Rectangle((width-200), (height - 70), button_width, button_height - 10);
		fullscreen.setBounds(rfullscreen);
		window.add(fullscreen);
		
		rresolution = new Rectangle(50,80,80,25);
		resolution.setBounds(rresolution);
		resolution.add("640, 480");
		resolution.add("800, 600");
		resolution.add("1024, 768");
		resolution.select(1);
		window.add(resolution);
		
		lwidth = new JLabel("Width: ");
		lwidth.setBounds(30, 150, 120, 20);
		twidth = new JTextField();
		twidth.setBounds(80, 150, 60, 20);
		window.add(twidth);
		window.add(lwidth);

		lheight = new JLabel("Height: ");
		lheight.setBounds(30, 180, 120, 20);
		theight = new JTextField();
		theight.setBounds(80, 180, 60, 20);
		window.add(theight);
		window.add(lheight);
		
		ljump = new JLabel("Jump Height: ");
		ljump.setBounds(10, 250, 120, 20);
		tjump = new JTextField("1");
		tjump.setBounds(80, 250, 60, 20);
		window.add(tjump);
		window.add(ljump);

		lmove = new JLabel("Move Speed: ");
		lmove.setBounds(10, 280, 120, 20);
		tmove = new JTextField("1");
		tmove.setBounds(80, 280, 60, 20);
		window.add(tmove);
		window.add(lmove);
		
		lmouse = new JLabel("Mouse Speed: ");
		lmouse.setBounds(10, 310, 120, 20);
		tmouse = new JTextField("10");
		tmouse.setBounds(80, 310, 60, 20);
		window.add(tmouse);
		window.add(lmouse);
		
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				Display.setMoveSpeed(Integer.parseInt(tmove.getText()));
				Display.setJumpHeight(Integer.parseInt(tjump.getText()));
				Display.setMouseSpeed(Integer.parseInt(tmouse.getText()));
				config.saveConfiguration("Width","Height",parseWidth(), parseHeight());
				System.out.println("Custom Resolution: "+w+","+h);
				System.out.println("Jump Height: "+Display.JumpHeight);
				System.out.println("Move Speed: "+Display.MoveSpeed);
				System.out.println("Mouse Speed: "+Display.MouseSpeed);
				new Launcher();
			}		
		});
		fullscreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				Display.setMoveSpeed(Integer.parseInt(tmove.getText()));
				Display.setJumpHeight(Integer.parseInt(tjump.getText()));
				Display.setMouseSpeed(Integer.parseInt(tmouse.getText()));
				config.saveConfiguration("Width","Height", ss.width, ss.height);
				System.out.println("Custom Resolution: "+w+","+h);
				System.out.println("Jump Height: "+Display.JumpHeight);
				System.out.println("Move Speed: "+Display.MoveSpeed);
				System.out.println("Mouse Speed: "+Display.MouseSpeed);
				new Launcher();
			}		
		});
	}
	
	private void drop(){
		int selection = resolution.getSelectedIndex();
		if(selection == 0){
			w = 640;
			h = 480;
			twidth.setText(""+w);
			theight.setText(""+h);
		}
		if(selection == 1 || selection == -1){
			w = 800;
			h = 600;
			twidth.setText(""+w);
			theight.setText(""+h);
		}
		if(selection == 2){
			w = 1024;
			h = 768;
			twidth.setText(""+w);
			theight.setText(""+h);
		}
		System.out.println("Default Resolution: "+w+","+h);
	}
	private int parseWidth(){
		try{
		int w = Integer.parseInt(twidth.getText());	
		this.w = w;
		return w;
		}catch(NumberFormatException e){
			drop();
			return w;
		}
	}
	private int parseHeight(){
		try{
		int h = Integer.parseInt(theight.getText());
		this.h = h;
		return h;
		}catch(NumberFormatException e){
			drop();
			return h;
		}
	}
}
