package com.example.suiyuancaipiao.service.impl;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.example.suiyuancaipiao.bean.User;
import com.example.suiyuancaipiao.net.potocal.Message;
import com.example.suiyuancaipiao.net.potocal.impl.UserLoginElement;
import com.example.suiyuancaipiao.service.BaseService;
import com.example.suiyuancaipiao.service.UserService;
import com.example.suiyuancaipiao.util.ConstantValue;
import com.example.suiyuancaipiao.util.DES;

public class UserServiceImpl extends BaseService implements UserService
{

	/**
	 * 用户登录
	 */
	public Message login(User user)
	{
		// 获取登录的xml
		UserLoginElement element = new UserLoginElement();
		element.getActpassword().setTagValue(user.getPassword());
		Message message = new Message();
		message.getHeader().getUsername().setTagValue(user.getUsername());
		String xml = message.getXml(element);

		// 如果验证通过 会返回一个result 否则返回为null
		Message result = getResult(xml);
		if (result != null)
		{

			// 第四步 请求结果的数据处理
			XmlPullParser parser = Xml.newPullParser();
			try
			{
				DES des = new DES();
				String body = "<body>" + des.authcode(result.getBody().getServiceBodyInsideDESInfo(), "ENCODE", ConstantValue.DES_PASSWORD) + "</body>";
				// 解析body 将body放到流Reader中
				parser.setInput(new StringReader(body));

				int eventType = parser.getEventType();
				String name;

				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					switch (eventType)
					{
					case XmlPullParser.START_TAG:
						name = parser.getName(); // 标签名称获取
						if ("errorcode".equals(name))
						{
							result.getBody().getOelement().setErrorcode(parser.nextText());
						}
						if ("errormsg".equals(name))
						{
							result.getBody().getOelement().setErrormsg(parser.nextText());
						}
						break;
					}
					eventType = parser.next();
				}

				return result;

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public Message getBalance(User user)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message bet(User user)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
