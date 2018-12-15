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
	
	private ResourceManager resourceManager = ResourceManager.getInstance();
	private LoginManager loginManager = LoginManager.getInstance();
	
	private AccountManager() {}
	
	public boolean isExistingAccount(String account) {
		return resourceManager.getAccountFile(account) != null;
	}
	
	@SuppressWarnings("unchecked")
	public int createNewAccount(String account, String password, String passwordVerify) {
		if (isExistingAccount(account) || account.equals("")) {  // 계좌가 중복이면 반환
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
			System.out.println("개설성공");
			return CREATE_ACCOUNT_SUCCESS;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void linkAccountToUser(String account) {
		String currentUser = loginManager.getUser();
		File userFile = resourceManager.getUserFile(currentUser);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			JSONArray accountArray = (JSONArray)jsonObject.get("accounts");
			accountArray.add(account);
			
	
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	private static class SingletonHolder {
		private static AccountManager instance = new AccountManager();
	}
	
	public static AccountManager getInstance() {
		return SingletonHolder.instance;
	}
}
