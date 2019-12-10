package com.soft.railway.inspection.utils;

import android.text.TextUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    private static  String formatYM="yyyy-MM";
    private static  String formatLongString="yyyy-MM-dd HH:mm:ss";
    private static  String formatYearMonthDate="yyyy-MM-dd";
    private static String formatHourMinuteString="HH:mm";
    public static String getNewDateStringShow(){
        Date date=new Date();
        DateFormat format=new SimpleDateFormat(formatYM);
        return format.format(date);
    }

    public static String getNewDateShow(){
        Date date=new Date();
        DateFormat format=new SimpleDateFormat(formatLongString);
        return format.format(date);
    }

    public static String getNewDateYearMonth(){
        Date date=new Date();
        DateFormat format=new SimpleDateFormat(formatYM);
        return format.format(date);
    }

    public static String getNewDate(String str){
        String res="";
        if(!TextUtils.isEmpty(str)){
            if("null".equals(str)){
                return res;
            }
            res=str.substring(0,str.length()-3);
        }
        return res;
    }

    public static Date getDate(String data){
        DateFormat dateFormat=new SimpleDateFormat(formatYearMonthDate);
        Date date=null;
        try{
            date=dateFormat.parse(data);
        }catch (ParseException e){
            MyException myException=new MyException();
            myException.buildException(e);
        }
        return date;
    }

    public static String getDate(Date date){
        DateFormat dateFormat=new SimpleDateFormat(formatYearMonthDate);
        return dateFormat.format(date);
    }
    public static String getFormatHourMinuteDate(String str){
        String res="";
        if(!TextUtils.isEmpty(str)){
            if("null".equals(str)){
                return res;
            }
            DateFormat dateFormat=new SimpleDateFormat(formatHourMinuteString);
            res=dateFormat.format(str);
        }
        return res;
    }

    public static String getDateStringSearch(String str,int monthNumber){
        DateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,monthNumber);
        Date date1=calendar.getTime();
        return format.format(date1);
    }

    public static Date getDateFormString(String str){
        Date date = null;
        if(!TextUtils.isEmpty(str)){
            DateFormat dateFormat=new SimpleDateFormat(formatLongString);
            try {
                date= dateFormat.parse(str);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 比较时间是否在指定时间范围内
     * @param beforeMonth 开始时间
     * @param lastMonth  结束时间
     * @param date  时间
     * @return
     */
    public static Boolean inTimeFrame(Date beforeMonth,Date lastMonth,Date date){
        Log.d("inTimeFrame  "+getDate(beforeMonth)+"  "+getDate(lastMonth)+"   "+getDate(date));
        if(beforeMonth==null||lastMonth==null||date==null){
            return false;
        }
        Log.d(date.before(lastMonth)+"    beforeMonth"+date.after(beforeMonth));
        if(date.before(lastMonth)&&date.after(beforeMonth)){
            return true;
        }
        return false;
    }

    public static Date getMonthLastDay() {
        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,1);
        calendar.set(Calendar.DAY_OF_MONTH,0);
        //calendar.add(Calendar.DAY_OF_MONTH,0);
        Log.d("getMonthLastDay  "+getDate(calendar.getTime()));
        return calendar.getTime();
    }

    public static Date getMonthDay(int day) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,day);
        Log.d("getMonthDay  "+getDate(calendar.getTime()));
        return calendar.getTime();
    }

    public static String getMonth(int i) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH,i);
        int month=calendar.get(Calendar.MONTH)+1;
        if(month<10){
            return "0"+month;
        }
        return month+"";
    }

    public static Date getQuarterLastDay() {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int month=(calendar.get(Calendar.MONTH)+1);
        int numMonth=0;
        if(month>0&&month<=3){
            numMonth=1;
        }else if(month>3&&month<=6){
            numMonth=2;
        }else if(month>6&&month<=9){
            numMonth=3;
        }else if(month>9&&month<=12){
            numMonth=4;
        }
        calendar.set(Calendar.MONTH,numMonth*3);
        calendar.set(Calendar.DAY_OF_MONTH,0);
        return calendar.getTime();
    }

    public static Date getQuarterCanChangeDay() {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int month=(calendar.get(Calendar.MONTH)+1);
        int numMonth=0;
        if(month>0&&month<=3){
            numMonth=1;
        }else if(month>3&&month<=6){
            numMonth=4;
        }else if(month>6&&month<=9){
            numMonth=7;
        }else if(month>9&&month<=12){
            numMonth=10;
        }
        calendar.set(Calendar.MONTH,numMonth);
        calendar.set(Calendar.DAY_OF_MONTH,0);
        return calendar.getTime();
    }

    public static Date getQuarterDay(int day) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int month=(calendar.get(Calendar.MONTH)+1);
        int numMonth=0;
        if(month>0&&month<=3){
            numMonth=0;
        }else if(month>3&&month<=6){
            numMonth=1;
        }else if(month>6&&month<=9){
            numMonth=2;
        }else if(month>9&&month<=12){
            numMonth=3;
        }
        calendar.set(Calendar.MONTH,numMonth*3);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        Log.d("getQuarterDay  "+getDate(calendar.getTime()));
        return calendar.getTime();
    }

    public static String getMonth(){
        String month="";
        Calendar calendar=Calendar.getInstance();
        int num=calendar.get(Calendar.MONTH)+1;
        month=num<10?"0"+num:num+"";
        return month;
    }

    public static String getQuarterStartMonth(){
        String month="";
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int num=(calendar.get(Calendar.MONTH)+1)/3*3+1;
        month=num<10?"0"+num:num+"";
        return month;

    }

    public static String getQuarterEndMonth(){
        String month="";
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int num=(calendar.get(Calendar.MONTH)+1)/3*4;
        month=num<10?"0"+num:num+"";
        return month;
    }

    public static String getQuarterLastStartMonth(){
        String result="";
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int month=(calendar.get(Calendar.MONTH)+1);
        int numMonth=0;
        if(month>0&&month<=3){
            numMonth=-1;
        }else if(month>3&&month<=6){
            numMonth=0;
        }else if(month>6&&month<=9){
            numMonth=1;
        }else if(month>9&&month<=12){
            numMonth=2;
        }
        calendar.set(Calendar.MONTH,numMonth*3);
        result=(calendar.get(Calendar.MONTH)+1)+"";
        return result;
    }

    public static String getQuarterLastEndMonth(){
        String result="";
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int month=(calendar.get(Calendar.MONTH)+1);
        int numMonth=0;
        if(month>0&&month<=3){
            numMonth=-1;
        }else if(month>3&&month<=6){
            numMonth=0;
        }else if(month>6&&month<=9){
            numMonth=1;
        }else if(month>9&&month<=12){
            numMonth=2;
        }
        calendar.set(Calendar.MONTH,numMonth*3);
        result=(calendar.get(Calendar.MONTH)+3)+"";
        return result;

    }
    public static void main(String[]sr){
        System.out.println( getNewDateShow());
    }
}
