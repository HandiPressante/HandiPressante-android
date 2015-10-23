package com.handipressante.handipressante;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 21/10/2015.
 */
public class OnlineDataModel implements IDataModel {
    public List<Toilet> getToilets(GPSCoordinates ref, double xRange, double yRange) {
        String strUrl = "http://handipressante.carbonkiwi.net/api.php/toilettes/" + ref.getL93X() + "/" + ref.getL93Y() + "/" + (xRange/2) + "/" + (yRange/2);
        //URL url = new URL(strUrl);

        return new ArrayList<Toilet>();
    }
}
