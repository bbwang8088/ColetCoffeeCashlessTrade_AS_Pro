package tech.bbwang.www.util;

import java.util.Locale;

public class ByteUtil {

    private final static char[] mChars = "0123456789ABCDEF".toCharArray();  
//    private final static String mHexStr = "0123456789ABCDEF";  
	/** 
     * bytes转换成十六进制字符串 
     * @param b byte[] byte数组 
     * @param iLen int 取前N位处理 N=iLen 
     * @return String 每个Byte值之间空格分隔 
     */  
    public static String byte2HexStr(byte[] b, int iLen){  
        StringBuilder sb = new StringBuilder();  
        for (int n=0; n<iLen; n++){  
            sb.append(mChars[(b[n] & 0xFF) >> 4]);  
            sb.append(mChars[b[n] & 0x0F]);  
            sb.append(' ');  
        }  
        return sb.toString().trim().toUpperCase(Locale.US);  
    }  

}
