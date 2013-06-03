package com.kremski.alert24.sdcard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.google.common.io.Closeables;
import com.kremski.alert24.domain.Event;

public class SDCardHelper {

	private final static String PHOTO_NAME = "event_photo.jpg";
	private final static String DIRECTORY_PATH = "alert24" + File.separator + "events";

	public String saveEventPhotoOnCard(Event event) throws IOException {
		FileOutputStream outputStream = null;

		try {
			String eventPhotoDirectoryPath = getPathOfEventPhotoDirectory(event);
			String pathOfEventPhotoFile = eventPhotoDirectoryPath + PHOTO_NAME; 
			createDirectoriesForEventPhoto(eventPhotoDirectoryPath);
			outputStream = writeEventPhotoToFile(pathOfEventPhotoFile, createStreamWithEventPhoto(event));
			return pathOfEventPhotoFile;
		}
		finally {
			Closeables.closeQuietly(outputStream);
		}
	}

	private String getPathOfEventPhotoDirectory(Event event) {
		return Environment.getExternalStorageDirectory() + File.separator + DIRECTORY_PATH + File.separator + event.getDatabaseId() + File.separator;
	}

	private ByteArrayOutputStream createStreamWithEventPhoto(Event event) {
		ByteArrayOutputStream streamWithEventPhoto = new ByteArrayOutputStream();
		Bitmap eventPhotoBitmap = event.getPhotoBitmap();
		eventPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamWithEventPhoto);
		return streamWithEventPhoto;
	}

	private void createDirectoriesForEventPhoto(String pathOfDirectoryForEvent) {
		File directoriesFotEventPhoto = new File(pathOfDirectoryForEvent);
		if (!directoriesFotEventPhoto.exists()) {
			directoriesFotEventPhoto.mkdirs();
		}
	}

	private FileOutputStream writeEventPhotoToFile(String pathOfEventPhotoFile, ByteArrayOutputStream streamWithEventPhoto) throws IOException	 {
		File fileWithPhoto = new File(pathOfEventPhotoFile);
		fileWithPhoto.createNewFile();
		FileOutputStream outputStream = new FileOutputStream(fileWithPhoto);
		outputStream.write(streamWithEventPhoto.toByteArray());
		return outputStream;
	}

	public Bitmap loadEventPhotoBitmap(Event event, int requestedWidth, int requestedHeight) throws IOException {
		long databaseId = event.getDatabaseId();
		String dirPath = Environment.getExternalStorageDirectory() + File.separator + DIRECTORY_PATH + File.separator + databaseId + File.separator;
		String filePath = dirPath + PHOTO_NAME; 
		return loadPhotoBitmap(filePath, requestedWidth, requestedHeight);
	}

	public Bitmap loadPhotoBitmap(String filePath, int requestedWidth, int requestedHeight) throws IOException {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = calculateInSampleSize(options, requestedWidth, requestedHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
	
	public Bitmap loadPhotoBitmap(InputStream inputStream, int requestedWidth, int requestedHeight) throws IOException {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, null, options);

		options.inSampleSize = calculateInSampleSize(options, requestedWidth, requestedHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(inputStream, null, options);
	}

	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}


	public boolean deleteEventPhotoFromCard(Event event) {
		String eventPhotoDirectoryPath = getPathOfEventPhotoDirectory(event);
		File directoryWithEventPhoto = new File(eventPhotoDirectoryPath);
		File[] filesInDirectory = directoryWithEventPhoto.listFiles();
		if (filesInDirectory != null) {
			for(File fileWithEventPhoto : filesInDirectory) {
				fileWithEventPhoto.delete();
			}
		}
		return directoryWithEventPhoto.delete();
	}

}
