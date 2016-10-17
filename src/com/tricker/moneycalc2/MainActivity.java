package com.tricker.moneycalc2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tricker.moneycalc2.db.BaseDao;
import com.tricker.moneycalc2.db.TrickerDB;
import com.tricker.moneycalc2.util.TrickerUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import android.support.v7.widget.Toolbar;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
	private static final BigDecimal HALF = new BigDecimal(0.5);// 1/2
	private static final BigDecimal TWO_PART = new BigDecimal(0.6666666667);// 2/3
	private static final BigDecimal ONE_THIRD = new BigDecimal(0.3333333333);// 1/3
	//	public static final String ACTION="com.tricker.moneycalc2.intent.action.";
	private Cursor editCursor;
	private int clickCount=0;
	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public Cursor getEditCursor() {
		return editCursor;
	}

	public void setEditCursor(Cursor editCursor) {
		this.editCursor = editCursor;
	}

	private BaseDao dao;
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	public NavigationDrawerFragment getmNavigationDrawerFragment() {
		return mNavigationDrawerFragment;
	}

	public void setmNavigationDrawerFragment(NavigationDrawerFragment mNavigationDrawerFragment) {
		this.mNavigationDrawerFragment = mNavigationDrawerFragment;
	}

	private int selectItem = 0;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		dao = new BaseDao(this);
	}


	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		switch (position) {
			case 0:
				selectItem = 0;
				fragmentManager.beginTransaction().replace(R.id.container, RecordFragment.newInstance(position + 1,getEditCursor()))
						.commit();
				//清空数据，用户再次点击记录，就是新增操作
				setEditCursor(null);
				//编辑后进入不可编辑状态，重新走逻辑后才能再次编辑
				clickCount=0;
				break;
			case 1:
				selectItem = 1;
				fragmentManager.beginTransaction().replace(R.id.container, QueryFragment.newInstance(position + 1))
						.commit();
				break;
			case 2:
				selectItem = 2;
				fragmentManager.beginTransaction().replace(R.id.container, MapFragment.newInstance(position + 1))
						.commit();
				break;
			case 3:
				selectItem = 3;
//			fragmentManager.beginTransaction().replace(R.id.container, WeatherFragment.newInstance(position + 1))
//			.commit();
				Intent intent = new Intent(this, ChooseAreaActivity.class);
				startActivity(intent);
				break;
			case 4:
				selectItem = 4;
				TrickerUtils.showToast(this, "很抱歉，正在施工！请绕行！！");
				break;
			default:
				break;
		}
	}


	public void onSectionAttached(int number) {
		switch (number) {
			case 1:
				mTitle = getString(R.string.title_section1);
				break;
			case 2:
				mTitle = getString(R.string.title_section2);
				break;
			case 3:
				mTitle = getString(R.string.title_section3);
				break;
			case 4:
				mTitle = getString(R.string.title_section4);
				break;
			case 5:
				mTitle = getString(R.string.title_section5);
				break;
		}
//		TrickerUtils.showToast(this, number+"");
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			// 查询才显示统计按钮
			if (selectItem == 1) {
				getMenuInflater().inflate(R.menu.main, menu);
			} else {
				getMenuInflater().inflate(R.menu.global, menu);
			}
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			TrickerUtils.showToast(this, "Tricker 出品，必属精品！！！");
			clickCount=0;
			return true;
		}else if(id==R.id.action_copyDB){
//			TrickerUtils.showToast(this, TrickerUtils.getPath(this, TrickerUtils.DATABASE_PATH));
//			TrickerUtils.copyDB(this);
			File fromFile = new File(TrickerUtils.getPath(this, TrickerUtils.DATABASE_PATH));
			File toFile = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"tricker"+File.separator+"tricker.db");
//			TrickerUtils.showToast(this,Environment.getExternalStorageDirectory().getPath());
			String result =TrickerUtils.copyfile(fromFile, toFile , true);
			TrickerUtils.showToast(this, result);
		}else if(id==R.id.action_coverDB){
			File fromFile = new File(TrickerUtils.getPath(this, TrickerUtils.DATABASE_PATH));
			File toFile = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"tricker"+File.separator+"tricker.db");
			String result =TrickerUtils.copyfile(toFile, fromFile , true);
			TrickerUtils.showToast(this, result);
//			TrickerUtils.coverDB(this);
		}
		else if (item.getItemId() == R.id.action_countMoney) {
			clickCount++;
			Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
			if (fragment instanceof QueryFragment) {
				QueryFragment queryFragment = (QueryFragment) fragment;
				Cursor c = queryFragment.getCursor();
				// c.moveToFirst();
				BigDecimal costMoney = new BigDecimal(0);
				BigDecimal cost2Money = new BigDecimal(0);
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					String strMoney = c.getString(c.getColumnIndex("money"));
					BigDecimal eJMoney = TrickerUtils.parseToDecimal(strMoney);
					BigDecimal myMoney = TrickerUtils.parseToDecimal(strMoney);
					String percent = c.getString(c.getColumnIndex("percent"));
					if (percent.equals("1/2")) {
						myMoney = myMoney.multiply(HALF);
						eJMoney = eJMoney.multiply(HALF);
					} else if (percent.equals("2/3")) {
						myMoney = myMoney.multiply(TWO_PART);
						eJMoney = eJMoney.multiply(ONE_THIRD);
					}
					costMoney = costMoney.add(myMoney);
					costMoney = costMoney.setScale(2, RoundingMode.HALF_UP);
					costMoney = TrickerUtils.setScale(costMoney, 2, RoundingMode.HALF_UP);
					cost2Money = cost2Money.add(eJMoney);
					cost2Money = TrickerUtils.setScale(cost2Money, 2, RoundingMode.HALF_UP);
				}
				TrickerUtils.showToast(MainActivity.this, "我共计获得金额：" + costMoney + "\n 二姐共获得金额：" + cost2Money,
						Toast.LENGTH_LONG);
			}
			return true;
		} else if (item.getItemId() == R.id.action_oneKeySet) {
			final Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("真的要设置吗？\n该操作不可恢复！！！");
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final EditText password = new EditText(MainActivity.this);
					// password.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
					builder.setTitle("请输入密码");
					builder.setMessage(null);
					builder.setView(password);
					builder.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
							int minute = Calendar.getInstance().get(Calendar.MINUTE);
							int total = hour - minute;
							if (password.getText() != null
									&& password.getText().toString().equals(String.valueOf(total))) {
								Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
								if (fragment instanceof QueryFragment) {
									QueryFragment queryFragment = (QueryFragment) fragment;
									Cursor c = queryFragment.getCursor();
									// c.moveToFirst();
									String ids = "";
									for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
										String state = c.getString(c.getColumnIndex("state"));
										// 只有未结算才执行更新操作，避免本来就是已结算还去更新，浪费资源
										if (!TextUtils.isEmpty(state) && state.equals("未结算")) {
											String _id = c.getString(c.getColumnIndex("_id"));
											ids += _id + ",";
										}
									}
									ids += "-999";// 避免多一个都好，又不想subStr
//									SQLiteDatabase db = dao.getWritableDatabase();
//									db.execSQL("update " + BaseDao.TABLE_PROJECT + " set state='已结算' where _id in("
//											+ ids + ")");
//									db.close();
									TrickerDB.getInstance(MainActivity.this).updateProject(ids);
									queryFragment.refreshView();

								}

							} else {
								TrickerUtils.showToast(MainActivity.this, "Are You Kidding Me??");
							}
						}
					});
					builder.show();

				}
			});
			builder.setNegativeButton("取消", null);
			builder.show();

			return true;
		}
//		else if(item.getItemId() == R.id.action_getPath){
//			String path = TrickerUtils.getPath(this, TrickerUtils.FILES_DIR);
//			TrickerUtils.showToast(this, path);
//		}
		return super.onOptionsItemSelected(item);
	}


	private long lastTime = 0;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - lastTime > 2000) {
			TrickerUtils.showToast(this, "再按一次退出登录！");
			lastTime = System.currentTimeMillis();
		} else {
			super.onBackPressed();
		}
	}


}
