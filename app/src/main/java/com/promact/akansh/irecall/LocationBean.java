package com.promact.akansh.irecall;

/**
 * Created by Akansh on 11-07-2017.
 */

public class LocationBean {
    public double latitude;
    public double longitude;
    public String userid;
    public String albumid;
    public String imageId;
    public String mediaId;
    public String caption;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
/*public LocationBean() {
    }*/

    public LocationBean(double latitude, double longitude, String userid, String albumid, String imageId, String mediaId, String caption) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userid = userid;
        this.albumid = userid;
        this.imageId = userid;
        this.mediaId = mediaId;
        this.caption= caption;
    }
}
