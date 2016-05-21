package fr.handipressante.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class SplashScreen extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        ImageView view = (ImageView)findViewById(R.id.logo); //Initialize ImageView via FindViewById or programatically

        AnimationSet animset = new AnimationSet(true);
        RotateAnimation anim1 = new RotateAnimation(0.0f, -35.0f, Animation.RELATIVE_TO_SELF, 0.2f, Animation.RELATIVE_TO_SELF, 1f);
        anim1.setInterpolator(new LinearInterpolator());
        anim1.setStartOffset(100);
        anim1.setDuration(600); //in milliseconds
        anim1.setFillAfter(true);
        animset.addAnimation(anim1);
        Animation anim2 =  new TranslateAnimation(0, 700,0, 0);
        anim2.setDuration(1300);
        anim2.setStartOffset(700);
        anim2.setFillAfter(true);
        animset.addAnimation(anim2);

        animset.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());

                //  Create a new boolean and preference and set it to true
                final boolean isFirstStart = getPrefs.getBoolean("first_run", true);

                //  If the activity has never started before...
                if (isFirstStart) {
                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("first_run", false);

                    //  Apply changes
                    e.apply();

                    startActivity(new Intent(SplashScreen.this, FirstRun.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }

                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

//Start animation
        view.startAnimation(animset);
//Later on, use view.setAnimation(null) to stop it.

    }
}