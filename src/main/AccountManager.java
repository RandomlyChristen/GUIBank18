package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AccountManager {
	public final static int CREATE_ACCOUNT_SUCCESS = -1;
	public final static int CREATE_ACCOUNT_FAILURE = -2;
	
	private AccountManager() {}
	
	// 존재하는 계좌인지 확인하는 메소드
	public boolean isExistingAccount(String account, ResourceManager resourceManager) {
		return resourceManager.getAccountFile(account) != null;
	}
	
	@SuppressWarnings("unchecked")  // 새로운 계좌를 생성하는 메소드
	public int createNewAccount(String account, String password, String passwordVerify, LoginManager loginManager, ResourceManager resourceManager) {
		if (isExistingAccount(account, resourceManager) || account.equals("")) {  // 계좌가 중복이면 반환
			System.out.println("계좌를 확인하세요!");
			return	CREATE_ACCOUNT_FAILURE;
		}
		
		if (!password.equals(passwordVerify)) {  // 비밀번호와 비밀번호확인입력이 일치하지않으면 반환
			System.out.println("비밀번호를 확인하세요!");
			return CREATE_ACCOUNT_FAILURE;
		}
		
		JSONObject jsonObject = new JSONObject(); JSONArray jsonArray = new JSONArray();
		File newAccountFile = new File(ResourceManager.ACCOUNT_DATA_PATH + account + ".json");
		jsonObject.put("password", password);
		jsonObject.put("balance", 0);
		jsonObject.put("liabilities", 0);
		jsonObject.put("transactions", jsonArray);  // 새로운 계좌에 대한 데이터를 생성
		
		try {
			FileWriter fileWriter = new FileWriter(newAccountFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 새로운 유저데이터를 파일에 작성
			resourceManager.accountFileAdded(newAccountFile);  // 새로운 파일데이터를 리소스매니저에 등록
			linkAccountToUser(account, loginManager, resourceManager);  // 현재 로그인된 계정에 계좌를 추가
			System.out.println("개설성공");
			return CREATE_ACCOUNT_SUCCESS;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")  // 계좌를 유저데이터에 연결시켜, 계좌의 소유자를 명시해주는 메소드
	public void linkAccountToUser(String account, LoginManager loginManager, ResourceManager resourceManager) {
		String currentUser = loginManager.getUser();
		File userFile = resourceManager.getUserFile(currentUser);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			JSONArray accountArray = (JSONArray)jsonObject.get("accounts");  // 파일로부터 계좌의 정보를 담는 배열객체를 받음
			accountArray.add(account);  // 계좌 배열에 새로운 계좌를 추가
			
			FileWriter fileWriter = new FileWriter(userFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 추가된 데이터를 파일로 저장
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	// 한 유저의 모든 계좌를 조회하여 예금 정보를 리턴하는 메소드
	public int getBalanceOfUser(LoginManager loginManager, ResourceManager resourceManager) {
		int balanceSum = 0;
		String currentUser = loginManager.getUser();
		File userFile = resourceManager.getUserFile(currentUser);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			JSONArray accountArray = (JSONArray)jsonObject.get("accounts");
			for (Object account : accountArray) {
				balanceSum += getBalanceOfAccount((String)account, resourceManager);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return balanceSum;
	}
	
	// 한 계좌의 예금 정보를 리턴하는 메소드
	public int getBalanceOfAccount(String account, ResourceManager resourceManager) {
		int balance = 0;
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			balance = ((Long)jsonObject.get("balance")).intValue();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return balance;
	}
	
	private static class SingletonHolder {
		private static AccountManager instance = new AccountManager();
	}
	
	public static AccountManager getInstance() {
		return SingletonHolder.instance;
	}
}
