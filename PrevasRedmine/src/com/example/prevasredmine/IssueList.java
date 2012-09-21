package com.example.prevasredmine;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prevasredmine.R;
import com.taskadapter.redmineapi.bean.Issue;

public class IssueList extends ListActivity 
{
	private ArrayAdapter<Issue> mIssueListAdapter;
	private ProgressDialog mProgressDialog;
	private String mProjectIdentifier;
	private static Issue mCurrentIssue;
	private static boolean mIssueNeedsUpdate = false;
	private static String mProgressMessage = " ";
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{		
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        mProjectIdentifier = intent.getStringExtra(StringConsts.PROJECT_ID);
        String projTitle = intent.getStringExtra(StringConsts.PROJECT_NAME);
        setTitle(projTitle);      
        mIssueListAdapter = null;
        mProgressMessage = "Loading...";
        
        new LoadViewTask().execute();
	}
	
	private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        	mProgressDialog = ProgressDialog.show(IssueList.this, "", mProgressMessage);
        }
        
        @Override  
        protected Void doInBackground(Void... params)  
        {    
             try  
             {  
            	 //Get the current thread's token  
            	 synchronized (this)  
            	 {  
            		 try {
            			 if (mIssueNeedsUpdate) {
            				 PrevasRedmine.updateIssue(mCurrentIssue);
            				 //mIssueNeedsUpdate = false;
            			 }
            			 List<Issue> issueList = PrevasRedmine.getIssuesWithJournals(mProjectIdentifier);
            			 if (null != issueList && issueList.size() > 0) {
            				 addListAdapter(issueList);
            			 }
            			 else {
            				 mIssueListAdapter = null;
            			 }
            		 } catch (Exception e) {
            			 e.printStackTrace();        	
            		 }
            	 }
             }
             catch (Exception e) {
            	 e.printStackTrace();
             }
             
			return null;
        }
        
        @Override  
        protected void onPostExecute(Void result)  
        {  
            //close the progress dialog  
            mProgressDialog.dismiss();
            // set the adapter
            if (null != mIssueListAdapter)
            	setListAdapter(mIssueListAdapter);
            else {
            	finish();
            }
            if (mIssueNeedsUpdate) {
            	mIssueNeedsUpdate = false;
        		Toast toast = Toast.makeText(IssueList.this, "Issue saved", Toast.LENGTH_SHORT);
        		toast.show();
            }
        }  
             
    }
	
	private void refreshIssueList()
	{
		mIssueNeedsUpdate = true;
		mProgressMessage = "Saving...";
		
		new LoadViewTask().execute();
	}
	
	private void addListAdapter(List<Issue> issueList)
	{
		mIssueListAdapter = new IssueAdapter(this, R.layout.issue_list_item_view, issueList);
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
	{
        // Start another activity to show issue details in tab view
		Intent intent = new Intent(this, IssueDetailTabs.class);		
		
		mCurrentIssue = mIssueListAdapter.getItem(position);
		
        //startActivity(intent);
		startActivityForResult(intent, 1);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if (RESULT_OK == resultCode) {
	    	refreshIssueList();
	    }
	}
	
	public static Issue getCurrentIssue()
	{
		return mCurrentIssue;
	}
	
	public static void updateCurrentIssue(Issue updatedIssue)
	{
		mCurrentIssue = updatedIssue;
	}
}
