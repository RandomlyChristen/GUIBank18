package main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String currentUser = "test";
		File userFile = ResourceManager.getInstance().getUserFile(currentUser);
		JSONParser jsonParser = new JSONParser();  // 입력 아이디로부터 값에 해당하는 파일을 파싱
		try {
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(userFile));
			JSONArray accountArray = (JSONArray)jsonObject.get("accounts");
			accountArray.add("9185135");
			System.out.println(jsonObject.toJSONString());
	
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}
