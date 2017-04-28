package com.example.xuqi.qqdemo.widget;

import android.support.v7.widget.helper.YolandaItemTouchHelper;

/**
 * Created by xuqi on 17/4/27.
 */

public class DefaultItemTouchHelper extends YolandaItemTouchHelper {
    private CustomItemTouchHelperCallback itemTouchHelpCallback;

    public DefaultItemTouchHelper(CustomItemTouchHelperCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        super(new CustomItemTouchHelperCallback(onItemTouchCallbackListener));
        itemTouchHelpCallback = (CustomItemTouchHelperCallback) getCallback();
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        itemTouchHelpCallback.setDragEnable(canDrag);
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        itemTouchHelpCallback.setSwipeEnable(canSwipe);
    }
}
