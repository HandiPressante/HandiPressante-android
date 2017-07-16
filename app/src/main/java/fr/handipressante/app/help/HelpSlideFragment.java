package fr.handipressante.app.help;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.handipressante.app.R;

/**
 * Created by Nico on 15/07/2017.
 */
public class HelpSlideFragment extends Fragment {
    private static final String ARG_DRAWABLE = "drawable";
    private static final String ARG_BG_COLOR = "bg_color";
    private static final String ARG_TITLE_COLOR = "title_color";
    private static final String ARG_DESC_COLOR = "desc_color";

    public static HelpSlideFragment newInstance(int imageDrawable) {
        return newInstance(imageDrawable, Color.parseColor("#ffffff"), 0, 0);
    }


    public static HelpSlideFragment newInstance(int imageDrawable, int bgColor, int titleColor, int descColor) {
        HelpSlideFragment sampleSlide = new HelpSlideFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DRAWABLE, imageDrawable);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_TITLE_COLOR, titleColor);
        args.putInt(ARG_DESC_COLOR, descColor);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    private int drawable, bgColor;

    public HelpSlideFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().size() != 0) {
            drawable = getArguments().getInt(ARG_DRAWABLE);
            bgColor = getArguments().getInt(ARG_BG_COLOR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_helpslide, container, false);
        ImageView i = (ImageView) v.findViewById(R.id.image);
        LinearLayout m = (LinearLayout) v.findViewById(R.id.main);

        i.setImageDrawable(ContextCompat.getDrawable(getActivity(), drawable));
        m.setBackgroundColor(bgColor);

        return v;
    }
}
