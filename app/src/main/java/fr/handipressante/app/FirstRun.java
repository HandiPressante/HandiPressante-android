package fr.handipressante.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by marc on 13/04/2016.
 */
public class FirstRun extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        /*addSlide(first_fragment);*/

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance("Bienvenue dans l'application HandiPressante", "Vous trouverez ici les toilettes disponibles classées des plus proches au plus éloignées.", R.drawable.help_list, Color.parseColor("#164F86")));
        addSlide(AppIntroFragment.newInstance("Navigation sur la carte", "En zoomant sur la carte, vous pourrez voir apparaître les toilettes recensées dans la zone géographique.", R.drawable.help_map, Color.parseColor("#164F86")));
        addSlide(AppIntroFragment.newInstance("Fiche d'informations des toilettes", "En cliquant sur des toilettes dans la liste ou sur la carte, une fiche d'informations s'ouvre. Cette fiche permet de démarrer un itinéraire rapidement.", R.drawable.help_sheet, Color.parseColor("#164F86")));
        addSlide(AppIntroFragment.newInstance("Ajout d'informations ou de commentaires", "HandiPressante est une application collaborative. Vous pouvez participer en cliquant sur \"Éditer\" lorsque vous êtes dans une fiche d'informations.", R.drawable.help_description, Color.parseColor("#164F86")));
        addSlide(AppIntroFragment.newInstance("Un menu latéral d'accès aux réglages et mémos", "Les réglages vous permettent d'adapter l'application à vos besoins. Les mémos apportent des informations médicales proposées par des médecins", R.drawable.help_menu, Color.parseColor("#164F86")));

        // OPTIONAL METHODS

        // Override bar/separator color
        setBarColor(Color.parseColor("#D32F2F"));
        setSeparatorColor(Color.parseColor("#164F86"));
        setSkipText("Passer");
        setDoneText("Ok");

        // SHOW or HIDE the statusbar
        showStatusBar(true);

        // Hide Skip/Done button
        /*showSkipButton(false);
        showDoneButton(false);*/

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest
        /*setVibrate(true);
        setVibrateIntensity(30);*/

        // Animations -- use only one of the below. Using both could cause errors.
        //setFadeAnimation(); // OR
        //setZoomAnimation(); // OR
        //setFlowAnimation(); // OR
        //setSlideOverAnimation(); // OR
       // setDepthAnimation(); // OR
        //setCustomTransformer(yourCustomTransformer);

        // Permissions -- takes a permission and slide number
        //askForPermissions(new String[]{Manifest.permission.VIBRATE}, 3);
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
