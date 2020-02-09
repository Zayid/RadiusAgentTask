package com.task.radiusagent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.task.radiusagent.R;
import com.task.radiusagent.ui.properties.PropertyListPresenter;
import com.task.radiusagent.ui.properties.PropertyTypeRowView;
import com.task.radiusagent.ui.properties.SubItemRowView;
import com.task.radiusagent.ui.properties.TitleRowView;

import java.util.ArrayList;
import java.util.List;


public class PropertyTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_PROPERTY = 1;
    private static final int TYPE_SUB = 2;
    private final PropertyListPresenter propertyListPresenter;
    private int checkedPosition = -1;
    private int excludedPosition = -1;
    private List<Integer> excludedPosList = new ArrayList<>();

    public PropertyTypeAdapter(PropertyListPresenter propertyListPresenter) {
        this.propertyListPresenter = propertyListPresenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_PROPERTY:
                View v1 = inflater.inflate(R.layout.row_item_property_type, parent, false);
                viewHolder = new PropertyTypeViewHolder(v1);
                break;
            case TYPE_SUB:
                View v2 = inflater.inflate(R.layout.row_item_sub_item, parent, false);
                viewHolder = new SubItemViewHolder(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.row_item_property_title, parent, false);
                viewHolder = new TitleViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_TITLE:
                TitleViewHolder vh1 = (TitleViewHolder) holder;
                propertyListPresenter.onBindTitleRowAtPosition(position, vh1);
                break;
            case TYPE_PROPERTY:
                PropertyTypeViewHolder vh2 = (PropertyTypeViewHolder) holder;
                propertyListPresenter.onBindPropertyTypeRowAtPosition(position, vh2);
                break;
            case TYPE_SUB:
                SubItemViewHolder vh3 = (SubItemViewHolder) holder;
                propertyListPresenter.onBindSubItemRowAtPosition(position, vh3);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return propertyListPresenter.getPropertyTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return propertyListPresenter.getPropertyType(position);
    }

    public void updateExclusionItem(int position) {
        int copyOfLastExcluded = excludedPosition;
        excludedPosition = position;
        notifyItemChanged(copyOfLastExcluded);
        notifyItemChanged(excludedPosition);
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder implements TitleRowView {

        TextView tvTitle;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_property_title);
        }

        @Override
        public void setTitle(String title) {
            tvTitle.setText(title);
        }
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder implements SubItemRowView {

        TextView tvSubOptionName;
        ImageView ivSubOption;

        SubItemViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvSubOptionName = itemView.findViewById(R.id.tv_sub_option_name);
            ivSubOption = itemView.findViewById(R.id.iv_sub_option);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemView.isSelected()) {
                        itemView.setSelected(false);
                    } else {
                        itemView.setSelected(true);
                    }
                }
            });
        }

        @Override
        public void setName(String name) {
            tvSubOptionName.setText(name);
        }

        @Override
        public void setIcon(String icon) {
            switch (icon) {
                case "rooms":
                    ivSubOption.setImageResource(R.drawable.ic_rooms);
                    break;
                case "no-room":
                    ivSubOption.setImageResource(R.drawable.ic_no_room);
                    break;
                case "swimming":
                    ivSubOption.setImageResource(R.drawable.ic_swimming);
                    break;
                case "garden":
                    ivSubOption.setImageResource(R.drawable.ic_garden);
                    break;
                case "garage":
                    ivSubOption.setImageResource(R.drawable.ic_garage);
                    break;

            }
        }

        @Override
        public void checkExclusion() {
            if (excludedPosition == getAdapterPosition()) {
                itemView.setBackgroundResource(R.drawable.state_disable_property_type);
                itemView.setFocusable(false);
            }
        }
    }

    public class PropertyTypeViewHolder extends RecyclerView.ViewHolder implements PropertyTypeRowView {

        TextView tvPropertyName;
        ImageView ivIcon;

        PropertyTypeViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvPropertyName = itemView.findViewById(R.id.tv_property_option_name);
            ivIcon = itemView.findViewById(R.id.iv_property_option);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int copyOfLastCheckedPosition = checkedPosition;
                    checkedPosition = getAdapterPosition();
                    notifyItemChanged(copyOfLastCheckedPosition);
                    notifyItemChanged(checkedPosition);

                    propertyListPresenter.onItemInteraction(getAdapterPosition());
                }
            });
        }

        @Override
        public void setPropertyName(String name) {
            tvPropertyName.setText(name);
        }

        @Override
        public void setIcon(String iconId) {
            switch (iconId) {
                case "apartment":
                    ivIcon.setImageResource(R.drawable.ic_apartment);
                    break;
                case "condo":
                    ivIcon.setImageResource(R.drawable.ic_condo);
                    break;
                case "boat":
                    ivIcon.setImageResource(R.drawable.ic_boat);
                    break;
                case "land":
                    ivIcon.setImageResource(R.drawable.ic_land);
                    break;
            }
        }

        @Override
        public void setCheckedPosition() {
            if (checkedPosition == -1) {
                itemView.setSelected(false);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    itemView.setSelected(true);
                } else {
                    itemView.setSelected(false);
                }
            }
        }
    }
}
