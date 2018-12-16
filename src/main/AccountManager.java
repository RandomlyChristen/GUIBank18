package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AccountManager {
	public final static int CREATE_ACCOUNT_SUCCESS = -1;
	public final static int CREATE_ACCOUNT_NOT_SIGNED = -2;
	public final static int CREATE_ACCOUNT_FAILURE = -3;
	
	public final static int TYPE_DEPOSIT = 0;
	public final static int TYPE_SAVING = 1;
	public final static int TYPE_LONE = 2;
	
	public final static int DEPOSIT_SUCCESS = -1;
	public final static int DEPOSIT_NOT_DEPOSABLE = -2;
	
	public final static int WITHDRAW_SUCCESS = -1;
	public final static int WITHDRAW_FAILURE = -2;
	
	public final static int TRANSTFER_SUCCESS = -1;
	public final static int TRANSTFER_FAILURE = -2;
	
	public final static int BORROW_SUCCESS = -1;
	public final static int BORROW_FAILURE = -2;
	
	public final static int REFUND_SUCCESS = -1;
	public final static int REFUND_LARGER_INPUT = -2;
	public final static int REFUND_LOW_BALANCE = -3;
	public final static int REFUND_FAILURE = -4;
	
	private double rateSaving = 0.02;
	private double rateLone = 0.03;
	
	private AccountManager() {}
	
	// 존재하는 계좌인지 확인하는 메소드
	public boolean isExistingAccount(String account, ResourceManager resourceManager) {
		return resourceManager.getAccountFile(account) != null;
	}
	
	// 계좌의 비밀번호를 확인하는 메소드
	public boolean isCorrectPassword(String password, String account, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			String passOfAccount = (String)jsonObject.get("password");  // 비밀번호를 가져옴
			return passOfAccount.equals(password);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// 계좌의 타입을 반환하는 메소드
	public int getTypeOfAccount(String account, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			long type = (long)jsonObject.get("type");  // 비밀번호를 가져옴
			return (int) type;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	// 계좌의 거래내역을 반환하는 메소드
	@SuppressWarnings("unchecked")
	public String[] getArrayOfTransactions(String account, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			JSONArray transactions = (JSONArray)jsonObject.get("transactions");  // 거래내역을 가져옴
			return (String[]) transactions.toArray(new String[transactions.size()]);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return new String[] {};
		}
	}
	
	// 입금 메소드
	@SuppressWarnings("unchecked")
	public int deposit(String account, long cash, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			JSONArray transactions = (JSONArray)jsonObject.get("transactions");  // 거래내역을 가져옴
			long balance = (long) jsonObject.get("balance"); balance += cash;
			jsonObject.put("balance", balance);  // 계좌에 변경된 값을 추가
			pushTransaction(transactions, "현금 입금 : " + cash);
			FileWriter fileWriter = new FileWriter(accountFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 추가된 데이터를 파일로 저장
			return DEPOSIT_SUCCESS;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return DEPOSIT_NOT_DEPOSABLE;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int borrow(String account, long cash, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			JSONArray transactions = (JSONArray)jsonObject.get("transactions");  // 거래내역을 가져옴
			long balance = (long) jsonObject.get("balance"); balance += cash;
			long liabilities = (long) jsonObject.get("liabilities"); liabilities += cash;  // 부채를 추가
			jsonObject.put("balance", balance); jsonObject.put("liabilities", liabilities); // 계좌에 변경된 값을 추가
			pushTransaction(transactions, "대출 : " + cash);
			FileWriter fileWriter = new FileWriter(accountFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 추가된 데이터를 파일로 저장
			return BORROW_SUCCESS;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return BORROW_FAILURE;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int refund(String account, long cash, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			JSONArray transactions = (JSONArray)jsonObject.get("transactions");  // 거래내역을 가져옴
			long liabilities = (long) jsonObject.get("liabilities"); 
			long balance = (long) jsonObject.get("balance");
			if (balance < cash) return REFUND_LOW_BALANCE;
			if (liabilities < cash) return REFUND_LARGER_INPUT;
			balance -= cash; liabilities -= cash;  // 부채를 상환
			jsonObject.put("balance", balance); jsonObject.put("liabilities", liabilities);  // 계좌에 변경된 값을 추가
			pushTransaction(transactions, "상환 : " + cash);
			FileWriter fileWriter = new FileWriter(accountFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 추가된 데이터를 파일로 저장
			return REFUND_SUCCESS;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return REFUND_FAILURE;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int withdraw(String account, long cash, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			JSONArray transactions = (JSONArray)jsonObject.get("transactions");  // 거래내역을 가져옴
			long balance = (long) jsonObject.get("balance");
			if (balance - cash < 0) return WITHDRAW_FAILURE;  // 잔액 부족
			balance -= cash;
			jsonObject.put("balance", balance);  // 계좌에 변경된 값을 추가
			pushTransaction(transactions, "현금 출금 : " + cash);
			FileWriter fileWriter = new FileWriter(accountFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 추가된 데이터를 파일로 저장
			return WITHDRAW_SUCCESS;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return WITHDRAW_FAILURE;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int transfer(String fromAccount, String toAccount, long amount, LoginManager loginManager, ResourceManager resourceManager) {
		File fromFile = resourceManager.getAccountFile(fromAccount);
		File toFile = resourceManager.getAccountFile(toAccount);
		
		try {
			JSONObject fromObject = (JSONObject)new JSONParser().parse(new FileReader(fromFile));
			long fromBalance = (long) fromObject.get("balance");
			
			long fees = 0;
			List<String> userAccounts = Arrays.asList(getAccountsOfUser(loginManager, resourceManager));
			if (!userAccounts.contains(toAccount)) fees += 1000;  // 타 계정 계좌로 보낼 때, 수수료
			
			if (fromBalance - (amount + fees) < 0) return TRANSTFER_FAILURE;  // 잔액 부족
			
			JSONObject toObject = (JSONObject)new JSONParser().parse(new FileReader(toFile));
			long toBalance = (long) toObject.get("balance");
			
			fromBalance -= amount; toBalance += amount;
			fromObject.put("balance", fromBalance); toObject.put("balance", toBalance);
			
			JSONArray fromTransactions = (JSONArray)fromObject.get("transactions");
			pushTransaction(fromTransactions, toAccount + "로 송금 : " + amount);
			JSONArray toTransactions = (JSONArray)toObject.get("transactions");
			pushTransaction(toTransactions, fromAccount + "로부터 입금 : " + amount);
			
			FileWriter fromWriter = new FileWriter(fromFile);
			fromWriter.write(fromObject.toJSONString());
			fromWriter.flush(); fromWriter.close();  // 추가된 데이터를 파일로 저장
			FileWriter toWriter = new FileWriter(toFile);
			toWriter.write(toObject.toJSONString());
			toWriter.flush(); toWriter.close();  // 추가된 데이터를 파일로 저장
			return TRANSTFER_SUCCESS;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return TRANSTFER_FAILURE;
		}
	}
	
	@SuppressWarnings("unchecked")  // 새로운 계좌를 생성하는 메소드
	public int createNewAccount(String account, String password, String passwordVerify, int accountType, LoginManager loginManager, ResourceManager resourceManager) {
		if (isExistingAccount(account, resourceManager) || account.equals(""))  // 계좌가 중복이면 반환, 발생시 논리오류
			return	CREATE_ACCOUNT_FAILURE;
		
		if (!password.equals(passwordVerify))  // 비밀번호와 비밀번호확인입력이 일치하지않으면 반환
			return CREATE_ACCOUNT_FAILURE;
		
		if (!loginManager.isSignedIn())  // 로그인 중이 아닐경우 반환
			return CREATE_ACCOUNT_NOT_SIGNED;
		
		JSONObject jsonObject = new JSONObject(); JSONArray jsonArray = new JSONArray();
		File newAccountFile = new File(ResourceManager.ACCOUNT_DATA_PATH + account + ".json");
		long time = new GregorianCalendar(Locale.KOREA).getTimeInMillis();
		jsonObject.put("password", password);
		jsonObject.put("type", accountType);
		jsonObject.put("lastTime", time);
		jsonObject.put("balance", 0); jsonObject.put("liabilities", 0);
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
	
	@SuppressWarnings("unchecked")
	public String[] getAccountsOfUser(LoginManager loginManager, ResourceManager resourceManager) {
		String currentUser = loginManager.getUser();
		File userFile = resourceManager.getUserFile(currentUser);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			JSONArray accountArray = (JSONArray)jsonObject.get("accounts");  // 파일로부터 계좌의 정보를 담는 배열객체를 받음
			return (String[])accountArray.toArray(new String[accountArray.size()]);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return new String[] {};
		}
	}
	
	// 한 유저의 모든 계좌를 조회하여 예금 정보를 리턴하는 메소드
	public long getBalanceOfUser(LoginManager loginManager, ResourceManager resourceManager) {
		long balanceSum = 0;
		String currentUser = loginManager.getUser();
		File userFile = resourceManager.getUserFile(currentUser);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			JSONArray accountArray = (JSONArray)jsonObject.get("accounts");  // 해당 유저의 계좌정보를 가져옴
			for (Object account : accountArray) {  // 각 계좌의 예금 정보를 더함
				balanceSum += getBalanceOfAccount((String)account, resourceManager);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return balanceSum;
	}
	
	// 한 계좌의 예금 정보를 리턴하는 메소드
	public long getBalanceOfAccount(String account, ResourceManager resourceManager) {
		long balance = 0;
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			balance = (long)jsonObject.get("balance");  // 해당 계좌의 잔액을 가져옴
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return balance;
	}
	
	// 한 유저의 모든 계좌를 조회하여 부채 정보를 리턴하는 메소드
	public long getliabilityOfUser(LoginManager loginManager, ResourceManager resourceManager) {
		long liabilitySum = 0;
		String currentUser = loginManager.getUser();
		File userFile = resourceManager.getUserFile(currentUser);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			JSONArray accountArray = (JSONArray)jsonObject.get("accounts");  // 해당 유저의 계좌정보를 가져옴
			for (Object account : accountArray) {  // 각 계좌의 부채 정보를 더함
				liabilitySum += getliabilityOfAccount((String)account, resourceManager);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return liabilitySum;
	}
	
	// 한 계좌의 부채 정보를 리턴하는 메소드
	public long getliabilityOfAccount(String account, ResourceManager resourceManager) {
		long liabilities = 0;
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();  // 입력 계좌로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			liabilities = (long)jsonObject.get("liabilities");  // 해당 계좌의 부채을 가져옴
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return liabilities;
	}
	
	// 모든 계좌정보로부터 이자를 추가하는 메소드
	public void makeInterestForAll (ResourceManager resourceManager) {
		for (String account : resourceManager.getAccounts()) {
			makeInterest(account, resourceManager);
		}
	}
	
	// 하나의 계좌를 대출과 적금을 구분하여 원금에 이자를 추가하는 메소드
	@SuppressWarnings("unchecked")
	public void makeInterest(String account, ResourceManager resourceManager) {
		File accountFile = resourceManager.getAccountFile(account);
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(accountFile));
			int type = ((Long)jsonObject.get("type")).intValue();
			long currentTime = new GregorianCalendar(Locale.KOREA).getTimeInMillis();
			long lastTime = (long)jsonObject.get("lastTime");
			
			if (type == TYPE_SAVING) {  // 계좌의 타입이 적금일 경우, 적금의 이자를 추가한다
				long principal = (long)jsonObject.get("balance");
				jsonObject.put("balance", getInterestedAmount(principal, rateSaving, lastTime, currentTime));
			}
			else if (type == TYPE_LONE) {  // 계좌의 타입이 대출일 경우, 대출의 이자를 추가한다
				long principal = (long)jsonObject.get("liabilities");
				jsonObject.put("liabilities", getInterestedAmount(principal, rateLone, lastTime, currentTime));
			}
			
			jsonObject.put("lastTime", currentTime);  // 이자 계산시 사용될 시간기록을 현재로 설정한다
			
			FileWriter fileWriter = new FileWriter(accountFile);
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush(); fileWriter.close();  // 추가된 데이터를 파일로 저장
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	// 원금에 이자를 추가하는 계산을 하는 메소드
	public long getInterestedAmount(long principal, double rate, long lastTime, long now) {
		long result = principal;
		GregorianCalendar currentDate = new GregorianCalendar(); GregorianCalendar lastDate = new GregorianCalendar();
		currentDate.setTimeInMillis(now); lastDate.setTimeInMillis(lastTime);
		int currentYear = currentDate.get(Calendar.YEAR); int lastYear = lastDate.get(Calendar.YEAR);
		int currentMonth = currentDate.get(Calendar.MONTH) + 1; int lastMonth = lastDate.get(Calendar.MONTH) + 1;
		// 개월 수 계산법, 예) (2018-12, 2020-5) -> (5 - 12) + (12 * (2020 - 2018))
		int monthlyDifference = (currentMonth - lastMonth) + (12 * (currentYear - lastYear)) + 12;  // 마지막 기록과 현재시간을 비교하여 개월 수를 구함
		for (int i = 0; i < monthlyDifference; i++) {
			result += result * rate;  // 1개월 마다 복리로 할당
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void pushTransaction(JSONArray transactions, String newTransaction) {
		if (transactions.size() > 10) transactions.remove(0);
		transactions.add(newTransaction);
	}
	
	private static class SingletonHolder {
		private static AccountManager instance = new AccountManager();
	}
	
	public static AccountManager getInstance() {
		return SingletonHolder.instance;
	}
}
