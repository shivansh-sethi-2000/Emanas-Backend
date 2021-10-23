package com.ehrc.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonFunction {

	 public static String getTime()
	 {
		  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
		   LocalDateTime now = LocalDateTime.now();
		   String date=dtf.format(now);
		   return date; 
	 }	
	
}
