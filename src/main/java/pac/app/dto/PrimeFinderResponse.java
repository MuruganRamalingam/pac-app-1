package pac.app.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class PrimeFinderResponse {

    private String message;
    private List<Integer> primes;

    public PrimeFinderResponse() {
    }

    public List<Integer> getPrimes() {
        return primes;
    }

    public void setPrimes(List<Integer> primes) {
        this.primes = primes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}