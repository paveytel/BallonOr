package com.paveynganpi.ballonor.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paveynganpi.ballonor.R;
import com.paveynganpi.ballonor.adapter.PostMessageCommentsAdapter;
import com.paveynganpi.ballonor.utils.ParseConstants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostMessageCommentsActivity extends AppCompatActivity {
    @InjectView(R.id.empty_view) TextView mEmptyView;
    @InjectView(R.id.PostMessageCommentsRecyclerView) RecyclerView mRecyclerView;
    protected List<ParseObject> postMessageComments;
    protected String postMessageObjectId;
    protected RecyclerView.LayoutManager layoutManager;
    protected ParseUser mCurrentUser;
    private FloatingActionButton fab;
    PostMessageCommentsAdapter postMessageCommentsAdapter;
    boolean setAdaper = false;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message_comments);
        ButterKnife.inject(this);

        mToolbar = (Toolbar) findViewById(R.id.comments_app_bar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Comments");;

        mCurrentUser = ParseUser.getCurrentUser();
        postMessageObjectId  = getIntent().getStringExtra(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID);
        layoutManager = new LinearLayoutManager(PostMessageCommentsActivity.this);

        fab = (FloatingActionButton)findViewById(R.id.PostMessageCommentsFloatingButton);
        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(PostMessageCommentsActivity.this);

                alert.setTitle("New Comment");
                alert.setMessage("Enter comment here");

                // Set an EditText view to get user input
                final EditText input = new EditText(PostMessageCommentsActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Comment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().isEmpty()) {
                            Toast.makeText(PostMessageCommentsActivity.this, "You have to comment something", Toast.LENGTH_LONG).show();
                        } else {
                            //create comment and save to parse
                            ParseObject comment = new ParseObject(ParseConstants.KEY_PARSE_OBJECT_COMMENTS);
                            comment.put(ParseConstants.KEY_CHELSEAFC_TABLE, input.getText().toString().trim());
                            comment.put(ParseConstants.KEY_TWITTER_FULL_NAME, mCurrentUser.getString(ParseConstants.KEY_TWITTER_FULL_NAME));
                            comment.put(ParseConstants.KEY_USERNAME, mCurrentUser.getUsername());
                            comment.put(ParseConstants.KEY_PROFILE_IMAGE_URL, mCurrentUser.getString(ParseConstants.KEY_PROFILE_IMAGE_URL));
                            comment.put(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, postMessageObjectId);

                            comment.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(PostMessageCommentsActivity.this, "Comment Success", Toast.LENGTH_LONG).show();
                                    } else {
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PostMessageCommentsActivity.this);
                                        builder.setMessage("Sorry, an error occured, Please try again")
                                                .setTitle("Opps, Error")
                                                .setPositiveButton(android.R.string.ok, null);
                                        android.app.AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                            retrieveComments();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_message_comments, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerView.setLayoutManager(layoutManager);
        retrieveComments();
    }

    public void retrieveComments(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_PARSE_OBJECT_COMMENTS);
        query.whereEqualTo(ParseConstants.KEY_POST_MESSAGE_OBJECT_ID, postMessageObjectId);
        query.addAscendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> comments, ParseException e) {
                if (e == null) {

                    if (mRecyclerView.getAdapter() == null) {
                        postMessageCommentsAdapter =
                                new PostMessageCommentsAdapter(PostMessageCommentsActivity.this, comments);
                        if (postMessageCommentsAdapter.getItemCount() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        mRecyclerView.setAdapter(postMessageCommentsAdapter);
                        setAdaper = true;
                    } else {
                        //if it exists, no need to recreate it,
                        //just set the data on the recyclerView
                        if (mRecyclerView.getAdapter().getItemCount() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                        ((PostMessageCommentsAdapter) mRecyclerView.getAdapter()).refill(comments);
                    }
//
                } else {
                    Log.d("retrieveComments", "error with retrieveComments");
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}