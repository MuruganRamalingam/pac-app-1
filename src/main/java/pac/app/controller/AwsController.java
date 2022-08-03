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
    @Post("/js")
    public String saveEvent(@Body String body) {
        LOG.info("Local Test1");
        LOG.info(body);
//        body="{'jan1':'1234567ABCDEF','jan1':'1234567ABCDEF','jan1':'1234567ABCDEF','rank':'1'}";
        String[] param = body.split(",");
        String[] param1 = param[0].split(":");
        String jan = param1[1].replace("\"", "").replace("'", "").trim();
        LOG.info(jan);
        String rank = "";
        //SONArray jsonArray = new JSONArray(body);
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();
        LOG.info("Local Test2");
        HashMap<String, Condition> scanFilter = new HashMap<>();
        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(jan));
        scanFilter.put("jan", condition);
        ScanRequest scanRequest = new ScanRequest("pac_all").withScanFilter(scanFilter);
        ScanResult scanResult = amazonDynamoDBClient.scan(scanRequest);
        List<java.util.Map<String, AttributeValue>> aa = scanResult.getItems();
        LOG.info(aa.size());
        AttributeValue cc = new AttributeValue();
        String base_point = "";
        String base_promotionDesc = "";
        String base_sk = "";
        for (int i = 0; i < aa.size(); i++) {
            java.util.Map<String, AttributeValue> bb = aa.get(i);
            Iterator<String> iterator = bb.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                cc = bb.get(key);
                if (key.equals("PromotionDesc")) {
                    base_promotionDesc = cc.toString().substring(4);
                    base_promotionDesc = base_promotionDesc.substring(0, base_promotionDesc.length() - 2);
                }
                if (key.equals("point")) {
                    base_point = cc.toString().substring(4);
                    base_point = base_point.substring(0, base_point.length() - 2);
                }
                if (key.equals("sk")) {
                    base_sk = cc.toString();
                }
                LOG.info(key);
                LOG.info(cc.toString());
                LOG.info(base_sk);
            }
        }
        String s = String.valueOf(cc);
        return "{\"point\":\"" + base_point + "\",\"PromotionDesc\":\"" + base_promotionDesc + "\"}";
    }

}
