package org.hxnry.rsp.watcher_server.api.util;

import java.util.Arrays;

/**
 * @author Hxnry
 * @since November 08, 2016
 */
public class ArrayUtil {

    public static <T> T[] addToArray(T[] array, T e) {
        array  = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = e;
        return array;
    }

    public static int[] addToArray(int[] array, int e) {
        array  = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = e;
        return array;
    }

    public static boolean arrayContains(final int[] array, final int key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) >= 0;
    }

    public static boolean arrayContains(final String[] array, final String key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) >= 0;
    }
}
