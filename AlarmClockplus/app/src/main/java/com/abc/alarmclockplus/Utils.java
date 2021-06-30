package com.abc.alarmclockplus;

import java.util.ArrayList;
import java.util.List;

public class Utils
{
   public static List<Double> Linspace(double min, double max, int points)
   {
      List<Double> d = new ArrayList<>(points);

      for (int i = 0; i < points; i++)
         d.add(min + i * (max - min) / (points - 1));

      return d;
   }
}
