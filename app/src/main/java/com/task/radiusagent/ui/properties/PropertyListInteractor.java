package com.task.radiusagent.ui.properties;

import com.task.radiusagent.data.db.model.Exclusions;
import com.task.radiusagent.data.db.model.Facilities;
import com.task.radiusagent.data.network.RetrofitInstance;
import com.task.radiusagent.data.network.model.Filter;
import com.task.radiusagent.data.network.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PropertyListInteractor implements PropertyListContract.Interactor {

    @Override
    public void getPropertyFilter(final OnFinishedListener onFinishedListener) {
        RetrofitInstance
                .getRetrofitInstance()
                .create(ApiService.class)
                .getFilterParams()
                .enqueue(new Callback<Filter>() {
                    @Override
                    public void onResponse(Call<Filter> call, Response<Filter> response) {
                        if (response.isSuccessful()) {
                            onFinishedListener.onFinished(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Filter> call, Throwable t) {
                        onFinishedListener.onFailure(t);
                    }
                });
    }

    @Override
    public void getPropertyFilterFromDb(OnDbReadFinishedListener onDbReadFinishedListener) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Facilities> facilitiesRealmResults = realm.where(Facilities.class).findAll();
        RealmResults<Exclusions> exclusionsRealmResults = realm.where(Exclusions.class).findAll();

        List<Facilities> facilityList = new ArrayList<>(realm.copyFromRealm(facilitiesRealmResults));
        List<Exclusions> exclusions = new ArrayList<>(realm.copyFromRealm(exclusionsRealmResults));

        if (facilityList.size() != 0) {
            onDbReadFinishedListener.onDbReadFinished(facilityList, exclusions);
        } else {
            onDbReadFinishedListener.onDbReadFailed();
        }
    }
}
