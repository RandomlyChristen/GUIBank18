package display;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.AccountManager;
import main.LoginManager;
import main.Main;
import main.ResourceManager;
import main.Sound;

import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import javax.swing.Box;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private JLabel welcomeText;		private JLabel netAssetText;
	private JLabel liabilityText;	private JLabel cashInserted;
	
	
	private JButton depositButton;	private JButton repayButton;
	private JButton withdrawButton;	private JButton openButton;
	private JButton transferButton;	private JButton lookUpButton;
	private JButton loanButton;		private JButton terminateBurron;
	
	private ResourceManager resourceManager = ResourceManager.getInstance();
	private AccountManager accountManager = AccountManager.getInstance();
	private LoginManager loginManager = LoginManager.getInstance();
	
	private long depositInsert = 0;
	
	public MainPanel() {
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setLayout(null);
		
		welcomeText = new JLabel("환영합니다!");
		welcomeText.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		welcomeText.setBounds(720, 25, 275, 16);
		add(welcomeText);
		
		netAssetText = new JLabel("예금 : ");
		netAssetText.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		netAssetText.setBounds(720, 50, 275, 16);
		add(netAssetText);
		
		liabilityText = new JLabel("부채 : ");
		liabilityText.setFont(new Font("Lucida Grande", Font.PLAIN, 17));
		liabilityText.setBounds(720, 75, 275, 16);
		add(liabilityText);
		
		depositButton = new JButton("입금");
		depositButton.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
		depositButton.setBounds(140, 185, 300, 100);
		depositButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Sound(resourceManager.getSoundFile("button")).playSound(); loginManager.resetTimer();
				if (depositInsert < 1) {  // 투입한 금액이 없는 경우, 반환
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "현금을 투입 후, 다시시도하세요!");
					cashInserted.requestFocus(); return;
				}
				JLabel insertText = new JLabel("입금할 현금 : " + depositInsert);
				JComboBox<String> accounts = new JComboBox<>(accountManager.getAccountsOfUser(loginManager, resourceManager));
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						insertText, accounts, space
				};
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "투입한 금액을 확인후, 계좌를 선택하세요", JOptionPane.OK_OPTION);
				String account = (String) accounts.getSelectedItem();
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					depositInsert = 0; cashInserted.requestFocus(); repaint(); return;  // 취소할 경우 반환
				}
				if (account == null) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "계좌가 유효하지 않습니다!");
					depositInsert = 0; cashInserted.requestFocus(); repaint(); return;
				}
				int result = accountManager.deposit(account, depositInsert, resourceManager);
				if (result == AccountManager.DEPOSIT_SUCCESS) {
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "입금되었습니다!");
				} else {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
				}
				depositInsert = 0; cashInserted.requestFocus(); repaint();
			}
		});
		add(depositButton);
		
		cashInserted = new JLabel("투입된 현금 (F) : " + depositInsert);
		cashInserted.setFont(new Font("Dialog", Font.PLAIN, 15));
		cashInserted.setBounds(140, 159, 275, 16);
		cashInserted.addKeyListener(new KeyAdapter() {
			int temp = 0;
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F) {
					temp += 175;
					try {
						Thread.sleep(25);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if (temp > 1000) {
						new Sound(resourceManager.getSoundFile("money")).playSound();
						temp = 0; depositInsert += 1000;
						cashInserted.setText("투입한 현금 : " + depositInsert);
					}
				}
			}
		});
		cashInserted.setFocusable(true);
		cashInserted.requestFocus();
		add(cashInserted);
		
		
		withdrawButton = new JButton("출금");
		withdrawButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		withdrawButton.setBounds(140, 295, 300, 100);
		withdrawButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Sound(resourceManager.getSoundFile("button")).playSound(); loginManager.resetTimer();
				
				JComboBox<String> accounts = new JComboBox<>(accountManager.getAccountsOfUser(loginManager, resourceManager));
				JTextField input = new JTextField(); input.setText("0");
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						accounts, input, space
				};
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "계좌를 선택 후, 출금할 금액을 입력하세요", JOptionPane.OK_OPTION);
				String account = (String) accounts.getSelectedItem();
				long cash = Long.parseLong(input.getText());
				
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					cashInserted.requestFocus(); repaint(); return;  // 취소할 경우 반환
				}
				
				int result = accountManager.withdraw(account, cash, resourceManager);
				if (result == AccountManager.WITHDRAW_SUCCESS) {
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "출금되었습니다!");
				} else {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "잔액이 부족합니다!");
				}
				cashInserted.requestFocus(); repaint();
			}
		});
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
				} while (accountManager.isExistingAccount(randAccount, resourceManager));  // 계좌가 존재하는지 확인하자
				
				JLabel account = new JLabel("당신의 계좌 : " + randAccount);
				JPasswordField newPassword = new JPasswordField();
				JPasswordField passwordConfirm = new JPasswordField();
				JComboBox<String> newAccountType = new JComboBox<>(new String[] {"예금 계좌", "적금 계좌", "대출 계좌"});
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						account, newPassword, passwordConfirm, newAccountType, space
				};
				
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "비밀번호를 두번 입력하세요", JOptionPane.YES_OPTION);
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					return;  // 취소할 경우 반환
				}
				
				String password = new String(newPassword.getPassword());
				String passwordVerify = new String(passwordConfirm.getPassword());
				int accountType = newAccountType.getSelectedIndex();
				
				int result = accountManager.createNewAccount(randAccount, password, passwordVerify, accountType, loginManager, resourceManager);
				
				switch (result) {
				case AccountManager.CREATE_ACCOUNT_SUCCESS:
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "개설 성공!");
					break;
					
				case AccountManager.CREATE_ACCOUNT_FAILURE:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "비밀번호가 일치하지 않습니다!");
					break;
					
				case AccountManager.CREATE_ACCOUNT_NOT_SIGNED:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "로그인 중이 아닙니다!");
					break;

				default:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					break;
				}
				repaint();
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
		backGround.setIcon(new ImageIcon(resourceManager.getImageFile("menu").getAbsolutePath()));
		backGround.setSize(1280, 720);
		add(backGround);
		//////////////////////////////////////////////////////////////////////////////////
	}
	
	@Override
	public void paint(Graphics g) {
		String currentUser = loginManager.getUser();
		long netAsset = accountManager.getBalanceOfUser(loginManager, resourceManager);
		long liability = accountManager.getliabilityOfUser(loginManager, resourceManager);
		welcomeText.setText("환영합니다, " + currentUser);
		netAssetText.setText("자산 : " + netAsset);
		liabilityText.setText("부채 : " + liability);
		cashInserted.setText("투입한 현금 : " + depositInsert);
		super.paint(g);
	}
}
