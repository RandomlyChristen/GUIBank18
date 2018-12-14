package main;

import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import display.LoginPanel;
import display.MainPanel;
import display.StartPanel;

public class Main extends JFrame{
	
	public static final int SCREEN_WIDTH = 1280;
	public static final int SCREEN_HEIGHT = 720;
	
	public Main() {
		setUndecorated(true);
		setTitle("ATM-2018");
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		getContentPane().add(new LoginPanel());
//		getContentPane().add(new StartPanel());
		setVisible(true);
	}
	
	public static void main(String[] args) throws InterruptedException {
		ManagedFrame managedFrame = ManagedFrame.getInstance();
		managedFrame.initialize();
		managedFrame.addPanels(StartPanel.class);
		managedFrame.addPanels(LoginPanel.class);
		managedFrame.addPanels(MainPanel.class);
		managedFrame.changeAndUpdate(StartPanel.class);
		
//		A panel = new A();
//		Main frame = new Main();
//		frame.getContentPane().removeAll();
//		frame.getContentPane().add(panel);
//		frame.revalidate();
//		frame.repaint();
//		
//		panel.a = "a";
//		Thread.sleep(1000);
//		frame.revalidate();
//		frame.repaint();
//		panel.a = "b";
//		Thread.sleep(1000);
//		frame.revalidate();
//		frame.repaint();
//		panel.a = "c";
//		Thread.sleep(1000);
//		frame.revalidate();
//		frame.repaint();
//		panel.a = "d";
//		Thread.sleep(1000);
//		frame.revalidate();
//		frame.repaint();
	}
	
	
}

class A extends JPanel {
	JLabel text = new JLabel("aaa");
	String a = "a";
	
	public A() {
		text.setText("AAA");
		text.setBounds(365, 310, 130, 30);
		add(this.text);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		text.setText(a);
		System.out.println("PAINT" + a);
	}
}
