package com.ifree.uu.uubuy.service.view;

import com.ifree.uu.uubuy.service.entity.ActivitiesDetailsEntity;

/**
 * Author：小火
 * Email：1403241630@qq.com
 * Created by 2018/9/19 0019
 * Description:
 */
public interface ActivitiesDetailsView extends View {
    void onSuccess(ActivitiesDetailsEntity mActivitiesDetailsEntity);
    void onError(String result);
}
