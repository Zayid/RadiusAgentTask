package com.task.radiusagent.data.db.model;

import io.realm.RealmObject;

public class ExclusionPair extends RealmObject {

    private String facilityId;
    private String optionId;

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }
}
