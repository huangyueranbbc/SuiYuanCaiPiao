package com.example.suiyuancaipiao.test;

import android.test.AndroidTestCase;

import com.example.suiyuancaipiao.net.HttpClientUtil;
import com.example.suiyuancaipiao.net.potocal.Message;
import com.example.suiyuancaipiao.net.potocal.impl.UserLoginElement;

public class NetTest extends AndroidTestCase
{
	public void testNetType()
	{
		// NetUtil.checkNet(getContext
	}

	public void testNet()
	{
		HttpClientUtil clientUtil = new HttpClientUtil();
		clientUtil.sendXml("http://10.0.2.2:8080/ZCWService/Entrance", new Message().getXml(new UserLoginElement()));
	}

}
