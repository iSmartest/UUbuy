package com.ifree.uu.uubuy.service.presenter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.ifree.uu.uubuy.dialog.ProgressDialog;
import com.ifree.uu.uubuy.service.entity.UserInfoEntity;
import com.ifree.uu.uubuy.service.manager.DataManager;
import com.ifree.uu.uubuy.service.view.UserInfoView;
import com.ifree.uu.uubuy.service.view.View;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Author：小火
 * Email：1403241630@qq.com
 * Created by 2018/9/20 0020
 * Description:
 */
public class CollectionPresenter implements Presenter{
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private UserInfoView mUserInfoView;
    private UserInfoEntity mUserInfoEntity;

    public CollectionPresenter(Context mContext){
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
        mUserInfoView = (UserInfoView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getSubmitIsCollection(String uid, String activitiesId, String type, String isCollection,String mContent){
        final Dialog dialog = ProgressDialog.createLoadingDialog(mContext,mContent);
        dialog.show();
        mCompositeSubscription.add(manager.getSubmitIsCollection(uid, activitiesId, type, isCollection)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoEntity>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                        if (mUserInfoEntity != null){
                            mUserInfoView.onSuccess(mUserInfoEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        mUserInfoView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(UserInfoEntity UserInfoEntity) {
                        mUserInfoEntity = UserInfoEntity;
                    }
                })
        );
    }
}
