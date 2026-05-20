package org.loovcik.smVotifierExt.utils;

import java.util.List;

public class StringExt {
   public static String listToString(List<String> list) {
      StringBuilder result = new StringBuilder();
      int i = 1;

      for (String text : list) {
         result.append(text);
         if (list.size() > i) {
            result.append(", ");
         }

         i++;
      }

      return result.toString();
   }

   public static boolean containsAny(String text, String[] patterns) {
      for (String pattern : patterns) {
         if (text.contains(pattern)) {
            return true;
         }
      }

      return false;
   }

   public static boolean containsAny(String text, List<String> patterns) {
      return containsAny(text, patterns.toArray(new String[0]));
   }
}
