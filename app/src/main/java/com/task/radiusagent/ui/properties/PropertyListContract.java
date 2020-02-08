package com.task.radiusagent.ui.properties;

import com.task.radiusagent.data.network.model.Filter;

import java.util.List;

public interface PropertyListContract {

    interface View {

        void showProgress();

        void hideProgress();

        void setDataToPropertyRecyclerView(List<Object> propertyFilterList);

        void onResponseFailure(Throwable throwable);
    }

    interface Interactor {

        void getPropertyFilter(OnFinishedListener onFinishedListener);

        interface OnFinishedListener {

            void onFinished(Filter propertyFilter);

            void onFailure(Throwable t);
        }
    }

    interface Presenter {

        void requestPropertyFilterFromServer();

        void requestPropertyFilterFromDb();
    }
}
