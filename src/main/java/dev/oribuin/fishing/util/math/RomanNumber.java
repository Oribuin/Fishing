package dev.oribuin.fishing.util.math;

import java.util.TreeMap;

/**
 * Thank you stack overflow, https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
 */
public class RomanNumber {

    private static final TreeMap<Integer, String> map = new TreeMap<>() {{
        this.put(1000, "M");
        this.put(900, "CM");
        this.put(500, "D");
        this.put(400, "CD");
        this.put(100, "C");
        this.put(90, "XC");
        this.put(50, "L");
        this.put(40, "XL");
        this.put(10, "X");
        this.put(9, "IX");
        this.put(5, "V");
        this.put(4, "IV");
        this.put(1, "I");
    }};

    /**
     * Convert an integer to a roman numeral string
     *
     * @param number The number to convert
     *
     * @return The roman numeral string
     */
    public static String toRoman(int number) {
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

}
