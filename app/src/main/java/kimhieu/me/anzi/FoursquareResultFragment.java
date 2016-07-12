package kimhieu.me.anzi;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import kimhieu.me.anzi.events.KeywordSubmitEvent;
import kimhieu.me.anzi.models.foursquare.FoursquareResponse;
import kimhieu.me.anzi.models.foursquare.Venue;
import kimhieu.me.anzi.models.foursquare_photo.PhotoResponse;
import kimhieu.me.anzi.network.FoursquareApi;
import kimhieu.me.anzi.network.FoursquareServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FoursquareResultFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private FoursquareApi foursquareApi;
    public List<Venue> venueList = new ArrayList<>();
    private FoursquareResultRecyclerViewAdapter mAdapter;
    private String APIversion="20130815";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoursquareResultFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FoursquareResultFragment newInstance(int columnCount) {
        FoursquareResultFragment fragment = new FoursquareResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foursquare_result_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new FoursquareResultRecyclerViewAdapter(venueList, mListener,getActivity());
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Subscribe
    public void onEvent(KeywordSubmitEvent event) {
        foursquareApi = FoursquareServiceGenerator.createService(FoursquareApi.class);
        Call<FoursquareResponse> call = foursquareApi.searchVenue("20130815", "10.7960682,106.6760491", event.getmQuery());
        call.enqueue(new Callback<FoursquareResponse>() {
            @Override
            public void onResponse(Call<FoursquareResponse> call, Response<FoursquareResponse> response) {
                Log.d("Success" , String.valueOf(response.body().getResponse().getVenues().size()));
                venueList.clear();
                venueList.addAll(response.body().getResponse().getVenues());
                getPhoto();


            }

            @Override
            public void onFailure(Call<FoursquareResponse> call, Throwable t) {
                Log.d("Failure", t.getMessage());
            }
        });
    }

    private void getPhoto() {
        for (int i=0;i<venueList.size();i++)
        {
            getPhotoFourSquare(venueList.get(i).getId(),i);
        }
    }

    private void getPhotoFourSquare(String id, final int pos) {
        foursquareApi = FoursquareServiceGenerator.createService(FoursquareApi.class);
        Call<PhotoResponse>call=foursquareApi.getPhoto(id,"20130815");
        call.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                Log.i("Get Photo",String.valueOf(response.body().getResponse().getPhotos().getCount()));
                venueList.get(pos).setPhoto(response.body().getResponse().getPhotos());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                Log.i("Fail to get photo",t.getMessage());
            }
        });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
