package com.kukus.library.github.stepView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.kukus.library.github.stepView.models.Step;

class VerticalStepViewIndicator extends StepViewIndicator {

  private float mCenterX;
  private float mLeftY;
  private float mRightY;

  private int mHeight;

  public VerticalStepViewIndicator(Context context) {
    this(context, null);
  }

  public VerticalStepViewIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VerticalStepViewIndicator(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = DEFAULT_STEP_INDICATOR_DIMENSION;
    mHeight = 0;
    final int numSteps = getNumOfSteps();
    if (numSteps > 0) {
      // The height of the VerticalStepViewIndicator is the sum of
      // The top padding of the view
      // The bottom padding of the view
      // The height/diameter of the circle (radius * 2) * number of steps
      // Length of lines between N steps [ = (N - 1) line lengths]
      mHeight = (int) (getPaddingTop()
              + getPaddingBottom()
              + (mCircleRadius * 2 * numSteps)
              + ((numSteps - 1) * mLineLength));
    }
    if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
      width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
    }
    setMeasuredDimension(width, mHeight);

  }

  @Override
  protected void onSizeChanged(int w, int h, int oldW, int oldH) {
    super.onSizeChanged(w, h, oldW, oldH);
    mCenterX = getWidth() / 2;
    mLeftY = mCenterX - (mCompletedLineHeight / 2);
    mRightY = mCenterX + (mCompletedLineHeight / 2);

    mCircleCenterPointPositionList.clear();
    final int numSteps = getNumOfSteps();
    for (int i = 0; i < numSteps; i++) {
      if (mIsReverseDraw) {
        mCircleCenterPointPositionList.add(mHeight - (mCircleRadius + i * mCircleRadius * 2 + i * mLineLength));
      } else {
        mCircleCenterPointPositionList.add(mCircleRadius + i * mCircleRadius * 2 + i * mLineLength);
      }
    }

    if (mUpdateIndicatorListener != null) {
      mUpdateIndicatorListener.onIndicatorUpdated();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
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

      Step nextStep = mStepList.get(i + 1);

      /*
       * If the next step is in completed state, draw a thin rectangle
       * leading to or leading from the completed step drawable (depending on mIsReverseDraw)
       * that represents the completed line
       */
      if (nextStep.getState() == Step.State.COMPLETED) {
        if (mIsReverseDraw) {
          canvas.drawRect(mLeftY, nextStepXPosition + mCircleRadius - 10, mRightY, stepXPosition - mCircleRadius + 10, mCompletedLinePaint);
        } else {
          canvas.drawRect(mLeftY, stepXPosition + mCircleRadius - 10, mRightY, nextStepXPosition - mCircleRadius + 10, mCompletedLinePaint);
        }
      }
      /*
       * If the step is in not completed state (current or not-completed),
       * draw a dotted line leading to or from the step drawable (depending on mIsReverseDraw)
       */
      else {
        if (mIsReverseDraw) {
          mPath.moveTo(mCenterX, nextStepXPosition + mCircleRadius);
          mPath.lineTo(mCenterX, stepXPosition - mCircleRadius);
          canvas.drawPath(mPath, mNotCompletedLinePaint);
        } else {
          mPath.moveTo(mCenterX, stepXPosition + mCircleRadius);
          mPath.lineTo(mCenterX, nextStepXPosition - mCircleRadius);
          canvas.drawPath(mPath, mNotCompletedLinePaint);
        }
        mPath.reset();
      }

    }

    // Draw Step icons
    for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
      final float stepXPosition = mCircleCenterPointPositionList.get(i);
      mRect.set((int) (mCenterX - mCircleRadius), (int) (stepXPosition - mCircleRadius), (int) (mCenterX + mCircleRadius), (int) (stepXPosition + mCircleRadius));
      Step step = mStepList.get(i);
      switch (step.getState()) {
        case COMPLETED:
          mCompletedStepIcon.setBounds(mRect);
          mCompletedStepIcon.draw(canvas);
          break;
        case CURRENT:
          mCompletedLinePaint.setColor(Color.WHITE);
          mCurrentStepIcon.setBounds(mRect);
          mCurrentStepIcon.draw(canvas);
          break;
        default:
          mNotCompletedStepIcon.setBounds(mRect);
          mNotCompletedStepIcon.draw(canvas);
      }
    }
  }
}
