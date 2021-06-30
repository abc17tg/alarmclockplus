package com.abc.alarmclockplus;

import java.util.ArrayList;
import java.util.List;

public class TimeRelatedUtils
{
   public static int ConvertStringTimeToIntegerSeconds(String time)
   {
      String[] hourMin = time.split(":");
      return Integer.parseInt(hourMin[0]) * 3600 + Integer.parseInt(hourMin[1]) * 60;
   }

   public static int MonitorNumberOfSecondsNotExceed24h(int seconds)
   {
      int oneDayinSeconds = 24*3600;
      if(seconds > oneDayinSeconds)
         seconds -= oneDayinSeconds;
      return seconds;
   }

   public static List<Integer> ListOfEvenlySpacedHours(int startTimeInSeconds, int endTimeInSeconds, int numberOfAlarms)
   {
      List<Double> TimeListD = Utils.Linspace(startTimeInSeconds, endTimeInSeconds, numberOfAlarms);
      List<Integer> TimeList = new ArrayList<>();

      for(double TL : TimeListD)
      {
         int temp = (int) Math.round(TL);
         TimeList.add(temp - temp % 60);
      }

      return TimeList;
   }

}
