package display;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
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
	private JButton loanButton;		
	
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
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;
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
					depositInsert = 0; cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 취소할 경우 반환
				}
				if (account == null) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "계좌가 유효하지 않습니다!");
					depositInsert = 0; cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;
				}
				int result = accountManager.deposit(account, depositInsert, resourceManager);
				if (result == AccountManager.DEPOSIT_SUCCESS) {
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "입금되었습니다!");
				} else {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
				}
				depositInsert = 0; cashInserted.requestFocus(); loginManager.resetTimer(); repaint();
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
				JPasswordField passwordIn = new JPasswordField();
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						accounts, input, passwordIn, space
				};
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "계좌를 선택 후, 출금할 금액을 입력하세요", JOptionPane.OK_OPTION);
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 취소할 경우 반환
				}
				
				String account = (String) accounts.getSelectedItem();
				if (accountManager.getTypeOfAccount(account, resourceManager) == AccountManager.TYPE_SAVING) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "이 계좌는 출금 거래가 불가능한 적금계좌입니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 출금 거래 불가
				}
				
				String password = new String(passwordIn.getPassword());
				
				if (!accountManager.isCorrectPassword(password, account, resourceManager)) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "패스워드가 일치하지 않습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 패스워드 불일치
				}
				
				long cash = 0;
				try {
					cash = Long.parseLong(input.getText());
				} catch (NumberFormatException e1) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "금액을 입력하십시오!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 금액을 입력하지않을 경우 반환
				}
				
				int result = accountManager.withdraw(account, cash, resourceManager);
				if (result == AccountManager.WITHDRAW_SUCCESS) {
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "출금되었습니다!");  // 성공
				} else {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "잔액이 부족합니다!");
				}
				cashInserted.requestFocus(); loginManager.resetTimer(); repaint();
			}
		});
		add(withdrawButton);
		
		
		transferButton = new JButton("이체");
		transferButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		transferButton.setBounds(140, 405, 300, 100);
		transferButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Sound(resourceManager.getSoundFile("button")).playSound(); loginManager.resetTimer();
				
				JComboBox<String> myAccounts = new JComboBox<>(accountManager.getAccountsOfUser(loginManager, resourceManager));
				JLabel lb1 = new JLabel("상대방 계좌 :"); JTextField counterAccountIn = new JTextField();
				JLabel lb2 = new JLabel("보내실 금액 :"); JTextField amountIn = new JTextField();
				JLabel lb3 = new JLabel("비밀번호 :"); JPasswordField passwordIn = new JPasswordField();
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						myAccounts, lb1, counterAccountIn, lb2, amountIn, lb3, passwordIn, space
				};
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "계좌를 선택 후, 이체 내역과 비밀번호를 입력하세요", JOptionPane.OK_OPTION);
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 취소할 경우 반환
				}
				
				String account = (String) myAccounts.getSelectedItem();
				if (accountManager.getTypeOfAccount(account, resourceManager) == AccountManager.TYPE_SAVING) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "이 계좌는 출금 거래가 불가능한 적금계좌입니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 출금 거래 불가
				}
				
				String counterAccount = counterAccountIn.getText();
				
				if (!accountManager.isExistingAccount(counterAccount, resourceManager)) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "존재하지않는 계좌번호입니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 입력한 계좌번호가 존재하지않을 경우 반환
				}
				
				long amount;
				try {
					amount = Long.parseLong(amountIn.getText());
				} catch (NumberFormatException e1) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "금액을 입력하십시오!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 금액을 입력하지않을 경우 반환
				}
				
				String password = new String(passwordIn.getPassword());
				if (!accountManager.isCorrectPassword(password, account, resourceManager)) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "패스워드가 일치하지 않습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 패스워드 불일치
				}
				
				int result = accountManager.transfer(account, counterAccount, amount, loginManager, resourceManager);
				if (result == AccountManager.TRANSTFER_SUCCESS) {
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "이체되었습니다!");  // 성공
				} else {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "잔액이 부족합니다!");
				}
				
				cashInserted.requestFocus(); loginManager.resetTimer(); repaint();
			}
		});
		add(transferButton);
		
		
		loanButton = new JButton("대출");
		loanButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		loanButton.setBounds(140, 515, 300, 100);
		loanButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Sound(resourceManager.getSoundFile("button")).playSound(); loginManager.resetTimer();
				
				JComboBox<String> myAccounts = new JComboBox<>(accountManager.getAccountsOfUser(loginManager, resourceManager));
				JTextField amountIn = new JTextField();
				JPasswordField passwordIn = new JPasswordField();
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						myAccounts, amountIn, passwordIn, space
				};
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "계좌를 선택 후, 대출하실 금액과 비밀번호를 입력하세요", JOptionPane.OK_OPTION);
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 취소할 경우 반환
				}
				
				String account = (String) myAccounts.getSelectedItem();
				if (accountManager.getTypeOfAccount(account, resourceManager) != AccountManager.TYPE_LONE) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "이 계좌는 대출 가능한 계좌가 아닙니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 대출 거래 불가
				}
				
				long amount;
				try {
					amount = Long.parseLong(amountIn.getText());
				} catch (NumberFormatException e1) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "금액을 입력하십시오!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 금액을 입력하지않을 경우 반환
				}
				
				String password = new String(passwordIn.getPassword());
				if (!accountManager.isCorrectPassword(password, account, resourceManager)) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "패스워드가 일치하지 않습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 패스워드 불일치
				}
				
				int result = accountManager.borrow(account, amount, resourceManager);
				if (result == AccountManager.BORROW_SUCCESS) {
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "대출되었습니다!");
				}
				else {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
				}
				
				cashInserted.requestFocus(); loginManager.resetTimer(); repaint();
			}
		});
		add(loanButton);
		
		
		repayButton = new JButton("상환");
		repayButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		repayButton.setBounds(840, 185, 300, 100);
		repayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Sound(resourceManager.getSoundFile("button")).playSound(); loginManager.resetTimer();
				
				JComboBox<String> myAccounts = new JComboBox<>(accountManager.getAccountsOfUser(loginManager, resourceManager));
				JTextField amountIn = new JTextField();
				JPasswordField passwordIn = new JPasswordField();
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						myAccounts, amountIn, passwordIn, space
				};
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "계좌를 선택 후, 상환하실 금액과 비밀번호를 입력하세요", JOptionPane.OK_OPTION);
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 취소할 경우 반환
				}
				
				String account = (String) myAccounts.getSelectedItem();
				if (accountManager.getTypeOfAccount(account, resourceManager) != AccountManager.TYPE_LONE) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "이 계좌는 대출 가능한 계좌가 아닙니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 대출 거래 불가
				}
				
				long amount;
				try {
					amount = Long.parseLong(amountIn.getText());
				} catch (NumberFormatException e1) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "금액을 입력하십시오!");
					cashInserted.requestFocus(); repaint(); return;  // 금액을 입력하지않을 경우 반환
				}
				
				String password = new String(passwordIn.getPassword());
				if (!accountManager.isCorrectPassword(password, account, resourceManager)) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "패스워드가 일치하지 않습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 패스워드 불일치
				}
				
				int result = accountManager.refund(account, amount, resourceManager);
				switch (result) {
				case AccountManager.REFUND_SUCCESS:
					new Sound(resourceManager.getSoundFile("ok")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "상환되었습니다!");
					break;
				
				case AccountManager.REFUND_LOW_BALANCE:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "잔금이 부족하여 해당 금액을 상환하지 못했습니다!");
					break;

				case AccountManager.REFUND_LARGER_INPUT:
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "해당 금액은 이 계좌의 총 부채보다 큽니다!");
					break;

				default:
					break;
				}
				
				cashInserted.requestFocus(); loginManager.resetTimer(); repaint();
			}
		});
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
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 취소할 경우 반환
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
				cashInserted.requestFocus(); loginManager.resetTimer(); repaint();
			}
		});
		add(openButton);
		
		lookUpButton = new JButton("조회");
		lookUpButton.setFont(new Font("Dialog", Font.PLAIN, 40));
		lookUpButton.setBounds(840, 405, 300, 100);
		lookUpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new Sound(resourceManager.getSoundFile("button")).playSound(); loginManager.resetTimer();
				
				JComboBox<String> myAccounts = new JComboBox<>(accountManager.getAccountsOfUser(loginManager, resourceManager));
				JPasswordField passwordIn = new JPasswordField();
				Component space = Box.createHorizontalStrut(300);
				final Component[] inputs = new Component[] {
						myAccounts, passwordIn, space
				};
				
				int answer = JOptionPane.showConfirmDialog(MainPanel.this, inputs, "계좌를 선택 후, 비밀번호를 입력하세요", JOptionPane.OK_OPTION);
				if (answer != JOptionPane.OK_OPTION) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "취소되었습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 취소할 경우 반환
				}
				new Sound(resourceManager.getSoundFile("ok")).playSound();
				
				String account = (String) myAccounts.getSelectedItem();
				String password = new String(passwordIn.getPassword());
				if (!accountManager.isCorrectPassword(password, account, resourceManager)) {
					new Sound(resourceManager.getSoundFile("warning")).playSound();
					JOptionPane.showMessageDialog(MainPanel.this, "패스워드가 일치하지 않습니다!");
					cashInserted.requestFocus(); repaint(); loginManager.resetTimer(); return;  // 패스워드 불일치
				}
				
				JLabel accountLb = new JLabel("계좌번호 : " + account);
				
				int type = accountManager.getTypeOfAccount(account, resourceManager);
				JLabel typeLb; switch (type) {
				case AccountManager.TYPE_DEPOSIT:
					typeLb = new JLabel("종류 : 일반 예금 계좌");
					break;
				
				case AccountManager.TYPE_LONE:
					typeLb = new JLabel("종류 : 대출 전용 계좌");
					break;
					
				case AccountManager.TYPE_SAVING:
					typeLb = new JLabel("종류 : 적금 전용 계좌");
					break;

				default:
					typeLb = new JLabel("종류 : 알 수 없음");
					break;
				}
				
				long balance = accountManager.getBalanceOfAccount(account, resourceManager);
				JLabel balanceLb = new JLabel("잔고 : " + balance);
				
				long liabilities = accountManager.getliabilityOfAccount(account, resourceManager);
				JLabel liabilityLb = new JLabel("부채 : " + liabilities);
				
				JList<String> transactionList = new JList<>(accountManager.getArrayOfTransactions(account, resourceManager));
				
				final Component[] inputs1 = new Component[] {
						accountLb, typeLb, balanceLb, liabilityLb, transactionList, space
				};
				JOptionPane.showMessageDialog(MainPanel.this, inputs1);
				
				cashInserted.requestFocus(); loginManager.resetTimer(); repaint();
			}
		});
		add(lookUpButton);
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
