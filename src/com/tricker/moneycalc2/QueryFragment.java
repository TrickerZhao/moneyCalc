package com.tricker.moneycalc2;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tricker.moneycalc2.db.BaseDao;
import com.tricker.moneycalc2.db.TrickerDB;
import com.tricker.moneycalc2.model.Project;
import com.tricker.moneycalc2.util.TrickerUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class QueryFragment extends ListFragment
		implements OnItemLongClickListener, OnKeyListener, OnItemSelectedListener, android.view.View.OnClickListener {
	private SimpleCursorAdapter adapter;
	private BaseDao dao;
	private TextView txtQueryInfo, txtTotalMoney;
	private EditText editQuery;
	private Spinner spSymbol;
	private Cursor cursor;


	public Cursor getCursor() {
		return cursor;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static QueryFragment newInstance(int sectionNumber) {
		QueryFragment fragment = new QueryFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public QueryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_query, container, false);
		txtQueryInfo = (TextView) rootView.findViewById(R.id.queryInfo);
		txtTotalMoney = (TextView) rootView.findViewById(R.id.countMoney);
		editQuery = (EditText) rootView.findViewById(R.id.editQuery);
		spSymbol = (Spinner) rootView.findViewById(R.id.spSymbol);
		editQuery.setOnClickListener(this);
		editQuery.setOnKeyListener(this);
		spSymbol.setOnItemSelectedListener(this);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*dao = new BaseDao(getActivity());
		SQLiteDatabase db = dao.getReadableDatabase();
		String sql = BaseDao.QUERY_ALL + " order by date desc";
		Cursor c = db.rawQuery(sql, null);
		setCursor(c);
		updateTitle(c);

		String[] from = new String[] { "project", "money", "date", "percent", "state" };
		int[] to = new int[] { R.id.txtProject, R.id.txtMoney, R.id.txtDate, R.id.txtPercent, R.id.txtState };
		// adapter = new SimpleCursorAdapter(context, layout, c, from, to);
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_cell, c, from, to,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(adapter);

		getListView().setOnItemLongClickListener(this);
		db.close();*/

		Cursor cursor =TrickerDB.getInstance(getActivity()).loadProjects();
		setCursor(cursor);
		updateTitle(cursor);
		String[] from = new String[] { "project", "money", "date", "percent", "state" };
		int[] to = new int[] { R.id.txtProject, R.id.txtMoney, R.id.txtDate, R.id.txtPercent, R.id.txtState };
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_cell, cursor, from, to,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(adapter);

		getListView().setOnItemLongClickListener(this);
	}

	private void updateTitle(Cursor c) {
		// double totalMoney = 0;
		BigDecimal totalMoney = new BigDecimal(0);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			// totalMoney+=c.getDouble(c.getColumnIndex("money"));
			String strMoney = c.getString(c.getColumnIndex("money"));
			totalMoney = totalMoney.add(TrickerUtils.parseToDecimal(strMoney));
		}
		totalMoney = TrickerUtils.setScale(totalMoney, 2, RoundingMode.HALF_UP);
		// while 循环 在删除数据后刷新会发生问题，暂时不知道怎么回事
		// while (c.moveToNext()) {
		// totalMoney+=c.getDouble(c.getColumnIndex("money"));
		// }
		txtQueryInfo.setText("以下是查询信息(共" + c.getCount() + "条)");
		txtTotalMoney.setText(totalMoney + "");

//		TrickerUtils.showToast(getActivity(), "数据刷新成功！");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		MainActivity mainActivity = (MainActivity) getActivity();
		int count = mainActivity.getClickCount();
		Cursor c = adapter.getCursor();
		if(count==3){
			c.moveToPosition(position);
			mainActivity.setEditCursor(c);
			//直接打开记录页
			mainActivity.getmNavigationDrawerFragment().selectItem(0);
			//改变标题
			mainActivity.onSectionAttached(1);
			mainActivity.restoreActionBar();
		}else{
			TrickerUtils.showToast(getActivity(), c.getString(c.getColumnIndex("remark")));
		}

	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		final Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示");
		builder.setMessage("真的要删除吗？\n该操作不可恢复！！！");
		builder.setPositiveButton("删除", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final EditText password = new EditText(getActivity());
				// password.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
				builder.setTitle("请输入密码");
				builder.setMessage(null);
				builder.setView(password);
				builder.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
						int minute = Calendar.getInstance().get(Calendar.MINUTE);
						int total = hour + minute;
						if (password.getText() != null && password.getText().toString().equals(String.valueOf(total))) {
							Cursor c = adapter.getCursor();
							c.moveToPosition(position);
							int projectId = c.getInt(c.getColumnIndex("_id"));
							TrickerDB.getInstance(getActivity()).deleteProject(projectId);
//							SQLiteDatabase db = dao.getWritableDatabase();
//							String[] condition = new String[] { projectId + "" };
//							db.delete(BaseDao.TABLE_PROJECT, "_id=?", condition);
//							db.close();
							refreshView();
							//删除数据成功后需要备份数据库(移动到具体的增删改)
//							File fromFile = new File(TrickerUtils.getPath(getActivity(), TrickerUtils.DATABASE_PATH));
//							File toFile = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"tricker"+File.separator+"tricker.db");
////							TrickerUtils.showToast(this,Environment.getExternalStorageDirectory().getPath());
//							String result =TrickerUtils.copyfile(fromFile, toFile , true);
						} else {
							TrickerUtils.showToast(getActivity(), "Are You Kidding Me??");
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

	public void refreshView() {
		refreshView("");

	}

	private void refreshView(String condition) {
//		String sql = BaseDao.QUERY_ALL + condition + " order by date desc";
//		SQLiteDatabase db = dao.getReadableDatabase();
//		Cursor c = db.rawQuery(sql, null);
//		setCursor(c);
//		// 更新title
//		adapter.swapCursor(c);
//		updateTitle(c);
//
//		db.close();
		Cursor cursor =TrickerDB.getInstance(getActivity()).loadProjects(condition);
		setCursor(cursor);
		adapter.swapCursor(cursor);
//		adapter.notifyDataSetChanged();
		updateTitle(cursor);
	}

	/*
	 * 处理提示，以及变色，已有官方属性 hint替代
	 */
	/*
	 * @Override public void onClick(View v) { String data =
	 * editQuery.getText().toString(); if (!TextUtils.isEmpty(data) &&
	 * data.equals("查询")) { editQuery.setText("");
	 * editQuery.setTextColor(Color.BLACK); } }
	 */

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		// 用户点击回车键，并且是弹起操作！
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
			execQuery();
		}
		return false;//设置成true，点击键盘上的   × 会无效
	}

	private void execQuery() {
		String data = editQuery.getText().toString();
		String strSymbol = spSymbol.getSelectedItem().toString();
		String condition = "";
		if (strSymbol == null || strSymbol.equals("")) {// like
			condition = " where project like '%" + data + "%'  or state like '%" + data + "%' or date like '%"
					+ data + "%'";
		} else if (strSymbol.equals("!")) {// 非 针对时间
			condition = " where date not like '%" + data + "%'";
		} else if (strSymbol.equals(">")) {// 大于 针对时间
			condition = " where date > '" + data + "'";
		} else if (strSymbol.equals("<")) {// ＜小于 针对时间
			condition = " where date < '" + data + "'";
		} else if (strSymbol.equals("=")) {// 等于 针对时间
			condition = " where date = '" + data + "'";
		}
		/*
		 * //查询条件匹配项目、日期、状态 String condition =
		 * " where project like '%"+data+"%'  or state like '%"
		 * +data+"%' or date like '%"+data+"%'";
		 * //判断如果输入的是日期，则按照日期查询，如果对日期取反，就按照非当前日期查询，如果是<日期就小于，＞日期就大于 String
		 * strData = data.replace("/", ""); int count = data.length() -
		 * strData.length(); if(count==2){//简单判断，如果包含2个斜杠就是日期
		 * if(data.startsWith("!")){ data=data.substring(1);
		 * condition=" where date not like '%"+data+"%'"; }else
		 * if(data.startsWith(">")){ data=data.substring(1);
		 * condition=" where date > '"+data+"'"; }else
		 * if(data.startsWith("<")){ data=data.substring(1);
		 * condition=" where date < '"+data+"'"; }else{
		 * condition=" where date like '%"+data+"%'"; } }
		 */
		// 强制关闭输入法
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editQuery.getWindowToken(), 0);
		refreshView(condition);

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
				setEditDate(false);
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				setEditDate(true);
				break;

			default:
				break;
		}
	}

	private void setEditDate(boolean isDate) {
		if (isDate) {
			editQuery.setText(TrickerUtils.getSystemDate());
			editQuery.setFocusable(false);
			editQuery.setFocusableInTouchMode(false);
		} else {
			editQuery.setFocusableInTouchMode(true);
			editQuery.setFocusable(true);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.editQuery:
				selectDate();
				break;

			default:
				break;
		}
	}

	private void selectDate() {
		if(!editQuery.isFocusableInTouchMode()){//弹出日期选择器
			OnDateSetListener callBack = new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String date = TrickerUtils.getDateFormat().format(new Date(view.getCalendarView().getDate()));
					editQuery.setText(date);
					execQuery();
				}
			};
			TrickerUtils.selectDate(getActivity(), editQuery,callBack);


		}
	}
}