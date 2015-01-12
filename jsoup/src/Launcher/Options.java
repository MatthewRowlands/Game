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
	private int button_width = 120;
	private int button_height = 40;
	private boolean fullsc = true;
	
	int w = 0;
	int h = 0;
	
	private JButton ok, fullscreen, cancel;
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
		rok = new Rectangle((width-120), (height - 70), button_width, button_height - 10);
		ok.setBounds(rok);
		window.add(ok);
		
		fullscreen = new JButton("Fullscreen: "+(fullsc? "Yes" : "No"));
		rfullscreen = new Rectangle((width-240), (height - 70), button_width, button_height - 10);
		fullscreen.setBounds(rfullscreen);
		window.add(fullscreen);
		
		rresolution = new Rectangle(50,80,80,25);
		resolution.setBounds(rresolution);
		
		String[] res = new String[13];
		res[0] = "640, 480";
		res[1] = "800, 600";
		res[2] = "1024, 768";
		res[3] = "1280, 720";
		res[4] = "1280, 800";
		res[5] = "1280, 1024";
		res[6] = "1366, 768";
		res[7] = "1440, 900";
		res[8] = "1600, 900";
		res[9] = "1680, 1050";
		res[10] = "1920, 1080";
		res[11] = "1920, 1200";
		res[12] = "2560, 1440";
		resolution.add(res[0]);
		resolution.add(res[1]);
		resolution.add(res[2]);
		resolution.add(res[3]);
		resolution.add(res[4]);
		resolution.add(res[5]);
		resolution.add(res[6]);
		resolution.add(res[7]);
		resolution.add(res[8]);
		resolution.add(res[9]);
		resolution.add(res[10]);
		resolution.add(res[11]);
		resolution.add(res[12]);	
		resolution.select(9);
		for(int i = 0; i < res.length; i++){
			if(res[i].equals(""+ss.width+", "+ss.height)){
				resolution.select(i);
			}
		}
		window.add(resolution);
		
		cancel = new JButton("Cancel");
		cancel.setBounds((width-360), (height - 70), button_width, button_height - 10);
		window.add(cancel);
		
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
				Display.fullscreen = fullsc;
				config.saveConfiguration("Width","Height",parseWidth(), parseHeight());
				System.out.println("Custom Resolution: "+w+","+h);
				System.out.println("Jump Height: "+Display.JumpHeight);
				System.out.println("Move Speed: "+Display.MoveSpeed);
				System.out.println("Mouse Speed: "+Display.MouseSpeed);
				new Launcher().startMenu();
			}		
		});
		fullscreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fullsc ^= true;
				fullscreen.setText("Fullscreen: "+(fullsc? "Yes" : "No"));
			}		
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Launcher().startMenu();
			}		
		});
	}
	
	private void drop(){
		int selection = resolution.getSelectedIndex();
		
		if(selection == 0)
			Selection(640, 480);
		if(selection == 1)
			Selection(800, 600);
		if(selection == 2)
			Selection(1024, 768);
		if(selection == 3)
			Selection(1280, 720);
		if(selection == 4)
			Selection(1280, 800);
		if(selection == 5)
			Selection(1280, 1024);
		if(selection == 6)
			Selection(1366, 768);
		if(selection == 7)
			Selection(1440, 900);
		if(selection == 8)
			Selection(1600, 900);
		if(selection == 9 || selection == -1)
			Selection(1680, 1050);
		if(selection == 10)
			Selection(1920, 1080);
		if(selection == 11)
			Selection(1920, 1200);
		if(selection == 12)
			Selection(2560, 1440);
		System.out.println("Default Resolution: "+w+","+h);
	}
	public void Selection(int width, int height){
		w = width;
		h = height;
		twidth.setText(""+w);
		theight.setText(""+h);
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
