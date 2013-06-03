package com.kremski.alert24.views.image;


public interface ImageUrlResolver<T> {
	public String resolve(T objectToResolve);
}
