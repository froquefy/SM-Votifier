package org.loovcik.smVotifierExt.utils;

import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time implements Comparable<Time> {
   private static final Pattern timePattern = Pattern.compile(
      "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
      2
   );
   private static final int maxYears = 100000;
   private static final String[] SHORT_IDENTIFIERS = new String[]{"y", "mo", "d", "h", "m", "s"};
   private final Duration internal;

   public static Time of(String from) {
      return new Time(from);
   }

   public static Time of(Duration duration) {
      return new Time(duration);
   }

   public static Time ofMillis(Long milliseconds) {
      return new Time(milliseconds);
   }

   public static Time ofSeconds(Long seconds) {
      return new Time(Duration.ofSeconds(seconds));
   }

   public static Time zero() {
      return new Time(Duration.ZERO);
   }

   public boolean isNegative() {
      return this.internal.isNegative();
   }

   public Long toMilliseconds() {
      return this.internal.toMillis();
   }

   public Long toTicks() {
      return Math.max(1L, this.internal.toMillis() / 50L);
   }

   public Long toSeconds() {
      return this.internal.toSeconds();
   }

   public Duration toDuration() {
      return this.internal;
   }

   public boolean isZero() {
      return this.internal.isZero();
   }

   public int compareTo(Time other) {
      return this.internal.compareTo(other.internal);
   }

   public String format(boolean forceHours) {
      Duration duration = this.toDuration();
      if (duration.toDays() > 0L) {
         return String.format("%dd %02d:%02d:%02d", duration.toDays(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
      } else {
         return duration.toHours() <= 0L && !forceHours
            ? String.format("%02d:%02d", duration.toMinutes(), duration.toSecondsPart())
            : String.format("%d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
      }
   }

   public String formatDate() {
      Date date = Date.from(Instant.ofEpochMilli(this.toMilliseconds()));
      return DateFormat.getDateTimeInstance().format(date);
   }

   public String format() {
      return this.format(false);
   }

   public Time add(long value, ChronoUnit units) {
      Duration duration = this.toDuration().plus(value, units);
      return of(duration);
   }

   private Long parseTime(String value) {
      Matcher m = timePattern.matcher(value);
      int years = 0;
      int months = 0;
      int weeks = 0;
      int days = 0;
      int hours = 0;
      int minutes = 0;
      int seconds = 0;
      boolean found = false;

      while (m.find()) {
         if (m.group() != null && !m.group().isEmpty()) {
            for (int i = 0; i < m.groupCount(); i++) {
               if (m.group(i) != null && !m.group(i).isEmpty()) {
                  found = true;
                  break;
               }
            }

            if (found) {
               if (m.group(1) != null && !m.group(1).isEmpty()) {
                  years = Integer.parseInt(m.group(1));
               }

               if (m.group(2) != null && !m.group(2).isEmpty()) {
                  months = Integer.parseInt(m.group(2));
               }

               if (m.group(3) != null && !m.group(3).isEmpty()) {
                  weeks = Integer.parseInt(m.group(3));
               }

               if (m.group(4) != null && !m.group(4).isEmpty()) {
                  days = Integer.parseInt(m.group(4));
               }

               if (m.group(5) != null && !m.group(5).isEmpty()) {
                  hours = Integer.parseInt(m.group(5));
               }

               if (m.group(6) != null && !m.group(6).isEmpty()) {
                  minutes = Integer.parseInt(m.group(6));
               }

               if (m.group(7) != null && !m.group(7).isEmpty()) {
                  seconds = Integer.parseInt(m.group(7));
               }
               break;
            }
         }
      }

      if (!found) {
         return 0L;
      } else {
         Calendar c = new GregorianCalendar();
         c.setTimeInMillis(0L);
         if (years > 0) {
            if (years > 100000) {
               years = 100000;
            }

            c.add(1, years);
         }

         if (months > 0) {
            c.add(2, months);
         }

         if (weeks > 0) {
            c.add(3, weeks);
         }

         if (days > 0) {
            c.add(5, days);
         }

         if (hours > 0) {
            c.add(11, hours);
         }

         if (minutes > 0) {
            c.add(12, minutes);
         }

         if (seconds > 0) {
            c.add(13, seconds);
         }

         Calendar max = new GregorianCalendar();
         max.add(1, 10);
         return c.after(max) ? max.getTimeInMillis() : c.getTimeInMillis();
      }
   }

   private Time(String from) {
      long millis;
      if (StringExt.containsAny(from, SHORT_IDENTIFIERS)) {
         millis = this.parseTime(from);
      } else {
         Pattern pattern = Pattern.compile("[a-zA-Z]");
         Matcher matcher = pattern.matcher(from);
         from = matcher.replaceAll("").trim();

         try {
            millis = Long.parseLong(from);
         } catch (Exception var7) {
            millis = 0L;
         }
      }

      this.internal = Duration.ofMillis(millis);
   }

   private Time(Long milliseconds) {
      if (milliseconds == null) {
         this.internal = Duration.ZERO;
      } else {
         this.internal = Duration.ofMillis(milliseconds);
      }
   }

   private Time(Duration duration) {
      if (duration == null) {
         this.internal = Duration.ZERO;
      } else {
         this.internal = Duration.of(duration.toMillis(), ChronoUnit.MILLIS);
      }
   }
}
