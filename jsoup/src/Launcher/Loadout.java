package Launcher;

import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Entity.Weapon;
import Main.Display;

public class Loadout extends JFrame{
	private static final long serialVersionUID = 1L;

	private int width = 550;
	private int height = 450;
	
	int w = 0;
	int h = 0;
	
	int index = 1;
	int maxindex = 12;
	Weapon wep = new Weapon(index);
	Weapon wep1 = new Weapon(index);
	Weapon wep2 = new Weapon(index+1);
	
	JPanel window = new JPanel();
	Configuration config = new Configuration();
	Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	private JButton ok, left, right, select1, select2;
	private JLabel name, fr, dm, ac, mg, fm, rt;
	Save save = new Save();
	
	public Loadout(){
		setTitle("Choose Loadout");
		setSize(new Dimension(width, height));
		getContentPane().add(window);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		window.setLayout(null);
		
		drawButtons();
		drawLabels();
		window.repaint();
	}
	
	private void drawLabels() {
		fr = new JLabel();
		dm = new JLabel();
		ac = new JLabel();
		mg = new JLabel();
		fm = new JLabel();
		rt = new JLabel();
		
		fr.setFont(new Font("Verdana", Font.PLAIN, 11));
		dm.setFont(new Font("Verdana", Font.PLAIN, 11));
		ac.setFont(new Font("Verdana", Font.PLAIN, 11));
		mg.setFont(new Font("Verdana", Font.PLAIN, 11));
		fm.setFont(new Font("Verdana", Font.PLAIN, 11));
		rt.setFont(new Font("Verdana", Font.PLAIN, 11));
		
		fr.setBounds(width/2 - 80, 150, 300, 30);
		dm.setBounds(width/2 - 80, 170, 300, 30);
		ac.setBounds(width/2 - 80, 190, 300, 30);
		mg.setBounds(width/2 - 80, 210, 300, 30);
		fm.setBounds(width/2 - 80, 230, 300, 30);
		rt.setBounds(width/2 - 80, 250, 300, 30);
		
		window.add(fr);
		window.add(dm);
		window.add(ac);
		window.add(mg);
		window.add(fm);
		window.add(rt);
		
		updateText();
	}

	private void updateText(){
		name.setText("Gun: "+wep.name);
		fr.setText("Firerate: "+wep.firerate);
		dm.setText("Damage: "+wep.WeaponDamage);
		ac.setText("Accuracy: "+(int)(1/wep.accuracy));
		mg.setText("Magazine Capacity: "+wep.WeaponAmmo);
		fm.setText("Firemode: "+wep.firemode);
		rt.setText("Reload Speed: "+wep.reloadspeed);
		select1.setText("Primary: "+wep1.name);
		select2.setText("Secondary: "+wep2.name);
	}
	
	private void drawButtons(){
		name = new JLabel("Gun: "+wep.name);
		name.setFont(new Font("Verdana", Font.PLAIN, 20));
		name.setBounds(width/2 - 100, 100, 300, 30);
		window.add(name);
		
		ok = new JButton("OK");
		ok.setBounds((width-90), (height - 60), 80, 30);
		window.add(ok);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				save.saveConfiguration(wep1, wep2);
				new Launcher();
			}		
		});
		select1 = new JButton("Primary: "+wep1.name);
		select1.setBounds(width/2-150, (height - 60), 150, 30);
		window.add(select1);
		select1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!wep.name.equals(wep2.name)){
				wep1 = wep;
				}else{
					Weapon weapon1 = wep1;
					Weapon weapon2 = wep2;
					wep1 = weapon2;
					wep2 = weapon1;
				}
				updateText();
			}		
		});
		select2 = new JButton("Secondary: "+wep2.name);
		select2.setBounds(width/2, (height - 60), 150, 30);
		window.add(select2);
		select2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!wep.name.equals(wep1.name)){
				wep2 = wep;
				}else{
					Weapon weapon1 = wep1;
					Weapon weapon2 = wep2;
					wep1 = weapon2;
					wep2 = weapon1;
				}
				updateText();
			}		
		});
		left = new JButton("<");
		left.setBounds(100, 170, 40, 80);
		window.add(left);
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(index > 1){
					index--;
				}else{
					index = maxindex;
				}
				wep = new Weapon(index);
				updateText();
			}		
		});
		right = new JButton(">");
		right.setBounds(width-140, 170, 40, 80);
		window.add(right);
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(index < maxindex){
					index++;
				}else{
					index = 1;
				}
				wep = new Weapon(index);
				updateText();
			}		
		});
	}
}
