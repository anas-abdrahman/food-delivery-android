package com.kukus.library.github.stepView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


import com.kukus.library.R;
import com.kukus.library.github.stepView.models.Step;

import java.util.ArrayList;
import java.util.List;

abstract class StepViewIndicator extends View {

  interface onUpdateIndicatorListener {
    void onIndicatorUpdated();
  }

  final int DEFAULT_STEP_INDICATOR_DIMENSION = (int) convertDpToPx(40);

  float mCompletedLineHeight; // completed line height
  float mCircleRadius; // Step circle radius

  Drawable mCompletedStepIcon; // Drawable/icon used for a completed step
  Drawable mCurrentStepIcon; // Drawable/icon used for the current step
  Drawable mNotCompletedStepIcon; // Drawable/icon used for a not completed (default) step

  Paint mNotCompletedLinePaint; // Style of line leading to a not-completed step
  Paint mCompletedLinePaint; // Style of line leading to a completed step

  int mNotCompletedLineColor = Color.DKGRAY; // Default color of a not-completed line
  int mCompletedLineColor = Color.parseColor("#32c360"); // Default color of a completed line
  float mLineLength; // Default spacing between circles of two steps

  List<Float> mCircleCenterPointPositionList; // List of center points of all circles
  List<Step> mStepList; // List of steps

  Path mPath; // Path of lines leading to not-completed steps
  final Rect mRect = new Rect(); // The bounding rectangle of the Step icon/drawables

  boolean mIsReverseDraw = true;

  onUpdateIndicatorListener mUpdateIndicatorListener;

  public StepViewIndicator(Context context) {
    this(context, null);
  }

  public StepViewIndicator(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public StepViewIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @NonNull
  public Drawable getCompletedStepIcon() {
    return mCompletedStepIcon;
  }

  public void setCompletedStepIcon(@NonNull Drawable completedStepIcon) {
    mCompletedStepIcon = completedStepIcon;
  }

  @NonNull
  public Drawable getNotCompletedStepIcon() {
    return mNotCompletedStepIcon;
  }

  public void setNotCompletedStepIcon(@NonNull Drawable notCompletedStepIcon) {
    mNotCompletedStepIcon = notCompletedStepIcon;
  }

  @NonNull
  public Drawable getCurrentStepIcon() {
    return mCurrentStepIcon;
  }

  public void setCurrentStepIcon(@NonNull Drawable currentStepIcon) {
    mCurrentStepIcon = currentStepIcon;
  }

  public int getCompletedLineColor() {
    return mCompletedLineColor;
  }

  public void setCompletedLineColor(int completedLineColor) {
    mCompletedLineColor = completedLineColor;
  }

  public float getCircleRadiusPx() {
    return mCircleRadius;
  }

  public void setCircleRadiusPx(float circleRadiusPx) {
    this.mCircleRadius = circleRadiusPx;
  }

  public void setCircleRadius(float circleRadiusDp) {
    Log.d("StepViewIndicator", "setCircleRadius called with " + circleRadiusDp);
    Log.d("StepViewIndicator", "convertDpToPx = " + convertDpToPx(circleRadiusDp));
    this.mCircleRadius = convertDpToPx(circleRadiusDp);
  }

  public float getLineLengthPx() {
    return mLineLength;
  }

  public void setLineLengthPx(float lineLengthPx) {
    this.mLineLength = lineLengthPx;
  }

  public void setLineLength(float lineLengthDp) {
    this.mLineLength = convertDpToPx(lineLengthDp);
  }

  public int getNotCompletedLineColor() {
    return mNotCompletedLineColor;
  }

  public void setNotCompletedLineColor(int notCompletedLineColor) {
    mNotCompletedLineColor = notCompletedLineColor;
  }

  public void setOnUpdateIndicatorListener(@NonNull onUpdateIndicatorListener updateIndicatorListener) {
    mUpdateIndicatorListener = updateIndicatorListener;
  }

  public void setReverse(boolean isReverseDraw) {
    this.mIsReverseDraw = isReverseDraw;
    invalidate();
  }

  public void setSteps(@Nullable List<Step> stepList) {
    int numSteps = getNumOfSteps();
    Log.d("StepView", "SVIndicator: setSteps called with " + (stepList == null ? "null" : stepList.size()) + " items");
    mStepList = stepList;

    invalidate();
    if (numSteps != mStepList.size()) {
      requestLayout();
    }
  }

  @NonNull
  List<Float> getCircleCenterPointPositionList() {
    return mCircleCenterPointPositionList;
  }

  int getNumOfSteps() {
    return mStepList == null ? 0 : mStepList.size();
  }

  private float convertDpToPx(float numDp) {
    Log.d("SVI", "convertDpToPx called with " + numDp);
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    Log.d("SVI", String.format("SVI displayMetrics: h: %d, w: %d. Density: %f", displayMetrics.heightPixels, displayMetrics.widthPixels, displayMetrics.density));
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, numDp, getResources().getDisplayMetrics());
  }


  private void init() {

    mPath = new Path();

    DashPathEffect mEffects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
    mCircleCenterPointPositionList = new ArrayList<>();

    mNotCompletedLinePaint  = new Paint();
    mCompletedLinePaint     = new Paint();
    mNotCompletedLinePaint.setAntiAlias(true);
    mNotCompletedLinePaint.setColor(mNotCompletedLineColor);
    mNotCompletedLinePaint.setStyle(Paint.Style.STROKE);
    mNotCompletedLinePaint.setStrokeWidth(2);

    mCompletedLinePaint.setAntiAlias(true);
    mCompletedLinePaint.setColor(mCompletedLineColor);
    mCompletedLinePaint.setStyle(Paint.Style.STROKE);
    mCompletedLinePaint.setStrokeWidth(2);

    mNotCompletedLinePaint.setPathEffect(mEffects);
    mCompletedLinePaint.setStyle(Paint.Style.FILL);

    mCompletedLineHeight = 0.05f * DEFAULT_STEP_INDICATOR_DIMENSION;
    mCircleRadius = 0.28f * DEFAULT_STEP_INDICATOR_DIMENSION;
    mLineLength = 0.85f * DEFAULT_STEP_INDICATOR_DIMENSION;

    mCompletedStepIcon    = AppCompatResources.getDrawable(getContext(), R.drawable.ic_completed);
    mCurrentStepIcon      = AppCompatResources.getDrawable(getContext(), R.drawable.ic_placed);
    mNotCompletedStepIcon = AppCompatResources.getDrawable(getContext(), R.drawable.ic_not_completed);
  }


}
