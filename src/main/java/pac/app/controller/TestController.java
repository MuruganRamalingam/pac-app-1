package pac.app.controller;

import pac.app.dto.PrimeFinderResponse;
import pac.app.service.TestService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/test")
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    private final TestService testService;

    public TestController(TestService primeFinderService) {
        this.testService = primeFinderService;
    }

    @Get("/find/{number}")
    public PrimeFinderResponse findPrimesBelow(int number) {
        PrimeFinderResponse resp = new PrimeFinderResponse();
        if (number >= testService.MAX_SIZE) {
            if (LOG.isInfoEnabled()) {
                LOG.info("This number is too big, you can't possibly want to know all the primes below a number this big.");
            }
            resp.setMessage("This service only returns lists for numbers below " + testService.MAX_SIZE);
            return resp;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Computing all the primes smaller than {} ...", number);
        }
        resp.setMessage("Success!");
        resp.setPrimes(testService.findPrimesLessThan(number));
        return resp;
    }
}