package main;

import java.io.File;
import java.util.Hashtable;
import java.util.Set;

public class ResourceManager {
	// 계좌, 유저, 이미지, 효과 등 파일리소스를 관리하기 위한 상수
	public final static String ACCOUNT_DATA_PATH = getProgramFilePath(".guibankdata/data/accounts/");
	public final static String USER_DATA_PATH = getProgramFilePath(".guibankdata/data/users/");
	public final static String IMAGE_PATH = getProgramFilePath(".guibankdata/res/image/");
	public final static String SOUND_PATH = getProgramFilePath(".guibankdata/res/sound/");
	
	// 각각의 데이터들을 보관하기위한 해쉬테이블
	private Hashtable<String, File> accountMap = new Hashtable<>();
	private Hashtable<String, File> userMap = new Hashtable<>();
	private Hashtable<String, File> imageMap = new Hashtable<>();
	private Hashtable<String, File> soundMap = new Hashtable<>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResourceManager() {  // 런타임 중 단 한번만 동작하기 때문에, 이후 데이터 추가에 대한 메소드 필요!!
		String[] cases = { ACCOUNT_DATA_PATH, USER_DATA_PATH, IMAGE_PATH, SOUND_PATH };
		Hashtable[] datas = { accountMap, userMap, imageMap, soundMap };
		String[] types = { ".json", ".json", ".png", ".mp3" };  // 리펙토링 필요... 누가 이딴식으로 하래
		
		for (int i = 0; i < cases.length; i++) {
			File pathDir = new File(cases[i]);
			if (!pathDir.exists()) pathDir.mkdirs(); // 각각의 데이터를 포함하는 디렉토리가 존재하지 않으면 생성
			
			File[] fileList = pathDir.listFiles();
			for (File file : fileList) {  // 디렉토리의 각각의 파일들의 정보를 받아 해쉬테이블에 입력
				if (file.getName().endsWith(types[i])) {  // 확장자를 확인하고, 허가되는 확장자만 데이터로 입력
					datas[i].put(file.getName().replace(types[i], ""), file);  // example.mp3 -> example
				}
			}
		}
	}
	
	public Set<String> getAccounts() {
		return accountMap.keySet();
	}
	
	public void accountFileAdded(File file) {
		accountMap.put(file.getName().replace(".json", ""), file);
	}
	
	public File getAccountFile(String filename) {
		return accountMap.get(filename);
	}
	
	public void userFileAdded(File file) {
		userMap.put(file.getName().replace(".json", ""), file);
	}
	
	public File getUserFile(String filename) {
		return userMap.get(filename);
	}
	
	public File getImageFile(String filename) {
		return imageMap.get(filename);
	}
	
	public File getSoundFile(String filename) {
		return soundMap.get(filename);
	}
	
	public static String getProgramFilePath(String relative) {
		File programFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String programFilePath = programFile.getAbsolutePath();
		return programFilePath.replace(programFile.getName(), relative);
	}
	
	private static class SingletonHolder {
		private static ResourceManager instance = new ResourceManager();
	}
	
	public static ResourceManager getInstance() {
		return SingletonHolder.instance;
	}

}
