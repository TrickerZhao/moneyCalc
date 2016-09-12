package com.tricker.moneycalc2.util;

public interface HttpCallbackListener {
	void onFinish(String response);
	void onError(Exception e);
}
