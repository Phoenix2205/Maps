package kimhieu.me.anzi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import kimhieu.me.anzi.events.ClusterMarker;
import kimhieu.me.anzi.models.foursquare.Venue;
import kimhieu.me.anzi.models.google.Location;
import kimhieu.me.anzi.models.google.Result;
import kimhieu.me.anzi.network.GPSTracker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private GoogleMap mMap;
    private List<Venue> foursquarelist = new ArrayList<>();
    private List<Result> googlelist=new ArrayList<>();
    private List<Marker>markerList= new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private android.location.Location mLastLocation;
    public static double mLatitude;
    public static double mLongitude;
    private ClusterManager<ClusterMarker> mClusterManager;
    private Venue venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foursquarelist = getIntent().getParcelableArrayListExtra("foursquarelist");
        googlelist=getIntent().getParcelableArrayListExtra("googlelist");
        setContentView(R.layout.activity_maps);
        venue=getIntent().getParcelableExtra("foursquare_specific_location");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "Service Connected");
                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                                // Show an expanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.

                            } else {

                                // No explanation needed, we can request the permission.

                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            }
                        }
                        //get latest position
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        if (mLastLocation != null) {
                            mLatitude = mLastLocation.getLatitude();
                            mLongitude = mLastLocation.getLongitude();
                            Log.d(TAG, String.valueOf(mLatitude));
                            Log.d(TAG, String.valueOf(mLongitude));
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "Connection Suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Connection Failed");
                    }
                })
                .addApi(LocationServices.API)
                .build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (venue==null) {
            displayAllLocation();
        }
        else
        {
            LatLng startingLocation=new LatLng(GPSTracker.latitude,GPSTracker.longtitude);
            mMap.addMarker(new MarkerOptions().position(startingLocation)
                    .title(venue.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            LatLng destination=new LatLng(venue.getLocation().getLat(),venue.getLocation().getLng());
            mMap.addMarker(new MarkerOptions().position(destination)
                    .title(venue.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            getDirection(startingLocation,destination);
        }


    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void displayAllLocation()
    {
        mClusterManager = new ClusterManager<ClusterMarker>(this, mMap);
        // Add a marker in Sydney and move the camera
        //LatLng currentLocation = new LatLng(10.7960682,106.6760491);
        LatLng currentLocation = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        for (Venue v : foursquarelist) {
            kimhieu.me.anzi.models.foursquare.Location l = v.getLocation();
            LatLng latLng = new LatLng(l.getLat(), l.getLng());

            markerList.add(mMap.addMarker(new MarkerOptions().position(latLng).title(v.getName())));
        }

        for (Result result : googlelist) {
            Location l = result.getGeometry().getLocation();
            LatLng latLng = new LatLng(l.getLat(), l.getLng());
            markerList.add(mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(result.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));

        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
            ClusterMarker markerItem = new ClusterMarker(marker.getPosition().latitude, marker.getPosition().longitude);
            mClusterManager.addItem(markerItem);

        }
        LatLngBounds bounds = builder.build();
        int padding = 0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        //googleMap.moveCamera(cu);
        //googleMap.animateCamera(cu);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);

        mMap.setOnCameraChangeListener(mClusterManager);
    }

    void getDirection(LatLng origin, LatLng destination )
    {
        String serverKey="AIzaSyB3g3k8Hsc85LbMvV2wlddNY2Fw3Dj0adw";
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        Route route = direction.getRouteList().get(0);
                        Leg leg = route.getLegList().get(0);
                        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(MapsActivity.this
                                , directionPositionList, 5, Color.RED);
                        mMap.addPolyline(polylineOptions);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });
    }

}
