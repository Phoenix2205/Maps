package kimhieu.me.anzi.network;

import kimhieu.me.anzi.models.google.LocationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by SONY on 7/7/2016.
 */
public interface GoogleApi {
    @GET("nearbysearch/json")
    Call<LocationResponse> searchVenue(@Query("location") String location,
                                       @Query("radius") String radius,
                                       @Query("name") String name,
                                       @Query("key")String key);
    //nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=YOUR_API_KEY
}
