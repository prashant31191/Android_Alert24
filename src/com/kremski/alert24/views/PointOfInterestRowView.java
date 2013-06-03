package com.kremski.alert24.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.PointOfInterest;

public class PointOfInterestRowView extends LinearLayout{

	private static final int LAYOUT_ID = R.layout.point_of_interest_row_view;

	private TextView pointNameLabel;
	private TextView pointAddressLabel;
	private TextView pointColorLabel;
	private PointOfInterest pointOfInterest;

	public PointOfInterestRowView(Context context) {
		super(context);
		initialize();
	}

	private void initialize() {
		inflateLayout();
		pointNameLabel = (TextView)findViewById(R.id.name);
		pointAddressLabel = (TextView)findViewById(R.id.address);
		pointColorLabel = (TextView)findViewById(R.id.color);
	}

	private void inflateLayout() {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(service);	
		inflater.inflate(LAYOUT_ID, this, true);
	}

	public void setPoint(PointOfInterest point) {
		pointOfInterest = point;
		pointNameLabel.setText(point.getName());
		pointAddressLabel.setText(point.getAddress());
		pointColorLabel.setBackgroundColor(point.getCircleColor());
	}

	public PointOfInterest getPoint() {
		return pointOfInterest;
	}
}
