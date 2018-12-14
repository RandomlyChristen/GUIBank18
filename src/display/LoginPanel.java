package display;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import main.LoginManager;
import main.Main;
import main.ManagedFrame;
import main.ResourceManager;
import main.Sound;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {
	
	private JTextField userField = new JTextField();
	private JPasswordField passwordField = new JPasswordField();
	private JPasswordField pwVerifyField = new JPasswordField();
	private JButton loginButton = new JButton("LOGIN");
	private JButton registerButton = new JButton("REGISTER");
	private LoginManager loginManager = LoginManager.getInstance();
	private ResourceManager resourceManager = ResourceManager.getInstance();
	private ManagedFrame managedFrame = ManagedFrame.getInstance();

	public LoginPanel() {
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setLayout(null);
		
		userField.setBounds(365, 310, 130, 30);
		add(userField);
		
		passwordField.setBounds(365, 365, 130, 30);
		add(passwordField);
		
		pwVerifyField.setBounds(660, 365, 130, 30);
		add(pwVerifyField);
		
		loginButton.setBounds(500, 310, 120, 85);
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					new Player(new BufferedInputStream(new FileInputStream(resourceManager.getSoundFile("button")))).play();
				} catch (FileNotFoundException | JavaLayerException e1) {
					e1.printStackTrace();
				}
				String user = userField.getText(); String password = new String(passwordField.getPassword());
				int result = loginManager.login(user, password);
				switch (result) {
				case LoginManager.LOGIN_SUCCESS:
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "로그인 성공");
					userField.setText(""); passwordField.setText(""); pwVerifyField.setText("");
					managedFrame.changeAndUpdate(MainPanel.class);
					break;
					
				case LoginManager.LOGIN_ID_ERROR:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "아이디를 확인하세요!");
					break;
					
				case LoginManager.LOGIN_PW_ERROR:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "비밀번호가 일치하지 않습니다!");
					break;

				default:
					break;
				}
			}
		});
		add(loginButton);
		
		registerButton.setBounds(795, 310, 120, 85);
		registerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					new Player(new BufferedInputStream(new FileInputStream(resourceManager.getSoundFile("button")))).play();
				} catch (FileNotFoundException | JavaLayerException e1) {
					e1.printStackTrace();
				}
				String user = userField.getText(); String password = new String(passwordField.getPassword());
				String passwordVerify = new String(pwVerifyField.getPassword());
				int result = loginManager.register(user, password, passwordVerify);
				switch (result) {
				case LoginManager.REGIST_SUCCESS:
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "가입 성공");
					userField.setText(""); passwordField.setText(""); pwVerifyField.setText("");
					break;
					
				case LoginManager.REGIST_ID_ERROR:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "아이디가 중복이거나 비어있습니다!");
					break;
					
				case LoginManager.REGIST_PW_ERROR:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "비밀번호가 일치하지 않습니다!");
					break;

				default:
					break;
				}
			}
		});
		add(registerButton);
		
		//////////////////////////////////////////////////////////////////////////////////
		JLabel backGround = new JLabel("");
		backGround.setIcon(new ImageIcon(resourceManager.getImageFile("login").getAbsolutePath()));
		backGround.setSize(1280, 720);
		add(backGround);
		//////////////////////////////////////////////////////////////////////////////////
	}
}
