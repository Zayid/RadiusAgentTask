package com.task.radiusagent.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Facility {
    @SerializedName("facility_id")
    @Expose
    private String facilityId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("options")
    @Expose
    private List<Option> options = null;

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
