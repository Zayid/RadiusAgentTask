package com.task.radiusagent.data.network.service;

import com.task.radiusagent.data.network.model.Filter;

import retrofit2.Call;
import retrofit2.http.GET;

import static com.task.radiusagent.util.Constants.FILTER_ENDPOINT;

public interface ApiService {

    @GET(FILTER_ENDPOINT)
    Call<Filter> getFilterParams();
}
