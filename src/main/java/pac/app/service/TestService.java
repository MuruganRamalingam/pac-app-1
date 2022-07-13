package pac.app.service;


import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Singleton
public class TestService {
    // Credit to https://www.geeksforgeeks.org/sieve-eratosthenes-0n-time-complexity/
    // for this clever O(n) implementation of the Sieve
    public final int MAX_SIZE = 1000001;
    // isPrime[] : isPrime[i] is true if number is prime
    // prime[] : stores all prime number less than N
    // SPF[] that store smallest prime factor of number
    // [for Exp : smallest prime factor of '8' and '16'
    // is '2' so we put SPF[8] = 2 , SPF[16] = 2 ]
    private Vector<Boolean> isPrime = new Vector<>(MAX_SIZE);
    private Vector<Integer> SPF = new Vector<>(MAX_SIZE);

    public TestService() {
        long startTime = System.currentTimeMillis();

        // Init the isPrime and SPF vectors
        for (int i = 0; i < MAX_SIZE; i++) {
            isPrime.add(true);
            SPF.add(2);
        }
        // 0 and 1 are not prime
        isPrime.set(0, false);
        isPrime.set(1, false);
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms");
    }

    public List<Integer> findPrimesLessThan(int n) {
        // Fill in the rest of the entries
        List<Integer> prime = new ArrayList<>();
        for (int i = 2; i < n; i++) {
            // If isPrime[i] == True then i is
            // prime number
            if (isPrime.get(i)) {
                // put i into prime[] vector
                prime.add(i);

                // A prime number is its own smallest
                // prime factor
                SPF.set(i, i);
            }

            // Remove all multiples of  i*prime[j] which are
            // not prime by making isPrime[i*prime[j]] = false
            // and put smallest prime factor of i*Prime[j] as prime[j]
            // [for exp :let  i = 5, j = 0, prime[j] = 2 [ i*prime[j] = 10]
            // so smallest prime factor of '10' is '2' that is prime[j] ]
            // this loop run only one time for number which are not prime
            for (int j = 0;
                 j < prime.size() &&
                         i * prime.get(j) < n && prime.get(j) <= SPF.get(i);
                 j++) {
                isPrime.set(i * prime.get(j), false);

                // put smallest prime factor of i*prime[j]
                SPF.set(i * prime.get(j), prime.get(j));
            }
        }
        return prime;
    }
}
