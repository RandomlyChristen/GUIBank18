package display;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;
import javax.swing.JButton;
import java.awt.Font;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	public MainPanel() {
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(936, 25, 61, 16);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setBounds(936, 57, 61, 16);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setBounds(936, 85, 61, 16);
		add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("입금");
		btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
		btnNewButton.setBounds(140, 220, 300, 100);
		add(btnNewButton);
		//////////////////////////////////////////////////////////////////////////////////
		JLabel backGround = new JLabel("");
		backGround.setIcon(new ImageIcon("/Users/isugyun/eclipse-workspace/GuiBank18/.guibankdata/res/image/menu.png"));
		backGround.setSize(1280, 720);
		add(backGround);
		//////////////////////////////////////////////////////////////////////////////////
	}
}
