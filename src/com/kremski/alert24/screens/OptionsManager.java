package com.kremski.alert24.screens;

import java.util.Map;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.common.collect.Maps;
import com.kremski.alert24.R;

public class OptionsManager {

	public static final String PREFERENCES_NAME = "ALERT24_";

	private static final String PREFERENCE_EVENT_CATEGORIES_DOWNLOADED_STATUS = "EVENT_CATEGORIES_STATUS_KEY";
	private static final String PREFERENCE_REFRESH_RADIUS_IN_METERES = "REFRESH_RADIUS_IN_METERS";
	private static final String PREFERENCE_REFRESH_INTERVAL_IN_MINUTES = "REFRESH_INTERVAL_IN_METERS";
	private static final String PREFERENCE_MAP_TYPE = "MAP_TYPE";
	private static final String PREFERENCE_DEVICE_KEY_ON_APPLICATION_SERVER = "DeviceKey";

	private static final int PREFERENCE_DEFAULT_REFRESH_RADIUS_IN_METERES = 2000;
	private static final int PREFERENCE_DEFAULT_REFRESH_INTERVAL_IN_MINUTES = 5;
	private static final String PREFERENCE_CURRENT_LOCATION_COLOR = "CURRENT_LOCATION_COLOR";
	private static final String PREFERENCE_DEFAULT_CURRENT_LOCATION_COLOR = "#22AA0000";
	private static final String PREFERENCE_FAVORITE_LOCATIONS_COLOR = "FAVORITE_LOCATIONS_COLOR";
	private static final String PREFERENCE_DEFAULT_FAVORITE_LOCATIONS_COLOR = "#220000AA";

	private SharedPreferences preferences = null;
	private Map<String, Integer> mapTypeNameToKeyMapping = Maps.newHashMap(); 
	private Resources res;

	public OptionsManager(SharedPreferences sharedPreferences, Resources res) {
		this.preferences = sharedPreferences;
		this.res = res;

		mapTypeNameToKeyMapping.put(res.getString(R.string.map_type_normal), GoogleMap.MAP_TYPE_NORMAL);
		mapTypeNameToKeyMapping.put(res.getString(R.string.map_type_hybrid), GoogleMap.MAP_TYPE_HYBRID);
		mapTypeNameToKeyMapping.put(res.getString(R.string.map_type_terrain), GoogleMap.MAP_TYPE_TERRAIN);
		mapTypeNameToKeyMapping.put(res.getString(R.string.map_type_satellite), GoogleMap.MAP_TYPE_SATELLITE);

	}

	public void setEventCategoriesDownloadedStatus(boolean status) {
		preferences.edit().putBoolean(PREFERENCE_EVENT_CATEGORIES_DOWNLOADED_STATUS, status).commit();
	}

	public boolean isEventCategoriesDownloaded() {
		return preferences.getBoolean(PREFERENCE_EVENT_CATEGORIES_DOWNLOADED_STATUS, false);
	}

	public void setMapTypeName(String mapType) {
		preferences.edit().putString(PREFERENCE_MAP_TYPE, mapType).commit();
	}

	public String getMapTypeName() {
		return preferences.getString(PREFERENCE_MAP_TYPE, res.getString(R.string.map_type_normal));
	}

	public int getMapTypeKey() {
		String mapTypeName = preferences.getString(PREFERENCE_MAP_TYPE, res.getString(R.string.map_type_normal));
		return mapTypeNameToKeyMapping.get(mapTypeName);
	}

	public void setDeviceKey(String deviceKey) {
		preferences.edit().putString(PREFERENCE_DEVICE_KEY_ON_APPLICATION_SERVER, deviceKey).commit();
	}

	public String getDeviceKey() {
		return preferences.getString(PREFERENCE_DEVICE_KEY_ON_APPLICATION_SERVER, "");
	}
	
	public void setCurrentLocationColor(String color) {
		preferences.edit().putString(PREFERENCE_CURRENT_LOCATION_COLOR, color).commit();
	}

	public int getCurrentLocationColor() {
		return Color.parseColor(preferences.getString(PREFERENCE_CURRENT_LOCATION_COLOR, PREFERENCE_DEFAULT_CURRENT_LOCATION_COLOR)) | 0x22000000;
	}
	
	public void setFavoriteLocationsDefaultColor(String color) {
		preferences.edit().putString(PREFERENCE_FAVORITE_LOCATIONS_COLOR, color).commit();
	}

	public int getFavoritesLocationDefaultColor() {
		return Color.parseColor(preferences.getString(PREFERENCE_FAVORITE_LOCATIONS_COLOR, PREFERENCE_DEFAULT_FAVORITE_LOCATIONS_COLOR)) | 0x22000000;}

	public void setRefreshRadius(int radius) {
		preferences.edit().putInt(PREFERENCE_REFRESH_RADIUS_IN_METERES, radius).commit();
	}

	public int getRefreshRadius() {
		return preferences.getInt(PREFERENCE_REFRESH_RADIUS_IN_METERES, 
				PREFERENCE_DEFAULT_REFRESH_RADIUS_IN_METERES);
	}

	public void setRefreshInterval(int interval) {
		preferences.edit().putInt(PREFERENCE_REFRESH_INTERVAL_IN_MINUTES, interval).commit();
	}

	public int getRefreshInterval() {
		return preferences.getInt(PREFERENCE_REFRESH_INTERVAL_IN_MINUTES, 
				PREFERENCE_DEFAULT_REFRESH_INTERVAL_IN_MINUTES);
	}

	public String getDeviceKeyPreferenceName() {
		return PREFERENCE_DEVICE_KEY_ON_APPLICATION_SERVER;
	}
}