package fr.handipressante.app.help;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import fr.handipressante.app.R;

/**
 * Created by marc on 21/05/2016.
 */
public class HelpSlideToiletSheet extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(HelpSlideFragment.newInstance(R.drawable.help_toilet_sheet_1));
        addSlide(HelpSlideFragment.newInstance(R.drawable.help_toilet_sheet_2));

        // Override bar/separator color
        setBarColor(Color.parseColor("#D32F2F"));
        setDoneText("Ok");

        // SHOW or HIDE the statusbar
        showStatusBar(true);

        // Hide Skip/Done button
        showSkipButton(false);
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }

    @Override
    public void onSkipPressed(){
        finish();
    }
}

