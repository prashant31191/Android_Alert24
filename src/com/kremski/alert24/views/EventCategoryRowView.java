package com.kremski.alert24.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.views.image.ImageContainer;

public class EventCategoryRowView extends LinearLayout implements ImageContainer{

	private TextView categoryNameLabel;
	private ProgressBar categoryImageLoadingProgressBar;
	private ImageView categoryImageLabel;
	private RadioButton checkCategoryButton;

	public EventCategoryRowView(Context context, final EventCategory eventCategory, final OnCategoryCheckedChangeListener changeListener, boolean isParent) {
		super(context);
		inflateLayout(isParent);
		categoryNameLabel = (TextView)findViewById(R.id.event_category_name);
		categoryNameLabel.setText(eventCategory.getName());
		checkCategoryButton = (RadioButton)findViewById(R.id.check_category);
		checkCategoryButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				changeListener.onCategoryCheckedChange(buttonView, isChecked, eventCategory);
			}
		});
		categoryImageLoadingProgressBar = (ProgressBar) findViewById(R.id.event_category_image_loading_progress_bar); 
		categoryImageLabel = (ImageView) findViewById(R.id.event_category_image);
	}

	private void inflateLayout(boolean isParent) {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(service);	
		if (isParent) {
			inflater.inflate(R.layout.event_parent_category_row_view, this, true);
		} else {
			inflater.inflate(R.layout.event_child_category_row_view, this, true);
		}
	}
	
	public void hideCheckButton() {
		checkCategoryButton.setVisibility(View.GONE);
	}

	public void setCheckButtonAsSelected() {
		checkCategoryButton.setChecked(true);
	}

	@Override
	public void showProgressOfLoadingImage() {
		categoryImageLabel.setVisibility(View.GONE);
		categoryImageLoadingProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void showLoadedImage(String objectKey, Bitmap bitmap) {
		categoryImageLoadingProgressBar.setVisibility(View.GONE);
		categoryImageLabel.setImageBitmap(bitmap);
		categoryImageLabel.setVisibility(View.VISIBLE);
	}

	@Override
	public ImageView getImageView(String imageKey) {
		return categoryImageLabel;
	}
	
	public interface OnCategoryCheckedChangeListener {
		public void onCategoryCheckedChange(CompoundButton buttonView, boolean isChecked, EventCategory category);
	}

}
