package treadsetters.bikesmart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity which displays a login screen to the user.
 */
public class SignUpActivity extends Activity {
    // UI references.
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        // Set up the signup form.
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);

        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
        passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_signup ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    signup();
                    return true;
                }
                return false;
            }
        });

        // Set up the submit button click handler
        Button mActionButton = (Button) findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });
    }

    private void signup() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (email.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        Double user_id = Math.random() * 1000000;
        user.put("user_id", user_id);
        user.put("default_bike_id", 0);
        user.put("bikes_owned", new ArrayList<Double>());
        user.put("bikes_used", new ArrayList<String>());
        user.put("bikes_rented", new ArrayList<Double>());
//        user.put("friends", "");
        user.put("groups", "");
        user.put("notifications", "");
        user.put("messages", "");
        user.put("active_bike", -1);
        user.put("balance", "");

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Application.updateParseInstallation(ParseUser.getCurrentUser());
                    initializeRentals();
                    Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    public void initializeRentals() {
        //*****************************************************************************************
        //TODO: REMOVE this section, used to clear rentals

        ParseQuery<ParseObject> query = ParseQuery.getQuery("rental");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    postList.get(0).deleteInBackground();
                    Log.d("Clearing Rentals", "rental deleted");
                } else {
                    Log.d("Clearing Rentals", "Delete rental failed...");
                }
            }
        });


        //*****************************************************************************

        String[] rentalNames = { "smartMountain", "smartRoad", "smartHybrid" };
        String[] rentalDescriptions = { "Standard Mountain Bike", "Standard Road Bike", "Standard Hybrid Bike" };
        Double[] rentalRates = { 25.00, 35.00, 20.00 };

        ParseUser current_user = ParseUser.getCurrentUser();
        ArrayList<Double> available_rentals = new ArrayList<Double>();
        byte[] byteArray = new byte[5]; // bullshit filler, fix
        ParseFile roundBikeImage = new ParseFile("roundBikeImage.jpg", byteArray);

        for (int k = 0; k < rentalNames.length; k++) {
            ParseObject rental = new ParseObject("rental");
            double bikeID = Math.random() * 1000000;
            rental.put("bike_id", bikeID);
            rental.put("bike_name", rentalNames[k]);
            rental.put("bike_description", rentalDescriptions[k]);
            rental.put("bike_rate", rentalRates[k]);
            rental.put("current_loc", new ParseGeoPoint(34.413329, -119.860972));
            rental.put("dist_traveled", 0);
            rental.saveInBackground();
            available_rentals.add(bikeID);
        }
        current_user.put("available_rentals", available_rentals);
        current_user.saveInBackground();

    }


}
