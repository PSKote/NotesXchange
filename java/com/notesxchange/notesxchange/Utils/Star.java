package com.notesxchange.notesxchange.Utils;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Changing star from white golden or vise-versa
 */

public class Star {

    private static final String TAG = "Star";

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public ImageView starWhite, starGolden;

    public Star(ImageView starWhite, ImageView starGolden) {
        this.starWhite = starWhite;
        this.starGolden = starGolden;
    }

    public void toggleLike(){
        Log.d(TAG, "toggleLike: toggling star.");

        AnimatorSet animationSet =  new AnimatorSet();


        if(starGolden.getVisibility() == View.VISIBLE){
            Log.d(TAG, "toggleLike: toggling golden star off.");
            starGolden.setScaleX(0.1f);
            starGolden.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(starGolden, "scaleY", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(starGolden, "scaleX", 1f, 0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            starGolden.setVisibility(View.GONE);
            starWhite.setVisibility(View.VISIBLE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        else if(starGolden.getVisibility() == View.GONE){
            Log.d(TAG, "toggleLike: toggling golden star on.");
            starGolden.setScaleX(0.1f);
            starGolden.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(starGolden, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(starGolden, "scaleX", 0.1f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECCELERATE_INTERPOLATOR);

            starGolden.setVisibility(View.VISIBLE);
            starWhite.setVisibility(View.GONE);

            animationSet.playTogether(scaleDownY, scaleDownX);
        }

        animationSet.start();

    }
}