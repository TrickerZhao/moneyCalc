package com.tricker.moneycalc2.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tricker.moneycalc2.model.City;
import com.tricker.moneycalc2.model.County;
import com.tricker.moneycalc2.model.Project;
import com.tricker.moneycalc2.model.Province;
import com.tricker.moneycalc2.util.TrickerUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class TrickerDB {
	public static final String DB_NAME = "db";
	public static final int VERSION=5;
	private static TrickerDB trickerDB;
	private SQLiteDatabase db;
	private Context context;
	/**
	 * 构造方法私有化
	 * @param context
	 */
	private TrickerDB(Context context){
		BaseDao  dbHelper = new BaseDao(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
		this.context = context;
	}
	/**
	 * 获取实例
	 * @param context
	 * @return
	 */
	public synchronized static TrickerDB getInstance(Context context){
		if(trickerDB==null){
			trickerDB = new TrickerDB(context);
		}
		return trickerDB;
	}
	/**
	 * 存储天气预报省份
	 * @param province
	 */
	public void saveProvice(Province province){
		if(province!=null){
			ContentValues values = new ContentValues();
			values.put("name", province.getName());
			values.put("code", province.getCode());
			db.insert("province", null, values);
		}
	}
	/**
	 * 存储天气预报城市
	 * @param city
	 */
	public void saveCity(City city){
		if(city!=null){
			ContentValues values = new ContentValues();
			values.put("name", city.getName());
			values.put("code", city.getCode());
			values.put("province_id", city.getProvinceId());
			db.insert("city", null, values);
		}
	}
	/**
	 * 存储天气预报县
	 * @param county
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values = new ContentValues();
			values.put("name", county.getName());
			values.put("code", county.getCode());
			values.put("city_id", county.getCityId());
			db.insert("county", null, values);
		}
	}
	public void saveProject(Project project){
		saveProject(project, false);
	}
	/**
	 * 保存及修改Project
	 * @param project
	 * @param isEdit
	 */
	public void saveProject(Project project,boolean isEdit){
		if(project!=null){
			ContentValues values = new ContentValues();
			values.put("project", project.getProject());
			values.put("money", project.getMoney());
			values.put("date", project.getDate());
			values.put("percent", project.getPercent());
			values.put("remark", project.getRemark());
			values.put("state", project.getState());
			if(isEdit){
				db.update(BaseDao.TABLE_PROJECT, values, " _id=?", new String[] { "" + project.getId() });
			}else{
				db.insert(BaseDao.TABLE_PROJECT, null, values);
			}
			//无论修改和添加都需要备份当前的最新数据库
			File fromFile = new File(TrickerUtils.getPath(context, TrickerUtils.DATABASE_PATH));
			File toFile = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"tricker"+File.separator+"tricker.db");
			String result =TrickerUtils.copyfile(fromFile, toFile , true);
		}
	}
	public void deleteProject(int projectId){
		String[] condition = new String[] { projectId + "" };
		db.delete(BaseDao.TABLE_PROJECT, "_id=?", condition);
		File fromFile = new File(TrickerUtils.getPath(this.context, TrickerUtils.DATABASE_PATH));
		File toFile = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"tricker"+File.separator+"tricker.db");
		String result =TrickerUtils.copyfile(fromFile, toFile , true);
	}
	/*public List<Project> loadProjects(){
		List<Project> list = new ArrayList<Project>();
//		Cursor cursor = db.query(BaseDao.TABLE_PROJECT, null,null,null,null,null,null);
		String sql = BaseDao.QUERY_ALL + " order by date desc";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			do {
				Project project = new Project();
				project.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				project.setProject(cursor.getString(cursor.getColumnIndex("project")));
				project.setMoney(cursor.getString(cursor.getColumnIndex("money")));
				project.setDate(cursor.getString(cursor.getColumnIndex("date")));
				project.setPercent(cursor.getString(cursor.getColumnIndex("percent")));
				project.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
				project.setState(cursor.getString(cursor.getColumnIndex("state")));
				list.add(project);
			} while (cursor.moveToNext());

		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}*/
	public Cursor loadProjects(){
		return loadProjects("");
	}
	public Cursor loadProjects(String condition){
		String sql = BaseDao.QUERY_ALL + condition+ " order by date desc";
		return db.rawQuery(sql, null);
	}
	/**
	 * 获取省份
	 * @return
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("province", null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				province.setName(cursor.getString(cursor.getColumnIndex("name")));
				province.setCode(cursor.getString(cursor.getColumnIndex("code")));
				list.add(province);
			} while (cursor.moveToNext());

		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	/**
	 * 根据省份获取所有城市
	 * @param provinceId
	 * @return
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("city", null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
		if(cursor.moveToFirst()){
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				city.setName(cursor.getString(cursor.getColumnIndex("name")));
				city.setCode(cursor.getString(cursor.getColumnIndex("code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());

		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	/**
	 * 根据城市获取所有的县
	 * @param cityId
	 * @return
	 */
	public List<County> loadCounties(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("county", null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
		if(cursor.moveToFirst()){
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				county.setName(cursor.getString(cursor.getColumnIndex("name")));
				county.setCode(cursor.getString(cursor.getColumnIndex("code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());

		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
}
