package com.kremski.alert24.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kremski.alert24.R;

public class ActionBarOptionView extends LinearLayout {

	private ImageView optionPhoto;
	private TextView optionName;

	public ActionBarOptionView(final Context context, final ActionBarOption option) {
		super(context);
		inflateLayout();
		optionPhoto = (ImageView) findViewById(R.id.option_photo);
		optionPhoto.setBackgroundResource(option.getImageResourceId());
		optionName = (TextView) findViewById(R.id.option_name);
		optionName.setText(option.getTextResourceId());
		setOnClickListener(option.getOnClickListener());
	}

	private void inflateLayout() {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(service);	
		inflater.inflate(R.layout.actionbar_option, this, true);
	}

}
