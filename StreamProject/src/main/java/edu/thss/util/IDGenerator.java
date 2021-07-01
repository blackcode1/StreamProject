package edu.thss.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IDGenerator {
	
	private static long preTime;
	private static long currentTime;
	
	public static synchronized String getPltID(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSS");
		currentTime = Long.parseLong(sdf.format(new Date()));
		
		if(currentTime <= preTime ){
			currentTime = preTime;
			currentTime += 1;
		}
		
		preTime = currentTime;
		
		String plt_id = "xy" + String.valueOf(currentTime);
		return plt_id;
	}

}
