package com.kremski.alert24.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.domain.PointOfInterest;

public class PointsOfInterestTable extends DefaultTableHelper<PointOfInterest> {

	public static final String TABLE_NAME = "points_of_interest";

	public static final String KEY_NAME = "point_of_interest_name";
	public static final String KEY_LATITUDE = "point_of_interest_latitude";
	public static final String KEY_LONGITUDE = "point_of_interest_longitude";
	public static final String KEY_ADDRESS = "point_of_interest_address";
	public static final String KEY_CIRCLE_COLOR = "point_of_interest_circle_color";
	public static final String KEY_DATABASE_ID = "point_of_interest_id";
	
	public static final String CREATE_TABLE_QUERY = "create table if not exists " + TABLE_NAME + " "
			+ "( " + KEY_NAME + " text not null, "
			+ KEY_LONGITUDE + " text not null, "
			+ KEY_LATITUDE + " text not null, "
			+ KEY_ADDRESS + " text not null, " 
			+ KEY_CIRCLE_COLOR + " text not null, " 
			+ KEY_DATABASE_ID + " integer primary key autoincrement); " ;
	
	public PointsOfInterestTable(Context context) {
		super(context);
	}

	@Override
	public PointOfInterest getHelperObject() {
		return new PointOfInterest(0, "", null, null, -1);
	}

	@Override
	protected Cursor getCursorWithRecord(PointOfInterest record) {
		return query(getTableName(), null, KEY_DATABASE_ID + "= ?", new String[]{Long.toString(record.getDatabaseId())}, null, null, null);
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected PointOfInterest getRecordFromCursor(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
		long databaseId = cursor.getLong(cursor.getColumnIndex(KEY_DATABASE_ID));
		double longitude = cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE));
		double latitude = cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE));
		String address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
		int circleColor = cursor.getInt(cursor.getColumnIndex(KEY_CIRCLE_COLOR));
		
		return new PointOfInterest(databaseId, name, new LatLng(latitude, longitude), address, circleColor);
	}

	@Override
	protected ContentValues createContentValuesFromRecord(PointOfInterest record) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, record.getName());
		contentValues.put(KEY_LATITUDE, record.getLatitude());
		contentValues.put(KEY_LONGITUDE, record.getLongitude());
		contentValues.put(KEY_ADDRESS, record.getAddress());
		contentValues.put(KEY_CIRCLE_COLOR, record.getCircleColor());
		return contentValues;
	}

	@Override
	protected String getIdKey() {
		return KEY_DATABASE_ID;
	}
}
