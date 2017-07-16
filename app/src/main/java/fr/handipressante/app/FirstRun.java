package fr.handipressante.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;

import fr.handipressante.app.help.HelpSlideFragment;

/**
 * Created by marc on 13/04/2016.
 */
public class FirstRun extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(HelpSlideFragment.newInstance(R.drawable.help_toilet_list_1));
        addSlide(HelpSlideFragment.newInstance(R.drawable.help_toilet_list_2));

        // Override bar/separator color
        setBarColor(Color.parseColor("#D32F2F"));
        setDoneText("Ok");

        // SHOW or HIDE the statusbar
        showStatusBar(true);

        // Hide Skip/Done button
        showSkipButton(false);
    }


    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }

    @Override
    public void onSkipPressed(){
        loadMainActivity();
        finish();
    }
}
