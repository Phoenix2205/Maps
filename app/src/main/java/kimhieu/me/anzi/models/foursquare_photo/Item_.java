
package kimhieu.me.anzi.models.foursquare_photo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Item_ implements Parcelable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("createdAt")
    @Expose
    private Integer createdAt;
    @SerializedName("source")
    @Expose
    private Source source;
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("suffix")
    @Expose
    private String suffix;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("checkin")
    @Expose
    private Checkin checkin;
    @SerializedName("visibility")
    @Expose
    private String visibility;

    private String size="original";

    protected Item_(Parcel in) {
        id = in.readString();
        prefix = in.readString();
        suffix = in.readString();
        visibility = in.readString();
        size = in.readString();
        url = in.readString();
    }

    public static final Creator<Item_> CREATOR = new Creator<Item_>() {
        @Override
        public Item_ createFromParcel(Parcel in) {
            return new Item_(in);
        }

        @Override
        public Item_[] newArray(int size) {
            return new Item_[size];
        }
    };

    public String getUrl() {
       return  prefix+"original"+suffix;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public Integer getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The createdAt
     */
    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The source
     */
    public Source getSource() {
        return source;
    }

    /**
     * 
     * @param source
     *     The source
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * 
     * @return
     *     The prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 
     * @param prefix
     *     The prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 
     * @return
     *     The suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 
     * @param suffix
     *     The suffix
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 
     * @return
     *     The width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * 
     * @param width
     *     The width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * 
     * @return
     *     The height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * 
     * @param height
     *     The height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * 
     * @return
     *     The user
     */
    public User getUser() {
        return user;
    }

    /**
     * 
     * @param user
     *     The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 
     * @return
     *     The checkin
     */
    public Checkin getCheckin() {
        return checkin;
    }

    /**
     * 
     * @param checkin
     *     The checkin
     */
    public void setCheckin(Checkin checkin) {
        this.checkin = checkin;
    }

    /**
     * 
     * @return
     *     The visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * 
     * @param visibility
     *     The visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(prefix);
        dest.writeString(suffix);
        dest.writeString(visibility);
        dest.writeString(size);
        dest.writeString(url);
    }
}
