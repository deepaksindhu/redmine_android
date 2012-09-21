package com.example.prevasredmine;

import java.util.ArrayList;
import java.util.List;

import com.example.prevasredmine.R;
import com.taskadapter.redmineapi.bean.Project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class ProjectFavouriteList extends Activity 
{
	private ListView mFavListView;
	private ProjectFavouriteAdapter mProjectAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_fav_list_view);
		
		List<Project> projectList = PrevasRedmine.mProjectList;
		mProjectAdapter = new ProjectFavouriteAdapter(this, 
				R.layout.project_fav_list_row_layout, R.id.label, projectList);
		
		mFavListView = (ListView) findViewById(R.id.favlistview);
		mFavListView.setAdapter(mProjectAdapter);
    }
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_prevas_redmine, menu);
        menu.getItem(0).setEnabled(false);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) {
    	case R.id.project_save:
    		saveFavouriteProject();
    		break;
    	}
    	return true;
    }
    
	@Override
	public void onBackPressed()
	{
		Intent data = new Intent();
    	data.putExtra(StringConsts.ACTIVITY_CLASS_NAME, "ProjectFavouriteList");
    	if (null == getParent()) {
    		setResult(Activity.RESULT_CANCELED, data);
    	}
    	else {
    		getParent().setResult(Activity.RESULT_CANCELED, data);
    	}
    	finish();
	}
    
    private void saveFavouriteProject()
    {
    	ArrayList<String> proj_identifiers = new ArrayList<String>();
    	
    	int itemCount = mProjectAdapter.getCount();
    	Project project = null;
    	for (int i = 0; i < itemCount; ++i) {
    		project = mProjectAdapter.getItem(i);
    		boolean value = mProjectAdapter.isItemChecked(i);
    		if (value) {    			
    			proj_identifiers.add(project.getIdentifier());
    		}
    	}
    	
    	ProjectPreferences.SaveFavoriteProjects(getApplicationContext(), proj_identifiers);
    	//finish();
    	
    	Intent data = new Intent();
    	data.putExtra(StringConsts.ACTIVITY_CLASS_NAME, "ProjectFavouriteList");
    	
    	/* finish() method only sends back the result if there is a mParent property set to null. 
    	* Otherwise the result is lost. */
    	if (null == getParent()) {
    		setResult(Activity.RESULT_OK, data);
    	}
    	else {
    		getParent().setResult(Activity.RESULT_OK, data);
    	}
    	finish();
    }
}


//private ArrayList<String> getSavedListItems()
//{
//	ArrayList<String> proj_identifiers = new ArrayList<String>();
//	Context context = getApplicationContext();
//	SharedPreferences preferences = context.getSharedPreferences(StringConsts.FAVORITE_PROJECTS,
//													Context.MODE_PRIVATE);
//	boolean wereFavSaved = preferences.getBoolean(StringConsts.PREF_SAVED, false);
//	if (!wereFavSaved) return null;
//	
//	int itemCount = preferences.getInt("FavProjectsCount", 0);
//	String key = "KEY";
//	for (int i = 0; i < itemCount; ++i) {
//		proj_identifiers.add(preferences.getString(key + "_" + i, "0"));
//	}
//	
//	return proj_identifiers;
//}