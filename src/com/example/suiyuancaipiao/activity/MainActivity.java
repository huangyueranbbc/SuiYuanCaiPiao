package com.example.suiyuancaipiao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.suiyuancaipiao.R;
import com.example.suiyuancaipiao.util.GlobalParams;
import com.example.suiyuancaipiao.util.PromptManager;
import com.example.suiyuancaipiao.view.Hall;
import com.example.suiyuancaipiao.view.manager.BaseUI;
import com.example.suiyuancaipiao.view.manager.BottomManager;
import com.example.suiyuancaipiao.view.manager.MiddleManager;
import com.example.suiyuancaipiao.view.manager.TitleManager;

public class MainActivity extends Activity
{

	private RelativeLayout middle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 获取屏幕的宽度
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		GlobalParams.WIN_WIDTH = metrics.widthPixels;

		initView();
	}

	private void initView()
	{
		TitleManager titleMananger = TitleManager.getInstance();
		titleMananger.init(this); // 静态对象，初始化一次就可以了
		titleMananger.showUnLoginTitle();

		BottomManager bottomManager = BottomManager.getInstrance();
		bottomManager.init(this);
		bottomManager.showGameBottom();

		middle = (RelativeLayout) findViewById(R.id.middle_layout);
		MiddleManager middleManager = MiddleManager.getInstance();
		middleManager.setMiddle(middle);

		// 建立观察者和被观察者之间的关系
		MiddleManager.getInstance().addObserver(titleMananger);
		MiddleManager.getInstance().addObserver(bottomManager);

		// middleManager.changeUI(FirstUI.class);
		middleManager.changeUI(Hall.class);

	}

	/**
	 * 切换界面
	 * 
	 * @param ui
	 */
	protected void changeUI(BaseUI ui)
	{
		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = ui.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(this, R.anim.ia_view_change));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			boolean goBack = MiddleManager.getInstance().goBack();
			// 没有历史记录 返回失败
			if (!goBack)
			{
				PromptManager.showExitSystem(this);
			}

			return goBack;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void isExit()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("退出系统");
		builder.setMessage("是否退出系统");
		builder.setNegativeButton("确定", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				MainActivity.this.finish();
			}
		}).setPositiveButton("取消", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		builder.create().show();
	}
}
