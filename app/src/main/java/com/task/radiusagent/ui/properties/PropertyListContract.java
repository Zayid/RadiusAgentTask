package com.task.radiusagent.ui.properties;

import com.task.radiusagent.data.db.model.Exclusions;
import com.task.radiusagent.data.db.model.Facilities;
import com.task.radiusagent.data.network.model.Filter;

import java.util.List;

public interface PropertyListContract {

    interface View {

        void showProgress();

        void hideProgress();

        void setDataToPropertyRecyclerView(List<Object> propertyFilterList);

        void onResponseFailure(Throwable throwable);

        void onNoDataInDb();

        void updateExclusionPosition(int positions);
    }

    interface Interactor {

        void getPropertyFilter(OnFinishedListener onFinishedListener);

        void getPropertyFilterFromDb(OnDbReadFinishedListener onDbReadFinishedListener);

        interface OnFinishedListener {

            void onFinished(Filter propertyFilter);

            void onFailure(Throwable t);
        }

        interface OnDbReadFinishedListener {

            void onDbReadFinished(List<Facilities> facilityList, List<Exclusions> exclusions);

            void onDbReadFailed();
        }
    }

    interface Presenter {

        void requestPropertyFilterFromServer();

        void requestPropertyFilterFromDb();

        void enqueueWorkRequest();
    }
}
