package com.kremski.alert24.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.views.PointOfInterestRowView;

public class PointOfInterestAdapter extends BaseAdapter{

	private Context context;
	private List<PointOfInterest> displayedPoints = Lists.newLinkedList();

	public PointOfInterestAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return displayedPoints.size();
	}

	@Override
	public Object getItem(int position) {
		return displayedPoints.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PointOfInterest point = displayedPoints.get(position);

		if (convertView == null) {
			convertView = new PointOfInterestRowView(context);
		} 
		PointOfInterestRowView pointRowView = (PointOfInterestRowView)convertView;
		pointRowView.setPoint(point);
		return pointRowView;
	}

	public void setDisplayedPointOfInterests(List<PointOfInterest> PointOfInterests) {
		this.displayedPoints = Lists.newLinkedList(PointOfInterests);
		notifyDataSetChanged();
	}

	public List<PointOfInterest> getDisplayedPointOfInterests() {
		return Lists.newLinkedList(displayedPoints);
	}

	public synchronized void removePointOfInterest(PointOfInterest PointOfInterestToRemove) {
		for (int position = 0; position < displayedPoints.size(); ++position) {
			if (displayedPoints.get(position).getDatabaseId() == PointOfInterestToRemove.getDatabaseId()) {
				displayedPoints.remove(position);
				notifyDataSetChanged();
				return;
			}
		}
	}

	public void addPoint(PointOfInterest insertedPoint) {
		displayedPoints.add(insertedPoint);
		notifyDataSetChanged();
	}

	public List<PointOfInterest> getAllPoints() {
		return displayedPoints;
	}
}
