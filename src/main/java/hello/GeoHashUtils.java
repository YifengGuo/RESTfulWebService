package hello;

/**
 * Created by guoyifeng on 7/18/18
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for encoding and decoding geohashes. Based on
 * <a href = "https://en.wikipedia.org/wiki/Geohash"> https://en.wikipedia.org/wiki/Geohash
 */
public class GeoHashUtils {
    private static char[] BASE_32 = {'0', '1', '2', '3', '4', '5', '6',
    '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private static Map<Character, Integer> DECODE_MAP = new HashMap<>();

    private static final int PRECISION = 12;
    private static final int[] BITS = {16, 8, 4, 2, 1};

    static {
        for (int i = 0; i < BASE_32.length; i++) {
            DECODE_MAP.put(Character.valueOf(BASE_32[i]), i);
        }
    }

    public GeoHashUtils() {}

    /**
     * encode latitude and longitude by BASE_32 hash function
     * @param latitude latitude to encode
     * @param longitude longitude to encode
     * @return geohash encoding of the latitude and longitude
     */
    public static String encode(double latitude, double longitude) {
        double[] latInterval = {-90.0, 90.0};
        double[] lngInterval = {-180.0, 180.0};

        final StringBuilder geohash = new StringBuilder();

        // the even bits are taken for the longitude code (0111110000000),
        // while the odd bits are taken for the latitude code (101111001001).
        boolean isEven = true;

        int bit = 0;
        int ch = 0;

        while (geohash.length() < PRECISION) {
            double mid = 0.0;
            if (isEven) { // for longitude
                mid = (lngInterval[0] + lngInterval[1]) / 2D;
                if (longitude > mid) { // belong to right interval
                    // it means at current bit, it should be set to 1
                    // because we need to write bit from left to right
                    // so ch will do OR operation on high bit from BITS[]
                    // for example lat: 40.5187, lng: 74.4121
                    // it will calculate as 1 1 0 0 1 -> 25  -> BASE_32[25] -> t as first character
                    // ch will grow if coordinate > mid at current binary reduction
                    ch |= BITS[bit]; // set current bit as 1
                    // System.out.println("EVEN " + ch);
                    lngInterval[0] = mid;
                } else {
                    lngInterval[1] = mid;
                }
            } else {
                mid = (latInterval[0] + latInterval[1]) / 2D;
                if (latitude > mid) {
                    // System.out.println("ODD " + ch);
                    // ch will grow if coordinate > mid at current binary reduction
                    ch |= BITS[bit]; // set current bit as 1
                    latInterval[0] = mid;
                } else {
                    latInterval[1] = mid;
                }
            }

            isEven = !isEven;

            if (bit < 4) {
                bit++;
            } else { // convert to character in BASE_32 by each 5 bits
                // System.out.println("CURRENT " + ch);
                geohash.append(BASE_32[ch]);
                bit = 0;
                ch = 0;
            }
        }
        return geohash.toString();
    }

    /**
     * decode geohash into latitude and longitude
     * @param geohash geohash of a place
     * @return latitude and longitude of given place
     */
    public static double[] decode(String geohash) {
        double[] latInterval = {-90.0, 90.0};
        double[] lngInterval = {-180.0, 180.0};

        boolean isEven = true;

        double latitude;
        double longitude;

        for (int i = 0; i < geohash.length(); i++) {
            // integer value of current character's mapping in DECODE_MAP
            final int cd = DECODE_MAP.get(Character.valueOf(geohash.charAt(i))).intValue();

            for (int mask : BITS) {
                if (isEven) { // decode for longitude
                    if ((cd & mask) != 0) { // meas current bit is 1 and real longitude belongs to right interval
                        lngInterval[0] = (lngInterval[0] + lngInterval[1]) / 2D;
                    } else {
                        lngInterval[1] = (lngInterval[0] + lngInterval[1]) / 2D; // belongs to left interval
                                                                                 // so make right limit less
                    }
                } else { // decode for latitude
                    if ((cd & mask) != 0) { // meas current bit is 1 and real latitude belongs to right interval
                        latInterval[0] = (latInterval[0] + latInterval[1]) / 2D;
                    } else {
                        latInterval[1] = (latInterval[0] + latInterval[1]) / 2D;
                    }
                }

                isEven = !isEven;
            }
        }

        latitude = (latInterval[0] + latInterval[1]) / 2D;
        longitude = (lngInterval[0] + lngInterval[1]) / 2D;

        return new double[]{latitude, longitude};
    }

    public static void main(String[] args) {
        double latitude = 40.5187;
        double longitude = 74.4121;

        String geohash = GeoHashUtils.encode(latitude, longitude);
        System.out.println(geohash);

        double[] coordinates = GeoHashUtils.decode(geohash);
        for (double d : coordinates) {
            System.out.print(d + " ");
        }
    }
}

