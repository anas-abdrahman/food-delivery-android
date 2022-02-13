package com.kukus.library.github.stepView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.kukus.library.github.stepView.models.Step;

class HorizontalStepViewIndicator extends StepViewIndicator {

  private float mCenterY;
  private float mLeftY;
  private float mRightY;

  private int mWidth;

  public HorizontalStepViewIndicator(Context context) {
    this(context, null);
  }

  public HorizontalStepViewIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HorizontalStepViewIndicator(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    int width = DEFAULT_STEP_INDICATOR_DIMENSION * 2;
    int height = DEFAULT_STEP_INDICATOR_DIMENSION;
    if (widthMode == MeasureSpec.EXACTLY) {
      width = widthSize;
    } else {
      width = Math.min(width, widthSize);
    }
    mWidth = width;

    if (heightMode != MeasureSpec.UNSPECIFIED) {
      height = Math.min(height, heightSize);
    }

    setMeasuredDimension(width, height);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldW, int oldH) {
    super.onSizeChanged(w, h, oldW, oldH);
    mCenterY = 0.5f * getHeight();
    mLeftY = mCenterY - (mCompletedLineHeight / 2);
    mRightY = mCenterY + mCompletedLineHeight / 2;

    mCircleCenterPointPositionList.clear();
    final int numSteps = getNumOfSteps();
    for (int i = 0; i < numSteps; i++) {
      float paddingLeft = (mWidth - numSteps * mCircleRadius * 2 - (numSteps - 1) * mLineLength) / 2;
      mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLineLength);
    }

    if (mUpdateIndicatorListener != null) {
      mUpdateIndicatorListener.onIndicatorUpdated();
    }
  }

  @Override
  protected synchronized void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    mCircleCenterPointPositionList.clear();
    final int numSteps = getNumOfSteps();
    for (int i = 0; i < numSteps; i++) {
      float paddingLeft = (mWidth - numSteps * mCircleRadius * 2 - (numSteps - 1) * mLineLength) / 2;
      mCircleCenterPointPositionList.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLineLength);
    }
    if (mUpdateIndicatorListener != null) {
      mUpdateIndicatorListener.onIndicatorUpdated();
    }
    mNotCompletedLinePaint.setColor(mNotCompletedLineColor);
    mCompletedLinePaint.setColor(mCompletedLineColor);

    // Draw line from step circle to the next, customizing the line styling based on the step state
    for (int i = 0; i < mCircleCenterPointPositionList.size() - 1; i++) {
      // Centre coordinate of the current step's circle
      final float stepXPosition = mCircleCenterPointPositionList.get(i);
      // Centre coordinate of the next step's circle
      final float nextStepXPosition = mCircleCenterPointPositionList.get(i + 1);

      if (mStepList.get(i + 1).getState() == Step.State.COMPLETED) {
        canvas.drawRect(stepXPosition + mCircleRadius - 10, mLeftY, nextStepXPosition - mCircleRadius + 10, mRightY, mCompletedLinePaint);
      } else {
        mPath.moveTo(stepXPosition + mCircleRadius, mCenterY);
        mPath.lineTo(nextStepXPosition - mCircleRadius, mCenterY);
        canvas.drawPath(mPath, mNotCompletedLinePaint);
      }
    }

    // Draw Step icons
    for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {

      final float stepXPosition = mCircleCenterPointPositionList.get(i);
      mRect.set((int) (stepXPosition - mCircleRadius), (int) (mCenterY - mCircleRadius), (int) (stepXPosition + mCircleRadius), (int) (mCenterY + mCircleRadius));

      Step step = mStepList.get(i);

      switch (step.getState()) {
        case NOT_COMPLETED:
          step.getIconUnComplete().setBounds(mRect);
          step.getIconUnComplete().draw(canvas);
          break;
        case CURRENT:
          mCompletedLinePaint.setColor(Color.WHITE);
          step.getIconCurrent().setBounds(mRect);
          step.getIconCurrent().draw(canvas);
          break;
        case COMPLETED:
          step.getIconComplete().setBounds(mRect);
          step.getIconComplete().draw(canvas);
          break;
      }
    }
  }
}
