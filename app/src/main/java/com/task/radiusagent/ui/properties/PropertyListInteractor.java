package com.task.radiusagent.ui.properties;

import com.task.radiusagent.data.network.RetrofitInstance;
import com.task.radiusagent.data.network.model.Filter;
import com.task.radiusagent.data.network.service.ApiService;

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
                        onFinishedListener.onFinished(response.body());
                    }

                    @Override
                    public void onFailure(Call<Filter> call, Throwable t) {
                        onFinishedListener.onFailure(t);
                    }
                });
    }
}
