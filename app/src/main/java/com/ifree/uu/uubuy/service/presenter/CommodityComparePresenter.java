package com.ifree.uu.uubuy.service.presenter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.ifree.uu.uubuy.dialog.ProgressDialog;
import com.ifree.uu.uubuy.service.entity.CommodityInfoEntity;
import com.ifree.uu.uubuy.service.entity.CompareCommodityEntity;
import com.ifree.uu.uubuy.service.manager.DataManager;
import com.ifree.uu.uubuy.service.view.CommodityInfoView;
import com.ifree.uu.uubuy.service.view.CompareView;
import com.ifree.uu.uubuy.service.view.View;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Author: 小火
 * Email:1403241630@qq.com
 * Created by 2018/8/22.
 * Description:
 */
public class CommodityComparePresenter implements Presenter {
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private CompareCommodityEntity mCompareCommodityEntity;
    private CompareView mCompareView;

    public CommodityComparePresenter(Context mContext){
        this.mContext = mContext;
    }
    @Override
    public void onCreate() {
        manager = new DataManager(mContext);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        if (mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void attachView(View view) {
        mCompareView = (CompareView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getSearchCompareInfo( String commodityId, int page, String mContent) {
        final Dialog dialog = ProgressDialog.createLoadingDialog(mContext, mContent);
        dialog.show();
        mCompositeSubscription.add(manager.getSearchCompareInfo(commodityId,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CompareCommodityEntity >() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                        if (mCompareCommodityEntity != null) {
                            mCompareView.onSuccess(mCompareCommodityEntity);
                            Log.i("TAG", "onCompare: " + new Gson().toJson(mCompareCommodityEntity));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        mCompareView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(CompareCommodityEntity  compareCommodityEntity) {
                        mCompareCommodityEntity = compareCommodityEntity;
                    }
                }));

    }

}
