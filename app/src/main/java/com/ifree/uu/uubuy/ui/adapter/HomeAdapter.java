package com.ifree.uu.uubuy.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifree.uu.uubuy.R;
import com.ifree.uu.uubuy.service.entity.HomeEntity;
import com.ifree.uu.uubuy.uitls.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 小火
 * Email:1403241630@qq.com
 * Created by 2018/8/20.
 * Description:
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{

    private Context context;
    private List<HomeEntity.DataBean.ActivitiesList> mList;
    public HomeAdapter(Context context, List<HomeEntity.DataBean.ActivitiesList> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_main,parent,false);
        HomeViewHolder viewHolder = new HomeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeEntity.DataBean.ActivitiesList activitiesList = mList.get(position);
        holder.address.setText(activitiesList.getActivitiesAdAddress());
        holder.name.setText(activitiesList.getActivitiesName());
        holder.time.setText("活动时间：" + activitiesList.getActivitiesTime());
        GlideImageLoader.imageLoader(context,activitiesList.getActivitiesPic(),holder.icon);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 10 : mList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_home_main_icon)
        ImageView icon;
        @BindView(R.id.tv_home_main_name)
        TextView name;
        @BindView(R.id.tv_home_main_time)
        TextView time;
        @BindView(R.id.tv_home_main_address)
        TextView address;
        public HomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
