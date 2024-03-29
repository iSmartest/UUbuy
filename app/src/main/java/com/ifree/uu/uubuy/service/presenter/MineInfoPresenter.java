package com.ifree.uu.uubuy.service.presenter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.ifree.uu.uubuy.dialog.ProgressDialog;
import com.ifree.uu.uubuy.service.entity.MineEntity;
import com.ifree.uu.uubuy.service.manager.DataManager;
import com.ifree.uu.uubuy.service.view.MineInfoView;
import com.ifree.uu.uubuy.service.view.View;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Author: 小火
 * Email:1403241630@qq.com
 * Created by 2018/8/21.
 * Description:
 */
public class MineInfoPresenter implements Presenter {
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private MineInfoView mMineInfoView;
    private MineEntity mMineEntity;

    public MineInfoPresenter(Context mContext) {
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
        mMineInfoView = (MineInfoView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getSearchMineInfo(String longitude, String latitude, String townAdCode, int page, String uid, String mContent) {
        final Dialog dialog = ProgressDialog.createLoadingDialog(mContext, mContent);
        dialog.show();
        mCompositeSubscription.add(manager.getSearchMineInfos(longitude, latitude, townAdCode, page, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MineEntity>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                        if (mMineEntity != null) {
                            mMineInfoView.onSuccess(mMineEntity);
                            Log.i("TAG", "onMine: " + new Gson().toJson(mMineEntity));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        mMineInfoView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(MineEntity mineEntity) {
                        mMineEntity = mineEntity;
                    }
                }));
    }
}
