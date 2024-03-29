package com.ifree.uu.uubuy.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ifree.uu.uubuy.R;
import com.ifree.uu.uubuy.uitls.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author：小火
 * Email：1403241630@qq.com
 * Created by 2018/9/14 0014
 * Description:
 */
public class CommodityInfoAdapter extends RecyclerView.Adapter<CommodityInfoAdapter.CommodityInfoViewHolder>{
    private Context context;
    private List<String> mList;
    public CommodityInfoAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public CommodityInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_commodity_icon,parent,false);
        CommodityInfoViewHolder viewHolder = new CommodityInfoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommodityInfoViewHolder holder, int position) {
        GlideImageLoader.imageLoader(context,mList.get(position),holder.mIcon);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class CommodityInfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_commodity_icon)
        ImageView mIcon;
        public CommodityInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
