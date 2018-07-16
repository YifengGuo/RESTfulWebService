package hello;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Created by guoyifeng on 7/16/18.
 */
public class Location {
    private long id;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;



    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public double getLatitude() {
        return latitude;
    }

    @JsonProperty
    public double getLongitude() {
        return longitude;
    }

    @JsonProperty
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty
    public String getGeoHash() {
        return GeoHashUtils.encode(latitude, longitude);
    }
}

