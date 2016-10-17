package com.tricker.moneycalc2;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

//import com.example.adapter.SearchHistoryAdapter;
//import com.example.adapter.SearchShowLvAdapter;
//import com.example.db.HistoryHelper;
//import com.example.domain.Contance;
//import com.example.domain.ContanceKey;
//import com.example.domain.HistoryShowBean;
//import com.example.domain.ImportContance;
//import com.example.domain.JingdianName;
//import com.example.domain.SearchShowBean;
//import com.example.utils.SearchClearEditText;
import com.tricker.moneycalc2.MainActivity;
import com.tricker.moneycalc2.R;
import com.tricker.moneycalc2.adapter.SearchHistoryAdapter;
import com.tricker.moneycalc2.adapter.SearchShowLvAdapter;
import com.tricker.moneycalc2.db.BaseDao;
import com.tricker.moneycalc2.model.ContanceKey;
import com.tricker.moneycalc2.model.HistoryShowBean;
import com.tricker.moneycalc2.model.JingdianName;
import com.tricker.moneycalc2.model.SearchShowBean;
import com.tricker.moneycalc2.util.SearchClearEditText;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private ImageButton ib_back_normal; // 搜索框左侧返回按钮
	private TextView tv_search; // 搜索框右边的搜索字样
	SearchClearEditText clear_et;
	private ListView lv_search_xianshi, lv_search_history;
	private Button clear_search_history_btn;

	// 初始化数据库打开器
//	HistoryHelper helper;
	BaseDao helper;
	private SearchHistoryAdapter his_adapter;

	private List<SearchShowBean> sea_data;
	private List<HistoryShowBean> his_data;
	static List<String> list = new ArrayList<String>();

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 点击清除记录按钮，刷新界面
					his_adapter.refresh(queryHistoryData());
					break;

				default:
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题，使用自己的布局
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.clear_et_activity);

		init();

	}

	/*
	 * 初始化组件
	 */
	private void init() {
		ib_back_normal = (ImageButton) findViewById(R.id.ib_back_normal);
		tv_search = (TextView) findViewById(R.id.tv_search);
		clear_et = (SearchClearEditText) findViewById(R.id.et_clear);
		clear_search_history_btn = (Button) findViewById(R.id.clear_search_history_btn);

		lv_search_xianshi = (ListView) findViewById(R.id.search_xianshi_lv);
		lv_search_history = (ListView) findViewById(R.id.search_history_lv);

		his_data = queryHistoryData();

		his_adapter = new SearchHistoryAdapter(his_data, this);
		lv_search_history.setAdapter(his_adapter);

		ib_back_normal.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		clear_search_history_btn.setOnClickListener(this);

		lv_search_history.setOnItemClickListener(this);
		lv_search_xianshi.setOnItemClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ib_back_normal:
//				Intent intent = new Intent(SearchActivity.this, MainActivity.class);
//				startActivity(intent);
				SearchActivity.this.finish(); // 结束当前界面
				// lv_search_xianshi.setVisibility(View.GONE);
				break;
			case R.id.tv_search: // 点击搜索按钮
			/*
			 * 点击搜索按钮先清空，是因为数据使用list集合保存的，如果搜索一个景点出来数据后，
			 * 直接搜索另一个，就会叠加，所以先清空，这样每次显示的都是最新的
			 */
				list.removeAll(list);
				// 点击搜索按钮，使得显示搜索结果的listview显示出来
				lv_search_xianshi.setVisibility(View.VISIBLE);
				lv_search_history.setVisibility(View.GONE);
				clear_search_history_btn.setVisibility(View.GONE);

				String search = clear_et.getText().toString().trim();
				int count = 0;
				for (int i = 0; i < JingdianName.names.length; i++) {
					if (JingdianName.names[i].contains(search) == true) {
						list.add(JingdianName.names[i]);
						count++;
					}
				}
				if (count == 0) { // 如果一个也没有查到，就显示没有景点
					list.removeAll(list); // 清空集合数据，重新查询
					getData(list, lv_search_xianshi);
					Toast.makeText(getApplicationContext(), "没有该景点",
							Toast.LENGTH_SHORT).show();
				} else { // 有数据就显示
					getData(list, lv_search_xianshi);
				}

				// 插入数据库数据
				insertHistory(search);

				break;

			case R.id.clear_search_history_btn: // 点击清除历史记录按钮
				deleteHistory();
				break;
			default:
				break;
		}
	}

	/*
	 * 删除历史记录
	 */
	private void deleteHistory() {
		helper = new BaseDao(getApplicationContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		// 删除表。
		db.execSQL("delete from history");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 此处handler发送一个message，用来更新ui
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}).start();
		// his_adapter.refresh(queryHistoryData());
		db.close();
	}

	/*
	 * 插入历史记录，点击按钮，获得edittext的值，写到数据库
	 */
	private void insertHistory(String search) {
		helper = new BaseDao(getApplicationContext());
		// 插入数据库
		SQLiteDatabase db = helper.getWritableDatabase();
		Log.i("create", "数据库表history创建成功");
		int count = 0;
		// 查询数据库，判断edittext的内容是否已经存在，如果存在了，就不写了，如果不存在，就插入数据库
		// 取回查询存放history表的h_name列的list集合
		List<String> list = queryHistorySql();
		for (int i = 0; i < list.size(); i++) {
			// 获取搜索框的输入内容，和数据已经存在的记录比对，如果有一样的，就count增加；
			if (list.get(i).equals(search)) {
				count++;
			}
		}
		// 如果count == 0，说明没有重复的数据，就可以插入数据库history表中
		if (count == 0) {
			db.execSQL("insert into history values(?,?)", new Object[] { null, search });
			Log.i("create", "数据库表history数据插入成功");
		} else {
			// Toast.makeText(getApplicationContext(), "已存在",
			// Toast.LENGTH_SHORT)
			// .show();
		}

		db.close();
	}

	/*
	 * 查询数据库的h_name一列，然后放到list集合中，用于判断是否插入数据
	 */
	private List<String> queryHistorySql() {
		helper = new BaseDao(getApplicationContext());
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from history", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			// 查询数据库，取出h_name这一列，然后全部放到list集合中，在前面调用此方法的时候，用来判断
			String name = cursor.getString(cursor.getColumnIndex("h_name"));
			list.add(name);
			cursor.moveToNext();
		}

		db.close();
		// 返回一个list集合
		return list;
	}

	/*
	 * 点击搜索按钮后，查询出来的结果
	 */
	private void getData(List<String> list, ListView listView) {
		sea_data = new ArrayList<SearchShowBean>();
		for (int i = 0; i < list.size(); i++) {
			SearchShowBean bean = new SearchShowBean();
			bean.setJingdian(list.get(i));
			bean.setImage(getResources().getDrawable(
					R.drawable.umeng_socialize_search_icon));
			sea_data.add(bean);
		}
		listView.setAdapter(new SearchShowLvAdapter(this, sea_data));
	}

	// 将每一行的数据封装成一个HistoryShowBean对象，然后放到his_list中
	private List<HistoryShowBean> queryHistoryData() {
		helper = new BaseDao(getApplicationContext());
		List<HistoryShowBean> his_list = new ArrayList<HistoryShowBean>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor his_c = db.rawQuery("select * from history", null);

		his_c.moveToFirst();
		while (!his_c.isAfterLast()) {
			int h_id = his_c.getInt(his_c.getColumnIndex("_id"));
			String h_name = his_c.getString(his_c.getColumnIndex("h_name"));

			// 用一个HistoryShowBean类来封装得到的数据
			final HistoryShowBean his_bean = new HistoryShowBean();
			his_bean.setJingdian(h_name);
			his_bean.setImage(getResources().getDrawable(
					R.drawable.history_icon));
			his_list.add(his_bean);
			his_c.moveToNext();
		}
		if (his_list.size() == 0) {
			clear_search_history_btn.setVisibility(View.GONE);
		}
		db.close();
		return his_list;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {// 按下的如果是BACK，同时没有重复
//			Intent intent = new Intent(SearchActivity.this, MainActivity.class);
//			startActivity(intent);
			SearchActivity.this.finish();
			// 这里不需要执行父类的点击事件，所以直接return
			return true;
		}
		// 继续执行父类的其他点击事件
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		switch (parent.getId()) { // 获得listview的id
			case R.id.search_xianshi_lv: // 显示listview
				// 获得当前点击的item
				view = lv_search_xianshi.getChildAt(position);
				// 获得item中的textview控件，并初始化
				TextView tv = (TextView) view.findViewById(R.id.search_xianshi_tv);
				// 获得textview的值
				String str = tv.getText().toString().trim();
				// 进行数据库查询并且跳转
				listViewJump(str);
				break;
			case R.id.search_history_lv:
				view = lv_search_history.getChildAt(position);
				TextView tv1 = (TextView) view.findViewById(R.id.search_history_tv);
				String str1 = tv1.getText().toString().trim();
				String string = null;
				// 之前已经把数据库的列“mingcheng”的值已经放到了一个数组中
				// 判断点击的item中的textview的值在mingcheng中是否存在，如果存在，就把那个值给了数据库查询
				Log.i("searchAcitivity", "11111");
				// Boolean boolean1 = null;
				int count1 = 0;
				for (int i = 0; i < JingdianName.names.length; i++) {
					Log.i("searchAcitivity", "22222");
					if (JingdianName.names[i].equals(str1)) {
						Log.i("searchAcitivity", "33333");
						string = JingdianName.names[i];
						// 方法放在for循环外面会有问题
						listViewJump(string);
						Log.i("searchAcitivity", "44444");
						// boolean1 = true;
						count1++;
						break;
					}
				}
				if (count1 == 0) {
					Log.i("searchAcitivity", "88888");
					Toast.makeText(getApplicationContext(), "没有该景点",
							Toast.LENGTH_SHORT).show();
				}
				Log.i("searchAcitivity", "99999");

				break;
			default:
				break;
		}
	}

	private void listViewJump(String str) {
//		String sql = "select * from jingdian where mingcheng = '" + str + "'";
//		Cursor c = Contance.SQLITE.rawQuery(sql, null);
//		c.moveToFirst();
//		while (!c.isAfterLast()) {
//			com.example.domain.ImportContance.MINGCHENG = c.getString(c
//					.getColumnIndex("mingcheng"));
//			com.example.domain.ImportContance.JIESHAO = c.getString(c.getColumnIndex("jieshao"));
//			com.example.domain.ImportContance.LEIXING = c.getString(c.getColumnIndex("leixing"));
//			com.example.domain.ImportContance.JIJIE = c.getString(c.getColumnIndex("jijie"));
//			com.example.domain.ImportContance.JIANYI = c.getString(c.getColumnIndex("jianyi"));
//			com.example.domain.ImportContance.MENPIAO = c.getString(c.getColumnIndex("menpiao"));
//			com.example.domain.ImportContance.SHIJIAN = c.getString(c.getColumnIndex("shijian"));
//			com.example.domain.ImportContance.DIZHI = c.getString(c.getColumnIndex("dizhi"));
//			c.moveToNext();
//		}
//
//		c.close();

//		Intent intent = new Intent(SearchActivity.this, SingleActivity.class);
//		intent.putExtra(ContanceKey.KEY_MINGCHENG, com.example.domain.ImportContance.MINGCHENG);
//		intent.putExtra(ContanceKey.KEY_JIESHAO, com.example.domain.ImportContance.JIESHAO);
//		intent.putExtra(ContanceKey.KEY_LEIXING, com.example.domain.ImportContance.LEIXING);
//		intent.putExtra(ContanceKey.KEY_JIJIE, com.example.domain.ImportContance.JIJIE);
//		intent.putExtra(ContanceKey.KEY_JIANYI, com.example.domain.ImportContance.JIANYI);
//		intent.putExtra(ContanceKey.KEY_MENPIAO, com.example.domain.ImportContance.MENPIAO);
//		intent.putExtra(ContanceKey.KEY_SHIJIAN, com.example.domain.ImportContance.SHIJIAN);
//		intent.putExtra(ContanceKey.KEY_DIZHI, com.example.domain.ImportContance.DIZHI);
//		startActivity(intent);
		SearchActivity.this.finish();
	}
}
