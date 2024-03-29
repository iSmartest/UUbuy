package com.ifree.uu.uubuy.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;

import com.ifree.uu.uubuy.R;
import com.ifree.uu.uubuy.service.entity.MessageEntity;
import com.ifree.uu.uubuy.service.presenter.MessagePresenter;
import com.ifree.uu.uubuy.service.view.MessageView;
import com.ifree.uu.uubuy.ui.adapter.AroundAdapter;
import com.ifree.uu.uubuy.ui.adapter.MessageAdapter;
import com.ifree.uu.uubuy.ui.base.BaseFragment;
import com.ifree.uu.uubuy.uitls.SPUtil;
import com.ifree.uu.uubuy.uitls.ToastUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Author: 小火
 * Email:1403241630@qq.com
 * Created by 2018/8/23.
 * Description:
 */
public class MessageListFragment extends BaseFragment {
    private MessagePresenter messagePresenter;
    @BindView(R.id.xr_message)
    XRecyclerView xRecyclerView;
    private String type;
    private int page = 1;
    private List<MessageEntity.DataBean.NotifyList> mList;
    private MessageAdapter mAdapter;
    @Override
    protected int getLayout() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initView() {
        mList = new ArrayList<>();
        messagePresenter = new MessagePresenter(context);
        type = getArguments().getString("type");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        xRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mList.clear();
                mAdapter.notifyDataSetChanged();
                initData();
                xRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                page ++ ;
                initData();
                xRecyclerView.loadMoreComplete();
                mAdapter.notifyDataSetChanged();
                xRecyclerView.setNoMore(true);
            }
        });
        mAdapter = new MessageAdapter(context,mList,type);
        xRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void initData() {
        messagePresenter.onCreate();
        messagePresenter.attachView(mMessageView);
        messagePresenter.getSearchMessages(SPUtil.getUid(context),type,page,"加载中...");
    }

    private MessageView mMessageView = new MessageView() {
        @Override
        public void onSuccess(MessageEntity mMessageEntity) {
            if (mMessageEntity.getResultCode().equals("1")){
                ToastUtils.makeText(context,mMessageEntity.getMsg());
                return;
            }

            List<MessageEntity.DataBean.NotifyList> notifyLists = mMessageEntity.getData().getNotifyList();
            if (notifyLists != null && !notifyLists.isEmpty()){
                mList.addAll(notifyLists);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onError(String result) {
            ToastUtils.makeText(context,result);
        }
    };
}
