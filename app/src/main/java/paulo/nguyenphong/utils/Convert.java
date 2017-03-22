package paulo.nguyenphong.utils;

/**
 * Created by Nguyen Phong on 3/19/2017.
 */

public class Convert {
    public static double[] convertDoubleArray(int[] numbers)
//changed double to double[]
    {
        double[] newNumbers = new double[numbers.length]; //changed 99 to numbers.length
        for (int index = 0; index < numbers.length; index++)
            newNumbers[index] = (double) numbers[index];

        return newNumbers;
    }

}
