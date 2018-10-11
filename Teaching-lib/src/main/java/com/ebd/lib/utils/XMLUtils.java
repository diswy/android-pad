package com.ebd.lib.utils;

import android.util.Xml;

import com.ebd.lib.bean.Book;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class XMLUtils {

    public void parseXml(String path, String fileName) {
        try {
            File mFile = new File(path, fileName);
            FileInputStream fis = new FileInputStream(mFile);
            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型


            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();// 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        System.out.println("----->>>>>TAG:" + tagName);
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;

                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }


    public String getXmlToJson(String path) {
        String ss = "";
        try {
            File mFile = new File(path);
            if (!mFile.exists())
                return ss;
            FileInputStream fis = new FileInputStream(mFile);
            XmlToJson xmlToJson = new XmlToJson.Builder(fis,null).build();
            ss = xmlToJson.toString().replace("\\n","")
                                .replace("\\t","");
            Logger.json(ss);
            Logger.wtf(ss);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return ss;
    }
}
