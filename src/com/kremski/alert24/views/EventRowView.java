package com.kremski.alert24.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.views.image.ImageContainer;

public class EventRowView extends LinearLayout implements ImageContainer{

	private static final int LAYOUT_ID = R.layout.event_row_view;

	private ImageView eventPhotoLabel;
	private ImageView eventCategoryPhotoLabel;
	private TextView eventNameLabel;
	private TextView eventCategoryLabel;;
	private TextView eventAddressLabel;
	private Event event;

	public EventRowView(Context context) {
		super(context);
		initialize();
	}

	private void initialize() {
		inflateLayout();
		eventPhotoLabel = (ImageView)findViewById(R.id.photo);
		eventNameLabel = (TextView)findViewById(R.id.name);
		eventCategoryLabel = (TextView)findViewById(R.id.category);
		eventCategoryPhotoLabel = (ImageView)findViewById(R.id.category_photo);
		eventAddressLabel = (TextView)findViewById(R.id.address);
	}

	private void inflateLayout() {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(service);	
		inflater.inflate(LAYOUT_ID, this, true);
	}

	public void setEvent(Event event) {
		this.event = event;
		eventPhotoLabel.setImageBitmap(event.getPhotoBitmap());
		eventNameLabel.setText(event.getName());
		eventAddressLabel.setText(event.getAddress());
		eventCategoryLabel.setText(event.getCategory().getName());
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public void showProgressOfLoadingImage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showLoadedImage(String objectKey, Bitmap bitmap) {
		eventCategoryPhotoLabel.setImageBitmap(bitmap);
	}

	@Override
	public ImageView getImageView(String imageKey) {
		return eventCategoryPhotoLabel;	
	}
}
