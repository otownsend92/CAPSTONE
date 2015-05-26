package treadsetters.bikesmart;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
<<<<<<< HEAD
import android.content.BroadcastReceiver;
=======
import android.app.FragmentManager;
import android.app.FragmentTransaction;
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
<<<<<<< HEAD
import android.location.LocationListener;
import android.net.Uri;
=======
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
import android.os.Bundle;
import android.os.IBinder;
import android.app.Fragment;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
<<<<<<< HEAD
=======
import com.parse.FindCallback;
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
<<<<<<< HEAD
=======
import java.util.List;

>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
/**
 * Created by Duncan Sommer on 4/5/2015.
 */


public class BikeDetailsFragment extends Fragment implements OnMapReadyCallback, CustomReceiver.Listener
{
    private static final String TAG = "Bike Details";

<<<<<<< HEAD
    LocationService myService;
    volatile boolean isBound = false;

    protected Location mLastLocation;
    protected LatLng mLastLatLng = new LatLng(34.4125, -119.8481);
    protected TextView distance_traveled_text_box;
    protected float distance_traveled = 0;
    protected GoogleMap mMap;
    protected Marker bikeMarker;
=======
    ParseObject bike;

    protected GoogleMap mMap;
    protected Marker bikeMarker;
    protected TextView distance_traveled_text_box;

    protected Location mLastLocation;
    protected LatLng mLastLatLng = new LatLng(34.4125, -119.8481);
    protected float distance_traveled = 0;

>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021

    public BikeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
<<<<<<< HEAD

        Activity currentActivity = getActivity();
        Intent intent = new Intent(currentActivity, LocationService.class);
        ComponentName myService = currentActivity.startService(intent);
        currentActivity.bindService(new Intent(intent), myConnection, Context.BIND_AUTO_CREATE);
=======
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.fragment_bike_details, container, false);
<<<<<<< HEAD

        final TextView bike_title = (TextView) V.findViewById(R.id.bike_name);
        String bike = (String) this.getArguments().getString("bike");
        //bike_title.setText(bike);
        Log.d(TAG, "Bike name:" + bike);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        distance_traveled_text_box = (TextView) V.findViewById(R.id.distance_traveled_text_box);
=======
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021

        final TextView bike_name = (TextView) V.findViewById(R.id.bike_name);
        final Double bike_id = this.getArguments().getDouble("bike_id");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("bike");
        query.whereEqualTo("bike_id", bike_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null && postList.size() > 0) {
                    Log.d(TAG, "bike found");
                    bike = postList.get(0);
                    bike_name.setText(bike.getString("bike_name"));
                } else {
                    Log.d(TAG,"Post retrieval failed...");
                }
            }
        });

<<<<<<< HEAD
        final Button get_location_button = (Button) V.findViewById(R.id.get_location_button);
        get_location_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLocationChanged();
            }
        });
=======

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment == null) { Log.d(TAG, "mapfrag==null"); }
        mapFragment.getMapAsync(this);

        distance_traveled_text_box = (TextView) V.findViewById(R.id.distance_traveled_text_box);
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021

        return V;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        mMap = map;


        map.moveCamera( CameraUpdateFactory.newLatLngZoom(mLastLatLng, 14.0f) );
<<<<<<< HEAD

        bikeMarker = map.addMarker(new MarkerOptions()
                                        .position(mLastLatLng)
                                        .title("Last known bike location"));

    }

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
=======

        bikeMarker = map.addMarker(new MarkerOptions()
                .position(mLastLatLng)
                .title("Last known bike location"));
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021

    };

    private void startLocationUpdates(){
        myService.startLocationUpdates();
    }

    public void onLocationChanged() {
<<<<<<< HEAD
        Location location = myService.getCurrentLocation();
=======
        // FIX THIS
        Location location = null;
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021

        if (location != null) {
            if (mLastLocation != null) {
                distance_traveled += mLastLocation.distanceTo(location);
            }

            mLastLocation = location;
            mLastLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            distance_traveled_text_box.setText(Float.toString(distance_traveled / 1000));
            animateMarker(bikeMarker, mLastLatLng);
        } else {
            Log.d(TAG, "Location Not Available");
        }
    }
    //CustomReceiver.setListener(this);

<<<<<<< HEAD

=======
>>>>>>> 601153cf374ce252455b8d3f54895e54e5048021
    static LatLng interpolate(float fraction, LatLng a, LatLng b) {
        double lat = (b.latitude - a.latitude) * fraction + a.latitude;
        double lng = (b.longitude - a.longitude) * fraction + a.longitude;
        return new LatLng(lat, lng);
    }

    static void animateMarker(Marker marker, LatLng finalPosition) {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(3000);
        animator.start();
    }
}