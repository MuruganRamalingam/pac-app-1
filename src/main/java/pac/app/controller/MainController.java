package pac.app.controller;

import java.util.HashMap;
import java.util.List;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.apache.client.impl.ApacheConnectionManagerFactory;
import com.amazonaws.http.apache.client.impl.ApacheHttpClientFactory;
import com.amazonaws.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.amazonaws.http.client.ConnectionManagerFactory;
import com.amazonaws.http.client.HttpClientFactory;
import com.amazonaws.http.conn.ClientConnectionManagerFactory;
import com.amazonaws.http.settings.HttpClientSettings;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Controller("/")
public class MainController {
    //static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    private static final Log LOG = LogFactory.getLog(MainController.class);
    private ObjectMapper mapper = new ObjectMapper()
            .disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
            .disable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
            .enable(JsonParser.Feature.ALLOW_COMMENTS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static final HttpClientFactory<ConnectionManagerAwareHttpClient> httpClientFactory = new
            ApacheHttpClientFactory();
    private HttpClient httpClient;
    private static DynamoDBMapper dbMapper = null;
    private static AmazonDynamoDB amazonDynamoDBClient = null;
   // static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDBClient);
    private static Table table = null;
    private String base_point;
    public MainController() {
        this.httpClient = httpClientFactory.create(HttpClientSettings.adapt(new ClientConfiguration()));
        LOG.info("init");
        final ConnectionManagerFactory<HttpClientConnectionManager> cmFactory = new ApacheConnectionManagerFactory();
        final HttpClientConnectionManager cm = cmFactory.create(HttpClientSettings.adapt(new ClientConfiguration()));
        ClientConnectionManagerFactory.wrap(cm);
        }

    @Get("/ping")
    public String index() throws IOException {
        LOG.info("Local Test");

        LOG.info("Local Test2");
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();

        dbMapper = new DynamoDBMapper(amazonDynamoDBClient);
        LOG.info("Local Test3");
        table = new DynamoDB(amazonDynamoDBClient).getTable("pac_all");
        LOG.info("Local Test4");
        Item item = table.getItem("pk", "001", "sk", "0002");
        LOG.info("Local Test5");
        String base_janCode = item.get("jan").toString();
        String base_promotionDesc = item.get("PromotionDesc").toString();
        LOG.info(base_janCode);
        LOG.info(base_promotionDesc);
        return "{\"jan\":\"" + base_janCode + "\",\"point\":\"" + base_promotionDesc + "\"}";
    }

    @Post("/js")
    public String saveEvent(@Body String body) {
        LOG.info("Local Test1");
        LOG.info(body);
//        body="{'jan1':'1234567ABCDEF','rank':'1'}";
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

    @Get("/test")
    public String ts() throws IOException {
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();
        LOG.info("Local Test3");
        HashMap<String, Condition> scanFilter = new HashMap<>();
        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS("2345567ABC001"));
        condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
        .withAttributeValueList(new AttributeValue().withS("1"));
        scanFilter.put("jan", condition);
        scanFilter.put("rank", condition);
        ScanRequest scanRequest = new ScanRequest("pac_all").withScanFilter(scanFilter);
        ScanResult scanResult = amazonDynamoDBClient.scan(scanRequest);
        List<java.util.Map<String, AttributeValue>> aa = scanResult.getItems();
        LOG.info(aa.size());
        AttributeValue cc = new AttributeValue();
        String base_point = "";
        String base_promotionDesc = "";
        String base_rank = "";
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
                if (key.equals("rank")) {
                    base_rank = cc.toString().substring(4);
                    base_rank = base_rank.substring(0, base_rank.length() - 2);
                }
                LOG.info(key);
                LOG.info(cc.toString());
                LOG.info(base_rank);
            }
        }
        String s = String.valueOf(cc);
        return "{\"point\":\"" + base_point + "\",\"PromotionDesc\":\"" + base_promotionDesc + "\",\"rank\":\"" + base_rank + "\"}";
       // return last;
    }

    @Get("/pe002")
    public String getEvent(@Body String body) {
        LOG.info("Local Test7");
        body = "jan:1234567ABCDEF";
        LOG.info(body);
        String [] s1 = body.split(":");
        String jan = s1[1];
        LOG.info(jan + "::" + s1[1].length());
        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_1).build();
        HashMap<String, Condition> scanFilter = new HashMap<>();
        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue().withS(jan));
        scanFilter.put("jan", condition);
        ScanRequest scanRequest1 = new ScanRequest("pac_all").withScanFilter(scanFilter);
        ScanResult scanResult1 = amazonDynamoDBClient.scan(scanRequest1);
        List<java.util.Map<String, AttributeValue>> aa = scanResult1.getItems();
        LOG.info(aa.size());
        AttributeValue cc = new AttributeValue();
        String base_masterStoreCode = "";
        String base_maStoreCode = "";
        String base_promotionCode = "";
        String base_rewardCode = "";
        String base_promotionDesc = "";
        String base_point = "";
        for (int i = 1; i < aa.size(); i++) {
            java.util.Map<String, AttributeValue> bb = aa.get(i);
            Iterator<String> iterator = bb.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                cc = bb.get(key);
                if (key.equals("jan")) {
                    base_masterStoreCode = jan.substring(0, 4);
                    base_maStoreCode = jan.substring(5, 6);
                    base_promotionCode = jan.substring(6, 10);
                    base_rewardCode = jan.substring(10);
                }
            }
            LOG.info(cc.toString());
            LOG.info(base_masterStoreCode);
        }
        return "{\"MasterStroreCode\":\"" + base_masterStoreCode + "\",\"MaStoreCode\":\"" + base_maStoreCode + "\",\"PromotionCode\":\"" + base_promotionCode + "\",\"RewardCode\":\"" + base_rewardCode + "\"}";
        // return "{\"Member rank\":\"" +jan + "\",\"All Points\":\"" +all_points + "\",\"PromotionCode\":\"" + base_promotionCode + "\",\"Promotion Desc\":\""+base_promotionDesc+ "\", \"Store Code\":\""+ base_maStoreCode+"\",\"RewardCode\":\""+base_rewardCode+"\"}";
    }





//    @Get("/pe003")
//    public String getPromotion(@Body String body) {
//        LOG.info("Local Test7");
//        body = "jan:1234567ABCDEF";
//        LOG.info(body);
//        String[] s1 = body.split(":");
//        String jan = s1[1];
//        LOG.info(jan + "::" + s1[1].length());
//        amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
//                .withCredentials(new DefaultAWSCredentialsProviderChain())
//                .withRegion(Regions.US_EAST_1).build();
//        HashMap<String, Condition> scanFilter = new HashMap<>();
//        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
//                .withAttributeValueList(new AttributeValue().withS(jan));
//        scanFilter.put("jan", condition);
//        ScanRequest scanRequest1 = new ScanRequest("pac_all").withScanFilter(scanFilter);
//        ScanResult scanResult1 = amazonDynamoDBClient.scan(scanRequest1);
//        List<java.util.Map<String, AttributeValue>> aa = scanResult1.getItems();
//        LOG.info(aa.size());
//        AttributeValue cc = new AttributeValue();
//        String base_promotionCode = "";
//        base_promotionCode = jan.substring(5, 10);
//        LOG.info(base_promotionCode);
//        String jan_code="";
//        String promotion_desc="";
//        int point_value=0;
//        String sdt="";
//        String type="";
//        for(Map<String, AttributeValue> item : scanResult1.getItems()) {
//                LOG.info(item.entrySet());
//                LOG.info(item.keySet());
//                LOG.info(item.values());
//                if (jan.contains(base_promotionCode)) {
//                    //jan_code=String.valueOf(item.get("jan"));
//                    LOG.info(item.get("jan"));
//                    LOG.info(item.get("PromotionDesc"));
//                   // promotion_desc=String.valueOf(item.get("PromotionDesc"));
//                    LOG.info(item.get("PromotionDesc"));
//                    LOG.info(item.get("point"));
//                    //String p=item.get("point").toString();
////                    point_value = Integer.parseInt(p);
////                    LOG.info(point_value);
//                    LOG.info(item.get("sdt"));
//                    sdt = String.valueOf(item.get("sdt"));
//                    LOG.info(sdt);
//                    LOG.info(item.get("type"));
////                    type=String.valueOf(item.get("type"));
////                    LOG.info(type);
//                }
//                else {
//                    LOG.info("This Promotion Code has expired!");
//                    System.out.println("This Promotion Code has expired!");
//                }
//            }
//        return "{\"Jan_Code\":\"" + jan_code + "\",\"Promotion_desc\":\"" + promotion_desc + "\",\"point_value\":\"" + point_value + "\",\"Promotion _Start _Date:\":\"" + sdt + "\"}";
//        }
//@Post("/insert")
//        public String CreateItems()
//        {
//            amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
//                    .withCredentials(new DefaultAWSCredentialsProviderChain())
//                    .withRegion(Regions.US_EAST_1).build();
//            Table table = new DynamoDB(amazonDynamoDBClient).getTable("pac_all");
//            try
//            {
//                Item item =new Item().withPrimaryKey("pk","004")
//                        .withString("sk","005")
//                        .withString("edt","20221031122003")
//                        .withString("jan","1234567ABCDED")
//                        .withString("point","20")
//                        .withString("PromotionDesc","Army Day")
//                        .withNumber("rank",2)
//                        .withString("sdt","20220729011904")
//                        .withString("type","Electronics Item");
//                    table.putItem(item);
//            }
//            catch(Exception e)
//            {
//                System.err.println("Create items failed.");
//            }
//            return  "Data Saved Successfully";
//        }
    }



