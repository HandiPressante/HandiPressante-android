package fr.handipressante.app.HelpSlides;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import fr.handipressante.app.R;

/**
 * Created by marc on 22/05/2016.
 */
public class HelpSlideMap extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Géolocalisation des toilettes", "Naviguez sur la carte pour trouver les toilettes à l'endroit que vous souhaitez. Appuyez sur le bouton + pour ajouter des toilettes sur la carte.",
                R.drawable.tuto_map, Color.parseColor("#164F86")));
       // addSlide(AppIntroFragment.newInstance("Ajout de toilettes", "Ensuite, appuyez longtemps sur la zone où vous souhaitez ajouter des toilettes et suivez ce qui est affiché à l'écran.",
       //         R.drawable.tuto_add_from_map, Color.parseColor("#164F86")));
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

