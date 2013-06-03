package com.kremski.alert24.views.image;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class DefaultImageManager<T> implements ImageManager<T> {

	private ImageUrlResolver<T> imgPathResolver;
	private ImageLoader imageLoader;
	private DisplayImageOptions imageOptions;
	private Context context;

	public DefaultImageManager(Context context, ImageUrlResolver<T> imageUrlResolver, ImageLoader imageLoader, DisplayImageOptions imageOptions) {
		this.imgPathResolver = imageUrlResolver;
		this.imageLoader = imageLoader;
		this.imageOptions = imageOptions;
		this.context = context;
	}

	@Override
	public void displayImage(String imageKey, T objectWithImage, ImageContainer imgContainer) {
		String imagePath = imgPathResolver.resolve(objectWithImage);
		imageLoader.displayImage(imagePath, imgContainer.getImageView(imageKey), 
				imageOptions, createImageLoadingListener(imageKey, imgContainer));
	}

	private ImageLoadingListener createImageLoadingListener(final String imageKey, final ImageContainer imgContainer) {
		return new ImageLoadingListener() {

			@Override
			public void onLoadingStarted() {
				imgContainer.showProgressOfLoadingImage();
			}

			@Override
			public void onLoadingFailed(FailReason reason) {
			}

			@Override
			public void onLoadingComplete(Bitmap loadedBitmap) {
				imgContainer.showLoadedImage(imageKey, loadedBitmap);
			}

			@Override
			public void onLoadingCancelled() {

			}
		};
	}

	@Override
	public void stopImageLoading() {
		imageLoader.stop();
	}

	@Override
	public MemoryCacheAware<String, Bitmap> getMemoryCache() {
		return imageLoader.getMemoryCache();
	}

	@Override
	public DiscCacheAware getDiscCache() {
		return imageLoader.getDiscCache();
	}

	@Override
	public void loadImage(T objectWithImage) {
		imageLoader.loadImage(context, imgPathResolver.resolve(objectWithImage), null);	
	}
}
