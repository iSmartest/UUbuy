package com.ifree.uu.uubuy.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ifree.uu.uubuy.R;
import com.ifree.uu.uubuy.app.MyApplication;
import com.ifree.uu.uubuy.config.BaseUrl;
import com.ifree.uu.uubuy.custom.ImageSlideshow;
import com.ifree.uu.uubuy.custom.MarqueeTextView;
import com.ifree.uu.uubuy.listener.MarqueeTextViewClickListener;
import com.ifree.uu.uubuy.listener.RecyclerItemTouchListener;
import com.ifree.uu.uubuy.service.entity.HomeEntity;
import com.ifree.uu.uubuy.service.presenter.HomePresenter;
import com.ifree.uu.uubuy.service.view.HomeView;
import com.ifree.uu.uubuy.ui.activity.BrandActivity;
import com.ifree.uu.uubuy.ui.activity.FirstClassifyActivity;
import com.ifree.uu.uubuy.ui.activity.FurnitureMarketActivity;
import com.ifree.uu.uubuy.ui.activity.MarketActivity;
import com.ifree.uu.uubuy.ui.activity.ShoppingMallActivity;
import com.ifree.uu.uubuy.ui.activity.StoreActivity;
import com.ifree.uu.uubuy.ui.adapter.AdTypeAdapter;
import com.ifree.uu.uubuy.ui.adapter.CityADAdapter;
import com.ifree.uu.uubuy.ui.adapter.HomeAdapter;
import com.ifree.uu.uubuy.ui.base.BaseFragment;
import com.ifree.uu.uubuy.uitls.GlideImageLoader;
import com.ifree.uu.uubuy.uitls.SPUtil;
import com.ifree.uu.uubuy.uitls.ToastUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Author: 小火
 * Email:1403241630@qq.com
 * Created by 2018/8/17.
 * Description:
 */
public class HomeFragment extends BaseFragment implements OnBannerClickListener {
    private HomePresenter mHomePresenter;
    @BindView(R.id.xr_home)
    XRecyclerView xRecyclerView;
    private ImageSlideshow imageSlideshow;
    private RecyclerView rc_type, rc_city_ad;
    private MarqueeTextView marqueeTv;
    private Banner mSlideshow;
    private View headView;
    int page = 1;
    private List<HomeEntity.DataBean.ActivitiesList> mList = new ArrayList<>();
    private List<HomeEntity.DataBean.BannerList> mBannerList = new ArrayList<>();
    private List<HomeEntity.DataBean.AdTypeList> mAdTypeList = new ArrayList<>();
    private List<HomeEntity.DataBean.UURecommendNotice> mNotice = new ArrayList<>();
    private List<HomeEntity.DataBean.CityADList> mCityADList = new ArrayList<>();
    private List<HomeEntity.DataBean.RotateADList> mRotateADList = new ArrayList<>();
    private HomeAdapter mAdapter;
    private AdTypeAdapter adTypeAdapter;
    private CityADAdapter cityADAdapter;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ifree.uu.location.changed");
        getActivity().registerReceiver(mAllBroad, intentFilter);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        mHomePresenter = new HomePresenter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        xRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        headView = LayoutInflater.from(context).inflate(R.layout.header_home, null);
        imageSlideshow = headView.findViewById(R.id.img_home_gallery);
        marqueeTv = headView.findViewById(R.id.marqueeTv);
        rc_type = headView.findViewById(R.id.rc_type);
        rc_city_ad = headView.findViewById(R.id.rc_city_ad);
        mSlideshow = headView.findViewById(R.id.img_rotate_gallery);
        mSlideshow.setOnBannerClickListener(this);
        if (headView != null) xRecyclerView.addHeaderView(headView);
        rc_type.setLayoutManager(new LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false));
        rc_city_ad.setLayoutManager(new GridLayoutManager(context, 2));
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mList.clear();
                mAdapter.notifyDataSetChanged();
                mCityADList.clear();
                cityADAdapter.notifyDataSetChanged();
                loadData();
            }

            @Override
            public void onLoadMore() {
                page++;
                loadData();
            }
        });

        mAdapter = new HomeAdapter(context, mList);
        xRecyclerView.setAdapter(mAdapter);

        adTypeAdapter = new AdTypeAdapter(context, mAdTypeList);
        rc_type.setAdapter(adTypeAdapter);

        cityADAdapter = new CityADAdapter(context, mCityADList);
        rc_city_ad.setAdapter(cityADAdapter);

        rc_type.addOnItemTouchListener(new RecyclerItemTouchListener(rc_type) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int position = vh.getAdapterPosition();
                if (position < 0 | position >= mAdTypeList.size()) {
                    return;
                }
                Log.i("TAG", "onItemClick: " + position);
                Bundle bundle = new Bundle();
                bundle.putString("adTypeId", mAdTypeList.get(position).getAdTypeId());
                bundle.putString("type", mAdTypeList.get(position).getType());
                bundle.putString("title", mAdTypeList.get(position).getAdTypeTitle());
                MyApplication.openActivity(context, FirstClassifyActivity.class, bundle);
            }
        });

        xRecyclerView.addOnItemTouchListener(new RecyclerItemTouchListener(xRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int position = vh.getAdapterPosition() - 2;
                if (position < 0 | position >= mList.size()) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("fristActivitiesId", mList.get(position).getActivitiesId());
                bundle.putString("fristActivitiesType", mList.get(position).getType());
                bundle.putString("fristActivitiesName", mList.get(position).getActivitiesName());
                switch (mList.get(position).getType()) {// 1 商城 2 超市 3 建材 4 车 5 品牌 6 教育
                    case "1":
                        if (mList.get(position).getActivitiesType().equals("1")) {
                            MyApplication.openActivity(context, StoreActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, ShoppingMallActivity.class, bundle);
                        }
                        break;
                    case "2"://超市
                        if (mList.get(position).getActivitiesType().equals("1")) {
                            MyApplication.openActivity(context, StoreActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, MarketActivity.class, bundle);
                        }
                        break;
                    case "3":
                        if (mList.get(position).getActivitiesType().equals("1")) {
                            MyApplication.openActivity(context, StoreActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, FurnitureMarketActivity.class, bundle);
                        }
                        break;
                    case "4":
                        if (mList.get(position).getActivitiesType().equals("1")) {
                            MyApplication.openActivity(context, BrandActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, ShoppingMallActivity.class, bundle);
                        }
                        break;
                    case "5":
                        if (mList.get(position).getActivitiesType().equals("1")) {
                            MyApplication.openActivity(context, BrandActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, ShoppingMallActivity.class, bundle);
                        }
                        break;
                    case "6":
                        MyApplication.openActivity(context, BrandActivity.class, bundle);
                        break;
                }
            }
        });

        rc_city_ad.addOnItemTouchListener(new RecyclerItemTouchListener(rc_city_ad) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int position = vh.getAdapterPosition();
                if (position < 0 | position >= mList.size()) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("fristActivitiesId", mCityADList.get(position).getCityADId());
                bundle.putString("fristActivitiesType", mCityADList.get(position).getType());
                bundle.putString("fristActivitiesName", mCityADList.get(position).getCityADName());
                switch (mCityADList.get(position).getType()) {// 1 商城 2 超市 3 建材 4 车 5 品牌 6 教育
                    case "1":
                        if (mCityADList.get(position).getCityADType().equals("1")) {
                            MyApplication.openActivity(context, StoreActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, ShoppingMallActivity.class, bundle);
                        }
                        break;
                    case "2"://超市
                        if (mCityADList.get(position).getCityADType().equals("1")) {
                            MyApplication.openActivity(context, StoreActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, MarketActivity.class, bundle);
                        }
                        break;
                    case "3":
                        if (mCityADList.get(position).getCityADType().equals("1")) {
                            MyApplication.openActivity(context, StoreActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, FurnitureMarketActivity.class, bundle);
                        }
                        break;
                    case "4":
                        if (mCityADList.get(position).getCityADType().equals("1")) {
                            MyApplication.openActivity(context, BrandActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, ShoppingMallActivity.class, bundle);
                        }
                        break;
                    case "5":
                        if (mCityADList.get(position).getCityADType().equals("1")) {
                            MyApplication.openActivity(context, BrandActivity.class, bundle);
                        } else {
                            MyApplication.openActivity(context, ShoppingMallActivity.class, bundle);
                        }
                        break;
                    case "6":
                        MyApplication.openActivity(context, BrandActivity.class, bundle);
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void loadData(){
        mHomePresenter.onCreate();
        mHomePresenter.attachView(mHomeView);
        mHomePresenter.getSearchHomes(SPUtil.getLongitude(context), SPUtil.getLatitude(context), SPUtil.getTownAdCode(context), page, "加载中...");
    }

    private HomeView mHomeView = new HomeView() {
        @Override
        public void onSuccess(HomeEntity mHomeEntity) {
            if (page == 1) {
                xRecyclerView.refreshComplete();
            } else {
                xRecyclerView.loadMoreComplete();
            }
            if (mHomeEntity.getResultCode().equals("1")) {
                ToastUtils.makeText(context, mHomeEntity.getMsg());
                return;
            }
            List<HomeEntity.DataBean.BannerList> bannerLists = mHomeEntity.getData().getBannerList();
            if (bannerLists != null && !bannerLists.isEmpty()) {
                if (mBannerList.size() == 0) {
                    mBannerList.addAll(bannerLists);
                    initTopViewData(mBannerList);
                }
            }

            List<HomeEntity.DataBean.AdTypeList> adTypeLists = mHomeEntity.getData().getAdTypeList();
            if (adTypeLists != null && !adTypeLists.isEmpty()) {
                mAdTypeList.clear();
                mAdTypeList.addAll(adTypeLists);
                adTypeAdapter.notifyDataSetChanged();

            }

            List<HomeEntity.DataBean.UURecommendNotice> uuRecommendNotices = mHomeEntity.getData().getUuRecommendNotice();
            if (uuRecommendNotices != null && !uuRecommendNotices.isEmpty()) {
                mNotice.clear();
                mNotice.addAll(uuRecommendNotices);
                initSetNotice(mNotice);

            }

            List<HomeEntity.DataBean.CityADList> cityADLists = mHomeEntity.getData().getCityADList();
            if (cityADLists != null && !cityADLists.isEmpty()) {
                mCityADList.addAll(cityADLists);
                cityADAdapter.notifyDataSetChanged();
            }

            List<HomeEntity.DataBean.RotateADList> rotateADLists = mHomeEntity.getData().getRotateADList();
            if (rotateADLists != null && !rotateADLists.isEmpty()) {
                mRotateADList.clear();
                mRotateADList.addAll(rotateADLists);
                initRotateViewData(mRotateADList);
            }
            List<HomeEntity.DataBean.ActivitiesList> activitiesLists = mHomeEntity.getData().getActivitiesList();
            if (activitiesLists != null && !activitiesLists.isEmpty()) {
                mList.addAll(activitiesLists);
                mAdapter.notifyDataSetChanged();
            }
            if (activitiesLists.size() < 10) {
                xRecyclerView.setNoMore(true);
            }
        }

        @Override
        public void onError(String result) {
            ToastUtils.makeText(context, result);
            if (page == 1) {
                xRecyclerView.refreshComplete();
            } else {
                xRecyclerView.loadMoreComplete();
            }
        }
    };

    private void initTopViewData(final List<HomeEntity.DataBean.BannerList> mBannerList) {
        for (int i = 0; i < mBannerList.size(); i++) {
            imageSlideshow.addImageTitle(BaseUrl.IMAGE_HTTP + mBannerList.get(i).getBannerPic());
        }
        imageSlideshow.setDotSpace(18);
        imageSlideshow.setDotSize(20);
        imageSlideshow.setDelay(3000);
        imageSlideshow.setOnItemClickListener(new ImageSlideshow.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        imageSlideshow.commit();
    }

    private void initSetNotice(List<HomeEntity.DataBean.UURecommendNotice> messageLists) {
        List<String> textArrays = new ArrayList<>();
        for (int i = 0; i < messageLists.size(); i++) {
            textArrays.add(messageLists.get(i).getUuRecommentContent());
        }
        marqueeTv.setTextArraysAndClickListener(context, textArrays, new MarqueeTextViewClickListener() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
//                MyApplication.openActivity(context, WebDetailsActivity.class, bundle);
            }
        });
    }

    private void initRotateViewData(List<HomeEntity.DataBean.RotateADList> rotateADLists) {
        List<String> imag = new ArrayList<>();
        for (int i = 0; i < rotateADLists.size(); i++) {
            imag.add(rotateADLists.get(i).getRotateADIcon());
        }
        mSlideshow.setImages(imag)
                .setBannerStyle(BannerConfig.NOT_INDICATOR)
                .setImageLoader(new GlideImageLoader())
                .start();
    }

    @Override
    public void OnBannerClick(int position) {

    }

    private BroadcastReceiver mAllBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            //接到广播通知后刷新数据源
            xRecyclerView.setRefreshing(true);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHomePresenter.onStop();
    }
}
