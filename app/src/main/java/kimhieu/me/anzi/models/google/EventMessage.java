package kimhieu.me.anzi.models.google;

/**
 * Created by SONY on 6/27/2016.
 */
public class EventMessage {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String message;
    public  EventMessage (String message)
    {
        this.message=message;
    }


}
