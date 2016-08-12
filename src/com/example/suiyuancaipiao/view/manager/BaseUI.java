package com.example.suiyuancaipiao.view.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.suiyuancaipiao.net.NetUtil;
import com.example.suiyuancaipiao.net.potocal.Message;
import com.example.suiyuancaipiao.util.PromptManager;

/**
 * 所有界面的基类
 * 
 * @author Administrator
 * 
 */
public abstract class BaseUI implements View.OnClickListener
{
	protected Context context;
	protected Bundle bundle;
	// 显示到中间容器
	protected ViewGroup showInMiddle;

	public BaseUI(Context context)
	{
		this.context = context;
		init();
		setListener();
	}

	public void setBundle(Bundle bundle)
	{
		this.bundle = bundle;
	}

	/**
	 * 界面的初始化
	 * 
	 * @return
	 */
	public abstract void init();

	/**
	 * 设置监听
	 * 
	 * @return
	 */
	public abstract void setListener();

	/**
	 * 获取需要在中间容器加载的内容
	 * 
	 * @return
	 */
	public View getChild()
	{
		// 设置layout参数

		// root=null
		// showInMiddle.getLayoutParams()=null
		// root!=null
		// return root

		// 当LayoutParams类型转换异常，向父容器看齐
		if (showInMiddle.getLayoutParams() == null)
		{
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			showInMiddle.setLayoutParams(params);
		}

		return showInMiddle;
	}

	/**
	 * 获取每个界面的标示——容器联动时的比对依据
	 * 
	 * @return
	 */
	public abstract int getID();

	@Override
	public void onClick(View v)
	{

	}

	public View findViewById_(int id)
	{
		return showInMiddle.findViewById(id);
	}

	/**
	 * 访问网络的工具
	 * 
	 * @author Administrator
	 * 
	 * @param <Params>
	 */
	protected abstract class MyHttpTask<Params> extends AsyncTask<Params, Void, Message>
	{
		/**
		 * 类似与Thread.start方法 由于final修饰，无法Override，方法重命名 省略掉网络判断
		 * 
		 * @param params
		 * @return
		 */
		public final AsyncTask<Params, Void, Message> executeProxy(Params... params)
		{
			if (NetUtil.checkNet(context))
			{
				return super.execute(params);
			} else
			{
				 PromptManager.showNoNetWork(context);
			}
			return null;
		}

	}

	/**
	 * 要出去的时候调用
	 */
	public void onPause()
	{

	}

	/**
	 * 进入到界面之后
	 */
	public void onResume()
	{
	}

	protected abstract class MyHttpAsyncTask<Params> extends AsyncTask<Params, Void, Message>
	{

		/*
		 * // 主线程运行 protected void onPreExecute() { // 显示UI
		 * super.onPreExecute(); }
		 * 
		 * // 子线程运行 protected Message doInBackground(Integer... params) { //
		 * 获取数据 return null; }
		 */

		/**
		 * 重新定义execute方法 原方法为final 无法修改 判断网络状态
		 * 
		 * @param params
		 * @return
		 */
		public AsyncTask<Params, Void, Message> executeProxy(Params... params)
		{
			if (NetUtil.checkNet(context))
			{
				return super.execute(params);
			} else
			{
				PromptManager.showNoNetWork(context);
			}
			return null;
		}

	}

}
