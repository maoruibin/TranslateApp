package name.gudong.translate.ui.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DefaultItemDecoration extends RecyclerView.ItemDecoration {

    int mDividerColor;
    Paint mBackgroundPaint, mDividerPaint;
    int mPaddingLeft;

    public DefaultItemDecoration(@ColorInt int backgroundColor,
                                 @ColorInt int dividerColor,
                                 @Dimension int middlePaddingLeft) {
        mDividerColor = dividerColor;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(backgroundColor);
        mDividerPaint = new Paint();
        mDividerPaint.setColor(mDividerColor);

        mPaddingLeft = middlePaddingLeft;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);

        int itemCount = state.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View itemView = parent.getChildAt(i);
            applyItemBackground(canvas, itemView);
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        int itemCount = state.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View itemView = parent.getChildAt(i);
            int adapterPosition = parent.getChildAdapterPosition(itemView);
            applyItemDecoration(canvas, itemView, itemCount, adapterPosition);
        }
    }

    private void applyItemBackground(Canvas canvas, View itemView) {
        if (itemView == null) return;

        canvas.drawRect(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom(), mBackgroundPaint);
    }

    private void applyItemDecoration(Canvas canvas, View itemView, int totalCount, int position) {
        if (itemView == null) return;

        int startX = itemView.getLeft();
        int stopX = itemView.getRight();
        int startY = itemView.getTop();
        int stopY = itemView.getTop();

        int itemHeight = itemView.getHeight();

        if (position == 0) {
            // First
            canvas.drawLine(startX, startY, stopX, stopY, mDividerPaint);
            canvas.drawLine(startX + mPaddingLeft, startY + itemHeight, stopX, stopY + itemHeight, mDividerPaint);
        } else if (position == totalCount - 1) {
            // Last
            canvas.drawLine(startX, startY + itemHeight, stopX, stopY + itemHeight, mDividerPaint);
        } else {
            // Others
            canvas.drawLine(startX + mPaddingLeft, startY + itemHeight, stopX, stopY + itemHeight, mDividerPaint);
        }
    }
}
