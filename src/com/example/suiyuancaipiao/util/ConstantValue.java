package com.example.suiyuancaipiao.util;

public class ConstantValue
{
	public static final String ENCONDING = "UTF-8";

	public static final String AGENTERID = "864851"; // 代理ID

	public static final String SOURCE = "ivr"; // 信息来源

	public static final String COMPRESS = "DES"; // body里面的数据加密算法

	public static final String AGENTER_PASSWORD = "9ab62a694d8bf6ced1fab6acd48d02f8"; // 子代理商的密钥(.so)JNI

	public static final String DES_PASSWORD = "9b2648fcdfbad80f"; // des加密用密钥

	/**
	 * 服务器地址
	 */
	public static final String LOTTERY_URI = "http://10.0.2.2:8080/ZCWService/Entrance";// 10.0.2.2模拟器如果需要跟PC机通信127.0.0.1
	// String LOTTERY_URI = "http://192.168.1.100:8080/ZCWService/Entrance";//
	// 10.0.2.2模拟器如果需要跟PC机通信127.0.0.1

	/**
	 * XmlPullParser parser = Xml.newPullParser(); try { parser.setInput(is,
	 * ConstantValue.ENCONDING);
	 * 
	 * int eventType = parser.getEventType(); String name;
	 * 
	 * while (eventType != XmlPullParser.END_DOCUMENT) { switch (eventType) {
	 * case XmlPullParser.START_TAG: name = parser.getName(); if
	 * ("".equals(name)) {
	 * 
	 * } break; } eventType = parser.next(); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 */
}
