package util;

import java.math.BigInteger;

public abstract class UtilCalculator {
    /** Apply only for prime numbers. */
    public static BigInteger eulerFunctionPrime(BigInteger inputNumber) {
        if (!inputNumber.isProbablePrime(90))
            throw new IllegalArgumentException("The inputNumber is definitely not prime.");

        return inputNumber.subtract(BigInteger.valueOf(1));
    }

    public static boolean areNumbersCoPrime(BigInteger no1, BigInteger no2) {
        return no1.gcd(no2).equals(BigInteger.valueOf(1));
    }
}
