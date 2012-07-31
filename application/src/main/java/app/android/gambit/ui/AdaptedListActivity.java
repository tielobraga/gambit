package app.android.gambit.ui;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import app.android.gambit.R;
import com.actionbarsherlock.app.SherlockListActivity;


abstract class AdaptedListActivity extends SherlockListActivity
{
	protected static final String LIST_ITEM_OBJECT_ID = "object";

	protected final List<Map<String, Object>> list;

	public AdaptedListActivity() {
		super();

		list = new ArrayList<Map<String, Object>>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		setUpListAdapter();
	}

	protected void setUpListAdapter() {
		setListAdapter(buildListAdapter());
	}

	protected abstract SimpleAdapter buildListAdapter();

	protected void populateList(List<?> listContent) {
		list.clear();

		for (Object listItemContent : listContent) {
			list.add(buildListItem(listItemContent));
		}

		refreshListContent();
	}

	protected abstract Map<String, Object> buildListItem(Object itemObject);

	protected void refreshListContent() {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		listAdapter.notifyDataSetChanged();
	}

	protected void setEmptyListText(int textId) {
		TextView emptyListTextView = (TextView) getListView().getEmptyView();
		String text = getString(textId);

		emptyListTextView.setText(text);
	}

	protected Object getListItemObject(int listPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(listPosition);

		return adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	@Override
	protected void onResume() {
		super.onResume();

		callListPopulation();
	}

	protected abstract void callListPopulation();
}