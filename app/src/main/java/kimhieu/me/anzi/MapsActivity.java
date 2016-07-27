package kimhieu.me.anzi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    private Result result;
    ArrayList<LatLng> pointList;
    private final Handler mHandler = new Handler();
    private Animator animator = new Animator();
    private Marker selectedMarker;
    List<Marker>route=new ArrayList<>();
    FloatingActionButton showWay;
    HashMap hashMap=new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foursquarelist = getIntent().getParcelableArrayListExtra("foursquarelist");
        googlelist=getIntent().getParcelableArrayListExtra("googlelist");
        setContentView(R.layout.activity_maps);
        venue=getIntent().getParcelableExtra("foursquare_specific_location");
        result=getIntent().getParcelableExtra("google_specific_location");
        showWay=(FloatingActionButton) findViewById(R.id.btn_showway);
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
        if (venue==null&&result==null) {
            displayAllLocation();
        }
        else
        {
            LatLng startingLocation = new LatLng(GPSTracker.latitude, GPSTracker.longtitude);
            if (venue!=null) {

                LatLng destination = new LatLng(venue.getLocation().getLat(), venue.getLocation().getLng());
                getDirection(startingLocation, destination);

            }
            if(result!=null)
            {
                LatLng destination = new LatLng(result.getGeometry().getLocation().getLat(),
                        result.getGeometry().getLocation().getLng());
                getDirection(startingLocation, destination);
            }
            showWay.setVisibility(View.VISIBLE);
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

    private void displayAllLocation() {
        if (foursquarelist.size() == 0 && googlelist.size() == 0) {
            GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);
            if (gpsTracker.canGetLocation()) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongtitude();
                Toast.makeText(MapsActivity.this,
                        "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                LatLng currentLocation = new LatLng(GPSTracker.latitude, GPSTracker.longtitude);
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

            }
        }
        else {
                mClusterManager = new ClusterManager<ClusterMarker>(this, mMap);

                if (foursquarelist.size() != 0) {
                    for (Venue v : foursquarelist) {
                        kimhieu.me.anzi.models.foursquare.Location l = v.getLocation();
                        LatLng latLng = new LatLng(l.getLat(), l.getLng());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(v.getName()).snippet(v.getLocation().getFormattedAddress().toString()));
                        markerList.add(marker);
                        hashMap.put(marker, v);
                    }
                }
                if (googlelist.size() != 0) {
                    for (Result result : googlelist) {
                        Location l = result.getGeometry().getLocation();
                        LatLng latLng = new LatLng(l.getLat(), l.getLng());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(result.getName()).snippet(result.getVicinity().toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        hashMap.put(marker, result);

                    }
                }

                if (markerList.size() != 0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markerList) {
                        builder.include(marker.getPosition());
                        ClusterMarker markerItem = new ClusterMarker(marker.getPosition().latitude, marker.getPosition().longitude);
                        mClusterManager.addItem(markerItem);
                        LatLngBounds bounds = builder.build();
                        int padding = 0;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.moveCamera(cu);
                        mMap.animateCamera(cu);
                        mMap.setOnCameraChangeListener(mClusterManager);
                    }

                }
            }

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(MapsActivity.this, LocationDetailActivity.class);
                    if (hashMap.get(marker) instanceof Venue)
                        intent.putExtra("foursquare marker", (Venue) hashMap.get(marker));
                    else
                        intent.putExtra("google marker", (Result) hashMap.get(marker));
                    startActivity(intent);
                }
            });

        }



    void getDirection(LatLng origin, LatLng destination)
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
                        pointList = leg.getDirectionPoint();
                        addMarkerToMap();
                        animator.startAnimation(true);
                        showWay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                animator.startAnimation(true);
                            }
                        });


                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });
    }



    public class Animator implements Runnable {

        private static final int ANIMATE_SPEEED = 1500;
        private static final int ANIMATE_SPEEED_TURN = 1000;
        private static final int BEARING_OFFSET = 20;


        private final Interpolator interpolator = new LinearInterpolator();

        int currentIndex = 0;

        float tilt = 90;
        float zoom = 15.5f;
        boolean upward=true;

        long start = SystemClock.uptimeMillis();

        LatLng endLatLng = null;
        LatLng beginLatLng = null;

        boolean showPolyline = false;

        private Marker trackingMarker;

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();

        }

        public void stop() {
            trackingMarker.remove();
            mHandler.removeCallbacks(animator);

        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;

            highLightMarker(0);

            if (showPolyLine) {
                polyLine = initializePolyLine();
            }

            // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
//            LatLng markerPos = new LatLng(pointList.get(0).latitude,pointList.get(0).longitude);
//            LatLng secondPos = new LatLng(pointList.get(1).latitude,pointList.get(1).longitude);
            LatLng markerPos = route.get(0).getPosition();
            LatLng secondPos = route.get(1).getPosition();
            setupCameraPositionForMovement(markerPos, secondPos);

        }

        private void setupCameraPositionForMovement(LatLng markerPos,
                                                    LatLng secondPos) {

            float bearing = bearingBetweenLatLngs(markerPos,secondPos);

            trackingMarker = mMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet"));

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(markerPos)
                            .bearing(bearing + BEARING_OFFSET)
                            .tilt(90)
                            .zoom(mMap.getCameraPosition().zoom >=16 ? mMap.getCameraPosition().zoom : 16)
                            .build();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");
                            animator.reset();
                            Handler handler = new Handler();
                            handler.post(animator);
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }
            );
        }

        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();


        private Polyline initializePolyLine() {
            //polyLinePoints = new ArrayList<LatLng>();
           // rectOptions.add(new LatLng(pointList.get(0).latitude,pointList.get(0).longitude));
            rectOptions.add(route.get(0).getPosition());
            return mMap.addPolyline(rectOptions);
        }

        /**
         * Add the marker to the polyline.
         */
        private void updatePolyLine(LatLng latLng) {
            List<LatLng> points = polyLine.getPoints();
            points.add(latLng);
            polyLine.setPoints(points);
        }


        public void stopAnimation() {
            animator.stop();
        }

        public void startAnimation(boolean showPolyLine) {
            if (pointList.size()>2) {
                animator.initialize(showPolyLine);
            }
        }


        @Override
        public void run() {

            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);

//			LatLng endLatLng = getEndLatLng();
//			LatLng beginLatLng = getBeginLatLng();

            double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
            double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
            LatLng newPosition = new LatLng(lat, lng);

            trackingMarker.setPosition(newPosition);

            if (showPolyline) {
                updatePolyLine(newPosition);
            }

            // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
            //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
            //navigateToPoint(newPosition,false);

            if (t< 1) {
                mHandler.postDelayed(this, 16);

            } else {

                System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + pointList.size());
                // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4

                if (currentIndex<pointList.size()-2) {

                    currentIndex++;

                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();


                    start = SystemClock.uptimeMillis();

                    LatLng begin = getBeginLatLng();
                    LatLng end = getEndLatLng();

                    float bearingL = bearingBetweenLatLngs(begin, end);

                    highLightMarker(currentIndex);

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(end) // changed this...
                                    .bearing(bearingL  + BEARING_OFFSET)
                                    .tilt(tilt)
                                    .zoom(mMap.getCameraPosition().zoom)
                                    .build();


                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );

                    start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(animator, 16);

                } else {
                    currentIndex++;
                    highLightMarker(currentIndex);
                    stopAnimation();
                }

            }
        }




        private LatLng getEndLatLng() {
            return new LatLng(pointList.get(currentIndex+1).latitude,pointList.get(currentIndex+1).longitude);
        }

        private LatLng getBeginLatLng() {
            return new LatLng(pointList.get(currentIndex).latitude,pointList.get(currentIndex).longitude);
        }

        private void adjustCameraPosition() {
            //System.out.println("tilt = " + tilt);
            //System.out.println("upward = " + upward);
            //System.out.println("zoom = " + zoom);
            if (upward) {

                if (tilt<90) {
                    tilt ++;
                    zoom-=0.01f;
                } else {
                    upward=false;
                }

            } else {
                if (tilt>0) {
                    tilt --;
                    zoom+=0.01f;
                } else {
                    upward=true;
                }
            }
        }
    };

    /**
     * Allows us to navigate to a certain point.
     */
    public void navigateToPoint(LatLng latLng,float tilt, float bearing, float zoom,boolean animate) {
        CameraPosition position =
                new CameraPosition.Builder().target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build();

        changeCameraPosition(position, animate);

    }

    public void navigateToPoint(LatLng latLng, boolean animate) {
        CameraPosition position = new CameraPosition.Builder().target(latLng).build();
        changeCameraPosition(position, animate);
    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }

    private android.location.Location convertLatLngToLocation(LatLng latLng) {
        android.location.Location loc = new android.location.Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        android.location.Location beginL= convertLatLngToLocation(begin);
        android.location.Location endL= convertLatLngToLocation(end);

        return beginL.bearingTo(endL);
    }

    public void toggleStyle() {
        if (GoogleMap.MAP_TYPE_NORMAL == mMap.getMapType()) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


    /**
     * Adds a marker to the map.
     */
    public void addMarkerToMap() {
        for(int i =0;i<pointList.size();i++) {
            LatLng position=new LatLng(pointList.get(i).latitude, pointList.get(i).longitude);
            String add=getAddress(position.latitude,position.longitude);
            Marker marker = mMap.addMarker(new MarkerOptions().
                    position(position).title(add));

            route.add(marker);
        }

    }

    /**
     * Clears all markers from the map.
     */
//    public void clearMarkers() {
//        mMap.clear();
//        markers.clear();
//    }

    /**
     * Remove the currently selected marker.
     */
//    public void removeSelectedMarker() {
//        this.markers.remove(this.selectedMarker);
//        this.selectedMarker.remove();
//    }

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {
        highLightMarker(route.get(index));
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {

		/*
		for (Marker foundMarker : this.markers) {
			if (!foundMarker.equals(marker)) {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			} else {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				foundMarker.showInfoWindow();
			}
		}
		*/
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        marker.showInfoWindow();

        //Utils.bounceMarker(googleMap, marker);

        this.selectedMarker=marker;
    }

    private void resetMarkers() {
        for (Marker marker : this.route) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        String add="";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
           add = obj.getAddressLine(0);
//            GUIStatics.currentAddress = obj.getSubAdminArea() + ","
//                    + obj.getAdminArea();
//            GUIStatics.latitude = obj.getLatitude();
//            GUIStatics.longitude = obj.getLongitude();
//            GUIStatics.currentCity= obj.getSubAdminArea();
//            GUIStatics.currentState= obj.getAdminArea();
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }


}

