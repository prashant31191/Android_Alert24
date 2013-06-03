package com.kremski.alert24.views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.views.image.ImageContainer;
import com.kremski.alert24.views.image.ImageManager;

public class EventGalleryFragment extends Fragment {

	private ViewPager eventPhotoPager;
	private ImageManager<String> photoImageManager;
	private Event displayedEvent;
	
	public static EventGalleryFragment newInstance(ImageManager<String> imageManager, Event event) {
		EventGalleryFragment instance = new EventGalleryFragment();
		instance.displayedEvent = event;
		instance.photoImageManager = imageManager;
		return instance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout =  inflater.inflate(R.layout.event_gallery_fragment, null);
		eventPhotoPager = (ViewPager)layout.findViewById(R.id.photo_pager);
		eventPhotoPager.setAdapter(new EventPhotosPagerAdapter());;
		return layout;
	}

	private class EventPhotosPagerAdapter extends FragmentPagerAdapter	 {

		private EventPhotosPagerAdapter() {
			super(getActivity().getSupportFragmentManager());
		}

		@Override
		public int getCount() {
			return displayedEvent.getPhotoUrls().size();
		}

		@Override
		public Fragment getItem(int position) {
			EventGallerySlideFragment slideFragment = new EventGallerySlideFragment();
			slideFragment.setPhotoImageManager(photoImageManager);
			slideFragment.setPhotoUrl(displayedEvent.getPhotoUrls().get(position));
			return slideFragment;
		}

	}

	public static class EventGallerySlideFragment extends Fragment implements ImageContainer {

		private String photoUrl;
		private ProgressBar imageLoadingBar;
		private ImageView imageLabel;
		private ImageManager<String> photoImageManager; 
		public static final String SLIDE_IMAGE_KEY = "SlideImageKey";

		public void setPhotoUrl(String photoUrl) {
			this.photoUrl = photoUrl;
		}

		public void setPhotoImageManager(ImageManager<String> photoImageManager) {
			this.photoImageManager = photoImageManager;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout =  inflater.inflate(R.layout.event_gallery_slide_fragment, null);
			imageLabel = (ImageView) layout.findViewById(R.id.photo);
			imageLoadingBar = (ProgressBar)layout.findViewById(R.id.photo_loading_bar);
			return layout;
		}

		@Override
		public void onResume() {
			super.onResume();
			photoImageManager.displayImage(SLIDE_IMAGE_KEY, photoUrl, this);
		}

		@Override
		public void showProgressOfLoadingImage() {
			imageLoadingBar.setVisibility(View.VISIBLE);
			imageLabel.setVisibility(View.GONE);
		}

		@Override
		public void showLoadedImage(String imageKey, Bitmap bitmap) {
			imageLoadingBar.setVisibility(View.GONE);
			imageLabel.setVisibility(View.VISIBLE);
			imageLabel.setImageBitmap(bitmap);
		}

		@Override
		public ImageView getImageView(String imageKey) {
			return imageLabel;
		}

	}
}

