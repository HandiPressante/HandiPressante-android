package fr.handipressante.app.HelpSlides;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import fr.handipressante.app.MainActivity;
import fr.handipressante.app.R;

/**
 * Created by marc on 21/05/2016.
 */
public class HelpSlideList extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Liste de toilettes", "Les toilettes disponibles classées du plus proche au plus éloigné. Cliquez sur l'une d'entre elle pour accéder à la fiche d'informations.", R.drawable.tuto_list, Color.parseColor("#164F86")));
        // OPTIONAL METHODS

        // Override bar/separator color
        setBarColor(Color.parseColor("#D32F2F"));
        setSeparatorColor(Color.parseColor("#164F86"));
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
