package kimhieu.me.anzi.events;

public class KeywordSubmitEvent {

    private String mQuery;

    public String getCurrentLocation() {
        return currentLocation;
    }

    private String currentLocation;

    public KeywordSubmitEvent(String query,String currentLocation) {
        this.mQuery = query;
        this.currentLocation=currentLocation;
    }

    public String getmQuery() {
        return mQuery;
    }
}