package com.promact.akansh.irecall;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Akansh on 24-07-2017.
 */

public class AlbumDetails implements Comparable {
    public String AlbumId;
    public String Date;
    public String Filename;
    public String Latitude;
    public String Longitude;
    public String MediaId;
    public String caption;

    @JsonCreator
    public AlbumDetails(@JsonProperty("AlbumId") String AlbumId, @JsonProperty("Date") String Date,
                 @JsonProperty("Filename") String Filename, @JsonProperty("Latitude") String Latitude,
                 @JsonProperty("Longitude") String Longitude, @JsonProperty("MediaId") String MediaId,
                 @JsonProperty("caption") String caption) {
        this.AlbumId = AlbumId;
        this.Date = Date;
        this.Filename = Filename;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.MediaId = MediaId;
        this.caption = caption;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
