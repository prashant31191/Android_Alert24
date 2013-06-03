package com.kremski.alert24.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;

import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.views.EventCategoryRowView;
import com.kremski.alert24.views.EventCategoryRowView.OnCategoryCheckedChangeListener;
import com.kremski.alert24.views.image.ImageManager;

public class EventCategoriesListAdapter  extends BaseExpandableListAdapter{

	private Context context;
	private List<EventCategory> categories;
	private List<List<EventCategory>> subcategories;
	private CompoundButton buttonOfCheckedCategory;
	private EventCategory checkedCategory;
	private ImageManager<EventCategory> imageManager;

	public EventCategoriesListAdapter(Context context, ImageManager<EventCategory> imageManager, EventCategory checkedEventCategory, List<EventCategory> categories, List<List<EventCategory>> subcategories) {
		this.categories = categories;
		this.subcategories = subcategories;
		this.context = context;
		this.checkedCategory = checkedEventCategory;
		this.imageManager = imageManager;
		createChangeListener();
	}

	private void createChangeListener() {
		changeListener = new OnCategoryCheckedChangeListener() {
			@Override
			public void onCategoryCheckedChange(CompoundButton buttonView,
					boolean isChecked, EventCategory checkedCategory) {
				if (isChecked) {
					onButtonChecked(buttonView, checkedCategory);
				} else {
					onButtonUnchecked(buttonView);
				}
			}

		};
	}

	private void onButtonChecked(CompoundButton buttonView, EventCategory checkedCategory) {
		if (buttonOfCheckedCategory != null) {
			buttonOfCheckedCategory.setChecked(false);
		}
		buttonView.setChecked(true);
		buttonOfCheckedCategory = buttonView;
		this.checkedCategory = checkedCategory;
	}

	private void onButtonUnchecked(CompoundButton buttonView) {
		buttonView.setChecked(false);
		if (buttonView == buttonOfCheckedCategory) {
			this.checkedCategory = null;
			buttonOfCheckedCategory = null;
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return subcategories.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean arg2, View arg3,
			ViewGroup arg4) {
		EventCategory categoryOfView = subcategories.get(groupPosition).get(childPosition);
		EventCategoryRowView childView =  new EventCategoryRowView(context, categoryOfView, changeListener, false);
		if (categoryOfView.equals(checkedCategory)) {
			childView.setCheckButtonAsSelected();
		}
		imageManager.displayImage("EVENT_CATEGORY_IMAGE", categoryOfView, childView);
		return childView;
	}

	private OnCategoryCheckedChangeListener changeListener = null; 

	@Override
	public int getChildrenCount(int groupPosition) {
		return subcategories.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return categories.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return categories.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean arg1, View arg2, ViewGroup arg3) {
		EventCategoryRowView parentView =  new EventCategoryRowView(context, categories.get(groupPosition), changeListener, true);
		parentView.hideCheckButton();
		return parentView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return false;
	}

	public EventCategory getChosenEventCategory() {
		return checkedCategory;
	}
}
