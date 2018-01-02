package tech.bbwang.www.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式类
 * @author wang-bingbing
 *
 */
public class DateUtil {
	
	public 	static Calendar			calendar 				= Calendar.getInstance();
	public 	static SimpleDateFormat	sdf_yyyy_MM_HH	            = new SimpleDateFormat( "yyyyMMdd_HH", Locale.getDefault( ) );
	public 	static SimpleDateFormat	sdf_yyyy_MM_dd_HH_mm	= new SimpleDateFormat( "yyyyMMdd_HH_mm", Locale.getDefault( ) );
	public 	static SimpleDateFormat	sdf_yyyyMMddHHmmssSSS	= new SimpleDateFormat( "yyyyMMddHHmmssSSS", Locale.getDefault( ) );
	public 	static SimpleDateFormat	sdf_yyyyMMddHHmmss		= new SimpleDateFormat( "yyyyMMddHHmmss", Locale.getDefault( ) );
	public 	static SimpleDateFormat	sdf_yyyy_MM_dd_HH_mm_ss		= new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.getDefault( ) );
	public	static SimpleDateFormat sdf_HHmmss 				= new SimpleDateFormat("HH:mm:ss", Locale.CHINESE);
	
	public static String getDateTime(SimpleDateFormat sdf) {
	
		return sdf.format(new Date());
	}
	
	public static String getDateTime(SimpleDateFormat sdf,Date date) {
		
		return sdf.format(date);
	}
	
	public static Date getDate(SimpleDateFormat sdf,String time){
		try
		{
			return sdf.parse( time );
		} catch ( ParseException e )
		{
			return null;
		}
	}
	
	public static Date getDate(Date date,long mistiming){
		calendar.setTime( date );
		calendar.add( Calendar.MILLISECOND, (int) mistiming );
		return calendar.getTime( );
	}
	
	public static Date getDate( int year, int month, int day, int hourOfDay, int minute ){
		month-=1;
		calendar.set( year, month, day, hourOfDay, minute );
		return calendar.getTime( );
	}
	
    /**
     * 获得几天之前或者几天之后的日期
     * @param diff 差值：正的往后推，负的往前推
     * @return
     */
    public static String getOtherDay(int diff) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.DATE, diff);
        return getDateFormat(mCalendar.getTime());
    }
    
    /**
     * 将date转成yyyy-MM-dd字符串<br>
     * @param date Date对象
     * @return yyyy-MM-dd
     */
    public static String getDateFormat(Date date) {
        return dateSimpleFormat(date, defaultDateFormat.get());
    }
    
    /**
     * 将date转成字符串
     * @param date Date
     * @param format SimpleDateFormat
     * <br>
     * 注： SimpleDateFormat为空时，采用默认的yyyy-MM-dd HH:mm:ss格式
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String dateSimpleFormat(Date date, SimpleDateFormat format) {
        if (format == null)
            format = defaultDateTimeFormat.get();
        return (date == null ? "" : format.format(date));
    }
    
    /** yyyy-MM-dd格式 */
    public static final ThreadLocal<SimpleDateFormat> defaultDateFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_FORMAT_DATE);
        }

    };

    /** yyyy-MM-dd HH:mm:ss格式 */
    public static final ThreadLocal<SimpleDateFormat> defaultDateTimeFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        }

    };
    
    /** yyyy-MM-dd HH:mm:ss字符串 */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** yyyy-MM-dd字符串 */
    public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";
}


