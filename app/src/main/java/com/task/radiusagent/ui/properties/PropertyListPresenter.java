package com.task.radiusagent.ui.properties;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.task.radiusagent.data.FilterWorker;
import com.task.radiusagent.data.db.model.ExclusionPair;
import com.task.radiusagent.data.db.model.Exclusions;
import com.task.radiusagent.data.db.model.Facilities;
import com.task.radiusagent.data.db.model.FacilityOption;
import com.task.radiusagent.data.network.model.Exclusion;
import com.task.radiusagent.data.network.model.Facility;
import com.task.radiusagent.data.network.model.FacilityTitle;
import com.task.radiusagent.data.network.model.Filter;
import com.task.radiusagent.data.network.model.Option;
import com.task.radiusagent.data.network.model.PropertyOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;

public class PropertyListPresenter implements PropertyListContract.Presenter,
        PropertyListContract.Interactor.OnFinishedListener,
        PropertyListContract.Interactor.OnDbReadFinishedListener {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_PROPERTY = 1;
    private static final int TYPE_SUB = 2;
    private PropertyListContract.View propertyListView;
    private PropertyListContract.Interactor propertyListInteractor;
    private List<Object> propertyFilterList;
    private List<List<Exclusion>> exclusionsList;
    private Realm realm;

    PropertyListPresenter(PropertyListContract.View propertyListView,
                          PropertyListContract.Interactor propertyListInteractor) {
        this.propertyListView = propertyListView;
        this.propertyListInteractor = propertyListInteractor;
    }

    @Override
    public void onFinished(Filter propertyFilter) {
        if (propertyListView != null) {

            // Realm insert operation
            try {
                realm = Realm.getDefaultInstance();
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
            } finally {
                realm.close();
            }

            arrangeDataForRv(propertyFilter);
            propertyListView.setDataToPropertyRecyclerView(propertyFilterList);
            propertyListView.hideProgress();
        }
    }

    private void arrangeDataForRv(Filter propertyFilter) {
        propertyFilterList = new ArrayList<>();
        exclusionsList = new ArrayList<>();

        // Re-arranging the schema for a multi-view RecyclerView
        for (Facility facility : propertyFilter.getFacilities()) {
            FacilityTitle facilityTitle = new FacilityTitle();
            facilityTitle.setFacilityId(facility.getFacilityId());
            facilityTitle.setFacilityName(facility.getName());
            propertyFilterList.add(facilityTitle);

            for (Option option : facility.getOptions()) {
                PropertyOptions propertyOptions = new PropertyOptions();
                propertyOptions.setFacilityId(facility.getFacilityId());
                propertyOptions.setOptionName(option.getName());
                propertyOptions.setOptionIcon(option.getIcon());
                propertyOptions.setOptionId(option.getId());
                propertyFilterList.add(propertyOptions);
            }
        }

        exclusionsList = propertyFilter.getExclusions();
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
        if (propertyListView != null) {
            propertyListView.showProgress();
        }

        propertyListInteractor.getPropertyFilterFromDb(this);
    }

    @Override
    public void enqueueWorkRequest() {
        WorkManager mWorkManager = WorkManager.getInstance();

        // Not adding any constraints, minimum repeat interval once per day
        PeriodicWorkRequest filterRequest =
                new PeriodicWorkRequest.Builder(FilterWorker.class, 1, TimeUnit.DAYS)
                        .build();

        mWorkManager.enqueue(filterRequest);
    }

    @Override
    public void onDbReadFinished(List<Facilities> facilityList, List<Exclusions> exclusionLists) {
        propertyFilterList = new ArrayList<>();
        exclusionsList = new ArrayList<>();

        // Re-arranging the schema for a multi-view RecyclerView
        for (Facilities facility : facilityList) {
            FacilityTitle facilityTitle = new FacilityTitle();
            facilityTitle.setFacilityId(facility.getFacilityId());
            facilityTitle.setFacilityName(facility.getName());
            propertyFilterList.add(facilityTitle);

            for (FacilityOption option : facility.getOption()) {
                PropertyOptions propertyOptions = new PropertyOptions();
                propertyOptions.setFacilityId(facility.getFacilityId());
                propertyOptions.setOptionName(option.getName());
                propertyOptions.setOptionIcon(option.getIcon());
                propertyOptions.setOptionId(option.getOptionId());
                propertyFilterList.add(propertyOptions);
            }
        }

        for (Exclusions exclusions : exclusionLists) {
            RealmList<ExclusionPair> exclusionPairList = exclusions.getExclusionPairs();

            List<Exclusion> exclusionListTemp = new ArrayList<>();
            for (ExclusionPair exclusionPair : exclusionPairList) {
                Exclusion exclusionTemp = new Exclusion();
                exclusionTemp.setFacilityId(exclusionPair.getFacilityId());
                exclusionTemp.setOptionsId(exclusionPair.getOptionId());
                exclusionListTemp.add(exclusionTemp);
            }

            exclusionsList.add(exclusionListTemp);
        }

        propertyListView.setDataToPropertyRecyclerView(propertyFilterList);
        propertyListView.hideProgress();
    }

    @Override
    public void onDbReadFailed() {
        if (propertyListView != null) {
            propertyListView.hideProgress();
            propertyListView.onNoDataInDb();
        }
    }

    public void onBindPropertyTypeRowAtPosition(int position, PropertyTypeRowView propertyTypeRowView) {
        PropertyOptions option = (PropertyOptions) propertyFilterList.get(position);

        propertyTypeRowView.setPropertyName(option.getOptionName());
        propertyTypeRowView.setIcon(option.getOptionIcon());
        propertyTypeRowView.setCheckedPosition();
    }

    public void onBindTitleRowAtPosition(int position, TitleRowView titleRowView) {
        FacilityTitle facilityTitle = (FacilityTitle) propertyFilterList.get(position);

        titleRowView.setTitle(facilityTitle.getFacilityName());
    }

    public void onBindSubItemRowAtPosition(int position, SubItemRowView subItemRowView) {
        PropertyOptions option = (PropertyOptions) propertyFilterList.get(position);

        subItemRowView.setName(option.getOptionName());
        subItemRowView.setIcon(option.getOptionIcon());
        subItemRowView.checkExclusion();
    }

    public int getPropertyTypeCount() {
        return propertyFilterList.size();
    }

    public int getPropertyType(int position) {
        if (propertyFilterList.get(position) instanceof FacilityTitle) {
            return TYPE_TITLE;
        } else if (propertyFilterList.get(position) instanceof PropertyOptions) {
            if (((PropertyOptions) propertyFilterList.get(position)).getFacilityId().equals("1")) {
                return TYPE_PROPERTY;
            } else {
                return TYPE_SUB;
            }
        }
        return -1;
    }

    public void onItemInteraction(int position) {
        if (propertyFilterList.get(position) instanceof PropertyOptions) {
            Exclusion propertyExclusion = new Exclusion();
            propertyExclusion.setOptionsId(((PropertyOptions) propertyFilterList.get(position)).getOptionId());
            propertyExclusion.setFacilityId(((PropertyOptions) propertyFilterList.get(position)).getFacilityId());

            // Mapping the exclusion object of selected item with available exclusion pairs
            int exclusionPosition = -1;
            for (List<Exclusion> exclusions : exclusionsList) {
                for (Exclusion exclusion : exclusions) {
                    if (exclusion.getFacilityId().equals(propertyExclusion.getFacilityId()) &&
                            exclusion.getOptionsId().equals(propertyExclusion.getOptionsId())) {
                        Exclusion exclude = exclusions.get(1);
                        for (int i = 0; i < propertyFilterList.size(); i++) {
                            Object rvItem = propertyFilterList.get(i);
                            if (rvItem instanceof PropertyOptions) {
                                if (exclude.getFacilityId().equals(((PropertyOptions) rvItem).getFacilityId()) &&
                                        exclude.getOptionsId().equals(((PropertyOptions) rvItem).getOptionId())) {
                                    exclusionPosition = i;
                                }
                            }
                        }
                    }
                }
            }

            if (propertyListView != null) {
                propertyListView.updateExclusionPosition(exclusionPosition);
            }
        }

    }
}
