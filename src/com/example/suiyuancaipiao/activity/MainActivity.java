package com.example.suiyuancaipiao.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.suiyuancaipiao.R;
import com.example.suiyuancaipiao.bean.User;
import com.example.suiyuancaipiao.net.potocal.Message;
import com.example.suiyuancaipiao.net.potocal.impl.UserLoginElement;
import com.example.suiyuancaipiao.service.impl.UserServiceImpl;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// HttpClientUtil clientUtil = new HttpClientUtil();
				// clientUtil.sendXml("http://10.0.2.2:8080/ZCWService/Entrance",
				// new Message().getXml(new UserLoginElement()));
				UserServiceImpl impl = new UserServiceImpl();
				System.out.println("发送成功1！");
				Message message = impl.login(new User("111", "aaa"));
				System.out.println("发送成功2！");

				System.out.println(message.getXml(new UserLoginElement()));
			}
		})
		{

		}.start();
	}
}
