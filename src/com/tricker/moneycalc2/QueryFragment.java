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
import com.tricker.moneycalc2.util.Constant;
import com.tricker.moneycalc2.util.TrickerUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.Toast;

import static android.R.attr.data;
import static com.amap.api.mapcore.offlinemap.a.b;
import static com.tricker.moneycalc2.R.string.condition;
import static com.tricker.moneycalc2.R.string.count;
import static com.tricker.moneycalc2.R.string.date;

public class QueryFragment extends ListFragment
		implements OnItemLongClickListener, OnKeyListener, OnItemSelectedListener, android.view.View.OnClickListener {
	private SimpleCursorAdapter adapter,adapter2,adapter3;
	private BaseDao dao;
	private TextView txtQueryInfo, txtTotalMoney;
	private EditText editQuery;
	private Spinner spSymbol,editType;
	private Cursor cursor;
//	private boolean isRent=true;
	private int type= -1;

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
		editType = (Spinner) rootView.findViewById(R.id.editType);
		editQuery.setOnClickListener(this);
		editQuery.setOnKeyListener(this);
		spSymbol.setOnItemSelectedListener(this);
		editType.setOnItemSelectedListener(this);
		editQuery.setText(TrickerUtils.getSystemDate());
		if(!MyApplication.getUser().getName().equals("Tricker")){
			editType.setSelection(2);
//			editType.setClickable(false);
			editType.setEnabled(false);//设置不可编辑
		}
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
		Cursor cursor2 =TrickerDB.getInstance(getActivity()).loadMarries();
		Cursor cursor3 =TrickerDB.getInstance(getActivity()).loadSales();
		setCursor(cursor);
		updateTitle(cursor);
		String[] from = new String[] { "project", "money", "date", "percent", "state" };
		int[] to = new int[] { R.id.txtProject, R.id.txtMoney, R.id.txtDate, R.id.txtPercent, R.id.txtState };
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_cell, cursor, from, to,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		String[] from2 = new String[] { "name", "getMoney", "payMoney"};
		int[] to2 = new int[] { R.id.txtProject, R.id.txtMoney,  R.id.txtPercent};
		adapter2 = new SimpleCursorAdapter(getActivity(), R.layout.list_cell, cursor2, from2, to2,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		String[] from3 = new String[] { "type", "money","date" ,"count", "week" };
		int[] to3 = new int[] { R.id.txtProject, R.id.txtMoney,R.id.txtDate,R.id.txtPercent, R.id.txtState};
		adapter3 = new SimpleCursorAdapter(getActivity(), R.layout.list_cell, cursor3, from3, to3,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


		setListAdapter(adapter);

		getListView().setOnItemLongClickListener(this);
	}
	private void updateTitle(Cursor c,int type) {
		// double totalMoney = 0;
		BigDecimal totalMoney = new BigDecimal(0);
		if(type==Constant.RENT){
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				// totalMoney+=c.getDouble(c.getColumnIndex("money"));
				String strMoney = c.getString(c.getColumnIndex("money"));
				totalMoney = totalMoney.add(TrickerUtils.parseToDecimal(strMoney));
			}
		}else if(type==Constant.MARRY){
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				// totalMoney+=c.getDouble(c.getColumnIndex("money"));
				String strMoney = c.getString(c.getColumnIndex("getMoney"));
				totalMoney = totalMoney.add(TrickerUtils.parseToDecimal(strMoney));
			}
		}else if(type==Constant.SALE){
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				String strMoney = c.getString(c.getColumnIndex("money"));
				totalMoney = totalMoney.add(TrickerUtils.parseToDecimal(strMoney));
			}
		}
		totalMoney = TrickerUtils.setScale(totalMoney, 2, RoundingMode.HALF_UP);
		txtQueryInfo.setText("以下是查询信息(共" + c.getCount() + "条)");
		txtTotalMoney.setText("￥"+totalMoney);

//		TrickerUtils.showToast(getActivity(), "数据刷新成功！");
	}
	private void updateTitle(Cursor c) {
		updateTitle(c,Constant.RENT);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		MainActivity mainActivity = (MainActivity) getActivity();
		int count = mainActivity.getClickCount();
		Cursor c = adapter.getCursor();
		int type = Constant.RENT;
		if(editType.getSelectedItem().equals("份子钱")){
			type= Constant.MARRY;
			c = adapter2.getCursor();
		}else if(editType.getSelectedItem().equals("销售额")){
			type = Constant.SALE;
			c = adapter3.getCursor();
		}
		if(count==3){
			c.moveToPosition(position);
			mainActivity.setEditCursor(c);
			//直接打开记录页
			mainActivity.getmNavigationDrawerFragment().selectItem(0);
			//改变标题
			mainActivity.onSectionAttached(1);
			mainActivity.restoreActionBar();
		}else{
			if(type==Constant.SALE){//由于销售额是合并的，所以需要提示每一项有多少个
				String date = c.getString(c.getColumnIndex("date"));
				String saleType = c.getString(c.getColumnIndex("type"));
				String result =TrickerDB.getInstance(getActivity()).getSaleInfo(date,saleType);
				TrickerUtils.showToast(getActivity(),result, Toast.LENGTH_LONG);
			}else{
				TrickerUtils.showToast(getActivity(), c.getString(c.getColumnIndex("remark")));
			}
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
							//判断是删除份子钱还是房租
							if(type==Constant.RENT){
								Cursor c = adapter.getCursor();
								c.moveToPosition(position);
								int projectId = c.getInt(c.getColumnIndex("_id"));
								TrickerDB.getInstance(getActivity()).deleteProject(projectId);
								refreshView();
							}else if(type==Constant.MARRY){
								Cursor c = adapter2.getCursor();
								c.moveToPosition(position);
								int marryId = c.getInt(c.getColumnIndex("_id"));
								TrickerDB.getInstance(getActivity()).deleteMarry(marryId);
								refreshView(Constant.MARRY);
							}else if(type==Constant.SALE){
								TrickerUtils.showToast(getActivity(), "多条数据不允许删除！");
								//不支持删除，因为每条数据可能是多条的合计
//								Cursor c = adapter3.getCursor();
//								c.moveToPosition(position);
//								int saleId = c.getInt(c.getColumnIndex("_id"));
//								TrickerDB.getInstance(getActivity()).deleteSale(saleId);
//								refreshView(Constant.SALE);
							}
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
		refreshView("",Constant.RENT);

	}
	public void refreshView(int type) {
		refreshView("",type);

	}
	private void refreshView(String condition) {
		refreshView(condition,Constant.RENT);
	}
	private void refreshView(String condition,int type) {
		if(type ==Constant.RENT){
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
			setListAdapter(adapter);
			adapter.swapCursor(cursor);
//		adapter.notifyDataSetChanged();
			updateTitle(cursor);
		}else if(type==Constant.MARRY){
//			Cursor cursor =TrickerDB.getInstance(getActivity()).loadProjects(condition);
//			setCursor(cursor);
//			adapter.swapCursor(cursor);

			setListAdapter(adapter2);
			Cursor cursor =TrickerDB.getInstance(getActivity()).loadMarries(condition);
			adapter2.swapCursor(cursor);
			updateTitle(cursor,Constant.MARRY);
		}else if(type ==Constant.SALE){
			setListAdapter(adapter3);
			Cursor cursor =TrickerDB.getInstance(getActivity()).loadSales(condition);
			adapter3.swapCursor(cursor);
			updateTitle(cursor,Constant.SALE);
		}
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
//		TrickerUtils.showToast(getActivity(),"Query");
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
			execQuery(type);

		}
		return false;//设置成true，点击键盘上的   × 会无效
	}


	/**
	 * 房租or份子钱
	 * @param type
     */
	private void execQuery(int type) {
		this.type = type;
		String condition = "";
		String data = editQuery.getText().toString();
		if(type ==Constant.RENT){
			String strSymbol = spSymbol.getSelectedItem().toString();
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
			refreshView(condition);
		}else if(type ==Constant.MARRY){
//			TrickerUtils.showToast(getActivity(),"份子钱查询");
			data="";//不加条件
			condition=" where name like '%"+data+"%' ";
			refreshView(condition,Constant.MARRY);
		}else if(type == Constant.SALE){
//			data="";//不加条件
			condition=" where type like '%"+data+"%' or date like '%"+data+"%' ";
			refreshView(condition,Constant.SALE);
		}
		// 强制关闭输入法
//		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(editQuery.getWindowToken(), 0);
		TrickerUtils.closeKeybord(editQuery,getActivity());
	}
	private void execQuery() {
		execQuery(Constant.RENT);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if(parent.getId()==R.id.editType){
			if(position==0){
				setWidgetVisible(position);
				execQuery(Constant.RENT);
			}else if(position==1){
				setWidgetVisible(position);
				execQuery(Constant.MARRY);
			}else if(position==2){
				setWidgetVisible(position);
				execQuery(Constant.SALE);
			}
		}else if(parent.getId()==R.id.spSymbol){
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
	}

	private void setWidgetVisible(int type) {
		if(type==Constant.RENT){
			spSymbol.setVisibility(View.VISIBLE);
			editQuery.setVisibility(View.VISIBLE);
		}else if(type==Constant.MARRY){
			spSymbol.setVisibility(View.GONE);
			editQuery.setVisibility(View.GONE);
		}else if(type==Constant.SALE){
			spSymbol.setVisibility(View.GONE);
			editQuery.setVisibility(View.VISIBLE);
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
				selectDate(v);
				break;

			default:
				break;
		}
	}

	private void selectDate(View view) {
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


		}else{
			//暂时屏蔽掉搜索历史功能
//			Intent intent = new Intent(getActivity(), SearchActivity.class);
//			startActivity(intent);

		}
	}
//	private void setProjectVisible(boolean b) {
//		execQuery(b);
//		if(b){
////			tableLayoutProject.setVisibility(View.VISIBLE);
////			tableLayoutMarry.setVisibility(View.GONE);
//		}else{
////			tableLayoutProject.setVisibility(View.GONE);
////			tableLayoutMarry.setVisibility(View.VISIBLE);
//		}
//	}
}