package com.example.suiyuancaipiao.test;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.test.AndroidTestCase;
import android.util.Log;
import android.util.Xml;

import com.example.suiyuancaipiao.bean.User;
import com.example.suiyuancaipiao.net.potocal.Message;
import com.example.suiyuancaipiao.net.potocal.impl.UserLoginElement;
import com.example.suiyuancaipiao.service.UserService;
import com.example.suiyuancaipiao.util.BeanFactory;
import com.example.suiyuancaipiao.util.ConstantValue;

public class XmlTest extends AndroidTestCase
{

	public void createXML1()
	{
		Message message = new Message();
		String xml = message.getXml(new UserLoginElement());

		Log.i("xml", xml);
	}

	public void createXML2()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument(ConstantValue.ENCONDING, null);
			Message message = new Message();
			message.serializerMessage(serializer);
			// message.getXml();

			serializer.endDocument();
		} catch (IllegalArgumentException | IllegalStateException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testUserLogin()
	{
		// UserServiceImpl impl = new UserServiceImpl();
		// User user = new User();
		// user.setUsername("13200000000");
		// user.setPassword("0000000");
		// Message login = impl.login(user);
		// Log.i("main", login.getBody().getOelement().getErrorcode());
		UserService engine = BeanFactory.getImpl(UserService.class);
		User user = new User();
		user.setUsername("13200000000");
		user.setPassword("0000000");
		Message login = engine.login(user);
		Log.i("main", login.getBody().getOelement().getErrorcode() + login.getBody().getOelement().getErrormsg());
	}
}
