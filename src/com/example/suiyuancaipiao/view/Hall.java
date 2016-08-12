package com.example.suiyuancaipiao.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.suiyuancaipiao.R;
import com.example.suiyuancaipiao.net.potocal.Element;
import com.example.suiyuancaipiao.net.potocal.Message;
import com.example.suiyuancaipiao.net.potocal.Oelement;
import com.example.suiyuancaipiao.net.potocal.impl.CurrentIssueElement;
import com.example.suiyuancaipiao.service.CommonInfoService;
import com.example.suiyuancaipiao.util.BeanFactory;
import com.example.suiyuancaipiao.util.ConstantValue;
import com.example.suiyuancaipiao.util.GlobalParams;
import com.example.suiyuancaipiao.util.PromptManager;
import com.example.suiyuancaipiao.view.manager.BaseUI;
import com.example.suiyuancaipiao.view.manager.MiddleManager;

/**
 * 购彩大厅
 * 
 * @author Administrator
 * 
 */
public class Hall extends BaseUI
{

	private ListView categoryList; // 彩种的入口

	// 资源信息
	private int[] logoResIds = new int[]
	{ R.drawable.id_ssq, R.drawable.id_3d, R.drawable.id_qlc };
	private int[] titleResIds = new int[]
	{ R.string.is_hall_ssq_title, R.string.is_hall_3d_title, R.string.is_hall_qlc_title };

	private CategoryAdapter categoryListViewAdapter;

	// ViewPager配置
	private ViewPager pager;
	private PagerAdapter pagerAdapter;

	private ImageView underLine;

	private List<View> pagers;

	private TextView fcTitle;// 福彩
	private TextView tcTitle;// 体彩
	private TextView gpcTitle;// 高频彩

	// 记录ViewPger上一个界面的position信息
	private int lastPosition = 0;

	private String text;

	private Bundle ssqBundle;

	public Hall(Context context)
	{
		super(context);
	}

	@Override
	public void init()
	{
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.il_hall, null);

		pager = (ViewPager) findViewById_(R.id.ii_viewpager);
		pagerAdapter = new MyPagerAdapter();

		categoryList = new ListView(context);

		// 注册ListView适配器
		categoryListViewAdapter = new CategoryAdapter();
		categoryList.setAdapter(categoryListViewAdapter);
		categoryList.setFadingEdgeLength(0);// 删除黑边（上下）

		initPager();

		pager.setAdapter(pagerAdapter);

		// 初始化选项卡的下划线
		initTabStrip();

		// 每次进入购彩大厅界面，获取最新的数据——>已进入到某个界面，想去修改界面信息（存储）——>当进入到某个界面的时候，开启耗费资源的操作，当要离开界面，清理耗费资源的操作
		// 进入界面做些工作，出去的时候还需要完成一些工作
		// onResume（当界面被加载了：add（View））
		// onPause（当界面要被删除：removeAllView）——Activity抄了两个方法
	}

	private void initTabStrip()
	{
		underLine = (ImageView) findViewById_(R.id.ii_category_selector);

		fcTitle = (TextView) findViewById_(R.id.ii_category_fc);
		tcTitle = (TextView) findViewById_(R.id.ii_category_tc);
		gpcTitle = (TextView) findViewById_(R.id.ii_category_gpc);

		fcTitle.setTextColor(Color.RED);
		// 屏幕宽度
		// GlobalParams.WIN_WIDTH;
		// 小图片的宽度
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.id_category_selector);
		// bitmap.getWidth();

		int offset = (GlobalParams.WIN_WIDTH / 3 - bitmap.getWidth()) / 2;

		// 设置图片初始位置——向右偏移
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		underLine.setImageMatrix(matrix);

	}

	private void initPager()
	{
		pagers = new ArrayList<View>();
		pagers.add(categoryList);

		TextView item = new TextView(context);
		item.setText("体彩");
		pagers.add(item);

		item = new TextView(context);
		item.setText("高频彩");
		pagers.add(item);
	}

	@Override
	public View getChild()
	{
		// 【如果inflate第三个参数为空 此layout不会设置LayoutParams，LayoutParams为null，需要自己设置】
		if (showInMiddle.getLayoutParams() == null)
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,//
					LinearLayout.LayoutParams.MATCH_PARENT);
			showInMiddle.setLayoutParams(params);
		}
		return showInMiddle;
	}

	@Override
	public int getID()
	{
		// TODO Auto-generated method stub
		return ConstantValue.VIEW_HALL;
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
				// 显示UI
				super.onPreExecute();
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
				// 更新UI界面
				if (result != null)
				{
					Oelement oelement = result.getBody().getOelement();
					if (ConstantValue.SUCCESS.equals(oelement.getErrorcode()))
					{
						// 获取成功
						changeNotice(result.getBody().getElements().get(0));
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

	/**
	 * 修改界面提示信息
	 * 
	 * @param element
	 */
	protected void changeNotice(Element element)
	{
		CurrentIssueElement currentIssueElement = (CurrentIssueElement) element;
		String issue = currentIssueElement.getIssue();
		String lasttime = getLasttime(currentIssueElement.getLasttime());
		text = context.getResources().getString(R.string.is_hall_common_summary);
		text = StringUtils.replaceEach(text, new String[]
		{ "ISSUE", "TIME" }, new String[]
		{ issue, lasttime });

		// TODO 更新界面 更新需要更新的内容
		TextView view = (TextView) categoryList.findViewWithTag(0);// tag :唯一
																	// 只有一個ListView的ITEM,所有position為0,tag為0
		if (view != null)
		{
			view.setText(text);
		}

		// 设置需要传输的数据
		ssqBundle = new Bundle();
		ssqBundle.putString("issue", issue);
	}

	/**
	 * 将秒时间转换成日时分格式
	 * 
	 * @param lasttime
	 * @return
	 */
	public String getLasttime(String lasttime)
	{
		StringBuffer result = new StringBuffer();
		if (StringUtils.isNumericSpace(lasttime))
		{
			int time = Integer.parseInt(lasttime);
			int day = time / (24 * 60 * 60);
			result.append(day).append("天");
			if (day > 0)
			{
				time = time - day * 24 * 60 * 60;
			}
			int hour = time / 3600;
			result.append(hour).append("时");
			if (hour > 0)
			{
				time = time - hour * 60 * 60;
			}
			int minute = time / 60;
			result.append(minute).append("分");
		}
		return result.toString();
	}

	@Override
	public void onResume()
	{
		getCurrentIssueInfo();
		super.onResume();
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void setListener()
	{

		fcTitle.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				pager.setCurrentItem(0);

			}
		});

		pager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				// position:0

				// fromXDelta toXDelta:相对于图片初始位置需要增加的量

				TranslateAnimation animation = new TranslateAnimation(lastPosition * GlobalParams.WIN_WIDTH / 3, position * GlobalParams.WIN_WIDTH / 3, 0, 0);
				animation.setDuration(300);
				animation.setFillAfter(true);// 移动完后停留到终点

				underLine.startAnimation(animation);
				lastPosition = position;

				// 滑动完成后
				/*
				 * switch(position) { case 1:// 当position从0移动到1
				 * TranslateAnimation animation=new
				 * TranslateAnimation(0*GlobalParams.WIN_WIDTH/3,
				 * 1*GlobalParams.WIN_WIDTH/3, 0, 0);
				 * animation.setDuration(300); animation.setFillAfter(true);//
				 * 移动完后停留到终点
				 * 
				 * underLine.startAnimation(animation); break; case 2://
				 * 当position从1移动到2 animation=new
				 * TranslateAnimation(1*GlobalParams.WIN_WIDTH/3,
				 * 2*GlobalParams.WIN_WIDTH/3, 0, 0);
				 * animation.setDuration(300); animation.setFillAfter(true);//
				 * 移动完后停留到终点
				 * 
				 * underLine.startAnimation(animation); break; }
				 */

				fcTitle.setTextColor(Color.BLACK);
				tcTitle.setTextColor(Color.BLACK);
				gpcTitle.setTextColor(Color.BLACK);

				switch (position)
				{
				case 0:
					fcTitle.setTextColor(Color.RED);
					break;
				case 1:
					tcTitle.setTextColor(Color.RED);
					break;
				case 2:
					gpcTitle.setTextColor(Color.RED);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// 属性动画
			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});

	}

	/**
	 * ListViewAdapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class CategoryAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;

			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = View.inflate(context, R.layout.il_hall_lottery_item, null);

				holder.logo = (ImageView) convertView.findViewById(R.id.ii_hall_lottery_logo);
				holder.title = (TextView) convertView.findViewById(R.id.ii_hall_lottery_title);
				holder.summary = (TextView) convertView.findViewById(R.id.ii_hall_lottery_summary);
				holder.bet = (ImageView) convertView.findViewById(R.id.ii_hall_lottery_bet);

				convertView.setTag(holder);

			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.logo.setImageResource(logoResIds[position]);
			holder.title.setText(titleResIds[position]);

			holder.summary.setTag(position); // 設置tag 只有一個所以position為0

			holder.bet.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (position == 0)
					{
						MiddleManager.getInstance().changeUI(PlaySSQ.class,ssqBundle);
					}

				}
			});

			return convertView;
		}

		private class ViewHolder
		{
			ImageView logo;
			TextView title;
			TextView summary;
			ImageView bet;
		}

	}

	/**
	 * Viewpager用adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter
	{

		public Object instantiateItem(ViewGroup container, int position)
		{
			View view = pagers.get(position);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			View view = pagers.get(position);

			container.removeView(view);
		}

		@Override
		public int getCount()
		{
			return pagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == object;
		}
	}
}
