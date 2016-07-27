package kimhieu.me.anzi.ViewPager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import kimhieu.me.anzi.R;
import kimhieu.me.anzi.models.foursquare_photo.Item_;
import kimhieu.me.anzi.models.google.Photo;

/**
 * Created by SONY on 6/11/2016.
 */
public class ImageSliderAdapterFourSquare extends PagerAdapter {
    Context context;

    //int[] imageListFourSquare;
    List<Item_> imageListFourSquare;
    List<Photo>imageListGoogle;
    public ImageSliderAdapterFourSquare(Context context, List<Item_>imageList) {
        this.context = context;
        //this.imageListFourSquare = imageListFourSquare;
        this.imageListFourSquare =imageList;
    }



    @Override
    public int getCount() {
        return imageListFourSquare.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_pager_image, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Glide.with(context).load(imageListFourSquare.get(position).getUrl()).into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }
}

