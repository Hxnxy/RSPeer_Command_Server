package org.hxnry.rsp.watcher_server.api.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hxnry
 * @since November 08, 2016
 */
public class PriceLookup {

    public enum PriceTrend {
        RISING,
        FALLING,
        STEADY
    }

    private static final ConcurrentHashMap<Integer, PriceLookup> cache = new ConcurrentHashMap<Integer, PriceLookup>();

    private final int itemID;
    private final int averageSellOffer;
    private final int averageBuyOffer;
    private final int highAlchValue;
    private final int recentTradePrice;
    private final boolean isAccurate;
    private final PriceTrend trend;

    public PriceLookup(final int itemID, final int recentPrice) {
        this.itemID = itemID;
        this.averageSellOffer = recentPrice;
        this.averageBuyOffer = recentPrice;
        this.highAlchValue = -1;
        this.recentTradePrice = recentPrice;
        this.isAccurate = true;
        this.trend = PriceTrend.STEADY;
    }

    @Deprecated
    public PriceLookup(int itemID, int averageSell, int averageBuy, int highAlchVal, int recentPrice, boolean accurate, PriceTrend trend) {
        this.itemID = itemID;
        this.averageSellOffer = averageSell;
        this.averageBuyOffer = averageBuy;
        this.highAlchValue = highAlchVal;
        this.recentTradePrice = recentPrice;
        this.isAccurate = accurate;
        this.trend = trend;
    }

    @Deprecated
    public int getAverageSellOffer() {
        return averageSellOffer;
    }

    @Deprecated
    public int getAverageBuyOffer() {
        return averageBuyOffer;
    }

    @Deprecated
    public int getHighAlchValue() {
        return highAlchValue;
    }

    public int getRecentTradePrice() {
        return recentTradePrice;
    }

    @Deprecated
    public boolean isPriceAccurate() {
        return isAccurate;
    }

    @Deprecated
    public PriceTrend getTrend() {
        return trend;
    }

    @Override
    public int hashCode() {
        return itemID;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && (o instanceof PriceLookup) && o.hashCode() == this.hashCode();
    }

    public static void removeFromCache(int itemID) {
        Integer key = new Integer(itemID);
        cache.remove(key);
    }

    public static int getPrice(int itemID) {
        PriceLookup item = lookup(itemID);
        if (item == null) {
            return -1;
        }
        return item.getRecentTradePrice();
    }

    public static PriceLookup lookup(int itemID) {
        if (itemID != 1) {
            PriceLookup lookup;
            Integer id = new Integer(itemID);
            lookup = cache.get(id);
            if (lookup != null) {
                return lookup;
            }
        }
        try {
            int price;
            if (itemID == 995) {
                price = 1;
            } else {
                final URL url = new URL("https://org.hxnry.api.rsbuddy.com/grandExchange?a=guidePrice&i=" + itemID);
                final HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                final BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                final String priceData = br.readLine();
                final String overall = jsonGet(priceData, "overall");
                try {
                    price = Integer.parseInt(overall);
                } catch (Exception e) {
                    price = 0;
                    e.printStackTrace();
                }

                if (price == 0) {
                    final String selling = jsonGet(priceData, "selling");
                    final String buying = jsonGet(priceData, "buying");
                    price = (Integer.parseInt(selling) + Integer.parseInt(buying)) / 2;
                }
            }
            return new PriceLookup(itemID, Integer.valueOf(price));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String jsonGet(final String jsonString, final String fieldToGet) {
        final String firstPart = jsonString.substring(jsonString.indexOf(fieldToGet) + fieldToGet.length() + 2);
        final int firstCommaFirstPart = firstPart.indexOf(",");
        final int firstBraceFirstPart = firstPart.indexOf("}");
        final int endFirstPart = firstCommaFirstPart == -1 ? firstBraceFirstPart : firstCommaFirstPart < firstBraceFirstPart ? firstCommaFirstPart : firstBraceFirstPart;
        if (endFirstPart == -1) {
            return null;
        }
        return firstPart.substring(0, endFirstPart);
    }

    // so users can also provide their own zybezname if the getZybezName()
    // doesn't cover it.
    /**
     * DOES NOT USE CACHE
     */
    @Deprecated
    public static int getPrice(String zybezName) {
        PriceLookup lookup = lookup(1, zybezName);
        if (lookup == null) {
            return -1;
        }
        return lookup.getRecentTradePrice();
    }

    @Deprecated
    public static PriceLookup lookup(int itemID, String zybezName) {
        return lookup(itemID);
    }

}

