package com.kukus.library.github.stepView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kukus.library.github.stepView.models.Step;

import java.util.ArrayList;
import java.util.List;

public abstract class StepView extends LinearLayout implements StepViewIndicator.onUpdateIndicatorListener {

  int mNotCompletedStepTextColor = Color.DKGRAY;
  int mCompletedStepTextColor = Color.DKGRAY;
  int mCurrentStepTextColor = Color.BLACK;
  int mTextSize = 11;

  StepViewIndicator mStepViewIndicator;
  RelativeLayout mTextContainer;

  List<Step> mStepList;
  final List<TextView> mTextViewList = new ArrayList<>();

  public StepView(Context context) {
    this(context, null);
  }

  public StepView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }


  @Nullable
  public List<Step> getSteps() {
    return mStepList;
  }



  public StepView setSteps(@Nullable List<Step> stepList) {
    mStepList = stepList;
    mStepViewIndicator.setSteps(mStepList);
    ensureTextViewCount();

    return this;
  }



  public StepView setStepState(@NonNull Step.State state, int stepPosition) {
    if (mStepList == null) {
      throw new NullPointerException(String.format("Invalid attempt to change step state at position %d. List of steps is null. Did you forget to call setSteps()?", stepPosition));
    }
    if (mStepList.size() <= stepPosition) {
      throw new IndexOutOfBoundsException(String.format("Invalid step position %d. The list of steps has %d items", stepPosition, mStepList.size()));
    }

    mStepList.get(stepPosition).setState(state);
    ensureTextViewCount();
    mStepViewIndicator.setSteps(mStepList);

    return this;
  }

  @NonNull
  public Step getStep(int stepPosition) {
    if (mStepList == null) {
      throw new NullPointerException(String.format("Invalid attempt to get step at position %d when list of steps is null. Did you forget to call setSteps()", stepPosition));
    }
    if (mStepList.size() <= stepPosition) {
      throw new IndexOutOfBoundsException(String.format("Invalid step position %d. The list of steps has %d items", stepPosition, mStepList.size()));
    }

    return mStepList.get(stepPosition);
  }

  public StepView setStep(@NonNull Step step, int stepPosition) {
    if (mStepList == null) {
      throw new NullPointerException(String.format("Invalid attempt to change step at position %d. List of steps is null. Did you forget to call setSteps()?", stepPosition));
    }
    if (mStepList.size() <= stepPosition) {
      throw new IndexOutOfBoundsException(String.format("Invalid step position %d. The list of steps has %d items", stepPosition, mStepList.size()));
    }

    mStepList.set(stepPosition, step);
    mStepViewIndicator.setSteps(mStepList);
    ensureTextViewCount();

    return this;
  }

  public int getTextSize() {
    return mTextSize;
  }


  public StepView setTextSize(int textSizeSp) {
    if (textSizeSp <= 0) {
      throw new Error(String.format("Invalid text size %d. Must be greater than zero", textSizeSp));
    }
    mTextSize = textSizeSp;

    return this;
  }


  public int getNotCompletedStepTextColor() {
    return mNotCompletedStepTextColor;
  }

  public StepView setNotCompletedStepTextColor(int notCompletedStepTextColor) {
    mNotCompletedStepTextColor = notCompletedStepTextColor;

    return this;
  }

  public int getCompletedStepTextColor() {
    return mCompletedStepTextColor;
  }

  public StepView setCompletedStepTextColor(int completedStepTextColor) {
    this.mCompletedStepTextColor = completedStepTextColor;

    return this;
  }

  public int getCurrentStepTextColor() {
    return mCurrentStepTextColor;
  }

  public StepView setCurrentStepTextColor(int currentStepTextColor) {
    this.mCurrentStepTextColor = currentStepTextColor;

    return this;
  }

  public int getNotCompletedLineColor() {
    return mStepViewIndicator.getNotCompletedLineColor();
  }

  public StepView setNotCompletedLineColor(int notCompletedLineColor) {
    mStepViewIndicator.setNotCompletedLineColor(notCompletedLineColor);

    return this;
  }

  public int getCompletedLineColor() {
    return mStepViewIndicator.getCompletedLineColor();
  }

  public StepView setCompletedLineColor(int completedLineColor) {
    mStepViewIndicator.setCompletedLineColor(completedLineColor);
    return this;
  }

  public Drawable getNotCompletedStepIcon() {
    return mStepViewIndicator.getNotCompletedStepIcon();
  }

  public StepView setNotCompletedStepIcon(@NonNull Drawable notCompletedStepIcon) {
    mStepViewIndicator.setNotCompletedStepIcon(notCompletedStepIcon);
    return this;
  }

  public Drawable getCompletedStepIcon() {
    return mStepViewIndicator.getCompletedStepIcon();
  }

  public StepView setCompletedStepIcon(@NonNull Drawable completedStepIcon) {
    mStepViewIndicator.setCompletedStepIcon(completedStepIcon);
    return this;
  }

  public Drawable getCurrentStepIcon() {
    return mStepViewIndicator.getCurrentStepIcon();
  }

  public StepView setCurrentStepIcon(@NonNull Drawable currentStepIcon) {
    mStepViewIndicator.setCurrentStepIcon(currentStepIcon);

    return this;
  }

  public float getLineLengthPx() {
    return mStepViewIndicator.getLineLengthPx();
  }

  public StepView setLineLengthPx(float lineLengthPx) {
    mStepViewIndicator.setLineLengthPx(lineLengthPx);

    return this;
  }

  public StepView setLineLength(float lineLengthDp) {
    mStepViewIndicator.setLineLength(lineLengthDp);

    return this;
  }

  public float getCircleRadiusPx() {
    return mStepViewIndicator.getCircleRadiusPx();
  }

  public StepView setCircleRadiusPx(float circleRadiusPx) {
    mStepViewIndicator.setCircleRadiusPx(circleRadiusPx);

    return this;
  }

  public StepView setCircleRadius(float circleRadiusDp) {
    mStepViewIndicator.setCircleRadius(circleRadiusDp);

    return this;
  }

  public StepView setReverse(boolean isReverse) {
    mStepViewIndicator.setReverse(isReverse);

    return this;
  }

  @Override
  public void onIndicatorUpdated() {
    updateView();
  }

  abstract protected void updateView();

  private void ensureTextViewCount() {
    int stepCount = mStepList == null ? 0 : mStepList.size();
    int textViewCount = mTextViewList.size();
    int delta = textViewCount - stepCount;
    if (stepCount == 0) { // All steps have been removed, or mStepsList is null
      mTextContainer.removeAllViews();
      mTextViewList.clear();
    } else if (delta < 0) { // More TextViews are needed
      for (int i = textViewCount; i < stepCount; i++) {
        TextView textView = new TextView(getContext());
        mTextViewList.add(textView);
        mTextContainer.addView(textView);
      }
    } else if (delta > 0) { // Some TextViews are unnecessary
      mTextContainer.removeViews(stepCount, delta);
      mTextViewList.removeAll(mTextViewList.subList(stepCount, textViewCount));
    }
  }
}
