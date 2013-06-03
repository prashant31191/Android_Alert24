package com.kremski.alert24.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Lists;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.EventCategoriesListAdapter;
import com.kremski.alert24.dialogs.EventCategoryDialog;
import com.kremski.alert24.dialogs.EventCategoryDialog.OnTypeAcceptListener;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.Event.EventBuilder;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.screens.Alert24Screen;
import com.kremski.alert24.screens.PickLocationScreen;
import com.kremski.alert24.screens.event_screens.AddressContainer;
import com.kremski.alert24.sdcard.SDCardHelper;
import com.kremski.alert24.serwer.service.WorkerRetrivingAddress;
import com.kremski.alert24.tasks.TaskLoadingEventCategories;
import com.kremski.alert24.tasks.TaskLoadingEventCategories.OnEventCategoriesLoadedListener;
import com.kremski.alert24.views.image.ImageContainer;

public abstract class EventFormFragment<T extends Alert24Screen> extends Fragment implements AddressContainer, OnEventCategoriesLoadedListener, OnTypeAcceptListener, ImageContainer {

	private EventCategoryDialog eventCategoryDialog;

	private LatLng eventLatLng;

	protected TextView eventNameLabel;
	private ProgressBar addressLoadingProgressBar;
	protected TextView eventAddresssLabel;
	protected TextView eventDescriptionLabel;
	protected ImageView eventPhotoLabel;
	protected ImageView eventCategoryImageLabel;
	private ProgressBar eventCategoryImageLoadingProgressBar;
	protected TextView eventCategoryLabel;

	protected EventCategory checkedEventCategory;
	protected EventBuilder eventBuilder = new EventBuilder();

	protected T alert24Screen;

	public static final int TAKE_PHOTO_REQUEST_CODE = 0;
	public static final int SELECT_LOCATION_REQUEST_CODE = 1;
	private final static int SELECT_PHOTO_REQUEST_CODE = 2;

	private SDCardHelper sdCardHelper = new SDCardHelper();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout =  inflater.inflate(getLayoutId(), container);

		eventNameLabel = (TextView)layout.findViewById(R.id.name);
		addressLoadingProgressBar = (ProgressBar)layout.findViewById(R.id.address_loading_bar);
		setUpEventAddressLabel(layout);
		eventDescriptionLabel = (TextView)layout.findViewById(R.id.description);
		eventCategoryImageLoadingProgressBar  = (ProgressBar) layout.findViewById(R.id.category_photo_loading_bar);
		eventCategoryImageLabel = (ImageView) layout.findViewById(R.id.category_photo);
		setUpEventCategoryLabel(layout);
		setUpAddPhotoButton(layout);
		setUpSelectPhotoButton(layout);
		setUpDeletePhotoButton(layout);
		eventPhotoLabel = (ImageView)layout.findViewById(R.id.photo);
		eventPhotoLabel.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_photo));
		setUpAcceptButton(layout);

		return layout;
	}
	
	protected int getLayoutId() {
		return R.layout.event_form;
	}

	private void setUpEventAddressLabel(View layout) {
		eventAddresssLabel = (TextView)layout.findViewById(R.id.address);
		View addressContainer = layout.findViewById(R.id.address_container);
		addressContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(getActivity(), PickLocationScreen.class), SELECT_LOCATION_REQUEST_CODE);
			}
		});
	}

	private void setUpEventCategoryLabel(View layout) {
		eventCategoryLabel = (TextView)layout.findViewById(R.id.category);
		eventCategoryLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new TaskLoadingEventCategories(alert24Screen, alert24Screen.getEventCategoriesTableHelper(), 
						EventFormFragment.this).execute(new Void[]{});
			}
		});
	}

	private void setUpAddPhotoButton(View layout) {
		TextView addPhotoButton = (TextView)layout.findViewById(R.id.add_photo);
		if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			addPhotoButton.setOnClickListener(createClickListenerForAddPhotoButton());
		} else {
			addPhotoButton.setVisibility(View.GONE);
		}
	}

	private OnClickListener createClickListenerForAddPhotoButton() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE);	
			}
		};
	}

	private void setUpSelectPhotoButton(View layout) {
		TextView addPhotoButton = (TextView)layout.findViewById(R.id.select_photo);
		if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			addPhotoButton.setOnClickListener(createClickListenerForSelectPhotoButton());
		};
	}

	private OnClickListener createClickListenerForSelectPhotoButton() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO_REQUEST_CODE);  
			}
		};
	}

	private void setUpDeletePhotoButton(View layout) {
		TextView deletePhotoButton = (TextView)layout.findViewById(R.id.delete_photo);
		deletePhotoButton.setOnClickListener(createClickListenerForDeletePhotoButton());
	}

	private OnClickListener createClickListenerForDeletePhotoButton() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				eventPhotoLabel.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_photo));
			}
		};
	}


	private void setUpAcceptButton(View layout) {
		TextView acceptButton = (TextView)layout.findViewById(R.id.accept);
		acceptButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onAcceptEvent(createEventFromFields());
			}
		});
	}

	protected abstract void onAcceptEvent(Event acceptedEvent);

	protected Event createEventFromFields()	 {
		eventBuilder.category(checkedEventCategory)
		.name(eventNameLabel.getText().toString())
		.address(eventAddresssLabel.getText().toString())
		.category(checkedEventCategory)
		.descritpion(eventDescriptionLabel.getText().toString());

		if (eventLatLng != null) {
			eventBuilder.latitude(eventLatLng.latitude).longitude(eventLatLng.longitude);
		}

		if (eventPhotoLabel.getDrawable() != null) {
			eventBuilder.photoBitmap(((BitmapDrawable)eventPhotoLabel.getDrawable()).getBitmap());
		}

		return eventBuilder.build();
	}

	public void setActivity(T alert24Screen) {
		this.alert24Screen = alert24Screen;
	}
	
	public void showAddressLoadingProgress() {
		addressLoadingProgressBar.setVisibility(View.VISIBLE);
	}

	public void setAddress(Address address, LatLng latLngSource) {
		eventAddresssLabel.setText(getParsedAddressText(address, latLngSource));
		addressLoadingProgressBar.setVisibility(View.GONE);
		eventAddresssLabel.setVisibility(View.VISIBLE);
		eventLatLng = latLngSource;
	}

	private String getParsedAddressText(Address address, LatLng latLngSource) {
		String textInAddressInputLabel = null;

		if (address != null) {
			textInAddressInputLabel= String.format("%s, %s, %s",
					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
		} 
		else if (latLngSource != null) {
			textInAddressInputLabel= String.format("%s:%s\n%s:%s", getResources().getString(R.string.prefix_for_latitude_value), latLngSource.latitude,
					getResources().getString(R.string.prefix_for_longitude_value), latLngSource.longitude);
		} 

		return textInAddressInputLabel;
	}

	@Override
	public void onEventCategoriesLoadedListener(
			LinkedHashMap<EventCategory, List<EventCategory>> groupedCategories) {
		List<EventCategory> categories = Lists.newLinkedList(groupedCategories.keySet());
		List<List<EventCategory>> subcategories = Lists.newLinkedList();
		for (EventCategory c : categories) {
			subcategories.add(groupedCategories.get(c));
		}

		eventCategoryDialog = new EventCategoryDialog.EventCategoryDialogBuilder().onTypeAcceptListener(this).
				eventCategoriesListAdapter(new EventCategoriesListAdapter(getActivity(), alert24Screen.getEventCategoryImageManager(), 
						checkedEventCategory, categories, subcategories)).build();
		eventCategoryDialog.show(getActivity().getSupportFragmentManager(), eventCategoryDialog.TAG);
	}

	@Override
	public void onTypeAccept(final EventCategory checkedEventCategory) {
		if (checkedEventCategory != null) {
			this.checkedEventCategory = checkedEventCategory;
			eventCategoryLabel.setText(checkedEventCategory.getName());
			alert24Screen.getEventCategoryImageManager().displayImage("EVENT_CATEGORY_IMAGE", checkedEventCategory, EventFormFragment.this);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
				setNewPhotoBitmapFromIntent(intent);
			} else if (requestCode == SELECT_LOCATION_REQUEST_CODE ){
				setNewLocationFromIntent(intent);
			}
			else if (requestCode == SELECT_PHOTO_REQUEST_CODE) {
				setUpSelectedPhotoFromIntent(intent);
			}
		}
	}

	private void setUpSelectedPhotoFromIntent(Intent intentWithSelectedPhoto) {
		Uri selectedImage = intentWithSelectedPhoto.getData();
		String[] filePathColumn = {MediaStore.Images.Media.DATA};

		Cursor cursor = getActivity().getContentResolver().query(
				selectedImage, filePathColumn, null, null, null);
		try {
			eventPhotoLabel.setImageBitmap(getBitmapFromCursor(cursor));
			eventPhotoLabel.setVisibility(View.VISIBLE);
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}

		if (cursor != null) cursor.close();
	}

	private Bitmap getBitmapFromCursor(Cursor cursor) throws IOException {
		if(cursor.moveToFirst()) {
			int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			String filePath = cursor.getString(columnIndex);
			return sdCardHelper.loadPhotoBitmap(filePath, 320, 240);
		} else {
			return null;
		}
	}

	private void setNewPhotoBitmapFromIntent(Intent intentWithBitmap) {
		Bundle extras = intentWithBitmap.getExtras();
		Bitmap photoBitmap = (Bitmap) extras.get("data");

		if (photoBitmap != null) {

			eventPhotoLabel.setImageBitmap(Bitmap.createScaledBitmap(photoBitmap, 3240, 240, true));
			eventPhotoLabel.setVisibility(View.VISIBLE);
		}
	}

	private void setNewLocationFromIntent(Intent intentWithLocation) {
		LatLng newLocationOfEvent = intentWithLocation.getParcelableExtra(LatLng.class.toString());
		WorkerRetrivingAddress worker = 
				new WorkerRetrivingAddress(getActivity(), new LatLng(newLocationOfEvent.latitude, newLocationOfEvent.longitude));
		addressLoadingProgressBar.setVisibility(View.VISIBLE);
		alert24Screen.executeWorker(worker);
	}


	@Override
	public void showProgressOfLoadingImage() {
		eventCategoryImageLoadingProgressBar.setVisibility(View.VISIBLE);
		eventCategoryImageLabel.setVisibility(View.GONE);
	}

	@Override
	public void showLoadedImage(String objectKey, Bitmap bitmap) {
		eventCategoryImageLoadingProgressBar.setVisibility(View.GONE);
		eventCategoryImageLabel.setImageBitmap(bitmap);
		eventCategoryImageLabel.setVisibility(View.VISIBLE);
	}

	@Override
	public ImageView getImageView(String imageKey) {
		return eventCategoryImageLabel;
	}
}
