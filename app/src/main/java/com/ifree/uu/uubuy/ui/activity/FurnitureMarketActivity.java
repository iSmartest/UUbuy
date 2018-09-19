package com.ifree.uu.uubuy.ui.activity;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifree.uu.uubuy.R;
import com.ifree.uu.uubuy.app.MyApplication;
import com.ifree.uu.uubuy.listener.RecyclerItemTouchListener;
import com.ifree.uu.uubuy.service.entity.SecondActivitiesEntity;
import com.ifree.uu.uubuy.service.presenter.SecondListPresenter;
import com.ifree.uu.uubuy.service.view.SecondListView;
import com.ifree.uu.uubuy.ui.adapter.MarketOrStoreAdapter;
import com.ifree.uu.uubuy.ui.base.BaseActivity;
import com.ifree.uu.uubuy.uitls.GlideImageLoader;
import com.ifree.uu.uubuy.uitls.ToastUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: 小火
 * Email:1403241630@qq.com
 * Created by 2018/8/25.
 * Description:商场
 */
public class FurnitureMarketActivity extends BaseActivity implements View.OnClickListener {
    private SecondListPresenter mSecondListPresenter;
    @BindView(R.id.xr_furniture_market_store)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_furniture_store)
    TextView mFurniture;
    @BindView(R.id.tv_building_material_store)
    TextView mBuilding;
    private View headView;
    private int page = 1;
    private MarketOrStoreAdapter mAdapter;
    private List<SecondActivitiesEntity.DataBean.BandCommodityList> mList = new ArrayList<>();
    private String fristActivitiesId;
    private String fristActivitiesType;
    private String fristActivitiesName;
    private TextView mName,mTime;
    private ImageView mPicture;
    private ColorStateList csl1,csl2;
    private String classify = "0";
    @Override
    protected int getLayoutId() {
        return R.layout.activity_furniture_market_store;
    }

    @Override
    protected void initView() {
        hideBack(6);
        setRightText("收藏");
        Resources resource = context.getResources();
        csl1 = resource.getColorStateList(R.color.text_green);
        csl2 = resource.getColorStateList(R.color.title_color);
        fristActivitiesId = getIntent().getStringExtra("fristActivitiesId");
        fristActivitiesType = getIntent().getStringExtra("fristActivitiesType");
        mSecondListPresenter = new SecondListPresenter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallPulse);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);
        xRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        headView = LayoutInflater.from(context).inflate(R.layout.header_market, null);
        headView.findViewById(R.id.ll_market_activities).setOnClickListener(this);
        mName = headView.findViewById(R.id.tv_market_name);
        mPicture = headView.findViewById(R.id.tv_market_picture);
        mTime = headView.findViewById(R.id.tv_market_time);
        headView.findViewById(R.id.tv_market_share).setOnClickListener(this);
        if (headView != null) xRecyclerView.addHeaderView(headView);
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mList.clear();
                mAdapter.notifyDataSetChanged();
                loadData();
                xRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                page++;
                loadData();
                xRecyclerView.loadMoreComplete();
                mAdapter.notifyDataSetChanged();
                xRecyclerView.setNoMore(true);
            }
        });

        mAdapter = new MarketOrStoreAdapter(context, mList);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.addOnItemTouchListener(new RecyclerItemTouchListener(xRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int position = vh.getAdapterPosition() - 2;
                if (position < 0 | position >= mList.size()){
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("fristActivitiesId",mList.get(position).getSecondActivitiesId());
                bundle.putString("fristActivitiesType",mList.get(position).getSecondActivitiesType());
                bundle.putString("fristActivitiesName",mList.get(position).getSecondActivitiesName());
                MyApplication.openActivity(context,StoreActivity.class,bundle);
            }
        });
    }

    @Override
    protected void loadData() {
        mSecondListPresenter.onCreate();
        mSecondListPresenter.attachView(mSecondListView);
        mSecondListPresenter.getSearchSecondListInfo(fristActivitiesId,page,"116",fristActivitiesType,classify,"加载中...");
    }

    private SecondListView mSecondListView = new SecondListView() {
        @Override
        public void onSuccess(SecondActivitiesEntity mSecondListEntity) {
            if (mSecondListEntity.getResultCode().equals("1")){
                ToastUtils.makeText(context,mSecondListEntity.getMsg());
                return;
            }
            List<SecondActivitiesEntity.DataBean.BandCommodityList> secondActivitiesList = mSecondListEntity.getData().getBandCommodityList();
            if (secondActivitiesList != null && !secondActivitiesList.isEmpty()){
                mList.addAll(secondActivitiesList);
                mAdapter.notifyDataSetChanged();
            }
            fristActivitiesName = mSecondListEntity.getData().getMarketInfo().getMarketName();
            mName.setText(fristActivitiesName);
            mTime.setText(mSecondListEntity.getData().getMarketInfo().getActivitiesTime());
            GlideImageLoader.imageLoader(context,mSecondListEntity.getData().getMarketInfo().getActivitiesPic(),mPicture);
        }

        @Override
        public void onError(String result) {
            ToastUtils.makeText(context,result);
        }
    };


    @OnClick({R.id.tv_base_rightText,R.id.tv_furniture_store,R.id.tv_building_material_store})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_base_rightText:
                ToastUtils.makeText(context, "收藏");
                break;
            case R.id.tv_furniture_store:
                mFurniture.setTextColor(csl1);
                mBuilding.setTextColor(csl2);
                classify = "0";
                xRecyclerView.setRefreshing(true);
                break;
            case R.id.tv_building_material_store:
                mFurniture.setTextColor(csl2);
                mBuilding.setTextColor(csl1);
                classify = "1";
                xRecyclerView.setRefreshing(true);
                break;
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_market_activities:
                Bundle bundle = new Bundle();
                bundle.putString("marketId",fristActivitiesId);
                bundle.putString("marketName",fristActivitiesName);
                bundle.putString("type",fristActivitiesType);
                MyApplication.openActivity(context,ActivitiesDetailsActivity.class,bundle);
                break;
            case R.id.tv_market_share:
                ToastUtils.makeText(context,"你点击分享");
                break;
        }
    }
}