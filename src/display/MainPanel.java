package display;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import main.AccountManager;
import main.LoginManager;
import main.Main;
import main.ManagedFrame;
import main.ResourceManager;
import main.Sound;

import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private JLabel welcomeText;
	private JLabel netAssetText;
	private JLabel liabilityText;
	
	private JButton depositButton;	private JButton repayButton;
	private JButton withdrawButton;	private JButton openButton;
	private JButton transferButton;	private JButton lookUpButton;
	private JButton loanButton;		private JButton terminateBurron;
	
	private ResourceManager resourceManager = ResourceManager.getInstance();
	private ManagedFrame managedFrame = ManagedFrame.getInstance();
	private AccountManager accountManager = AccountManager.getInstance();
	private LoginManager loginManager = LoginManager.getInstance();
	
	public MainPanel() {
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setLayout(null);
		
		welcomeText = new JLabel("환영합니다!");
		welcomeText.setBounds(720, 25, 275, 16);
		add(welcomeText);
		
		netAssetText = new JLabel("당신의 순자산 : ");
		netAssetText.setBounds(720, 50, 275, 16);
		add(netAssetText);
		
		liabilityText = new JLabel("당신의 부채 : ");
		liabilityText.setBounds(720, 75, 275, 16);
		add(liabilityText);
		
		depositButton = new JButton("입금");
		depositButton.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
		depositButton.setBounds(140, 185, 300, 100);
		add(depositButton);
		
		withdrawButton = new JButton("출금");
		withdrawButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		withdrawButton.setBounds(140, 295, 300, 100);
		add(withdrawButton);
		
		transferButton = new JButton("이체");
		transferButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		transferButton.setBounds(140, 405, 300, 100);
		add(transferButton);
		
		loanButton = new JButton("대출");
		loanButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		loanButton.setBounds(140, 515, 300, 100);
		add(loanButton);
		
		repayButton = new JButton("상환");
		repayButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		repayButton.setBounds(840, 185, 300, 100);
		add(repayButton);
		
		openButton = new JButton("개설");
		openButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		openButton.setBounds(840, 295, 300, 100);
		openButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Sound(resourceManager.getSoundFile("button")).playSound(); loginManager.resetTimer();
				String randAccount;
				do {
					randAccount = Integer.toString(((int)(Math.random() * 9000000) + 999999));
				} while (accountManager.isExistingAccount(randAccount));  // 계좌가 존재하는지 확인하자
				
				JLabel account = new JLabel("당신의 계좌 : " + randAccount);
				JPasswordField newPassword = new JPasswordField();
				JPasswordField passwordConfirm = new JPasswordField();
				final JComponent[] inputs = new JComponent[] {
						account, newPassword, passwordConfirm
				};
				
				int answer = JOptionPane.showConfirmDialog(managedFrame, inputs, "비밀번호를 두번 입력하세요", JOptionPane.PLAIN_MESSAGE);
				if (answer != JOptionPane.OK_OPTION) return;  // 취소할 경우 반환
				
				String password = new String(newPassword.getPassword());
				String passwordVerify = new String(passwordConfirm.getPassword());
				
				int result = accountManager.createNewAccount(randAccount, password, passwordVerify);
				
				switch (result) {
				case AccountManager.CREATE_ACCOUNT_SUCCESS:
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "개설 성공!");
					break;
					
				case AccountManager.CREATE_ACCOUNT_FAILURE:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(managedFrame, "비밀번호가 일치하지 않습니다!");
					break;

				default:
					break;
				}
			}
		});
		add(openButton);
		
		lookUpButton = new JButton("조회");
		lookUpButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		lookUpButton.setBounds(840, 405, 300, 100);
		add(lookUpButton);
		
		terminateBurron = new JButton("해지");
		terminateBurron.setFont(new Font("Dialog", Font.PLAIN, 40));
		terminateBurron.setBounds(840, 515, 300, 100);
		add(terminateBurron);
		//////////////////////////////////////////////////////////////////////////////////
		JLabel backGround = new JLabel("");
		backGround.setIcon(new ImageIcon("C:\\Users\\DISKSMART\\Desktop\\eclipse-workspace\\GUIBank18\\.guibankdata\\res\\image\\menu.png"));
		backGround.setSize(1280, 720);
		add(backGround);
		//////////////////////////////////////////////////////////////////////////////////
	}
}
