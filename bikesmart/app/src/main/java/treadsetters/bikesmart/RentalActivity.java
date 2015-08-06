package treadsetters.bikesmart;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class RentalActivity extends ActionBarActivity {
    private ArrayList<String> rentalNames;
    private int numDays = 0;
    private ParseObject bike = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);

        createRentalSpinner();
        createDaysSpinner();
        createRentButton();
    }

    public void createRentalSpinner() {
        rentalNames = new ArrayList<String>();
        rentalNames.add("Select a bike...");
        Spinner nameSpinner = (Spinner) findViewById(R.id.rental_spinner);
        ParseUser current_user = ParseUser.getCurrentUser();
        ArrayList<Double> available_rentals = (ArrayList<Double>) current_user.get("available_rentals");
        Log.d("RentalActivity", "num rentals = " + available_rentals.size());
        for (Double id : available_rentals) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("rental");
            query.whereEqualTo("bike_id", id);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> postList, ParseException e) {
                    if (e == null && postList.size() > 0) {
                        rentalNames.add(postList.get(0).getString("bike_name"));
                        Log.d("RentalActivity", "Loaded " + postList.get(0).getString("bike_name") + ", rate: " + postList.get(0).getDouble("bike_rate"));
                    } else {
                        Log.d("RentalActivity","Get rentals failed...");
                    }
                }
            });
        }
        ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rentalNames); //selected item will look like a spinner set from XML
        namesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(namesArrayAdapter);
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.holo_green_light));                String bikeName = parent.getItemAtPosition(position).toString();
                ParseQuery<ParseObject> bike_query = ParseQuery.getQuery("rental");
                bike_query.whereEqualTo("bike_name", bikeName);
                bike_query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> postList, ParseException e) {
                        if (e == null && postList.size() > 0) {
                            bike = postList.get(0);
                            TextView description_text = (TextView) findViewById(R.id.bikeDescription);
                            TextView rate_text = (TextView) findViewById(R.id.bikeRate);
                            description_text.setText("Description: " + bike.getString("bike_description"));
                            rate_text.setText("Rate: $" + bike.getDouble("bike_rate") + " per day");
                        } else {
                            Log.d("RentalActivity", "Error in item selected listener");
                        }
                    }

                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void createDaysSpinner() {
        ArrayList<Integer> rentalDurations = new ArrayList<Integer>();
        for (int k = 1; k < 8; k++) {
            rentalDurations.add(k);
        }
        Spinner daysSpinner = (Spinner) findViewById(R.id.days_spinner);
        ArrayAdapter<Integer> daysArrayAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, rentalDurations); //selected item will look like a spinner set from XML
        daysArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.holo_green_light));
                numDays = (int) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        daysSpinner.setAdapter(daysArrayAdapter);
    }

    public void createRentButton() {
        Button rentButton = (Button) findViewById(R.id.rent_button);
        rentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("RentalActivity", "Renting for " + numDays + "days at $" + bike.getDouble("bike_rate") + " per day");

                // Remove rental from available rentals
                final ParseUser current_user = ParseUser.getCurrentUser();
                ArrayList<Double> available_rentals = (ArrayList<Double>) current_user.get("available_rentals");
                available_rentals.remove(bike.getDouble("bike_id"));
                current_user.put("available_rentals", available_rentals);
                current_user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("Rent Button", "num available: " + ((ArrayList<Double>) current_user.get("available_rentals")).size());
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error renting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Add bikes to users current rentals
                ArrayList<Double> bikes_rented = (ArrayList<Double>) current_user.get("bikes_rented");
                bikes_rented.add(bike.getDouble("bike_id"));
                current_user.put("bikes_rented", bikes_rented);
                current_user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Bike successfully rented! Due in " + numDays + " days.", Toast.LENGTH_SHORT).show();
                            Log.d("Rent Button", "num rented: " + ((ArrayList<Double>) current_user.get("bikes_rented")).size());
                            Intent intent = getIntent();
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error renting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            intent.putExtra("result", "test");
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        }
                    }
                });
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rental, menu);
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

}
