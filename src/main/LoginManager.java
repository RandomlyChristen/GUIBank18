package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import display.StartPanel;

public class LoginManager {
	public final static int LOGIN_SUCCESS = -1;
	public final static int LOGIN_ID_ERROR = -2;
	public final static int LOGIN_PW_ERROR = -3;
	public final static int REGIST_SUCCESS = -4;
	public final static int REGIST_ID_ERROR = -5;
	public final static int REGIST_PW_ERROR = -6;
	
	// 로그인 상태를 확인하기 위한 필드, 한번에 한번의 로그인 정보만 받을 수 있음
	private boolean	signedIn = false;
	private String user = "";
	private int timeLimit = 10;
	
	private LoginManager() {}
	
	public int login(String user, String password, ResourceManager resourceManager, ManagedFrame managedFrame) {
		if (isSignedIn()) {  // 이미 로그인 되어있으면 반환
			System.out.println("이미 로그인 되어 있음!");
			return LOGIN_ID_ERROR;
		}
		if (!isExistingUser(user, resourceManager) || (user.equals(""))) { // 입력으로부터 값을 찾을 수 없거나, 입력이 비어있으면 반환
			System.out.println("아이디를 확인하세요!");
			return LOGIN_ID_ERROR;
		}
		
		File userFile = resourceManager.getUserFile(user);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			String pwData = (String) jsonObject.get("password");
			
			if (pwData.equals(password)) {  // 입력과 비밀번호가 일치하는지 확인
				this.user = user; this.signedIn = true;  // 입력으로부터 로그인 성공, 현재상태 -> 로그인됨
				System.out.println("로그인성공!"); startTimer(managedFrame); // 30초내에 아무런 행동이 없으면 로그아
				return LOGIN_SUCCESS;
			} else {
				System.out.println("비밀번호틀림!");
				return LOGIN_PW_ERROR;
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int register(String user, String password, String pwVerify, ResourceManager resourceManager) {
		if (isExistingUser(user, resourceManager) || user.equals("")) {  // 아이디가 중복이면 반환
			System.out.println("아이디를 확인하세요!");
			return	REGIST_ID_ERROR;
		}
		
		if (!password.equals(pwVerify)) {  // 비밀번호와 비밀번호확인입력이 일치하지않으면 반환
			System.out.println("비밀번호를 확인하세요!");
			return REGIST_PW_ERROR;
		}
		
		JSONObject jsonObject = new JSONObject(); JSONArray jsonArray = new JSONArray();
		File newUserFile = new File(ResourceManager.USER_DATA_PATH + user + ".json");
		jsonObject.put("password", password);
		jsonObject.put("accounts", jsonArray);  // 새로운 유저에 대한 데이터를 생성
		
		try {
			FileWriter fileWriter = new FileWriter(newUserFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 새로운 유저데이터를 파일에 작성
			resourceManager.userFileAdded(newUserFile);  // 새로운 파일데이터를 리소스매니저에 등록
			System.out.println("가입성공");
			return REGIST_SUCCESS;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void resetTimer() {  // 로그아웃 타임아웃을 초기화
		timeLimit = 30;
	}
	
	private void startTimer(ManagedFrame managedFrame) {  // 30초간 입력이 없으면 로그아웃을 하는 메소드
		resetTimer();  // 시작 전 초기화
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (timeLimit > 0) {  // 제한시간이 0 이하가 되면 탈출
					try {
						Thread.sleep(1000);
						timeLimit -= 1;  // 1초마다 제한시간 감소
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				managedFrame.changeAndUpdate(StartPanel.class);  // 제한시간이 완료되면 시작화면으로 이동
				signedIn = false;  // 로그인 해제
			}
		}).start();
	}
	
	public boolean isSignedIn() {
		return signedIn;
	}
	
	public String getUser() {
		return user;
	}
	
	public boolean isExistingUser(String user, ResourceManager resourceManager) {
		return resourceManager.getUserFile(user) != null;
	}
	
	private static class SingletonHolder {
		private static LoginManager instance = new LoginManager();
	}
	
	public static LoginManager getInstance() {
		return SingletonHolder.instance;
	}
}
