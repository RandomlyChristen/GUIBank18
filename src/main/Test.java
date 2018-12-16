package main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		long lastTime = new GregorianCalendar(2018, Calendar.MAY, 24).getTimeInMillis();
		long now = new GregorianCalendar(Locale.KOREA).getTimeInMillis();
		System.out.println(AccountManager.getInstance().getInterestedAmount(50000000, 0.02, lastTime, now));
	}

}
