package com.iradetskiy.utility;

public class TimeUtility {
	public static String formatSeconds(String seconds) {
		
		int sec = Integer.parseInt(seconds);
		
		int min = sec / 60;
		sec = sec % 60;
		
		seconds = (sec > 9) ? "" : "0";
		seconds += sec;
		
		int hour = min / 60;
		min = min % 60;
		
		
		return (min == 0) ? "0:" + seconds : 
			(hour == 0) ? min + ":" + seconds : 
				(min > 9) ? hour + ":" + min + ":" + seconds : hour + ":0" + min + ":" + seconds;
	}
}
