package pac.app.controller;
import pac.app.dto.Book;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Controller("/aws")
public class AwsController {
    private static final Log LOG = LogFactory.getLog(MainController.class);

    @Post(value = "/book", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Status(HttpStatus.CREATED)
    Book search(@Body Book book) {
        LOG.info(book.toString());
        return book;
    }

    @Post("/pick/{pk}")
    public String pick(String pk) {
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();

        dbMapper = new DynamoDBMapper(amazonDynamoDBClient);
        LOG.info("Local Test3");
        table = new DynamoDB(amazonDynamoDBClient).getTable("pac_all");
        LOG.info("Local Test4");
        Item item = table.postItem("pk", "000367853124");
        LOG.info("Local Test5");
        String base_janCode = item.get("jan").toString();
        String base_point = item.get("PromotionDesc").toString();
        LOG.info(base_point);
        return "{\"jan\":\"" + base_janCode + "\",\"point\":\"" + base_point + "\"}";

        LOG.info(pk.toString());
        return pk;
    }

    @Post("/pi/{rank}/{jan}")
    public String pi(String rank,String jan) {
        LOG.info(rank.toString());
        return rank+jan;
    }

}
