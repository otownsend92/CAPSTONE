package treadsetters.bikesmart;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChangeBikesActivity extends ActionBarActivity {

    private ArrayList<String> mybikes;
    public static ArrayList<ParseObject> global_postList;
    public static ArrayAdapter<String> adapter;

    public ExpandableListAdapter listAdapter;
    public ExpandableListView expListView;
    public static String MYTAG = "MYTAG";
    List<String> bikeHeaders;
    HashMap<String, List<String>> bikeLists;
    List<String> bikesOwned;
    List<Double> bikesOwnedId;
    List<String> bikesUsed;
    List<Double> bikesUsedId;

    ParseObject sharedBike;
    boolean shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bikes);

        bikeHeaders = new ArrayList<String>();
        bikeLists = new HashMap<String, List<String>>();

        bikeHeaders.add("Bikes I Own");
        bikeHeaders.add("Bikes Shared With Me");

        bikesOwned = new ArrayList<String>();
        bikesOwnedId = new ArrayList<Double>();
        bikesUsed = new ArrayList<String>();
        bikesUsedId = new ArrayList<Double>();
        expListView = (ExpandableListView) findViewById(R.id.bike_lists);

        getMyBikes();
        getSharedBikes();
        listAdapter = new ExpandableListAdapter(this, bikeHeaders, bikeLists);
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent result = new Intent();
                if(groupPosition == 0){
                    result.putExtra("bike_name", bikesOwned.get(childPosition));
                    result.putExtra("bike_id", bikesOwnedId.get(childPosition));
                }
                else if(groupPosition == 1){
                    result.putExtra("bike_name", bikesUsed.get(childPosition));
                    result.putExtra("bike_id", bikesUsedId.get(childPosition));
                }
                setResult(Activity.RESULT_OK, result);
                finish();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_bikes, menu);
        return true;
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

    public void getMyBikes() {

        ParseUser current_user = ParseUser.getCurrentUser();

        ArrayList<Double> bikes_owned_copy = new ArrayList<Double>();
        ArrayList<String> bikes_used_copy = new ArrayList<String>();

        bikes_owned_copy = (ArrayList<Double>) current_user.get("bikes_owned");
        bikes_used_copy = (ArrayList<String>) current_user.get("bike_used");

        bikesOwned.clear();
        bikesOwnedId.clear();

        for (final Double bike_id : bikes_owned_copy) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
            Log.d(MYTAG, "bikeID is!! : " + bike_id);
            query.whereEqualTo("bike_id", bike_id);

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null && postList.size() > 0) {
                        bikesOwned.add(postList.get(0).getString("bike_name"));
                        bikesOwnedId.add(bike_id);
                    } else {
                        Log.d("MYTAG","Post retrieval failed...");
                    }
                }
            });
        }

        bikeLists.put(bikeHeaders.get(0), bikesOwned);
    }

    public void getSharedBikes() {
        final String username=ParseUser.getCurrentUser().getUsername();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    // Get list of usernames and make sure this user actually exists
                    for (ParseObject o : postList) {
                        if (o.get("access") != null) {
                            if (o.get("access").toString().contains(username)) {
                                bikesUsed.add(o.get("bike_name").toString());
                                bikesUsedId.add((Double) o.get("bike_id"));
                            }
                        }
                    }
                    bikeLists.put(bikeHeaders.get(1), bikesUsed);
                } else {
                    Log.d("Friends", "Post retrieval failed...");
                }
            }
        });


    }
}
