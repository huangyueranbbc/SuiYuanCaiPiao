package com.example.suiyuancaipiao.view.manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.suiyuancaipiao.R;

/**
 * @author Administrator
 * @category 中间容器的管理工具
 */
public class MiddleManager extends Observable
{
	private static MiddleManager instance = new MiddleManager();

	// 利用手机内存空间，换应用应用的运行速度
	private static Map<String, BaseUI> VIEWCACHE = new HashMap<String, BaseUI>();

	private BaseUI currentUI; // 当前正在展示的UI

	private LinkedList<String> HISTORY = new LinkedList<String>();// 用户操作的历史记录

	private MiddleManager()
	{
	}

	public static MiddleManager getInstance()
	{
		return instance;
	}

	private RelativeLayout middle;

	public void setMiddle(RelativeLayout middle)
	{
		this.middle = middle;
	}

	/**
	 * 如果需要传递数据给界面
	 * 
	 * @param targetClazz
	 * @param bundle
	 */
	public void changeUI(Class<? extends BaseUI> targetClazz, Bundle bundle)
	{
		// 判断：当前正在展示的界面和切换目标界面是否相同 如果相同 则无需改变界面
		if (currentUI != null && currentUI.getClass() == targetClazz)
		{
			return;
		}

		BaseUI targetUI = null;
		// 一旦创建过，重用
		// 判断是否创建了——曾经创建过的界面需要存储
		String key = targetClazz.getSimpleName();
		if (VIEWCACHE.containsKey(key))
		{
			// 创建了，重用
			targetUI = VIEWCACHE.get(key);
		} else
		{
			// 为创建，创建
			try
			{
				Constructor<? extends BaseUI> constructor = targetClazz.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());
				VIEWCACHE.put(key, targetUI);
			} catch (Exception e)
			{
				throw new RuntimeException("constructor new instance error");
			}

		}

		// 传递数据
		if (targetUI != null)
		{
			targetUI.setBundle(bundle);
		}

		Log.i("main", targetUI.toString());

		// 清理当前正在展示的界面之前---onPause方法

		if (currentUI != null)
		{
			currentUI.onPause();
		}

		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = targetUI.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));

		// 在加载玩界面之后，读取数据修改内容---onResume方法
		targetUI.onResume();

		currentUI = targetUI;

		// 将当前显示的界面放到栈顶
		HISTORY.addFirst(key);

		changeTitleAndBottom();
	}

	/**
	 * 切换界面
	 * 
	 * @param ui
	 */
	public void changeUI(Class<? extends BaseUI> targetClazz)
	{
		// 判断：当前正在展示的界面和切换目标界面是否相同 如果相同 则无需改变界面
		if (currentUI != null && currentUI.getClass() == targetClazz)
		{
			return;
		}

		BaseUI targetUI = null;
		// 一旦创建过，重用
		// 判断是否创建了——曾经创建过的界面需要存储
		String key = targetClazz.getSimpleName();
		if (VIEWCACHE.containsKey(key))
		{
			// 创建了，重用
			targetUI = VIEWCACHE.get(key);
		} else
		{
			// 为创建，创建
			try
			{
				Constructor<? extends BaseUI> constructor = targetClazz.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());
				VIEWCACHE.put(key, targetUI);
			} catch (Exception e)
			{
				throw new RuntimeException("constructor new instance error");
			}

		}

		Log.i("main", targetUI.toString());

		// 清理当前正在展示的界面之前---onPause方法

		if (currentUI != null)
		{
			currentUI.onPause();
		}

		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = targetUI.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));

		// 在加载玩界面之后，读取数据修改内容---onResume方法
		targetUI.onResume();

		currentUI = targetUI;

		// 将当前显示的界面放到栈顶
		HISTORY.addFirst(key);

		changeTitleAndBottom();
	}

	/**
	 * 返回键处理
	 * 
	 * @return
	 */
	public boolean goBack()
	{
		// 记录一下用户操作历史
		// 频繁操作栈顶（添加）——在界面切换成功
		// 获取栈顶
		// 删除了栈顶
		// 有序集合
		if (HISTORY.size() > 0)
		{
			// 当用户误操作返回键（不退出应用）
			if (HISTORY.size() == 1)
			{
				return false;
			}

			// Throws:NoSuchElementException - if this LinkedList is empty.
			HISTORY.removeFirst();

			if (HISTORY.size() > 0)
			{
				// Throws:NoSuchElementException - if this LinkedList is empty.
				String key = HISTORY.getFirst();

				BaseUI targetUI = VIEWCACHE.get(key);
				if (targetUI != null)
				{
					currentUI.onPause();
					middle.removeAllViews();
					middle.addView(targetUI.getChild());
					targetUI.onResume();
					currentUI = targetUI;

					changeTitleAndBottom();
				} else
				{
					// 处理方式一：创建一个新的目标界面：存在问题——如果有其他的界面传递给被删除的界面
					// 处理方式二：寻找一个不需要其他界面传递数据——跳转到首页
					// changeUI(Hall.class);
					// PromptManager.showToast(getContext(), "应用在低内存下运行");
				}

				return true;

			}
		}

		return false;
	}

	private void changeTitleAndBottom()
	{
		// 1、界面一对应未登陆标题和通用导航
		// 2、界面二对应通用标题和玩法导航

		// 当前正在展示的如果是第一个界面
		// 方案一：存在问题，比对的依据：名称 或者 字节码
		// 在界面处理初期，将所有的界面名称确定
		// 如果是字节码，将所有的界面都的创建完成
		// if(currentUI.getClass()==FirstUI.class){
		// TitleManager.getInstance().showUnLoginTitle();
		// BottomManager.getInstrance().showCommonBottom();
		// }
		// if(currentUI.getClass().getSimpleName().equals("SecondUI")){
		// TitleManager.getInstance().showCommonTitle();
		// BottomManager.getInstrance().showGameBottom();
		// }

		// 方案二：更换比对依据

		/*
		 * switch (currentUI.getID()) { case ConstantValue.VIEW_FIRST:
		 * TitleManager.getInstance().showUnLoginTitle();
		 * BottomManager.getInstrance().showCommonBottom(); //
		 * LeftManager\RightManager break; case ConstantValue.VIEW_SECOND:
		 * TitleManager.getInstance().showCommonTitle();
		 * BottomManager.getInstrance().showGameBottom(); break; case 3:
		 * TitleManager.getInstance().showCommonTitle();
		 * BottomManager.getInstrance().showGameBottom(); break; }
		 */

		// 降低三个容器的耦合度
		// 当中间容器变动的时候，中间容器“通知”其他的容器，你们该变动了，唯一的标示传递，其他容器依据唯一标示进行容器内容的切换
		// 通知：
		// 广播：多个应用
		// 为中间容器的变动增加了监听——观察者设计模式

		// ①将中间容器变成被观察的对象
		// ②标题和底部导航变成观察者
		// ③建立观察者和被观察者之间的关系（标题和底部导航添加到观察者的容器里面）
		// ④一旦中间容器变动，修改boolean，然后通知所有的观察者.updata()

		setChanged();
		notifyObservers(currentUI.getID());
		Log.i("caipiao", "已经通知被观察对象" + currentUI.getID());
	}

	public Context getContext()
	{
		return middle.getContext();
	}

	public BaseUI getCurrentUI()
	{
		return currentUI;
	}

	public void clear()
	{
		HISTORY.clear();
	}

}
