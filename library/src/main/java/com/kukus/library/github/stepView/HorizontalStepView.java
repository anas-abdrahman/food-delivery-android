package com.kukus.library.github.stepView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.kukus.library.R;
import com.kukus.library.github.stepView.models.Step;


import java.util.List;

public class HorizontalStepView extends StepView {

  public HorizontalStepView(Context context) {
    this(context, null);
  }

  public HorizontalStepView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HorizontalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @Override
  protected void updateView() {

    List<Float> centerPointPositionList = mStepViewIndicator.getCircleCenterPointPositionList();

    if (mStepList == null || centerPointPositionList.size() == 0) {
      return;
    }
    if (mStepList.size() != mTextViewList.size()) {
      return;
    }

    for (int i = 0; i < mStepList.size(); i++) {

      Step step = mStepList.get(i);
      TextView textView = mTextViewList.get(i);

      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
      textView.setText(step.getName());

      final int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
      textView.measure(spec, spec);

      final int measuredWidth = textView.getMeasuredWidth();
      textView.setX(centerPointPositionList.get(i) - measuredWidth / 2);

      switch (step.getState()) {

        case CURRENT:
          textView.setTypeface(null, Typeface.BOLD);
          textView.setTextColor(mCurrentStepTextColor);
          break;

        case COMPLETED:
          textView.setTypeface(null, Typeface.NORMAL);
          textView.setTextColor(mCompletedStepTextColor);
          break;

        case NOT_COMPLETED:
          textView.setTypeface(null, Typeface.NORMAL);
          textView.setTextColor(mNotCompletedStepTextColor);
          break;

      }
    }
    mTextContainer.requestLayout();
  }

  private void init() {
    View rootView = inflate(getContext(), R.layout.step_view_horizontal, this);
    mStepViewIndicator = rootView.findViewById(R.id.steps_indicator);
    mStepViewIndicator.setOnUpdateIndicatorListener(this);
    mTextContainer = rootView.findViewById(R.id.rl_text_container);
  }
}
