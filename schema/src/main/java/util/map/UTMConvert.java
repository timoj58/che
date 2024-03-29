package util.map;

import model.UTM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timmytime on 10/02/15.
 */
public class UTMConvert {


    public static final int LONG_ZONES = 60;
    public static final List<String> latValues = new ArrayList<String>() {{
        add("C");
        add("D");
        add("E");
        add("F");
        add("G");
        add("H");
        add("J");
        add("K");
        add("L");
        add("M");
        add("N");
        add("P");
        add("Q");
        add("R");
        add("S");
        add("T");
        add("U");
        add("V");
        add("W");
        add("X");
    }};
    private static final int LAT_DEG = 8;
    private static final int LONG_DEG = 6;
    private static final int LAT_OFFSET = 80;
    private static final int LONG_OFFSET = 180;
    private final List<String> utmGrids = new ArrayList<>();
    private final List<String> subUtmGrids = new ArrayList<>();
    private double LAT_SUB_DEG = 0.05;
    private double LONG_SUB_DEG = 0.1;


    public UTMConvert() {
        createUTMGrids();
    }


    public UTMConvert(double utmSubZoneLat, double utmSubZoneLong) {
        LAT_SUB_DEG = utmSubZoneLat;
        LONG_SUB_DEG = utmSubZoneLong;
        createUTMGrids();
    }

    private void createUTMGrids() {
        //it goes letters, 1 - 60. simples. lol i put it wrong way round..oh well too late to change it all now....super bug!!
        for (String v : latValues) {
            for (int i = 1; i <= 60; i++) {
                utmGrids.add(v + i);
            }
        }

        //sub utm...runs 1C0 to 8X59....ie 1 to 8. then lat vals 1 . n + 0 - 59. probably could lambda, but this is cleaner really.
        for (int i = 1; i < 9; i++) {
            for (String v : latValues) {
                for (int j = 0; j < 60; j++) {
                    subUtmGrids.add(i + v + j);
                }
            }
        }

    }

    public double getLatOffset() {
        return LAT_SUB_DEG;
    }

    public double getLongOffset() {
        return LONG_SUB_DEG;
    }

    public List<String> getUtmGrids() {
        return utmGrids;
    }

    public List<String> getSubUtmGrids() {
        return subUtmGrids;
    }

    public UTM getUTMGrid(double latitude, double longitude) {
        return new UTM(latToUTM(latitude), longToUTM(longitude));
    }

    public UTM getUTMSubGrid(UTM utm, double latitude, double longitude) {
        return new UTM(latToSubUTM(utm.getUtmLat(), latitude), longToSubUTM(utm.getUtmLong(), longitude));
    }


    private String longToUTM(double longitude) {
        //There are 60 longitudinal projection zones numbered 1 to 60 starting at 180°W. Each of these zones is 6 degrees wide, apart from a few exceptions around Norway and Svalbard.
        //rns -180 to 180
        return String.valueOf((int) Math.floor((longitude + LONG_OFFSET) / LONG_DEG) + 1);
    }

    private String latToUTM(double latitude) {
        //There are 20 latitudinal zones spanning the latitudes 80°S to 84°N and denoted by the letters C to X, ommitting the letter O. Each of these is 8 degrees south-north, apart from zone X which is 12 degrees south-north.
        //note some of W is wrong and who gives a fuck about X unless your a seal
        return latValues.get((int) Math.floor((latitude + LAT_OFFSET) / LAT_DEG));
    }

    /*
      Basically the below is me bastardizing algorithm...it works.  test cases may be a pain in ass
      easch sub grid has a fucking lot of sectors...
     */

    private String longToSubUTM(String utmLong, double longitude) {
        final int end = (Integer.valueOf(utmLong) * LONG_DEG) - LONG_OFFSET;

        return String.valueOf((int) ((LONG_DEG - (end - longitude)) / LONG_SUB_DEG));
    }

    private String latToSubUTM(String utmLat, double latitude) {
        final int end = ((latValues.indexOf(utmLat) + 1) * LAT_DEG) - LAT_OFFSET;
        final int sector = (int) ((LAT_DEG - (end - latitude)) / LAT_SUB_DEG);
        final int prefix = (int) Math.ceil((sector / latValues.size()) + 1);

        return prefix + latValues.get(sector - (latValues.size() * (prefix - 1)));

    }
}