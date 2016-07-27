package kimhieu.me.anzi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import kimhieu.me.anzi.ViewPager.CirclePageIndicator;
import kimhieu.me.anzi.ViewPager.ImageSliderAdapterFourSquare;
import kimhieu.me.anzi.ViewPager.ImageSliderAdapterGoogle;
import kimhieu.me.anzi.models.foursquare.Venue;
import kimhieu.me.anzi.models.google.Photo;
import kimhieu.me.anzi.models.google.Result;
import kimhieu.me.anzi.network.GPSTracker;

public class LocationDetailActivity extends AppCompatActivity {
    ViewPager viewPager;
    private CirclePageIndicator mIndicator;
    private Venue venue;
    private Result result;
    private TextView address,price,distance,hours;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        venue=getIntent().getParcelableExtra("LocationFoursquare");
        if (venue==null)
            venue=getIntent().getParcelableExtra("foursquare marker");
        result=getIntent().getParcelableExtra("LocationGoogle");
        if(result==null)
            result=getIntent().getParcelableExtra("google marker");
        onMap();
        setView();
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationDetailActivity.this,MapsActivity.class);
                if (venue!=null)
                  intent.putExtra("foursquare_specific_location",venue);
                if (result!=null)
                    intent.putExtra("google_specific_location",result);
                startActivity(intent);
            }
        });
    }

    void onMap()
    {
        viewPager =(ViewPager)findViewById(R.id.view_pager);
        mIndicator=(CirclePageIndicator)findViewById(R.id.indicator);
        address=(TextView)findViewById(R.id.location_detail_address);
        distance=(TextView)findViewById(R.id.location_detail_distance);
        price =(TextView)findViewById(R.id.location_detail_price);
        hours=(TextView)findViewById(R.id.location_detail_opening_hours);

    }
    void setView()
    {

        if(venue!=null) {
            if (venue.getPhoto() != null) {
                viewPager.setAdapter(new ImageSliderAdapterFourSquare(this, venue.getPhoto().getItems()));
                mIndicator.setViewPager(viewPager);
            }
            address.setText(venue.getLocation().getFormattedAddress().toString());
            LatLng origin=new LatLng(GPSTracker.latitude,GPSTracker.longtitude);
            distance.setText(" "+venue.distanceToVenue(origin)+" km");


        }

        if (result!=null)
        {
            if (result.getPhotos()!=null)
            {
                List<Photo> photoList=result.getPhotos();
                ImageSliderAdapterGoogle imageSliderAdapterGoogle= new ImageSliderAdapterGoogle(this, photoList);
                viewPager.setAdapter(imageSliderAdapterGoogle);
                mIndicator.setViewPager(viewPager);
            }
            address.setText(result.getVicinity());
            LatLng origin=new LatLng(GPSTracker.latitude,GPSTracker.longtitude);
            distance.setText(String.valueOf(""+result.distanceToVenue(origin)+" km"));
        }


    }
}
