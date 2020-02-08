package com.task.radiusagent.ui.properties;

import com.task.radiusagent.data.network.model.Filter;

import java.util.ArrayList;
import java.util.List;

public class PropertyListPresenter implements PropertyListContract.Presenter,
        PropertyListContract.Interactor.OnFinishedListener {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_PROPERTY = 1;
    private static final int TYPE_SUB = 2;
    private PropertyListContract.View propertyListView;
    private PropertyListContract.Interactor propertyListInteractor;
    private Filter filter;
    private List<Object> propertyFilterList;

    public PropertyListPresenter(PropertyListContract.View propertyListView,
                                 PropertyListContract.Interactor propertyListInteractor) {
        this.propertyListView = propertyListView;
        this.propertyListInteractor = propertyListInteractor;
    }

    @Override
    public void onFinished(Filter propertyFilter) {
        if (propertyListView != null) {
            filter = propertyFilter;
            propertyFilterList = new ArrayList<>();

            propertyListView.setDataToPropertyRecyclerView(propertyFilterList);
            propertyListView.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if (propertyListView != null) {
            propertyListView.onResponseFailure(t);
            propertyListView.hideProgress();
        }
    }

    @Override
    public void requestPropertyFilterFromServer() {
        if (propertyListView != null) {
            propertyListView.showProgress();
        }

        propertyListInteractor.getPropertyFilter(this);
    }

    @Override
    public void requestPropertyFilterFromDb() {

    }
}
