package com.kukus.library.github.mapHelper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Property;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Animation utilities for markers with Maps API.
 *
 * Note - this class must remain in this .map.googleMapsv2 package so that the Google and Amazon
 * build flavors work correctly.
 */
public class MarkerAnimation {

    /**
     * Animates a marker from it's current position to the provided finalPosition
     *
     * @param marker        marker to animate
     * @param finalPosition the final position of the marker after the animation
     */
    public static void animateMarkerTo(final Marker marker, final LatLng finalPosition) {

        animateMarkerToICS(marker, finalPosition);

    }

    private static void animateMarkerToICS(Marker marker, LatLng finalPosition) {

        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Linear();
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator
                .ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(2000);
        animator.start();
    }

    /**
     * For other LatLngInterpolator interpolators, see https://gist.github.com/broady/6314689
     */
    interface LatLngInterpolator {

        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class Linear implements LatLngInterpolator {

            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;

                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    public static  double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


    private static Boolean isMarkerRotating = false;


    public static void rotateMarker(final Marker marker, final float toRotation) {

        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing =  -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }


}