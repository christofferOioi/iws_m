package com.intermercato.iws_m.utils;

import java.util.List;

/**
 * Created by christofferhedin on 2013-08-29.
 */

public final class Primitives {

    public static char max(char[] values) {
        char max = Character.MIN_VALUE;
        for (char value : values) {
            if (value > max)
                max = value;
        }
        return max;
    }

    public static byte max(byte[] values) {
        byte max = Byte.MIN_VALUE;
        for (byte value : values) {
            if (value > max)
                max = value;
        }
        return max;
    }

    public static short max(short[] values) {
        short max = Short.MIN_VALUE;
        for (short value : values) {
            if (value > max)
                max = value;
        }
        return max;
    }

    public static int max(int[] values) {
        int max = Integer.MIN_VALUE;
        for (int value : values) {
            if (value > max)
                max = value;
        }
        return max;
    }

    public static long max(long[] values) {
        long max = Long.MIN_VALUE;
        for (long value : values) {
            if (value > max)
                max = value;
        }
        return max;
    }

    public static Double max(List<Double> list) {
        Double max = Double.MIN_VALUE;
        for (Double value : list) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static Integer maxIntList(List<Integer> list) {
        Integer max = Integer.MIN_VALUE;
        for (Integer value : list) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static char maxChar(char... values) {
        return max(values);
    }

    public static byte maxByte(byte... values) {
        return max(values);
    }

    public static short maxShort(short... values) {
        return max(values);
    }

    public static int maxInt(int... values) {
        return max(values);
    }

    public static long maxLong(long... values) {
        return max(values);
    }

    public static char min(char[] values) {
        char min = Character.MAX_VALUE;
        for (char value : values) {
            if (value < min)
                min = value;
        }
        return min;
    }

    public static byte min(byte[] values) {
        byte min = Byte.MAX_VALUE;
        for (byte value : values) {
            if (value < min)
                min = value;
        }
        return min;
    }

    public static short min(short[] values) {
        short min = Short.MAX_VALUE;
        for (short value : values) {
            if (value < min)
                min = value;
        }
        return min;
    }

    public static Double min(List<Double> list) {
        Double min = Double.MAX_VALUE;
        for (Double value : list) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static int min(int[] values) {
        int min = Integer.MAX_VALUE;
        for (int value : values) {
            if (value < min)
                min = value;
        }
        return min;
    }

    public static long min(long[] values) {
        long min = Long.MAX_VALUE;
        for (long value : values) {
            if (value < min)
                min = value;
        }
        return min;
    }

    public static char minChar(char... values) {
        return min(values);
    }

    public static byte minByte(byte... values) {
        return min(values);
    }

    public static short minShort(short... values) {
        return min(values);
    }

    public static int minInt(int... values) {
        return min(values);
    }

    public static long minLong(long... values) {
        return min(values);
    }

    /**
     * round n down to nearest multiple of m
     */

    public static double roundDown(double n, double m) {
        return n >= 0 ? (n / m) * m : ((n - m + 1) / m) * m;
    }

    /**
     * round n up to nearest multiple of m
     */
    public static long roundUp(long n, long m) {
        return n >= 0 ? ((n + m - 1) / m) * m : (n / m) * m;
    }
    public static double roundToScalePart(double weight, long scalePart){
        return Math.rint(weight / scalePart) * scalePart;
    }
}

