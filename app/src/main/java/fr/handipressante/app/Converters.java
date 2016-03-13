package fr.handipressante.app;

import java.text.DecimalFormat;

/**
 * Created by Nico on 03/02/2016.
 */
public class Converters {

    /**
     * Return PMR resource corresponding to a PMR state
     * @param pmr PMR state
     * @return PMR resource
     */
    public static Integer pmrFromBoolean(Boolean pmr) {
        if (pmr) {
            return R.drawable.handicap_icon;
        } else {
            return R.drawable.not_handicap_icon;
        }
    }

    /**
     * Return PMR pin resource corresponding to a PMR state
     * @param pmr PMR state
     * @return PMR pin resource
     */
    public static Integer pmrPinFromBoolean(Boolean pmr) {
        if (pmr) {
            return R.drawable.pmr_pin;
        } else {
            return R.drawable.not_pmr_pin;
        }
    }

    /**
     * Return rank resource corresponding to an integer rank
     * @param rank Toilet rank
     * @return Rank resource
     */
    public static Integer rankFromInteger(Integer rank) {
        switch (rank) {
            case 0:
                return R.drawable.star_zero;
            case 1:
                return R.drawable.star_one;
            case 2:
                return R.drawable.star_two;
            case 3:
                return R.drawable.star_three;
            case 4:
                return R.drawable.star_four;
            case 5:
                return R.drawable.star_five;
            default:
                return R.drawable.no_rate_stars;

        }
    }

    /**
     * Return formatted distance string
     * @param distance Distance in meters
     * @return Formatted distance string
     */
    public static String formattedDistanceFromDouble(Double distance) {
        if (distance > 1000) {
            // Kilometers
            distance = distance / 1000;
            return (new DecimalFormat(".#").format(distance)) + " km";
        } else {
            // meters
            return distance.intValue() + " m";
        }
    }
}
