package cn.edu.gdaib.zxingdemo.utils;

import java.util.Calendar;

/**
 * 通用工具类
 */
public class CommonUtil {
    /*
     * 获取系统日期时间
     * @return 返回指定格式的系统时间日期
     */
    public static String getSystemTime(){
        //利用Calendar获取系统时间
        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH)+1;
        int day=c.get(Calendar.DAY_OF_MONTH);
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        String temp_month,temp_day;

        if(month<10){
            temp_month="0"+month;
        }else{
            temp_month=month+"";
        }
        if(day<10){
            temp_day="0"+day;
        }else{
            temp_day=day+"";
        }

        return year+"-"+temp_month+"-"+temp_day+" "+hour+":"+minute;
    }

}
