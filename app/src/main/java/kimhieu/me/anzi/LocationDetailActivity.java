package kimhieu.me.anzi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import kimhieu.me.anzi.ViewPager.CirclePageIndicator;
import kimhieu.me.anzi.ViewPager.ImageSliderAdapter;
import kimhieu.me.anzi.models.foursquare.Venue;

public class LocationDetailActivity extends AppCompatActivity {
    ViewPager viewPager;
    private CirclePageIndicator mIndicator;
    private Venue venue;
    private TextView address,price,distance,hours;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        venue=getIntent().getParcelableExtra("LocationFoursquare");
        onMap();
        setView();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(LocationDetailActivity.this,MapsActivity.class);
                if (venue!=null)
                  intent.putExtra("foursquare_specific_location",venue);
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
                viewPager.setAdapter(new ImageSliderAdapter(this, venue.getPhoto().getItems()));
                mIndicator.setViewPager(viewPager);
            }
            address.setText(venue.getLocation().getFormattedAddress().toString());

        }


    }
}
