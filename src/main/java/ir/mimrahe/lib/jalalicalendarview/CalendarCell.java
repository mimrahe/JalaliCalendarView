package ir.mimrahe.lib.jalalicalendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.TextView;

public class CalendarCell extends TextView implements Checkable {
    private boolean checked = false;
    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public CalendarCell(Context context) {
        super(context);
    }

    public CalendarCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }
}
