package main;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Test {
	public static void main(String[] args) {
		long lastTime = new GregorianCalendar(2018, Calendar.MAY, 24).getTimeInMillis();
		long now = new GregorianCalendar(Locale.KOREA).getTimeInMillis();
		System.out.println(AccountManager.getInstance().getInterestedAmount(50000000, 0.02, lastTime, now));
	}

}
