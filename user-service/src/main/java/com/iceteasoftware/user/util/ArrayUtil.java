package com.iceteasoftware.user.util;

public class ArrayUtil {
    public static boolean[] append(boolean[]... arrays) {
        int length = 0;

        for (boolean[] array : arrays) {
            length += array.length;
        }

        boolean[] newArray = new boolean[length];

        int previousLength = 0;

        for (boolean[] array : arrays) {
            System.arraycopy(array, 0, newArray, previousLength, array.length);

            previousLength += array.length;
        }

        return newArray;
    }

    public static boolean[] append(boolean[] array, boolean value) {
        boolean[] newArray = new boolean[array.length + 1];

        System.arraycopy(array, 0, newArray, 0, array.length);

        newArray[newArray.length - 1] = value;

        return newArray;
    }
    public static byte[] append(byte[]... arrays) {
        int length = 0;

        for (byte[] array : arrays) {
            length += array.length;
        }

        byte[] newArray = new byte[length];

        int previousLength = 0;

        for (byte[] array : arrays) {
            System.arraycopy(array, 0, newArray, previousLength, array.length);

            previousLength += array.length;
        }

        return newArray;
    }

    public static byte[] append(byte[] array, byte value) {
        byte[] newArray = new byte[array.length + 1];

        System.arraycopy(array, 0, newArray, 0, array.length);

        newArray[newArray.length - 1] = value;

        return newArray;
    }

    public static char[] append(char[]... arrays) {
        int length = 0;

        for (char[] array : arrays) {
            length += array.length;
        }

        char[] newArray = new char[length];

        int previousLength = 0;

        for (char[] array : arrays) {
            System.arraycopy(array, 0, newArray, previousLength, array.length);

            previousLength += array.length;
        }

        return newArray;
    }

    public static char[] append(char[] array, char value) {
        char[] newArray = new char[array.length + 1];

        System.arraycopy(array, 0, newArray, 0, array.length);

        newArray[newArray.length - 1] = value;

        return newArray;
    }

    public static double[] append(double[]... arrays) {
        int length = 0;

        for (double[] array : arrays) {
            length += array.length;
        }

        double[] newArray = new double[length];

        int previousLength = 0;

        for (double[] array : arrays) {
            System.arraycopy(array, 0, newArray, previousLength, array.length);

            previousLength += array.length;
        }

        return newArray;
    }

    public static double[] append(double[] array, double value) {
        double[] newArray = new double[array.length + 1];

        System.arraycopy(array, 0, newArray, 0, array.length);

        newArray[newArray.length - 1] = value;

        return newArray;
    }

    public static float[] append(float[]... arrays) {
        int length = 0;

        for (float[] array : arrays) {
            length += array.length;
        }

        float[] newArray = new float[length];

        int previousLength = 0;

        for (float[] array : arrays) {
            System.arraycopy(array, 0, newArray, previousLength, array.length);

            previousLength += array.length;
        }

        return newArray;
    }

    public static float[] append(float[] array, float value) {
        float[] newArray = new float[array.length + 1];

        System.arraycopy(array, 0, newArray, 0, array.length);

        newArray[newArray.length - 1] = value;

        return newArray;
    }

}
