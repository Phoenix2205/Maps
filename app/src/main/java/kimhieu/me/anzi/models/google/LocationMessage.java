package kimhieu.me.anzi.models.google;

import java.util.List;

/**
 * Created by SONY on 6/28/2016.
 */
public class LocationMessage {
    public List<Result> getResultsList() {
        return resultsList;
    }

    public void setResultsList(List<Result> resultsList) {
        this.resultsList = resultsList;
    }

    List<Result>resultsList;
    public LocationMessage(List<Result>results)
    {
        this.resultsList=results;
    }
}
