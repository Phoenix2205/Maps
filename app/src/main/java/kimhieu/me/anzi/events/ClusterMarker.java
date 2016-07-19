package kimhieu.me.anzi.events;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by SONY on 7/18/2016.
 */
public class ClusterMarker implements ClusterItem {
    private final LatLng mPosition;

    public ClusterMarker(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
