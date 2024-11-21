package com.example.bumap.utils;


import android.content.Context;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;

public class GraphHopperHelper {
    private static GraphHopper hopper;

    public static GraphHopper getGraphHopper(Context context) {
        if (hopper == null) {
            hopper = new GraphHopper()
                    .setGraphHopperLocation(context.getFilesDir() + "/graph-cache")
                    .setOSMFile(context.getFilesDir() + "/data/map.osm.pbf")
                    .forMobile();
            hopper.importOrLoad();
        }
        return hopper;
    }
}
