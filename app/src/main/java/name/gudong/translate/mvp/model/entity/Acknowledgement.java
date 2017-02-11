package name.gudong.translate.mvp.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 7/17/16
 * Time: 12:44 PM
 * Desc: Acknowledgement
 */
public class Acknowledgement {

    @SerializedName("name")
    public String name;

    @SerializedName("url")
    public String url;

    @SerializedName("description")
    public String description;

    @SerializedName("icon")
    public String icon;

    @SerializedName("license_name")
    public String licenseName;

    @SerializedName("license")
    public String licensePath;
}
