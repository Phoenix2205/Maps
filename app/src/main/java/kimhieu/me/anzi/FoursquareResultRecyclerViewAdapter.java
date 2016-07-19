package kimhieu.me.anzi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import kimhieu.me.anzi.dummy.DummyContent.DummyItem;
import kimhieu.me.anzi.models.foursquare.Venue;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FoursquareResultRecyclerViewAdapter extends RecyclerView.Adapter<FoursquareResultRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private final List<Venue> mValues;
    private final OnListFragmentInteractionListener mListener;

    public FoursquareResultRecyclerViewAdapter(List<Venue> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_foursquare_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getName());
        holder.mContentView.setText(mValues.get(position).getLocation().getFormattedAddress().toString());
        if(mValues.get(position).getPhoto()!=null)
                if(mValues.get(position).getPhoto().getItems()!=null)
                    if(mValues.get(position).getPhoto().getItems().size()!=0) {

                        String url=mValues.get(position).getPhoto().getItems().get(0).getUrl();

                        Glide.with(context).load(url).into(holder.imageView);
                    }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LocationDetailActivity.class);
                intent.putExtra("LocationFoursquare",mValues.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView imageView;
        public final CardView cardView;
        public Venue mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.foursquare_text_view_name);
            mContentView = (TextView) view.findViewById(R.id.foursquare_text_view_address);
            imageView=(ImageView)view.findViewById(R.id.foursquare_image_view_place);
            cardView=(CardView)view.findViewById(R.id.card_view_foursquare);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
