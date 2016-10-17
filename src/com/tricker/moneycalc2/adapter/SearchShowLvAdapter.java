package com.tricker.moneycalc2.adapter;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tricker.moneycalc2.R;
import com.tricker.moneycalc2.model.SearchShowBean;

public class SearchShowLvAdapter extends BaseAdapter {

	private Context mContext;
	private List<SearchShowBean> mdata;

	public SearchShowLvAdapter(Context context, List<SearchShowBean> data) {
		this.mContext = context;
		this.mdata = data;
	}

	@Override
	public int getCount() {
		return mdata.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			// 如果视图等于null，就给个布局
			convertView = View.inflate(mContext,
					R.layout.search_xianshi_lv_item, null);
			// 初始化hodler
			holder = new ViewHolder();
			// 每项的视图布局是一样的
			holder.textView = (TextView) convertView
					.findViewById(R.id.search_xianshi_tv);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.search_xianshi_iv);
			// 给View添加数据,然后用getTag()取出来
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SearchShowBean bean = mdata.get(position);
		holder.textView.setText(bean.getJingdian());
		holder.imageView.setImageDrawable(bean.getImage());

		return convertView;
	}

	/*
	 * 临时的储存器
	 */
	private static class ViewHolder {
		TextView textView;
		ImageView imageView;
	}

}
