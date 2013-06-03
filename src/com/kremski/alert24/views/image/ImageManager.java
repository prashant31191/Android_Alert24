package com.kremski.alert24.views.image;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;

public interface ImageManager<T> {
	//TODO zunifkiowac T - imageManager do wszystkich objectow taki sam
	public void displayImage(String imageKey, T objectWithImage, ImageContainer imgContainer);
	public void loadImage(T objectWithImage);
	public void stopImageLoading();
	public MemoryCacheAware<String, Bitmap> getMemoryCache();
	public DiscCacheAware getDiscCache();
}
