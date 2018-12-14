package display;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import main.Main;
import main.ManagedFrame;
import main.ResourceManager;

@SuppressWarnings("serial")
public class StartPanel extends JPanel {
	private ResourceManager resourceManager = ResourceManager.getInstance();
	private ManagedFrame managedFrame = ManagedFrame.getInstance();
	
	public StartPanel() {
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					new Player(new BufferedInputStream(new FileInputStream(resourceManager.getSoundFile("ok")))).play();
				} catch (FileNotFoundException | JavaLayerException e1) {
					e1.printStackTrace();
				}
				// 로그인 페이지로 이동
				managedFrame.changeAndUpdate(LoginPanel.class);
			}
		});
		
		//////////////////////////////////////////////////////////////////////////////////
		JLabel backGround = new JLabel("");
		backGround.setIcon(new ImageIcon(resourceManager.getImageFile("start").getAbsolutePath()));
		backGround.setSize(1280, 720);
		add(backGround);
		//////////////////////////////////////////////////////////////////////////////////
	}
}
