package com.tricker.moneycalc2.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class TrickerUtils {
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd",Locale.CHINA);
	public static final int PACKAGE_RESOURCE_PATH = 0;
	public static final int FILES_DIR = 1;
	public static final int FILES_DIR_ABSOLUTE_PATH = 2;
	public static final int DATABASE_PATH = 3;

	/**
	 * 字符串转换成BigDecimal
	 *
	 * @param num
	 * @return
	 */
	public static BigDecimal parseToDecimal(String num) {
		return new BigDecimal(num);
	}

	/**
	 * 设置BigDecimal 精度
	 *
	 * @param num
	 * @param newScale
	 * @param roundingMode
	 * @return
	 */
	public static BigDecimal setScale(BigDecimal num, int newScale, RoundingMode roundingMode) {
		return num.setScale(newScale, roundingMode);
	}

	/**
	 * Toast
	 *
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void showToast(Context context, String text, int duration) {
		Toast.makeText(context, text, duration).show();
		;
	}

	public static void showToast(Context context, String text) {
		if (TextUtils.isEmpty(text)) {
			text = "并没有提示内容！";
		}
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		;
	}

	public static void selectDate(Context context, final EditText editDate) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String data = editDate.getText().toString();
		if (!TextUtils.isEmpty(data)) {
			String strs[] = data.split("/");
			year = Integer.parseInt(strs[0]);
			month = Integer.parseInt(strs[1]) - 1;
			day = Integer.parseInt(strs[2]);
		}
		OnDateSetListener callBack = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				String date = format.format(new Date(view.getCalendarView().getDate()));
				editDate.setText(date);
			}
		};
		DatePickerDialog dlg = new DatePickerDialog(context, callBack, year, month, day);
		dlg.show();
	}

	public static void selectDate(Context context, final EditText editDate, OnDateSetListener callBack) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String data = editDate.getText().toString();
		if (!TextUtils.isEmpty(data)) {
			String strs[] = data.split("/");
			year = Integer.parseInt(strs[0]);
			month = Integer.parseInt(strs[1]) - 1;
			day = Integer.parseInt(strs[2]);
		}
		/*
		 * OnDateSetListener callBack = new OnDateSetListener() {
		 *
		 * @Override public void onDateSet(DatePicker view, int year, int
		 * monthOfYear, int dayOfMonth) { String date = format.format(new
		 * Date(view.getCalendarView().getDate())); editDate.setText(date); } };
		 */
		DatePickerDialog dlg = new DatePickerDialog(context, callBack, year, month, day);
		dlg.show();
	}

	public static String getPath(Context context, int path) {
		switch (path) {
			case PACKAGE_RESOURCE_PATH:
				return context.getPackageResourcePath();
			case FILES_DIR:
				return context.getFilesDir().toString();
			case FILES_DIR_ABSOLUTE_PATH:
				return context.getFilesDir().getAbsolutePath();
			case DATABASE_PATH:
				return context.getDatabasePath("db").getAbsolutePath();
			default:
				return null;
		}
	}

	public static String copyDB(Context context) {
		String result = "success";
		return result;
	}

	public static String coverDB(Context context) {
		String result = "";
		return result;
	}

	public static String copyfile(File fromFile, File toFile, Boolean rewrite) {
		String result="success";
		if (!fromFile.exists()) {
			result="复制的文件不存在";
			return result;
		}

		if (!fromFile.isFile()) {
			result="复制的不是文件";
			return result;
		}

		if (!fromFile.canRead()) {
			result="复制的文件不可读";
			return result;
		}

		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}

		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		// 当文件不存时，canWrite一直返回的都是false

		// if (!toFile.canWrite()) {

		// MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" +
//		 toFile.getPath());

		// Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);

		// return ;

		// }
		java.io.FileInputStream fosfrom=null;
		java.io.FileOutputStream fosto=null;
		try {

			fosfrom = new java.io.FileInputStream(fromFile);
			fosto = new FileOutputStream(toFile);

			byte bt[] = new byte[1024];

			int c;

			while ((c = fosfrom.read(bt)) > 0) {

				fosto.write(bt, 0, c); // 将内容写到新文件当中

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fosfrom!=null){
					fosfrom.close();
				}
				if(fosto!=null){
					fosto.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return result;
	}

	public static String getSystemDate() {
		String date = format.format(Calendar.getInstance().getTime());
		return date;
	}

	public static SimpleDateFormat getDateFormat() {
		return format;
	}

}
