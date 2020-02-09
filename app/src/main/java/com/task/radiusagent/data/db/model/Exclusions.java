package com.task.radiusagent.data.db.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Exclusions extends RealmObject {

    private RealmList<ExclusionPair> exclusionPairs;

    public RealmList<ExclusionPair> getExclusionPairs() {
        return exclusionPairs;
    }

    public void setExclusionPairs(RealmList<ExclusionPair> exclusionPairs) {
        this.exclusionPairs = exclusionPairs;
    }
}
