package com.kremski.alert24.views;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.OptionsManager;
import com.kremski.alert24.serwer.service.DefaultWorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerDownloadingEventCategories;
import com.kremski.alert24.serwer.service.WorkerRegisteringDevice;
import com.kremski.alert24.serwer.service.WorkerResult;

public class OptionsFragment extends Fragment{

	private Spinner choosableIntervalOptions;
	private Spinner choosableRadiusOptions;
	private Spinner choosableMapTypeOptions;
	private View currentLocationColorPicker;
	private View favoriteLocationsColorPicker;

	private TextView registerStatusLabel;
	private View registerButton;

	private TextView eventCategoriesStatusLabel;
	private View downloadEventCategoriesButton;

	private OnOptionsChangeListener onOptionsChangeListener;

	private String[] allIntervalOptions;
	private String[] allRadiusOptions;
	private String[] allMapTypeOptions;

	private OptionsManager optionsManager;
	private Alert24Screen activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout =  inflater.inflate(R.layout.options_fragment, container);

		setUpRegisterStatusLabel(layout);
		setUpRegisterButton(layout);

		setUpEventCategoriesStatusLabel(layout);
		setUpDownloadEventCategoriesButton(layout);

		setUpIntervalOptions(layout);
		setUpRadiusOptions(layout);

		setUpMapTypeOptions(layout);

		setUpCurrentLocationColorPicker(layout);
		setUpFavoriteLocationsColorPicker(layout);

		return layout;
	}

	private void setUpRegisterStatusLabel(View layout) {
		registerStatusLabel = (TextView)layout.findViewById(R.id.register_status);
	}

	private void setUpRegisterButton(View layout) {
		registerButton = (View)layout.findViewById(R.id.register_button);
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!optionsManager.getDeviceKey().equals("")) {
					activity.showInfoDialog(R.string.already_registered);
				} else {
					registerDevice();
				}
			}
		});
	}

	private void registerDevice() {
		activity.registerWorkerResultHandler(WorkerRegisteringDevice.class.toString(), new DeviceRegisteredHandler());
		WorkerRegisteringDevice worker = new WorkerRegisteringDevice(activity, activity.getServerUtilities());
		activity.executeWorker(worker);
	}

	private void setUpEventCategoriesStatusLabel(View layout) {
		eventCategoriesStatusLabel = (TextView)layout.findViewById(R.id.event_categories_status);
	}

	private void setUpDownloadEventCategoriesButton(View layout) {
		downloadEventCategoriesButton = (View)layout.findViewById(R.id.download_event_categories_button);
		downloadEventCategoriesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (optionsManager.isEventCategoriesDownloaded()) {
					activity.showInfoDialog(R.string.event_categories_already_downloaded);
				} else {
					downloadEventCategories();
				}
			}
		});
	}

	private void downloadEventCategories() {
		activity.registerWorkerResultHandler(WorkerDownloadingEventCategories.class.toString(), new EventCategoriesDownloadedHandler());
		WorkerDownloadingEventCategories worker = new WorkerDownloadingEventCategories(
				activity.getEventCategoryImageManager(), activity, activity.getServerUtilities(), activity.getEventCategoriesTableHelper());
		activity.executeWorker(worker);
	}

	private void setUpIntervalOptions(View layout) {
		choosableIntervalOptions = (Spinner)layout.findViewById(R.id.refresh_interval_options);
		allIntervalOptions = getResources().getStringArray(R.array.refresh_interval_options);
		choosableIntervalOptions.setOnItemSelectedListener(createListenerForIntervalSelection());
	}

	private OnItemSelectedListener createListenerForIntervalSelection() {
		return new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View selectedItem,
					int position, long arg3) {
				int selectedInterval = getMinutesFromIntervalOption(allIntervalOptions[position]);
				optionsManager.setRefreshInterval(selectedInterval);
				if (onOptionsChangeListener != null) {
					onOptionsChangeListener.onRefreshIntervalChange(selectedInterval);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private void setUpRadiusOptions(View layout) {
		choosableRadiusOptions = (Spinner)layout.findViewById(R.id.refresh_radius_options);
		allRadiusOptions = getResources().getStringArray(R.array.refresh_radius_options);
		choosableRadiusOptions.setOnItemSelectedListener(createListenerForRefreshRadiusSelection());
	}

	private OnItemSelectedListener createListenerForRefreshRadiusSelection() {
		return new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View selectedItem,
					int position, long arg3) {
				int radius = getMetersFromRadiusOption(allRadiusOptions[position]);
				optionsManager.setRefreshRadius(radius);
				if (onOptionsChangeListener != null) {
					onOptionsChangeListener.onRefreshRadiusChange(radius);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private int getMetersFromRadiusOption(String radiusOption) {
		int radiusInMeters = Integer.parseInt(radiusOption.replaceAll("km", "")) * 1000;
		return radiusInMeters;
	}

	private int getMinutesFromIntervalOption(String intervalOption) {
		int refreshIntervalInMinutes = Integer.parseInt(intervalOption.replaceAll("min", ""));
		return refreshIntervalInMinutes;
	}

	private void setUpMapTypeOptions(View layout) {
		choosableMapTypeOptions = (Spinner)layout.findViewById(R.id.map_type_options);
		allMapTypeOptions = getResources().getStringArray(R.array.map_type_options);
		choosableMapTypeOptions.setOnItemSelectedListener(createListenerForMapTypeSelection());
	}

	private OnItemSelectedListener createListenerForMapTypeSelection() {
		return new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View selectedItem,
					int position, long arg3) {
				String selectedMapType = allMapTypeOptions[position]; 
				optionsManager.setMapTypeName(selectedMapType);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private void setUpCurrentLocationColorPicker(View layout) {
		currentLocationColorPicker = (View)layout.findViewById(R.id.current_location_color_picker);
		currentLocationColorPicker.setOnClickListener(createCurrentLocationColorPickerListener());
	}

	private View.OnClickListener createCurrentLocationColorPickerListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialogToCurrentLocationColor();
			}
		};
	}

	private void showDialogToCurrentLocationColor() {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), optionsManager.getCurrentLocationColor(), new OnAmbilWarnaListener()
		{
			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
			}
			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {
				String hexColor = Integer.toHexString(color);
				optionsManager.setCurrentLocationColor("#66" + hexColor.substring(2, hexColor.length()));
				currentLocationColorPicker.setBackgroundColor(optionsManager.getCurrentLocationColor());
			}
		});
		dialog.show();
	}

	private void setUpFavoriteLocationsColorPicker(View layout) {
		favoriteLocationsColorPicker = (View)layout.findViewById(R.id.favorite_locations_color_picker);
		favoriteLocationsColorPicker.setOnClickListener(createFavoriteLocationsColorPickerListener());
	}

	private View.OnClickListener createFavoriteLocationsColorPickerListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialogToPickFavoriteLocationsColor();
			}
		};
	}

	private void showDialogToPickFavoriteLocationsColor() {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), optionsManager.getFavoritesLocationDefaultColor(), new OnAmbilWarnaListener()
		{
			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
			}
			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {
				String hexColor = Integer.toHexString(color);
				optionsManager.setFavoriteLocationsDefaultColor("#66" + hexColor.substring(2, hexColor.length()));
				favoriteLocationsColorPicker.setBackgroundColor(optionsManager.getFavoritesLocationDefaultColor());
			}
		});
		dialog.show();
	}

	public void setOnOptionsChangeListener(OnOptionsChangeListener onOptionsChangeListener) {
		this.onOptionsChangeListener = onOptionsChangeListener;
	}

	public void setOptionsManager(OptionsManager optionsManager) {
		this.optionsManager = optionsManager;
		setUpInitialInterval();
		setUpInitialRadius();
		setUpInitialMapType();
		setUpRegisterStatus();
		setUpEventCategoriesStatus();
		currentLocationColorPicker.setBackgroundColor(optionsManager.getCurrentLocationColor());
		favoriteLocationsColorPicker.setBackgroundColor(optionsManager.getFavoritesLocationDefaultColor());
	}

	public void setActivity(Alert24Screen activity) {
		this.activity = activity;
	}

	private void setUpEventCategoriesStatus() {
		int eventCategoriesTextColorId = 0;

		if (optionsManager.isEventCategoriesDownloaded()) {
			eventCategoriesStatusLabel.setText(R.string.downloaded);
			eventCategoriesTextColorId = R.color.downloaded;
		} else {
			eventCategoriesStatusLabel.setText(R.string.not_downloaded);
			eventCategoriesTextColorId = R.color.not_downloaded;
		}

		eventCategoriesStatusLabel.setTextColor(getResources().getColor(eventCategoriesTextColorId));
	}

	private void setUpRegisterStatus() {
		int registerStatusTextColorId = 0;

		if (optionsManager.getDeviceKey().equals("")) {
			registerStatusLabel.setText(R.string.not_registered);
			registerStatusTextColorId = R.color.not_registered;
		} else if (optionsManager.getDeviceKey().equals("Banned")) {
			registerStatusLabel.setText(R.string.banned);
			registerStatusTextColorId = R.color.banned;
		} else {
			registerStatusLabel.setText(R.string.registered);
			registerStatusTextColorId = R.color.registered;
		}

		registerStatusLabel.setTextColor(getResources().getColor(registerStatusTextColorId));
	}

	private void setUpInitialInterval() {
		int currentInterval = optionsManager.getRefreshInterval(); 

		for (int i = 0; i < allIntervalOptions.length; ++i) {
			String interval = allIntervalOptions[i];

			if (getMinutesFromIntervalOption(interval) == currentInterval) {
				choosableIntervalOptions.setSelection(i);
			}
		}
	}

	private void setUpInitialRadius() {
		int currentRadius = optionsManager.getRefreshRadius();

		for (int i = 0; i < allRadiusOptions.length; ++i) {
			String radius = allRadiusOptions[i];

			if (getMetersFromRadiusOption(radius) == currentRadius) {
				choosableRadiusOptions .setSelection(i);
			}
		}
	}

	private void setUpInitialMapType() {
		String currentMapType =  optionsManager.getMapTypeName();

		for (int i = 0; i < allMapTypeOptions.length; ++i) {
			String mapTypeOption = allMapTypeOptions[i];

			if (mapTypeOption.equals(currentMapType)) {
				choosableMapTypeOptions.setSelection(i);
			}
		}
	}

	public interface OnOptionsChangeListener {
		public void onRefreshRadiusChange(int newRadius);
		public void onRefreshIntervalChange(int newRefreshInterval);
	}


	private class DeviceRegisteredHandler extends DefaultWorkerResultHandler{

		public DeviceRegisteredHandler() {
			super(activity);
		}

		@Override
		public void onSuccess(WorkerResult workerResult,
				Intent intentWithResult) {
			JSONObject serverResponseAsJson;
			try {
				serverResponseAsJson = new JSONObject(workerResult.getResult());
				GCMRegistrar.setRegisteredOnServer(alert24Screen, true);
				String deviceKey = serverResponseAsJson.getString(optionsManager.getDeviceKeyPreferenceName());
				optionsManager.setDeviceKey(deviceKey);
				setUpRegisterStatus();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private class EventCategoriesDownloadedHandler extends DefaultWorkerResultHandler{

		public EventCategoriesDownloadedHandler () {
			super(activity);
		}

		@Override
		public void onSuccess(WorkerResult workerResult,
				Intent intentWithResult) {

			//TODO powtarzajacy sie kod
			final List<EventCategory> downloadedEventCategories = intentWithResult.getParcelableArrayListExtra(List.class.toString());

			optionsManager.setEventCategoriesDownloadedStatus(true);

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					for (EventCategory eventCategory : downloadedEventCategories) {
						activity.getEventCategoryImageManager().loadImage(eventCategory);
					}
					setUpEventCategoriesStatus();
				}
			});

			return;
		}

	}

}

