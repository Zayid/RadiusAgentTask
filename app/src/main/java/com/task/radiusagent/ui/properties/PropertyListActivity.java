package com.task.radiusagent.ui.properties;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.task.radiusagent.R;
import com.task.radiusagent.adapter.PropertyTypeAdapter;

import java.util.List;

public class PropertyListActivity extends AppCompatActivity implements PropertyListContract.View {

    private Toolbar toolbar;
    private ImageView ivFilter;
    private PropertyListPresenter propertyListPresenter;
    private RecyclerView rvPropertyFilter;
    private ProgressBar pbProgress;
    private PropertyTypeAdapter propertyTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        setUpToolbar();
        initViews();
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.app_bar);
        ivFilter = findViewById(R.id.iv_filter);
        pbProgress = findViewById(R.id.pb_progress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        ivFilter.setOnClickListener(new NavigationIconClickListener(
                PropertyListActivity.this,
                findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                getResources().getDrawable(R.drawable.ic_filter), // Menu open icon
                getResources().getDrawable(R.drawable.ic_close))); // Menu close icon

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findViewById(R.id.product_grid).setBackgroundResource(R.drawable.bg_shape_product_grid);
        }
    }

    private void initViews() {
        propertyListPresenter = new PropertyListPresenter(this, new PropertyListInteractor());

        rvPropertyFilter = findViewById(R.id.rv_property_filter);
        propertyListPresenter.requestPropertyFilterFromDb();
    }

    @Override
    public void showProgress() {
        pbProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbProgress.setVisibility(View.GONE);
    }

    @Override
    public void setDataToPropertyRecyclerView(List<Object> propertyFilterList) {
        propertyTypeAdapter = new PropertyTypeAdapter(propertyListPresenter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = propertyTypeAdapter.getItemViewType(position);
                if (type == 1 || type == 2)
                    return 1;
                else
                    return 2;
            }
        });

        rvPropertyFilter.setLayoutManager(gridLayoutManager);
        rvPropertyFilter.setAdapter(propertyTypeAdapter);

        propertyListPresenter.enqueueWorkRequest();
    }

    @Override
    public void onNoDataInDb() {
        propertyListPresenter.requestPropertyFilterFromServer();
    }

    @Override
    public void updateExclusionPosition(int position) {
        propertyTypeAdapter.updateExclusionItem(position);
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        throwable.getMessage();
        View contextView = findViewById(R.id.fl_parent);

        Snackbar.make(contextView, throwable.getMessage(), Snackbar.LENGTH_LONG)
                .show();
    }
}
