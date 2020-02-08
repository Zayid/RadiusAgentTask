package com.task.radiusagent.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exclusion {
    @SerializedName("facility_id")
    @Expose
    private String facilityId;
    @SerializedName("options_id")
    @Expose
    private String optionsId;

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getOptionsId() {
        return optionsId;
    }

    public void setOptionsId(String optionsId) {
        this.optionsId = optionsId;
    }
}
