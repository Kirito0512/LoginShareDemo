package com.example.xuqi.qqdemo.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.xuqi.qqdemo.R;

public class LoadingDialog extends Dialog {
    private View mPanel;
    private TextView messageTextView;
    private Context context;
    private static LoadingDialog mProgressDialog;

    public LoadingDialog(Context context) {
        super(context, R.style.dialog_loading_style);
        this.context = context;
        mPanel = getLayoutInflater().inflate(R.layout.dialog_loading, null);

        messageTextView = (TextView) mPanel.findViewById(R.id.loading_message);
        WindowManager wm = ((Activity) context).getWindowManager();
        mPanel.setMinimumHeight(wm.getDefaultDisplay().getWidth() / 4);
        mPanel.setMinimumWidth(wm.getDefaultDisplay().getWidth() / 4);

        setContentView(mPanel);
    }

    public void setMessage(CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            messageTextView.setVisibility(View.GONE);
            return;
        }
        if (messageTextView.getVisibility() != View.VISIBLE) {
            messageTextView.setVisibility(View.VISIBLE);
        }
        messageTextView.setText(message);
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    public static Builder create(Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private Context mContext;
        private CharSequence message;
        private boolean cancelaOnTouchOutside;
        private boolean cancelable;
        private boolean mFinishWhenDismiss;
        private OnCancelListener mCancelListener;

        public Builder(Context context) {
            mContext = context;
            cancelable = true;
            cancelaOnTouchOutside = true;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancelable) {
            this.cancelaOnTouchOutside = cancelable;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            this.mCancelListener = listener;
            return this;
        }

        public Builder setFinishWhenDismiss(boolean finish) {
            this.mFinishWhenDismiss = finish;
            return this;
        }

        public LoadingDialog show() {
            LoadingDialog dialog = new LoadingDialog(mContext);
            dialog.setMessage(message);
            dialog.setCanceledOnTouchOutside(cancelaOnTouchOutside);
            dialog.setCancelable(cancelable);
            dialog.setOnCancelListener(mCancelListener);
            dialog.show();
            if (mFinishWhenDismiss) {
                dialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (mContext != null && ((mContext instanceof Activity) && !((Activity) mContext).isFinishing())) {
                            ((Activity) mContext).finish();
                        }
                    }
                });
            }
            return dialog;
        }
    }

    @Override
    public void show() {
        try {
            if (!isInvaid(context) && !isShowing()) {
                super.show();
            }
        } catch (RuntimeException e) {
        }
    }

    public static boolean isInvaid(Context context) {
        return context == null || ((context instanceof Activity) && ((Activity) context).isFinishing());
    }

    public static void showProgressDialog(Context mContext) {
        showProgressDialog(null, mContext);
    }

    public static void showProgressDialog(String message, Context mContext) {
        mProgressDialog = getLoadingDialog(mContext);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public static LoadingDialog getLoadingDialog(Context mContext) {
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(mContext);
            mProgressDialog.setCancelable(true);
        }
        return mProgressDialog;
    }

    public static void hideProgressDialog(Context mContext) {
        try {
            mProgressDialog = getLoadingDialog(mContext);
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
