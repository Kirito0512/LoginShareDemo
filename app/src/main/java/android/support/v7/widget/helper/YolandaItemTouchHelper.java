package android.support.v7.widget.helper;

/**
 * Created by xuqi on 17/4/27.
 */

public class YolandaItemTouchHelper extends ItemTouchHelper {
    public YolandaItemTouchHelper(Callback callback) {
        super(callback);
    }

    public Callback getCallback() {
        return mCallback;
    }
}
