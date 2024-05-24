package com.timaimee.vpdemo.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.bean.TimeZoneBean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    private static final String TAG_ROOT = "TimeZoneArray";
    private static final String TAG_TIMEZONE = "TimeZoneBean";

    /**
     * 解析xml配置文件成升级设备配置信息
     */
    public static List<TimeZoneBean> parseXml2TimeZoneBeanList(Context context, int xmlFile) {
        List<TimeZoneBean> timeZoneBeanList = new ArrayList<>();
        XmlResourceParser xmlParser = context.getResources().getXml(xmlFile);
        Logger.d("----------------------开始解析了 xmlParser = " + xmlParser.getName());
        try {
            int type = xmlParser.getEventType();
            Logger.d("----------------------开始解析了 type = " + getEventType(type));
            while (type != XmlPullParser.END_DOCUMENT) {
                type = xmlParser.next();
                Logger.d("Start--------------------- name = " + xmlParser.getName() + " type = " + getEventType(type));
                if (type == XmlPullParser.START_TAG) {
                    switch (xmlParser.getName()) {
                        case TAG_ROOT:
                            Logger.d("ROOT--------------------- name = " + xmlParser.getName() + " type = " + getEventType(type));
                            //开启下一个循环-->
                            while (!(xmlParser.getEventType() == XmlPullParser.END_TAG && xmlParser.getName().equals(TAG_ROOT))) {
                                type = xmlParser.nextTag();
                                if (xmlParser.getName().equals(TAG_TIMEZONE) && xmlParser.getEventType() == XmlPullParser.START_TAG) {
                                    /*
                                     *  <TimeZoneBean
                                     *      abbreviation="GMT"
                                     *      cityName="阿比让"
                                     *      originalTimeZoneName="Africa/Abidjan"
                                     *      shortGenericTimeZoneName="格林尼治标准时间" />
                                     */
                                    String abbreviation = xmlParser.getAttributeValue(null, "abbreviation");
                                    String cityName = xmlParser.getAttributeValue(null, "cityName");
                                    String originalTimeZoneName = xmlParser.getAttributeValue(null, "originalTimeZoneName");
                                    String shortGenericTimeZoneName = xmlParser.getAttributeValue(null, "shortGenericTimeZoneName");

                                    TimeZoneBean timeZoneBean = new TimeZoneBean(
                                            abbreviation,
                                            cityName,
                                            originalTimeZoneName,
                                            shortGenericTimeZoneName
                                    );
                                    timeZoneBeanList.add(timeZoneBean);
                                } else {
                                    continue;
                                }
                            }
                            break;
                    }
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            Logger.e("===========================> " + e.getMessage());
        } finally {
            xmlParser.close();
        }

        return timeZoneBeanList;
    }

    private static String getEventType(int eventType) {
        switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                return "START_DOCUMENT";
            case XmlPullParser.END_DOCUMENT:
                return "END_DOCUMENT";
            case XmlPullParser.START_TAG:
                return "START_TAG";
            case XmlPullParser.END_TAG:
                return "END_TAG";
            case XmlPullParser.TEXT:
                return "TEXT";
            default:
                return "UNKNOWN";
        }
    }
}
