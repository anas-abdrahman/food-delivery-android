package com.kukus.library.github.stepView.models;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class Step {

  public enum State {

    CURRENT,
    NOT_COMPLETED,
    COMPLETED
  }

  private String name;
  private State state;
  private Drawable iconUnComplete;
  private Drawable iconComplete;
  private Drawable iconCurrent;

  public Drawable getIconUnComplete() {
    return iconUnComplete;
  }

  public void setIconUnComplete(Drawable iconUnComplete) {
    this.iconUnComplete = iconUnComplete;
  }

  public Drawable getIconComplete() {
    return iconComplete;
  }

  public void setIconComplete(Drawable iconComplete) {
    this.iconComplete = iconComplete;
  }

  public Drawable getIconCurrent() {
    return iconCurrent;
  }

  public void setIconCurrent(Drawable iconCurrent) {
    this.iconCurrent = iconCurrent;
  }

  public String getName() {
    return name;
  }
  public void setName(@NonNull String name) {
    this.name = name;
  }

  public State getState() {
    return state;
  }
  public void setState(@NonNull State state) {
    this.state = state;
  }

  public Step(@NonNull String name) {
    this(name, State.NOT_COMPLETED);
  }

  private Step(@NonNull String name, @NonNull State state) {
    this.name = name;
    this.state = state;
  }

  public Step(@NonNull String name, @NonNull State state, Drawable iconUnComplete, Drawable iconComplete, Drawable iconCurrent ) {
    this.name = name;
    this.state = state;
    this.iconUnComplete = iconUnComplete;
    this.iconComplete   = iconComplete;
    this.iconCurrent    = iconCurrent;
  }
}
