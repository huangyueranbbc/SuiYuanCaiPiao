package com.example.suiyuancaipiao.service;

import com.example.suiyuancaipiao.bean.User;
import com.example.suiyuancaipiao.net.potocal.Message;

/**
 * 用户相关的业务操作的接口
 * 
 * @author Administrator
 * 
 */
public interface UserService
{
	/**
	 * 用户登录
	 * 
	 * @param user
	 * @return
	 */
	Message login(User user);

	/**
	 * 获取用户余额
	 * 
	 * @param user
	 * @return
	 */
	Message getBalance(User user);

	/**
	 * 用户投注
	 * 
	 * @param user
	 * @return
	 */
	Message bet(User user);
}
