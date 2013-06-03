package com.kremski.alert24.database;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.Event.EventBuilder;
import com.kremski.alert24.domain.EventCategory;

public class EventsTable extends DefaultTableHelper<Event> {

	public static final String TABLE_NAME = "events";

	private static final String KEY_NAME = "event_name";
	private static final String KEY_DESCRIPTION = "event_description";
	private static final String KEY_DATABASE_ID = "event_database_id";
	private static final String KEY_EVENT_CATEGORY_ID = "event_category_id";
	private static final String KEY_ADDRESS = "event_address";
	private static final String KEY_LONGITUDE = "event_longitude";
	private static final String KEY_LATITUDE = "event_latitude";
	private static final String KEY_PHOTO_PATH = "event_photo_path";
	private static final String KEY_IS_SENT_FLAG = "event_is_sent_flag";
	
	private final EventBuilder eventBuilder = new EventBuilder();
	
	public static final String CREATE_TABLE_QUERY = "create table if not exists " + TABLE_NAME + " "
			+ "( " + KEY_NAME + " text not null, "
			+ KEY_DESCRIPTION + " text not null, " 
			+ KEY_DATABASE_ID + " integer primary key autoincrement, " 
			+ KEY_IS_SENT_FLAG + " integer, " 
			+ KEY_EVENT_CATEGORY_ID + " integer, " 
			+ KEY_ADDRESS + " text, " 
			+ KEY_LONGITUDE + " text not null, " 
			+ KEY_LATITUDE + " text not null, " 
			+ KEY_PHOTO_PATH + " text, "
			+ "FOREIGN KEY (" + KEY_EVENT_CATEGORY_ID + ") references  " + EventCategoriesTable.TABLE_NAME + 
			" ( " + EventCategoriesTable.KEY_DATABASE_ID + "));";
	
	public EventsTable(Context context) {
		super(context);
	}

	@Override
	public Event getHelperObject() {
		return eventBuilder.build();
	}

	@Override
	protected Cursor getCursorWithRecord(Event record) {
		return query(getTableName(), null, KEY_DATABASE_ID + "= ?", new String[]{Long.toString(record.getDatabaseId())}, null, null, null);
	}
	

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}
	
	private List<Event> getAll(String whereSql) {
		String query = String.format("select * from %s LEFT OUTER JOIN %s ON %s = %s " + whereSql , TABLE_NAME, 
				EventCategoriesTable.TABLE_NAME, KEY_EVENT_CATEGORY_ID, EventCategoriesTable.KEY_DATABASE_ID);
		
		Cursor cursor = sqliteDatabase.rawQuery(query, null);
		
		List<Event> events = getRecordsFromCursor(cursor); 
		
		if (cursor != null) {
			cursor.close();
		}
		
		return events;
	}

	@Override
	protected Event getRecordFromCursor(Cursor cursor) {
		int databaseId = cursor.getInt(cursor.getColumnIndex(KEY_DATABASE_ID));
		String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
		String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
		double latitude= cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE));
		double longitude = cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE));
		String photoPath = cursor.getString(cursor.getColumnIndex(KEY_PHOTO_PATH));
		String address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
		boolean isSentFlag = (cursor.getInt(cursor.getColumnIndex(KEY_IS_SENT_FLAG)) == 1);
		
		return eventBuilder.databaseId(databaseId)
				.name(name)
				.descritpion(description)
				.address(address)
				.category(getEventCategoryFromCursor(cursor))
				.latitude(latitude)
				.sent(isSentFlag)
				.longitude(longitude)
				.photoPath(photoPath).build();
		
	}
	
	private EventCategory getEventCategoryFromCursor(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex(EventCategoriesTable.KEY_NAME));
		String iconUrl = cursor.getString(cursor.getColumnIndex(EventCategoriesTable.KEY_ICON_URL));
		long databaseId = cursor.getLong(cursor.getColumnIndex(EventCategoriesTable.KEY_DATABASE_ID));
		long databaseParentId = cursor.getLong(cursor.getColumnIndex(EventCategoriesTable.KEY_DATABASE_PARENT_ID));
		String serverId = cursor.getString(cursor.getColumnIndex(EventCategoriesTable.KEY_SERVER_ID));
		
		return new EventCategory(name, iconUrl, databaseId, databaseParentId, serverId);
	}

	@Override
	protected ContentValues createContentValuesFromRecord(Event record) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, record.getName());
		contentValues.put(KEY_DESCRIPTION, record.getDescription());
		contentValues.put(KEY_LATITUDE, record.getLatitude());
		contentValues.put(KEY_LONGITUDE, record.getLongitude());
		contentValues.put(KEY_PHOTO_PATH, record.getPhotoPath());
		contentValues.put(KEY_IS_SENT_FLAG, record.isSent());
		contentValues.put(KEY_ADDRESS, record.getAddress());
		EventCategory category = record.getCategory();
		if (category != null) {
			contentValues.put(KEY_EVENT_CATEGORY_ID, category.getDatabaseId());
		}
		return contentValues;
	}

	@Override
	protected String getIdKey() {
		return KEY_DATABASE_ID;
	}

	public List<Event> getAllNotSentEvents() {
		return getAll(" WHERE " + KEY_IS_SENT_FLAG + " = 0");
	}
	
	public List<Event> getAllSentEvents() {
		return getAll(" WHERE " + KEY_IS_SENT_FLAG + " = 1");
	}

}
