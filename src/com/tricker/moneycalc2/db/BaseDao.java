package com.tricker.moneycalc2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB类
 */
public class BaseDao extends SQLiteOpenHelper {
	public static final String TABLE_PROJECT = "PROJECT";
	public static final String QUERY_ALL = "select * from " + BaseDao.TABLE_PROJECT;
	public static final String CREATE_PROJECT = "create table project (_id integer primary key autoincrement,"
			+ "project text default 0,money int default 0,date date,percent real,remark text  default \"备注\","
			+ "state text default \"未结算\")";
	public static final String CREATE_PROVINCE="create table province(_id integer primary key autoincrement,"
			+ "name text,code text)";
	public static final String CREATE_CITY="create table city(_id integer primary key autoincrement,"
			+ "name text,code text,province_id integer)";
	public static final String CREATE_COUNTY="create table county(_id integer primary key autoincrement,"
			+ "name text,code text,city_id integer)";

	public BaseDao(Context context) {
		super(context, "db", null, TrickerDB.VERSION);
	}
	public BaseDao(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROJECT);
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//版本升级，每个版本修改的  升级 sql，不加break，避免跳版本升级出现问题
		switch (oldVersion) {
			case 1:
				db.execSQL("alter table project add remark text  default \"备注\"");
			case 2:
				db.execSQL("alter table project add state text default \"未结算\"");
			case 3:
				//天气需要的表
				db.execSQL(CREATE_PROVINCE);
				db.execSQL(CREATE_CITY);
				db.execSQL(CREATE_COUNTY);
			case 4:
				//由于天气表字段错误
				db.execSQL("drop table city");
				db.execSQL(CREATE_CITY);
			case 5:
			default:
				break;
		}

	}

}
