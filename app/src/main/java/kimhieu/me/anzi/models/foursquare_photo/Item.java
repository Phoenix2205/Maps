
package kimhieu.me.anzi.models.foursquare_photo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Item {

    @SerializedName("unreadCount")
    @Expose
    private Integer unreadCount;

    /**
     * 
     * @return
     *     The unreadCount
     */
    public Integer getUnreadCount() {
        return unreadCount;
    }

    /**
     * 
     * @param unreadCount
     *     The unreadCount
     */
    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

}
