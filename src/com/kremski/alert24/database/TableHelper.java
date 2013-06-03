package com.kremski.alert24.database;

import java.util.List;

import com.google.gson.reflect.TypeToken;

public interface TableHelper<T extends TableRecord>{
	public void openDatabase();
	public void closeDatabase();
	public void update(T newRecordData);
	public void delete(T recordToDelete);
	public long insert(T newRecord);
	public T getHelperObject();
	public List<T> getAll();
	public void deleteAll();
}
