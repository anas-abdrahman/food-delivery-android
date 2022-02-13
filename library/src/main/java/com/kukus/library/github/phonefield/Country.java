package com.kukus.library.github.phonefield;

import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Country object that holds the country iso2 code, title, and dial code.
 * @author Ismail Almetwally
 */
public class Country {

  private final String mCode;

  private final String mName;

  private final int mDialCode;

  public Country(String code, String name, int dialCode) {
    mCode = code;
    mName = name;
    mDialCode = dialCode;
  }

  public String getCode() {
    return mCode;
  }

  public String getName() {
    return mName;
  }

  int getDialCode() {
    return mDialCode;
  }

  String getDisplayName() {
    return new Locale("", mCode).getDisplayCountry(Locale.US);
  }

  int getResId(Context context) {
    String name = String.format("country_flag_%s", mCode.toLowerCase());
    final Resources resources = context.getResources();
    return resources.getIdentifier(name, "drawable", context.getPackageName());
  }
}
