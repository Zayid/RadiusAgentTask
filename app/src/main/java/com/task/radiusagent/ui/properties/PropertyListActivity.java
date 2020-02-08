package com.task.radiusagent.ui.properties;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.task.radiusagent.R;

import java.util.ArrayList;
import java.util.List;

public class PropertyListActivity extends AppCompatActivity implements PropertyListContract.View,
        PropertyTypeItemClickListener {

    private Toolbar toolbar;
    private ImageView ivFilter;
    private PropertyListPresenter propertyListPresenter;
    private RecyclerView rvPropertyFilter;

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
        propertyListPresenter.requestPropertyFilterFromServer();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setDataToPropertyRecyclerView(List<Object> propertyFilterList) {

    }

    @Override
    public void onResponseFailure(Throwable throwable) {

    }

    @Override
    public void onPropertyItemClick(int position) {

    }
}
