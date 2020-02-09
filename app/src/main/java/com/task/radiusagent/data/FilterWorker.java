package com.task.radiusagent.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.task.radiusagent.data.db.model.ExclusionPair;
import com.task.radiusagent.data.db.model.Exclusions;
import com.task.radiusagent.data.db.model.Facilities;
import com.task.radiusagent.data.db.model.FacilityOption;
import com.task.radiusagent.data.network.RetrofitInstance;
import com.task.radiusagent.data.network.model.Exclusion;
import com.task.radiusagent.data.network.model.Facility;
import com.task.radiusagent.data.network.model.Filter;
import com.task.radiusagent.data.network.model.Option;
import com.task.radiusagent.data.network.service.ApiService;

import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterWorker extends Worker {

    public FilterWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        RetrofitInstance
                .getRetrofitInstance()
                .create(ApiService.class)
                .getFilterParams()
                .enqueue(new Callback<Filter>() {
                    @Override
                    public void onResponse(Call<Filter> call, Response<Filter> response) {
                        if (response.isSuccessful()) {
                            //sync data with realm db
                            syncData(Objects.requireNonNull(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Filter> call, Throwable t) {
                    }
                });
        return Result.success();
    }

    private void syncData(Filter propertyFilter) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(Exclusions.class);
                }
            });

            for (List<Exclusion> exclusions : propertyFilter.getExclusions()) {
                final RealmList<ExclusionPair> exclusionPairs = new RealmList<>();
                for (final Exclusion exclusion : exclusions) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ExclusionPair exclusionPair = realm.createObject(ExclusionPair.class);
                            exclusionPair.setFacilityId(exclusion.getFacilityId());
                            exclusionPair.setOptionId(exclusion.getOptionsId());

                            exclusionPairs.add(exclusionPair);
                        }
                    });
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Exclusions exclusions = realm.createObject(Exclusions.class);
                        exclusions.setExclusionPairs(exclusionPairs);
                    }
                });
            }

            for (final Facility facility : propertyFilter.getFacilities()) {
                final RealmList<FacilityOption> facilityOptionsList = new RealmList<>();
                for (final Option option : facility.getOptions()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            FacilityOption facilityOption = realm.createObject(FacilityOption.class);
                            facilityOption.setFacilityId(facility.getFacilityId());
                            facilityOption.setName(option.getName());
                            facilityOption.setIcon(option.getIcon());
                            facilityOption.setOptionId(option.getId());
                            facilityOptionsList.add(facilityOption);
                        }
                    });
                }

                //insert only if the value does not exist
                Facilities facilitiesObj = realm.where(Facilities.class)
                        .equalTo("facilityId", facility.getFacilityId()).findFirst();
                if (facilitiesObj == null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Facilities facilitiesObj = realm.createObject(Facilities.class);
                            facilitiesObj.setFacilityId(facility.getFacilityId());
                            facilitiesObj.setName(facility.getName());
                            facilitiesObj.setOption(facilityOptionsList);
                        }
                    });
                }
            }
        }
    }
}
