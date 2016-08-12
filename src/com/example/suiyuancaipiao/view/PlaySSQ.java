package com.example.suiyuancaipiao.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.example.suiyuancaipiao.R;
import com.example.suiyuancaipiao.bean.ShoppingCart;
import com.example.suiyuancaipiao.bean.Ticket;
import com.example.suiyuancaipiao.net.potocal.Message;
import com.example.suiyuancaipiao.net.potocal.Oelement;
import com.example.suiyuancaipiao.net.potocal.impl.CurrentIssueElement;
import com.example.suiyuancaipiao.service.CommonInfoService;
import com.example.suiyuancaipiao.util.BeanFactory;
import com.example.suiyuancaipiao.util.ConstantValue;
import com.example.suiyuancaipiao.util.PromptManager;
import com.example.suiyuancaipiao.view.adapter.PoolAdapter;
import com.example.suiyuancaipiao.view.custom.MyGridView;
import com.example.suiyuancaipiao.view.custom.MyGridView.OnActionUpListener;
import com.example.suiyuancaipiao.view.manager.BaseUI;
import com.example.suiyuancaipiao.view.manager.BottomManager;
import com.example.suiyuancaipiao.view.manager.MiddleManager;
import com.example.suiyuancaipiao.view.manager.PlayGame;
import com.example.suiyuancaipiao.view.manager.TitleManager;
import com.example.suiyuancaipiao.view.sensor.ShakeListener;

/**
 * 
 * @author Administrator
 * @category 双色球选号界面
 */
public class PlaySSQ extends BaseUI implements PlayGame
{

	// ④手机摇晃处理
	// 加速度传感器：
	// 方式一：任意一个轴的加速度值在单位时间内（1秒），变动的速率达到设置好的阈值
	// 方式二：获取三个轴的加速度值，记录，当过一段时间之后再次获取三个轴的加速度值，计算增量，将相邻两个点的增量进行汇总，当达到设置好的阈值

	// 机选
	private Button randomRed;
	private Button randomBlue;
	// 选号容器
	private MyGridView redContainer;
	private GridView blueContainer;

	private PoolAdapter redAdapter;
	private PoolAdapter blueAdapter;

	private List<Integer> redNums;
	private List<Integer> blueNums;

	private ShakeListener listener; // 震动监听
	private SensorManager manager;

	public PlaySSQ(Context context)
	{
		super(context);
	}

	@Override
	public void init()
	{
		showInMiddle = (ViewGroup) View.inflate(context, R.layout.il_playssq, null);

		redContainer = (MyGridView) findViewById_(R.id.ii_ssq_red_number_container);
		blueContainer = (GridView) findViewById_(R.id.ii_ssq_blue_number_container);
		randomRed = (Button) findViewById_(R.id.ii_ssq_random_red);
		randomBlue = (Button) findViewById_(R.id.ii_ssq_random_blue);

		redNums = new ArrayList<Integer>();
		blueNums = new ArrayList<Integer>();

		redAdapter = new PoolAdapter(context, 33, redNums, R.drawable.id_redball);
		blueAdapter = new PoolAdapter(context, 16, blueNums, R.drawable.id_blueball);

		redContainer.setAdapter(redAdapter);
		blueContainer.setAdapter(blueAdapter);

		manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	@Override
	public void setListener()
	{
		randomRed.setOnClickListener(this);
		randomBlue.setOnClickListener(this);

		redContainer.setOnActionUpListener(new OnActionUpListener()
		{

			@Override
			public void onActionUp(View view, int position)
			{
				// 判断当前点击的item是否被选中了
				if (!redNums.contains(position + 1))
				{
					// 如果没有被选中
					// 背景图片切换到红色
					view.setBackgroundResource(R.drawable.id_redball);
					redNums.add(position + 1);
				} else
				{
					// 被选中
					// 还原背景图片
					view.setBackgroundResource(R.drawable.id_defalut_ball);
					redNums.remove((Object) (position + 1));
				}
				changeNotice();

			}
		});

		/*
		 * redContainer.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) { // 判断当前点击的item是否被选中了 if
		 * (!redNums.contains(position + 1)) { // 如果没有被选中 // 背景图片切换到红色
		 * view.setBackgroundResource(R.drawable.id_redball); // 摇晃的动画
		 * view.startAnimation(AnimationUtils.loadAnimation(context,
		 * R.anim.ia_ball_shake)); redNums.add(position + 1); } else { // 被选中 //
		 * 还原背景图片 view.setBackgroundResource(R.drawable.id_defalut_ball);
		 * redNums.remove((Object) (position + 1)); }
		 * 
		 * } });
		 */

		blueContainer.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// 判断当前点击的item是否被选中了
				if (!blueNums.contains(position + 1))
				{
					// 如果没有被选中
					// 背景图片切换到红色
					view.setBackgroundResource(R.drawable.id_blueball);
					// 摇晃的动画
					view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.ia_ball_shake));
					blueNums.add(position + 1);
				} else
				{
					// 被选中
					// 还原背景图片
					view.setBackgroundResource(R.drawable.id_defalut_ball);
					blueNums.remove((Object) (position + 1));
				}
				changeNotice();

			}
		});
	}

	@Override
	public int getID()
	{
		// TODO Auto-generated method stub
		return ConstantValue.VIEW_SSQ;
	}

	@Override
	public void onClick(View v)
	{
		Random random = new Random();
		switch (v.getId())
		{
		case R.id.ii_ssq_random_red:
			// 机选红球
			redNums.clear();
			while (redNums.size() < 6)
			{
				int num = random.nextInt(33) + 1;

				if (redNums.contains(num))
				{
					continue;
				}
				redNums.add(num);
			}

			// 处理展示
			redAdapter.notifyDataSetChanged();
			changeNotice();
			break;
		case R.id.ii_ssq_random_blue:
			blueNums.clear();
			int num = random.nextInt(16) + 1;
			blueNums.add(num);

			blueAdapter.notifyDataSetChanged();
			changeNotice();
			break;
		}
		super.onClick(v);
	}

	@Override
	public void onResume()
	{
		changeTitle();
		changeNotice();
		clear();
		// 注册震动监听器
		listener = new ShakeListener(context)
		{

			@Override
			public void randomCure()
			{
				randomSSQ();
			}
		};

		manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		super.onResume();
	}

	/**
	 * 机选一注
	 */
	private void randomSSQ()
	{

		Random random = new Random();
		redNums.clear();
		blueNums.clear();

		// 机选红球
		while (redNums.size() < 6)
		{
			int num = random.nextInt(33) + 1;

			if (redNums.contains(num))
			{
				continue;
			}
			redNums.add(num);
		}
		int num = random.nextInt(16) + 1;
		blueNums.add(num);

		// 处理展示
		redAdapter.notifyDataSetChanged();
		blueAdapter.notifyDataSetChanged();

		changeNotice();

	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		// 注销震动监听
		manager.unregisterListener(listener);
		super.onPause();
	}

	/**
	 * 修改标题
	 */
	private void changeTitle()
	{
		String titleInfo = "";
		// ①标题——界面之间的数据传递(Bundle)
		// 判断购彩大厅是否获取到期次信息
		if (bundle != null)
		{
			// 如果获取到：拼装标题
			titleInfo = "双色球第" + bundle.getString("issue") + "期";
		} else
		{
			// 否则默认的标题展示
			titleInfo = "双色球选号";
		}

		TitleManager.getInstance().changeTitle(titleInfo);
	}

	/**
	 * 改变底部导航的提示信息
	 */
	private void changeNotice()
	{
		String notice = "";
		// 以一注为分割
		if (redNums.size() < 6)
		{
			notice = "您还需要选择" + (6 - redNums.size()) + "个红球";
		} else if (blueNums.size() == 0)
		{
			notice = "您还需要选择" + 1 + "个蓝球";
		} else
		{
			notice = "共 " + calc() + " 注 " + calc() * 2 + " 元";
		}

		BottomManager.getInstrance().changeGameBottomNotice(notice);
	}

	/**
	 * 计算注数
	 * 
	 * @return
	 */
	private int calc()
	{
		int redC = (int) (factorial(redNums.size()) / (factorial(6) * factorial(redNums.size() - 6)));
		int blueC = blueNums.size();
		return redC * blueC;
	}

	/**
	 * 计算一个数的阶乘
	 * 
	 * @param num
	 * @return
	 */
	private long factorial(int num)
	{
		// num=7 7*6*5...*1

		if (num > 1)
		{
			return num * factorial(num - 1);
		} else if (num == 1 || num == 0)
		{
			return 1;
		} else
		{
			throw new IllegalArgumentException("num >= 0");
		}
	}

	/**
	 * 清除
	 */
	public void clear()
	{
		redNums.clear();
		blueNums.clear();
		changeNotice();

		redAdapter.notifyDataSetChanged();
		blueAdapter.notifyDataSetChanged();

	}

	/**
	 * 选好了
	 */
	public void done()
	{
		// 1.判断 用户是否至少选择了一注彩票
		if (redNums.size() >= 6 && blueNums.size() >= 1)
		{
			// 一个购物车只能放一个彩种，当前期的投注信息
			// 2.判断是否获取到当前销售期的信息
			if (bundle != null)
			{
				// 3.封装用户的投注信息
				Ticket ticket = new Ticket();
				DecimalFormat decimalFormat = new DecimalFormat("00");

				// 红球
				StringBuffer redBuffer = new StringBuffer();
				for (Integer item : redNums)
				{
					redBuffer.append(" ").append(decimalFormat.format(item));
				}
				ticket.setRedNum(redBuffer.substring(1));

				// 蓝球
				StringBuffer blueBuffer = new StringBuffer();
				for (Integer item : blueNums)
				{
					blueBuffer.append(" ").append(decimalFormat.format(item));
				}
				ticket.setBlueNum(blueBuffer.substring(1));

				ticket.setNum(calc());

				// 4.创建彩票购物车,将投注信息添加到购物车中
				ShoppingCart.getInstance().getTickets().add(ticket);
				// 5.设置彩种的标示，设置彩种期次
				ShoppingCart.getInstance().setIssue(bundle.getString("issue"));
				ShoppingCart.getInstance().setLotteryid(ConstantValue.SSQ);

				// 6.界面跳转——购物车展示
				MiddleManager.getInstance().changeUI(Shopping.class, bundle);

			} else
			{
				// 重新获取期次信息
				getCurrentIssueInfo();
			}
		} else
		{
			// 提示：需要选择一注
			PromptManager.showToast(context, "需要选择一注");
		}

		// 分支

	}

	/**
	 * 获取当前销售期信息(双色球)
	 */
	public void getCurrentIssueInfo()
	{
		System.out.println("执行了getCurrentIssueInfo");
		// 自定义的异步任务抽象类
		new MyHttpAsyncTask<Integer>()
		{

			// 主线程运行
			protected void onPreExecute()
			{
				// 显示滚动条
				PromptManager.showProgressDialog(context);
			}

			// 子线程运行
			protected Message doInBackground(Integer... params)
			{
				// 获取数据
				CommonInfoService service = BeanFactory.getImpl(CommonInfoService.class);
				return service.getCurrentIssueInfo(params[0]);
			}

			@Override
			protected void onPostExecute(Message result)
			{
				PromptManager.closeProgressDialog();
				// 更新UI界面
				if (result != null)
				{
					Oelement oelement = result.getBody().getOelement();
					if (ConstantValue.SUCCESS.equals(oelement.getErrorcode()))
					{
						CurrentIssueElement currentIssueElement = (CurrentIssueElement) result.getBody().getElements().get(0);
						// 获取成功 创建bundle
						bundle = new Bundle();
						bundle.putString("issue", currentIssueElement.getIssue());
						changeTitle();
					} else
					{
						PromptManager.showToast(context, oelement.getErrormsg());
					}
				} else
				{
					// 可能：网络不通、权限、服务器出错、非法数据……
					// 如何提示用户
					PromptManager.showToast(context, "服务器正在嘿嘿嘿，请稍后联络~~~");
				}
				super.onPostExecute(result);
			}

		}.execute(ConstantValue.SSQ);
	}
}
