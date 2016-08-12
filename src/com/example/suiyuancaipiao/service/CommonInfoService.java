package com.example.suiyuancaipiao.service;

import com.example.suiyuancaipiao.net.potocal.Message;

/**
 * 
 * @author Administrator
 * @category 公共数据处理
 */
public interface CommonInfoService
{
	/**
	 * 获取当前销售期信息
	 * 
	 * @param integer
	 *            ：彩种的标示
	 * @return
	 */
	Message getCurrentIssueInfo(Integer integer);
}
