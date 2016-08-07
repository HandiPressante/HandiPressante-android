package fr.handipressante.app.help;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import fr.handipressante.app.R;

/**
 * Created by marc on 25/05/2016.
 */
public class HelpSlideSettings extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Aide réglages", "Adaptez l'application à vos besoins. Ajoutez ou supprimez des boutons de navigations, filtrez les données affichées.",
                R.drawable.tuto_settings, Color.parseColor("#164F86")));
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


