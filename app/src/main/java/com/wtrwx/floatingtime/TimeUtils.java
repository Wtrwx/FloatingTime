package com.wtrwx.floatingtime;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static final String THIRTEEN_STRING_DATE = "HH:mm:ss.S";
    /**
     * 13位时间戳转 String 格式(yyyy-MM-dd HH:mm:ss)日期
     *
     * @param timestamp
     * @return
     */
    public static String unix2String(String timestamp) {
        String date;
        date = new SimpleDateFormat(THIRTEEN_STRING_DATE).format(Long.parseLong(timestamp));
        return date;
    }

    public static final String TIME_SERVER = "ntp.aliyun.com";
    public static long getCurrentNetworkTime() throws IOException {
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        //long localDeviceTime = timeInfo.getReturnTime();
        long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        Date time = new Date(serverTime);
        return serverTime;
    }

}
