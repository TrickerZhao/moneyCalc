package com.tricker.moneycalc2.model;

import android.graphics.drawable.Drawable;

public class HistoryShowBean {
	private int _id;
	private String jingdian;
	private Drawable image;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public String getJingdian() {
		return jingdian;
	}

	public void setJingdian(String jingdian) {
		this.jingdian = jingdian;
	}
}
