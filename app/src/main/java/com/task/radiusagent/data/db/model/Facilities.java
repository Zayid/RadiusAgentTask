package com.task.radiusagent.data.db.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Facilities extends RealmObject {
    private String facilityId;
    private String name;
    private RealmList<FacilityOption> option;

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

    public RealmList<FacilityOption> getOption() {
        return option;
    }

    public void setOption(RealmList<FacilityOption> option) {
        this.option = option;
    }
}
