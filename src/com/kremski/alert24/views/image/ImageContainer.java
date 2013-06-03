package com.kremski.alert24.views.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageContainer {
	public void showProgressOfLoadingImage();
	public void showLoadedImage(String imageKey, Bitmap bitmap);
	public ImageView getImageView(String imageKey);
}
