package com.sih.adminkeyblue;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.google.android.gms.maps.model.GroundOverlay;

public class RadiusAnimation extends Animation {
    private GroundOverlay groundOverlay;

    public RadiusAnimation(GroundOverlay groundOverlay) {
        this.groundOverlay = groundOverlay;

    }
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        groundOverlay.setDimensions( (10000 * interpolatedTime) );
        groundOverlay.setTransparency( interpolatedTime );

    }


    @Override
    public void initialize(int width, int height, int parentWidth,int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }
}
