package main;

import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ManagedFrame extends JFrame {
	// 모든 화면을 담는 해쉬테이블 해쉬브라운 아님^^
	private Hashtable<Class<? extends JPanel>, JPanel> panelMap = new Hashtable<>();
	
	private ManagedFrame () {}
	
	public void initialize() {  // 초기 화면 세팅
		setUndecorated(true);  // 윈도우의 상단바를 제거
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);  // 윈도우의 사이즈를 세팅
		setResizable(false);  // 윈도우 크기변경 불허
		setLocationRelativeTo(null);  // 윈도우 위치를 화면 중앙으로
		getContentPane().add(new JLabel("Initializing..."));  // 초기화 중이라는 메세지
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 창이 닫히면 프로그램 종료
		setVisible(true);
	}
	
	// 외부에서 호출되어 상태 화면을 추가하는 메소드
	public void addPanels(Class<? extends JPanel> panelClass) {
		try {
			panelMap.put(panelClass, panelClass.newInstance());  // 화면구성요소를 해쉬테이블에 추가
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	// 외부에서 호출되어 화면 상태를 갱신하는 메소드
	public void changeAndUpdate(Class<? extends JPanel> panelClass) {
		JPanel panel = panelMap.get(panelClass);
		if (!getContentPane().equals(panel)) {
			getContentPane().removeAll();
			getContentPane().add(panel);  // 현재 화면 구성과 입력이 다를경우 화면구성을 초기화하고 새로 작성
		}
		revalidate();
		repaint();
	}
	
	private static class SingletonHolder {
		private static ManagedFrame instance = new ManagedFrame();
	}
	
	public static ManagedFrame getInstance() {
		return SingletonHolder.instance;
	}
}
