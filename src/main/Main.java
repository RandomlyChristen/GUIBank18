package main;

import javax.swing.JFrame;

import display.LoginPanel;
import display.MainPanel;
import display.StartPanel;

@SuppressWarnings("serial")
public class Main extends JFrame{
	
	public static final int SCREEN_WIDTH = 1280;
	public static final int SCREEN_HEIGHT = 720;
	
	public static void main(String[] args) throws InterruptedException {
		ResourceManager resourceManager = ResourceManager.getInstance();
		AccountManager accountManager = AccountManager.getInstance();
		accountManager.makeInterestForAll(resourceManager);  // 시작할때 이자를 재설정함
		ManagedFrame managedFrame = ManagedFrame.getInstance();
		managedFrame.initialize();
		managedFrame.addPanels(StartPanel.class);
		managedFrame.addPanels(LoginPanel.class);
		managedFrame.addPanels(MainPanel.class);
		managedFrame.changeAndUpdate(StartPanel.class);
	}
	
	
}
