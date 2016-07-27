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
import kimhieu.me.anzi.models.google.Photo;

/**
 * Created by SONY on 7/20/2016.
 */
public class ImageSliderAdapterGoogle extends PagerAdapter {
    Context context;
    List<Photo>imageListGoogle;

    public ImageSliderAdapterGoogle(Context context, List<Photo>imageList) {
        this.context = context;
        this.imageListGoogle =imageList;
    }



    @Override
    public int getCount() {
        return imageListGoogle.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.view_pager_image, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        if(imageListGoogle.size()!=0)
        Glide.with(context).load(imageListGoogle.get(position).getUrl()).into(imageView);
        else  Glide.with(context).load(imageListGoogle.get(0).getUrl()).into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }
}


