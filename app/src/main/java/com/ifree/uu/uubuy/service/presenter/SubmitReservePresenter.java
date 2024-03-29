package com.ifree.uu.uubuy.service.presenter;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.ifree.uu.uubuy.dialog.ProgressDialog;
import com.ifree.uu.uubuy.service.entity.OrderEntity;
import com.ifree.uu.uubuy.service.entity.UserInfoEntity;
import com.ifree.uu.uubuy.service.manager.DataManager;
import com.ifree.uu.uubuy.service.view.OrderView;
import com.ifree.uu.uubuy.service.view.SubmitReserveView;
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
public class SubmitReservePresenter implements Presenter {
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private SubmitReserveView mSubmitReserveView;
    private UserInfoEntity mUserInfoEntity;

    public SubmitReservePresenter(Context mContext){
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
        if (mCompositeSubscription.hasSubscriptions()){
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void attachView(View view) {
        mSubmitReserveView = (SubmitReserveView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getSubmitReserveInfo(String commodityId,String type,String count,String shopId,String uid,String mContent){
        final Dialog dialog = ProgressDialog.createLoadingDialog(mContext,mContent);
        dialog.show();
        mCompositeSubscription.add(manager.getSubmitReserveInfo(commodityId,type,count,shopId,uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoEntity>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                        if (mUserInfoEntity != null){
                            mSubmitReserveView.onSuccess(mUserInfoEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        mSubmitReserveView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(UserInfoEntity userInfoEntity) {
                        mUserInfoEntity = userInfoEntity;
                    }
                }));
    }
}
