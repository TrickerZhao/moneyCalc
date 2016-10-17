/*
package com.example.activity;


import com.tricker.moneycalc2.MainActivity;
import com.tricker.moneycalc2.R;
import com.tricker.moneycalc2.model.ContanceKey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleActivity extends Activity implements OnClickListener {

	private ImageButton single_back;
	TextView tv_mingcheng, tv_jieshao, tv_jijie, tv_leixing, tv_jianyi,
			tv_shijian, tv_menpiao, tv_dizhi;
	TextView single_title_name, single_content_xiangqing;
	public String mingcheng, jieshao, leixing, jijie, jianyi, menpiao, shijian,
			dizhi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_activity);

		init();

	}

	private void init() {
		single_back = (ImageButton) findViewById(R.id.single_back);
		single_back.setOnClickListener(this);

		tv_mingcheng = (TextView) findViewById(R.id.single_content_mingcheng_content);
		tv_jieshao = (TextView) findViewById(R.id.single_content_jieshao_content);
		tv_leixing = (TextView) findViewById(R.id.single_content_leixing_content);
		tv_jijie = (TextView) findViewById(R.id.single_content_jijie_content);
		tv_jianyi = (TextView) findViewById(R.id.single_content_jianyi_content);
		tv_menpiao = (TextView) findViewById(R.id.single_content_menpiao_content);
		tv_shijian = (TextView) findViewById(R.id.single_content_shijian_content);
		tv_dizhi = (TextView) findViewById(R.id.single_content_dizhi_content);

		single_title_name = (TextView) findViewById(R.id.single_title_name);
		single_content_xiangqing = (TextView) findViewById(R.id.single_content_xiangqing);
		single_content_xiangqing.setOnClickListener(this);

		Intent intent = getIntent();
		mingcheng = intent.getStringExtra(ContanceKey.KEY_MINGCHENG);
		jieshao = intent.getStringExtra(ContanceKey.KEY_JIESHAO);
		leixing = intent.getStringExtra(ContanceKey.KEY_LEIXING);
		jijie = intent.getStringExtra(ContanceKey.KEY_JIJIE);
		jianyi = intent.getStringExtra(ContanceKey.KEY_JIANYI);
		menpiao = intent.getStringExtra(ContanceKey.KEY_MENPIAO);
		shijian = intent.getStringExtra(ContanceKey.KEY_SHIJIAN);
		dizhi = intent.getStringExtra(ContanceKey.KEY_DIZHI);

		tv_mingcheng.setText(mingcheng);
		tv_jieshao.setText(jieshao);
		tv_leixing.setText(leixing);
		tv_jijie.setText(jijie);
		tv_jianyi.setText(jianyi);
		tv_menpiao.setText(menpiao);
		tv_shijian.setText(shijian);
		tv_dizhi.setText(dizhi);

		single_title_name.setText(mingcheng);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.single_back:
				Intent intent = new Intent(SingleActivity.this, MainActivity.class);
				startActivity(intent);
				SingleActivity.this.finish();
				break;
			case R.id.single_content_xiangqing:
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				// 获得layout的填充器
				// LayoutInflater inflater = this.getLayoutInflater();
				// View view = inflater.inflate(R.layout.content_dialog, null);
				// dialog.setView(view);
				LayoutInflater inflater = LayoutInflater.from(this);
				View view = inflater.inflate(R.layout.content_dialog, null);
				builder.setView(view);

				TextView dialog_tv = (TextView) view.findViewById(R.id.content_tv);
				dialog_tv.setText("\t\t" + jieshao);
				ImageView dialog_close = (ImageView) view
						.findViewById(R.id.close_dialog);
				// Intent intent2 = getIntent();
				// String xinxi = intent2.getStringExtra(ContanceKey.KEY_JIESHAO);
				// textView.setText("\t\t" + xinxi);
				builder.create();
				// 先获得一个dialog对象
				final AlertDialog dialog = builder.show();
				dialog_close.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(SingleActivity.this, MainActivity.class);
			startActivity(intent);
			SingleActivity.this.finish();
			// 这里不需要执行父类的点击事件，所以直接return
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
*/
