
package kimhieu.me.anzi.models.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Photo {

    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions = new ArrayList<String>();
    @SerializedName("photo_reference")
    @Expose
    private String photoReference;
    @SerializedName("width")
    @Expose
    private Integer width;
    private String size="maxwidth=400";//+getWidth().toString()+AMPERAND+"maxheight="+getHeight().toString();
    private String apiPref="photoreference="+getPhotoReference();
    private String key="key="+KEY;
    private static final String API="https://maps.googleapis.com/maps/api/place/photo?";
    private static final String KEY="AIzaSyB3g3k8Hsc85LbMvV2wlddNY2Fw3Dj0adw";
    private static final String AMPERAND="&";

    public String getUrl() {
        //return url= API+size+AMPERAND+apiPref+AMPERAND+key;
        return url=API+size+AMPERAND+"photoreference="+photoReference+AMPERAND+"key="+KEY;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;


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
     *     The htmlAttributions
     */
    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    /**
     * 
     * @param htmlAttributions
     *     The html_attributions
     */
    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    /**
     * 
     * @return
     *     The photoReference
     */
    public String getPhotoReference() {
        return photoReference;
    }

    /**
     * 
     * @param photoReference
     *     The photo_reference
     */
    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
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



}
