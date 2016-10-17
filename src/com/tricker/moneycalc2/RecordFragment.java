package com.tricker.moneycalc2;

import java.io.File;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.tricker.moneycalc2.db.BaseDao;
import com.tricker.moneycalc2.db.TrickerDB;
import com.tricker.moneycalc2.model.Project;
import com.tricker.moneycalc2.util.TrickerUtils;

import android.app.Activity;
import android.app.Fragment;
//import android.support.v4.app.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class RecordFragment extends Fragment implements OnClickListener, LocationSource, AMapLocationListener {
	private Toolbar mToolbar;
	private Toast mToast;
	private PopupWindow mPopupWindow;
	private EditText editDate, editMoney, editProject, editRemark;
	private Spinner editPercent, editState;
	// private SimpleDateFormat format;
	private Button btnSave;
	private TextView txtLocation;
	private static boolean isEdit;
	private LocationManager locationManager;
	private String provider;
	private static final int SHOW_RESPONSE = 0;
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	// 声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;

	/**
	 * 定位监听
	 */

	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static RecordFragment newInstance(int sectionNumber, Cursor cursor) {

		RecordFragment fragment = new RecordFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		if (cursor != null) {// 说明是从查询界面点击回来的，需要修改
			isEdit = true;
			args.putInt("_id", cursor.getInt(cursor.getColumnIndex("_id")));// _id
			args.putString("project", cursor.getString(cursor.getColumnIndex("project")));// project
			args.putString("money", cursor.getString(cursor.getColumnIndex("money")));// money
			args.putString("date", cursor.getString(cursor.getColumnIndex("date")));// date
			args.putString("percent", cursor.getString(cursor.getColumnIndex("percent")));// percent
			args.putString("remark", cursor.getString(cursor.getColumnIndex("remark")));// remark
			args.putString("state", cursor.getString(cursor.getColumnIndex("state")));// state
		} else {
			isEdit = false;
		}
		fragment.setArguments(args);
		return fragment;
	}

	public RecordFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_record, container, false);
		findViews(rootView);
		if (isEdit) {// 编辑
			setValues();
		}
//		mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//		// App Logo
////        mToolbar.setLogo(R.drawable.app_icon);
//		// 主标题,默认为app_label的名字
//		mToolbar.setTitle("Title");
//		mToolbar.setTitleTextColor(Color.YELLOW);
//		// 副标题
//		mToolbar.setSubtitle("Sub title");
//		mToolbar.setSubtitleTextColor(Color.parseColor("#80ff0000"));
//		//侧边栏的按钮
//		mToolbar.setNavigationIcon(R.drawable.back_normal);
//		//取代原本的actionbar
//		((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
//		//设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
//		//因为setSupportActionBar里面也会setNavigationOnClickListener
//		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mToast.setText("click NavigationIcon");
//				mToast.show();
//			}
//		});
//		//设置toolBar上的MenuItem点击事件
//		mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				switch (item.getItemId()) {
//					case R.id.action_edit:
//						mToast.setText("click edit");
//						break;
//					case R.id.action_share:
//						mToast.setText("click share");
//						break;
//					case R.id.action_overflow:
//						//弹出自定义overflow
//						popUpMyOverflow();
//						return true;
//				}
//				mToast.show();
//				return true;
//			}
//		});
//		//ToolBar里面还可以包含子控件
//		mToolbar.findViewById(R.id.btn_diy).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mToast.setText("点击自定义按钮");
//				mToast.show();
//			}
//		});
//		mToolbar.findViewById(R.id.tv_title).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mToast.setText("点击自定义标题");
//				mToast.show();
//			}
//		});
//		setHasOptionsMenu(true);
		//利用安卓API加google地图定位，并不是非常准确，用高德地图代替
		/*locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(true);
		if (providers.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		}else if(providers.contains(LocationManager.PASSIVE_PROVIDER)){
			provider = LocationManager.PASSIVE_PROVIDER;
		}
		else {
			TrickerUtils.showToast(getActivity(), "尚未开启任何定位服务！");
			provider=null;
		}
		if (provider != null) {
//			locationManager.setTestProviderEnabled("gps",true);
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				showLocation(location);
			}else{
				TrickerUtils.showToast(getActivity(), provider+"暂未获取到位置信息");
			}
			locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
		}*/

		mlocationClient = new AMapLocationClient(getActivity());
		mLocationOption = new AMapLocationClientOption();
		//设置定位监听
		mlocationClient.setLocationListener(this);
		//设置为高精度定位模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);

		mlocationClient.setLocationOption(mLocationOption);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用onDestroy()方法
		// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
		mlocationClient.startLocation();
//		mLocationOption.setOnceLocation(false);
		AMapLocation amapLocation= mlocationClient.getLastKnownLocation();
		if (amapLocation != null
				&& amapLocation.getErrorCode() == 0) {
			txtLocation.setText("1:"+amapLocation.getAddress());
		}
		return rootView;
	}
	/**
	 * 弹出自定义的popWindow
	 */
	public void popUpMyOverflow() {
		//获取状态栏高度
		Rect frame = new Rect();
		((AppCompatActivity) getActivity()).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		//状态栏高度+toolbar的高度
		int yOffset = frame.top + mToolbar.getHeight();
		if (null == mPopupWindow) {
			//初始化PopupWindow的布局
			View popView = ((AppCompatActivity) getActivity()).getLayoutInflater().inflate(R.layout.action_overflow_popwindow, null);
			//popView即popupWindow的布局，ture设置focusAble.
			mPopupWindow = new PopupWindow(popView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, true);
			//必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效
			mPopupWindow.setBackgroundDrawable(new ColorDrawable());
			//点击外部关闭。
			mPopupWindow.setOutsideTouchable(true);
			//设置一个动画。
			mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
			//设置Gravity，让它显示在右上角。
			mPopupWindow.showAtLocation(mToolbar, Gravity.RIGHT | Gravity.TOP, 0, yOffset);
			//设置item的点击监听
			popView.findViewById(R.id.ll_item1).setOnClickListener(this);
			popView.findViewById(R.id.ll_item2).setOnClickListener(this);
			popView.findViewById(R.id.ll_item3).setOnClickListener(this);
		} else {
			mPopupWindow.showAtLocation(mToolbar, Gravity.RIGHT | Gravity.TOP, 0, yOffset);
		}

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//		mapView.onDestroy();
		if (null != mlocationClient) {
			mlocationClient.onDestroy();
		}
		/*if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
//			locationManager.setTestProviderEnabled("gps",false);
		}*/
	}
	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
//		mapView.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
//		mapView.onPause();
		deactivate();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState
		// (outState)，实现地图生命周期管理
//		mapView.onSaveInstanceState(outState);
	}

	/*private void showLocation(final Location location) {
		if (location == null) {
			TrickerUtils.showToast(getActivity(), "暂未获取到位置信息");
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					StringBuilder url = new StringBuilder();
					url.append("http://maps.google.cn/maps/api/geocode/json?latlng=");
					url.append(location.getLatitude()).append(",");
					url.append(location.getLongitude());
					url.append("&sensor=false");
					// TrickerUtils.showToast(getActivity(), url.toString());
					HttpURLConnection connection = null;
					URL uUrl = new URL(url.toString());
					connection = (HttpURLConnection) uUrl.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);

					}
					// Log.e("tricker", response.toString());
					JSONObject json = new JSONObject(response.toString());
					JSONArray resultArray = json.getJSONArray("results");
					if (resultArray.length() > 0) {
						JSONObject subObject = resultArray.getJSONObject(0);
						String address = subObject.getString("formatted_address");
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						message.obj = address;
						handler.sendMessage(message);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}*/

	// private Handler handler = new Handler(new Handler.Callback() {
	// @Override
	// public boolean handleMessage(Message msg) {
	// return false;
	// }
	// }) ;

	/*private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
//			TrickerUtils.showToast(getActivity(), "位置改变！");
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = msg.obj.toString();
				txtLocation.setText(response);
				break;

			default:
				break;
			}
		};
	};*/
	/*private LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			Location location = locationManager.getLastKnownLocation(provider);
			if(location!=null){
				showLocation(location);
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			TrickerUtils.showToast(getActivity(), "你刚关闭了【"+provider+"】定位服务");
		}

		@Override
		public void onLocationChanged(Location location) {
			showLocation(location);

		}
	};*/

	private void setValues() {
		editDate.setText(getArguments().getString("date"));
		editMoney.setText(getArguments().getString("money"));
		editProject.setText(getArguments().getString("project"));
		String percent = getArguments().getString("percent");
		String state = getArguments().getString("state");
		int position = 0;
		if (!TextUtils.isEmpty(percent) && percent.equals("1/2")) {
			position = 1;
		}
		editPercent.setSelection(position);
		editRemark.setText(getArguments().getString("remark"));

		position = 0;
		if (!TextUtils.isEmpty(state) && state.equals("已结算")) {
			position = 1;
		}
		editState.setSelection(position);

		btnSave.setText("修改");
	}

	private void findViews(View rootView) {
		editDate = (EditText) rootView.findViewById(R.id.editDate);
		editMoney = (EditText) rootView.findViewById(R.id.editMoney);
		editProject = (EditText) rootView.findViewById(R.id.editProject);
		editPercent = (Spinner) rootView.findViewById(R.id.editPercent);
		btnSave = (Button) rootView.findViewById(R.id.btnSave);
		editRemark = (EditText) rootView.findViewById(R.id.editRemark);
		editState = (Spinner) rootView.findViewById(R.id.editState);
		btnSave.setText("保存");
		txtLocation = (TextView) rootView.findViewById(R.id.txtLocation);

		// format = new SimpleDateFormat("yyyy/MM/dd");
		// String date = format.format(Calendar.getInstance().getTime());
		editDate.setText(TrickerUtils.getSystemDate());
		editDate.setKeyListener(null);// 相当于设置不可编辑
		editDate.setOnClickListener(this);
		btnSave.setOnClickListener(this);

		txtLocation.setOnClickListener(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//注释
//		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnSave:
				saveData();
				break;
			case R.id.editDate:
				// selectDate();
				TrickerUtils.selectDate(getActivity(), editDate);
				break;
			case R.id.txtLocation:
				MainActivity mainActivity = (MainActivity) getActivity();
				//直接打开地图页
				mainActivity.getmNavigationDrawerFragment().selectItem(2);
				//改变标题
				mainActivity.onSectionAttached(3);
				mainActivity.restoreActionBar();
				break;
			case R.id.ll_item1:
				mToast.setText("哈哈");
				break;
			case R.id.ll_item2:
				mToast.setText("呵呵");
				break;
			case R.id.ll_item3:
				mToast.setText("嘻嘻");
				break;

			default:
				break;
		}
		//点击PopWindow的item后,关闭此PopWindow
		if (null != mPopupWindow && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
		mToast.show();
	}

	private void saveData() {
		BaseDao dao = new BaseDao(getActivity());
//		SQLiteDatabase db = dao.getWritableDatabase();

		String projectName = this.editProject.getText().toString();
		String money = this.editMoney.getText().toString();
		String editDate = this.editDate.getText().toString();
		String editPercent = this.editPercent.getSelectedItem().toString();
		String editRemark = this.editRemark.getText().toString();
		String editState = this.editState.getSelectedItem().toString();
		if (TextUtils.isEmpty(money)) {
			TrickerUtils.showToast(getActivity(), "金额不能为空！");
		}
		if (TextUtils.isEmpty(projectName)) {
			TrickerUtils.showToast(getActivity(), "项目不能为空！");
			return;
		}
		if (TextUtils.isEmpty(editPercent)) {
			TrickerUtils.showToast(getActivity(), "占比不能为空！");
		}
		Project project = new Project();
		project.setProject(projectName);
		project.setMoney(money);
		project.setDate(editDate);
		project.setPercent(editPercent);
		project.setRemark(editRemark);
		project.setState(editState);
		if (isEdit) {
			project.setId(getArguments().getInt("_id"));
			TrickerDB.getInstance(getActivity()).saveProject(project,true);
			// TrickerUtils.showToast(getActivity(), "修改成功！");
			MainActivity mainActivity = (MainActivity) getActivity();
			mainActivity.getmNavigationDrawerFragment().selectItem(1);
		} else {
			TrickerDB.getInstance(getActivity()).saveProject(project);
			TrickerUtils.showToast(getActivity(), "保存成功！");
		}

//		//无论修改和添加都需要备份当前的最新数据库（提取到增删改里）
//		File fromFile = new File(TrickerUtils.getPath(getActivity(), TrickerUtils.DATABASE_PATH));
//		File toFile = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"tricker"+File.separator+"tricker.db");
//		String result =TrickerUtils.copyfile(fromFile, toFile , true);
//		TrickerUtils.showToast(this,Environment.getExternalStorageDirectory().getPath());
//		TrickerUtils.showToast(this, result);
		// getActivity().finish();
	}

	/**
	 * 提取该方法到TrickerUtils
	 */
	/*
	 * private void selectDate() { int year =
	 * Calendar.getInstance().get(Calendar.YEAR); int month =
	 * Calendar.getInstance().get(Calendar.MONTH); int day =
	 * Calendar.getInstance().get(Calendar.DAY_OF_MONTH); String data
	 * =editDate.getText().toString(); if(!TextUtils.isEmpty(data)){ String
	 * strs[] =data.split("/"); year= Integer.parseInt(strs[0]); month=
	 * Integer.parseInt(strs[1])-1; day= Integer.parseInt(strs[2]); }
	 * OnDateSetListener callBack = new OnDateSetListener() {
	 *
	 * @Override public void onDateSet(DatePicker view, int year, int
	 * monthOfYear, int dayOfMonth) { String date = format.format(new
	 * Date(view.getCalendarView().getDate())); editDate.setText(date); } };
	 * DatePickerDialog dlg = new DatePickerDialog(getActivity(), callBack,
	 * year, month, day); dlg.show(); }
	 */

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(getActivity());
			mLocationOption = new AMapLocationClientOption();
			//设置定位监听
			mlocationClient.setLocationListener(this);
			//设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			//设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
		}

	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getErrorCode() == 0) {
//				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				txtLocation.setText("2："+amapLocation.getAddress());
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
				Log.e("AmapErr",errText);
				TrickerUtils.showToast(getActivity(), errText);
			}
		}
	}
}