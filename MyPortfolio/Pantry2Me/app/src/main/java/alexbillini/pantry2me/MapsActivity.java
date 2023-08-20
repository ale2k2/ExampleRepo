package alexbillini.pantry2me;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private float mZoomLevel = 12;

    private double pantryLatCoord, pantryLngCoord;
    private double pantryLatCoord2, pantryLngCoord2;
    private double pantryLatCoord3, pantryLngCoord3;
    private String pantryTitle, pantryTitle2, pantryTitle3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (hasLocationPermission()) {
            trackLocation();
        }
    }

    private void trackLocation() {

        // Create location request
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .build();

        // Create location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for (Location location : locationResult.getLocations()) {
                    updateMap(location);
                }
            }
        };

        mClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void updateMap(Location location) {

        String api = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.getLatitude() + "%2C" + location.getLongitude() + "&radius=5000&keyword=foodpantries&key=AIzaSyDmehC_m7o5gW_cqYSQM4fARqRO-tCe3O8";
        Log.d("MapsActivity", api);

        // Create a new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // Create a new JsonObjectRequest that requests available subjects
        JsonObjectRequest requestObj = new JsonObjectRequest
                (Request.Method.GET, api, null,
                        response -> {
                            try {
                                pantryLatCoord = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                pantryLngCoord = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                pantryTitle = response.getJSONArray("results").getJSONObject(0).getString("name");
                                pantryLatCoord2 = response.getJSONArray("results").getJSONObject(1).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                pantryLngCoord2 = response.getJSONArray("results").getJSONObject(1).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                pantryTitle2 = response.getJSONArray("results").getJSONObject(1).getString("name");
                                pantryLatCoord3 = response.getJSONArray("results").getJSONObject(2).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                pantryLngCoord3 = response.getJSONArray("results").getJSONObject(2).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                pantryTitle3 = response.getJSONArray("results").getJSONObject(2).getString("name");
                                Log.d("MapsActivity", "JSON Response: " + response.toString());
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        error -> Log.d("MapsActivity", "Error: " + error.toString()));

        // Add the request to the RequestQueue
        queue.add(requestObj);

        // Get current location
        LatLng myLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());

        //Add location of the first food pantry
        LatLng firstPantry = new LatLng(pantryLatCoord, pantryLngCoord);
        LatLng secondPantry = new LatLng(pantryLatCoord2, pantryLngCoord2);
        LatLng thirdPantry = new LatLng(pantryLatCoord3, pantryLngCoord3);

        // Place a marker at the current location
        MarkerOptions myMarker = new MarkerOptions()
                .title("Here you are!")
                .position(myLatLng);

        //Create marker for food pantry
        MarkerOptions myPantryMarker = new MarkerOptions()
                .title(pantryTitle)
                .position(firstPantry);

        MarkerOptions myPantryMarker2 = new MarkerOptions()
                .title(pantryTitle2)
                .position(secondPantry);

        MarkerOptions myPantryMarker3 = new MarkerOptions()
                .title(pantryTitle3)
                .position(thirdPantry);


        // Remove previous marker
        mMap.clear();

        // Add new marker
        //mMap.addMarker(myMarker);
        mMap.addMarker(myPantryMarker);
        mMap.addMarker(myPantryMarker2);
        mMap.addMarker(myPantryMarker3);

        // Add a new circle
        mMap.addCircle(new CircleOptions()
                .center(myLatLng)
                .radius(7500)
                .strokeColor(Color.CYAN)
                .fillColor(0x220000FF)
                .strokeWidth(5)
        );

        // Zoom to previously saved level
        /*CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myLatLng, mZoomLevel);
        mMap.animateCamera(update);*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnPoiClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Save zoom level
        mMap.setOnCameraMoveListener(() -> {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            mZoomLevel = cameraPosition.zoom;
        });

        // Handle marker click
        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(MapsActivity.this, "Lat: " + marker.getPosition().latitude +
                            System.getProperty("line.separator") + "Long: " + marker.getPosition().longitude,
                    Toast.LENGTH_LONG).show();
            return false;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mClient != null) {
            mClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();

        if (hasLocationPermission()) {
            mClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.getMainLooper());
        }
    }

    private boolean hasLocationPermission() {

        // Request fine location permission if not already granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            mRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return false;
        }

        return true;
    }

    private final ActivityResultLauncher<String> mRequestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    trackLocation();
                }
            });

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(this, "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG)
                .show();
    }
}