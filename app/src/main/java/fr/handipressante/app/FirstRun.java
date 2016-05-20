package fr.handipressante.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by marc on 13/04/2016.
 */
public class FirstRun extends AppIntro2 {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        /*addSlide(first_fragment);*/

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance("Bienvenue dans l'application HandiPressante", "Vous trouverez ici les toilettes disponibles classées du plus proche au plus éloigné.", R.drawable.g2, Color.GRAY));
        addSlide(AppIntroFragment.newInstance("Navigation sur carte", "Les toilettes recensées sont affichées à l'écran. Elles indiquent celles qui sont marquées comme accessibles ou non.", R.drawable.g4, Color.GRAY));
        addSlide(AppIntroFragment.newInstance("Fiche d'informations des toilettes", "Il suffit de cliquer sur une toilette pour ouvrir la fiche correspondante", R.drawable.g3, Color.GRAY));
        addSlide(AppIntroFragment.newInstance("Ajout d'informations ou de commentaires", "Quand la fiche est ouverte, cliquer sur \"modifier fiche\" ", R.drawable.g3, Color.GRAY));
        addSlide(AppIntroFragment.newInstance("Un menu latéral d'accès aux réglages et mémos", "Les réglages vous permettent d'adapter l'appliation à votre besoin. Les mémos apportent des informations médicales proposées par des médecins", R.drawable.g1, Color.GRAY));

        // OPTIONAL METHODS

        // Override bar/separator color
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

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
}
