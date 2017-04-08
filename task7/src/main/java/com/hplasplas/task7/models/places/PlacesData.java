
package com.hplasplas.task7.models.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hplasplas.task7.models.commonclass.Coord;

public class PlacesData {

    @SerializedName("_id")
    @Expose
    private int cityId;
    @SerializedName("name")
    @Expose
    private String cityName;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("coord")
    @Expose
    private Coord coord;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

}
