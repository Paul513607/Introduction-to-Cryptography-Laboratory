package attack;

import lombok.Data;
import rsacryptosystem.RSACryptosystem;
import util.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Data
public class WienerAttack {
    private final RSACryptosystem rsaCryptosystem = new RSACryptosystem();
    private BigInteger myD, myP, myQ, myPhiN;
    private BigInteger generatedD;
    private boolean foundSolution = true;

    public WienerAttack() {
        generateParamsForCryptosystem();
    }

    private void generateParamsForCryptosystem() {
        Random random = new Random();
        BigInteger P = BigInteger.probablePrime(1024, random);
        BigInteger Q = BigInteger.probablePrime(1024, random);
        while (Q.compareTo(P) < 1 && Q.compareTo(P.multiply(BigInteger.TWO)) > -1) {
            Q = BigInteger.probablePrime(1024, random);
        }

        BigInteger N = P.multiply(Q);

        // setUp
        rsaCryptosystem.setP(P);
        rsaCryptosystem.setQ(Q);
        rsaCryptosystem.setN(N);

        MathContext mc = new MathContext(6);
        BigDecimal copyN = new BigDecimal(N);
        BigDecimal targetN = copyN.sqrt(mc).sqrt(mc).multiply(BigDecimal.valueOf(1 / 3.0));

        BigInteger D = new BigInteger(512, random);
        BigDecimal copyD = new BigDecimal(D);
        while (copyD.compareTo(targetN) > -1) {
            D = new BigInteger(512, random);
            copyD = new BigDecimal(D);
        }

        generatedD = D;

        rsaCryptosystem.setD(D);
        rsaCryptosystem.setE(D.modInverse(N));
    }

    private Pair<BigInteger, BigInteger> sumConvergents(List<BigInteger> convergents) {
        if (convergents.size() == 1 && Objects.equals(convergents.get(0), BigInteger.ZERO)) {
            return new Pair<>(BigInteger.ZERO, BigInteger.ONE);
        }

        BigInteger numerator, denominator;
        numerator = BigInteger.ONE;
        denominator = convergents.get(convergents.size() - 1);
        for (int index = convergents.size() - 1; index > 0; index--) {
            BigInteger nextVal = convergents.get(index - 1);
            nextVal = nextVal.multiply(denominator);
            numerator = numerator.add(nextVal);

            BigInteger temp = numerator;
            numerator = denominator;
            denominator = temp;
        }

        BigInteger temp = numerator;
        numerator = denominator;
        denominator = temp;

        return new Pair<>(numerator, denominator);
    }

    // verifies that the equation x^2 - (N - phiN + 1) * x + N = 0 has integer solutions
    private boolean checkQuadratic(BigInteger N, BigInteger phiN) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = N.subtract(phiN).add(BigInteger.ONE);
        BigInteger c = N;

        BigInteger delta = b.multiply(b).subtract(
                a.multiply(c).multiply(BigInteger.valueOf(4)));

        if (delta.compareTo(BigInteger.ZERO) < 0) {
            return false;
        }
        BigInteger[] tempArr = delta.sqrtAndRemainder();
        if (tempArr[1].compareTo(BigInteger.ZERO) != 0) {
            return false;
        }

        BigInteger numerator1 = b.negate().subtract(delta.sqrt());
        if (!numerator1.mod(a.multiply(BigInteger.TWO)).equals(BigInteger.ZERO)) {
            return false;
        }

        BigInteger numerator2 = b.negate().add(delta.sqrt());
        if (!numerator2.mod(a.multiply(BigInteger.TWO)).equals(BigInteger.ZERO)) {
            return false;
        }

        return true;
    }

    public void runAttack(BigInteger N, BigInteger E) {
        BigInteger phiN = BigInteger.ONE;
        BigInteger D    ;
        List<BigInteger> convergentList = new ArrayList<>();

        BigInteger val = E;
        BigInteger valToMod = N;
        while (true) {
            BigInteger convergent = val.divide(valToMod);
            BigInteger remainder = val.mod(valToMod);
            convergentList.add(convergent);

            Pair<BigInteger, BigInteger> testPair = sumConvergents(convergentList);

            D = testPair.getNum2();
            if (!testPair.getNum1().equals(BigInteger.ZERO))
                phiN = E.multiply(D).subtract(BigInteger.ONE).mod(testPair.getNum1());

            // check that D is odd
            if (D.mod(BigInteger.TWO).equals(BigInteger.ONE)
                    && !D.equals(BigInteger.ONE)
                    // test that (E * D - 1) / L is a whole number
                    && phiN.equals(BigInteger.ZERO)) {

                if (checkQuadratic(N, phiN)) {
                    phiN = E.multiply(D).subtract(BigInteger.ONE).divide(testPair.getNum1());
                    break;
                }
            }

            val = valToMod;
            valToMod = remainder;
            if (valToMod.equals(BigInteger.ZERO)) {
                foundSolution = false;
                break;
            }
        }

        myD = D;
        myPhiN = phiN;
        System.out.println("Done with attack.");
    }

    public void printSolution() {
        if (!foundSolution) {
            System.out.println("Couldn't find a solution.");
            return;
        }

        System.out.println("Generated exponent D: " + generatedD);
        System.out.println("Found decryption exponent D: " + myD);
        System.out.println("And phiN: " + myPhiN);
    }
}
