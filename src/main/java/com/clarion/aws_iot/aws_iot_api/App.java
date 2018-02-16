package com.clarion.aws_iot.aws_iot_api;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.model.Action;
import com.amazonaws.services.iot.model.AttachPolicyResult;
import com.amazonaws.services.iot.model.AttachThingPrincipalResult;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.amazonaws.services.iot.model.CreatePolicyResult;
import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.CreateThingTypeResult;
import com.amazonaws.services.iot.model.CreateTopicRuleRequest;
import com.amazonaws.services.iot.model.ListThingsResult;
import com.amazonaws.services.iot.model.RepublishAction;
import com.amazonaws.services.iot.model.TopicRulePayload;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;

public class App
{

	public static final String thingName = "java_1";
	private static final String TestTopic = "Topic1";
	private static request request = new request();
	public static final String fileLocation =  "C:\\Users\\Ruchika\\Desktop\\credentials\\";
	public static final String certificateFile = App.fileLocation+"0ddb6ec476-certificate.pem.crt";
	public static final String privateKeyFile = App.fileLocation+"0ddb6ec476-private.pem.key";

	public static void main(String[] args)throws Exception {
        AWSIotClient iotClient = BasicConnectionUtil.initIotClientUsingAccessKeyAndSecretKey();
//		AWSIotDataClient iotDataClient = BasicConnectionUtil.initIotDataClientUsingAccessKeyAndSecretKey();
//		AWSIotMqttClient iotMqttClient = BasicConnectionUtil.initMqttClient();
//		createThingEndToEnd(iotClient);
//		publishToTheTopic(iotMqttClient);
//		createThingShadow(iotDataClient);
//		getThingShadow(iotDataClient);
//		AmazonDynamoDB dynamoDbClient = BasicConnectionUtil.initIotDynemoDbClientUsingAccessKeyAndSecretKey();
//		
//		getDynemoDbData(dynamoDbClient);
		
		attachCertificate(iotClient,"aws:iot:us-east-2:923415519113:cert/0ddb6ec4764946bf6d18afd3ddccf76d65a0c6b30b492040a2fd3c89a65d5347");
		
}
	
	
	
   public static void createTLSConnection() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, URISyntaxException{
	   util util = new util();
		com.clarion.aws_iot.aws_iot_api.util.KeyStorePasswordPair pair = util.getKeyStorePasswordPair(certificateFile,privateKeyFile,null);
		

		SSLContext sslContext = SSLContexts.custom()
		        .loadKeyMaterial(pair.keyStore,pair.keyPassword.toCharArray())
		        .build();

		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,new String[]{"TLSv1.2"},null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
		
		URI uri = new URI("https://a1s44wlly1l2e0.iot.us-east-2.amazonaws.com:8443/things/euro/shadow");
		
		
		String JSON_STRING=
				"{\"state\":{\"reported\":{\"device_id\":\"B827EB8CB947878\",\"motion_info\":\"stop\"}},\"metadata\":{\"reported\":{\"device_id\":{\"timestamp\":1516800848},\"motion_info\":{\"timestamp\":1516800848}}},\"version\":28,\"timestamp\":1517579144}";
		
		StringEntity requestEntity = new StringEntity(JSON_STRING,
			    ContentType.APPLICATION_JSON);

			HttpPost postMethod = new HttpPost("https://a1s44wlly1l2e0.iot.us-east-2.amazonaws.com:8443/things/euro/shadow");
			postMethod.setEntity(requestEntity);

			HttpResponse rawResponse = httpClient.execute(postMethod);
		
		
	//	HttpResponse response = httpClient.execute(new HttpGet(uri));
		 HttpEntity entity = rawResponse.getEntity();

		 // Read the contents of an entity and return it as a String.
        String content = EntityUtils.toString(entity);
        System.out.println(content);
   }
	

	public static void createThingEndToEnd(AWSIotClient iotClient) throws IOException{
		createThingType(iotClient);
		createThing(iotClient);

		HashMap<String, String> credential = createCertificate(iotClient);

	    attachCertificate(iotClient,credential.get("certificateArn"));
		createPolicy(iotClient);
		attachPolicyToThing(iotClient,credential.get("certificateArn"));
	}
	
	

	public static void createThing(AWSIotClient iotClient){
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("att_"+thingName,"val_"+thingName);

		CreateThingResult response = iotClient.createThing(request.createThingRequest(thingName,"type_"+thingName,attributes));

		System.out.println("createThing response is"+response.toString());
	}

	public static void getThing(AWSIotClient iotClient){
		ListThingsResult response = iotClient.listThings(request.getThingRequest());
		System.out.println("getThing response is"+response.toString());
	}

	public static void createThingType(AWSIotClient iotClient){
		CreateThingTypeResult response = iotClient.createThingType(request.createThingTypeRequest("type_"+thingName));
		System.out.println("createThingType response is"+response.toString());
	}


	public static HashMap<String, String> createCertificate(AWSIotClient iotClient) throws IOException{

		HashMap<String,String> certificateCredential = new HashMap<String,String>();
		CreateKeysAndCertificateResult response = iotClient.createKeysAndCertificate(request.createCertificateRequest(true));

		
		certificateCredential.put("certificateArn", response.getCertificateArn());
		certificateCredential.put("certificatePem", response.getCertificatePem());
		certificateCredential.put("privateKey", response.getKeyPair().getPrivateKey());

		System.out.println("createCertificate response is"+response.toString());

		return certificateCredential;
	}

	public static void attachCertificate(AWSIotClient iotClient, String certificateArn){

		AttachThingPrincipalResult response = iotClient.attachThingPrincipal(request.attachCertificatewithCertificateArnRequest(thingName, certificateArn));
		System.out.println(response.toString());
		System.out.println("attachCertificate response is"+response.toString());

	}

	public static void createPolicy(AWSIotClient iotClient){

		String json ="{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":\"iot:*\",\"Resource\":\"*\"}]}";
		CreatePolicyResult response = iotClient.createPolicy(request.createPolicyRequest(json,"policy_"+thingName));
		System.out.println("createPolicy response is"+response.toString());

	}

	public static void attachPolicyToThing(AWSIotClient iotClient, String certificateArn){
		AttachPolicyResult response = iotClient.attachPolicy(request.attachPolicyToThing("policy_"+thingName, certificateArn));
		System.out.println("attachPolicyToThing response is"+response.toString());
	}

	public static void createThingShadow(AWSIotDataClient IotDataClient){

		String payloadInJson = "{\"state\":{\"reported\":{\"device_id\":\"B827EB8CB946\",\"motion_info\":\"START\"}}}";
		UpdateThingShadowResult updateThingsResult = IotDataClient.updateThingShadow(request.createThingShadowRequest(thingName, payloadInJson));
		System.out.println("createThingShadow response is"+updateThingsResult.toString());
		
		
	}

	public static void getThingShadow(AWSIotDataClient IotDataClient){

		GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest();
		getThingShadowRequest.setThingName("testThing");
		GetThingShadowResult getThingShadowResult = IotDataClient.getThingShadow(request.getThingShadowRequest(thingName));

		ByteBuffer payload = getThingShadowResult.getPayload();
		CharBuffer charBuffer = StandardCharsets.UTF_8.decode(payload);
		String text = charBuffer.toString();
		System.out.println("getThingShadow response is"+ text);
		
	}
	
	public static void createRule(AWSIotClient iotClient){
		CreateTopicRuleRequest ad = new CreateTopicRuleRequest();
		ad.setRuleName("rule_"+thingName);
		String sqlQuery = "SELECT id FROM 'sdk/test/java' WHERE id>1";
		TopicRulePayload topicRulePayload = new TopicRulePayload();
		String description = "test rule query";
		topicRulePayload.setDescription(description);
		Collection<Action> actions = null;
		Action act = new Action();
		RepublishAction republish = new RepublishAction();
		republish.setTopic("republishTopic");
		act.setRepublish(republish);
		actions.add(act);
		topicRulePayload.setActions(actions);
		topicRulePayload.setSql(sqlQuery);
		ad.setTopicRulePayload(topicRulePayload );
		iotClient.createTopicRule(ad);
	}
	
	public static void getDynemoDbData(AmazonDynamoDB dynamoDbClient){
		Map<String,String> expressionAttributesNames = new HashMap<>();
	    expressionAttributesNames.put("#device_id","device_id");
	 
	    Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
	    expressionAttributeValues.put(":euro",new AttributeValue().withS("euro"));
		
		 QueryRequest queryRequest = new QueryRequest()
		            .withTableName("iotData1")
		            .withKeyConditionExpression("#device_id = :euro")
		            .withExpressionAttributeNames(expressionAttributesNames)
                    .withExpressionAttributeValues(expressionAttributeValues);
		 
		 QueryResult queryResult = dynamoDbClient.query(queryRequest);
		 
		
		 
		 
		 List<Map<String,AttributeValue>> attributeValues = queryResult.getItems();
		 
		 int row = 1;
		 for(Map<String, AttributeValue> attributeList:attributeValues){
			 System.out.print("row: "+row+" ");
			 getItem(attributeList);
			 row++;
		 }
		 
	}
	
	private static void getItem(Map<String, AttributeValue> attributeList) {
        for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
           
            if(item.getKey().equals("timestamp")){
            	 System.out.print(item.getKey()+": "+item.getValue().getS()+" ");
            }
            if(item.getKey().equals("data")){
            	 System.out.print("AndroidID"+": "+item.getValue().getM().get("reported").getM().get("AndroidID").getS()+" ");
            	 System.out.print("sensorvalue"+": "+item.getValue().getM().get("reported").getM().get("sensorvalue").getN()+" ");
            	 System.out.print("longitude"+": "+item.getValue().getM().get("reported").getM().get("longitude").getN()+" ");
            	 System.out.print("latitude"+": "+item.getValue().getM().get("reported").getM().get("latitude").getN()+" ");
            }
            if(item.getKey().equals("device_id")){
            	System.out.print(item.getKey()+": "+item.getValue().getS()+" ");
            }
        }
        System.out.println();
    }
		
	
	public static void publishToTheTopic(AWSIotMqttClient iotClient) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, AWSIotException, InterruptedException{
		iotClient.connect();
		
		AWSIotTopic topic = new AWSIotTopic(TestTopic);
		iotClient.subscribe(topic, true);
		
		Thread blockingPublishThread = new Thread(new BlockingPublisher(iotClient));
		blockingPublishThread.start();
		blockingPublishThread.join();
	}

	public static class BlockingPublisher implements Runnable {
        private final AWSIotMqttClient awsIotClient;

        public BlockingPublisher(AWSIotMqttClient awsIotClient) {
            this.awsIotClient = awsIotClient;
        }

        @Override
        public void run() {
            long counter = 1;

            while (true) {
                String payload = "hello from blocking publisher - " + (counter++);
                try {
                    awsIotClient.publish(TestTopic, payload);
                } catch (AWSIotException e) {
                    System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
                }
                System.out.println(System.currentTimeMillis() + ": >>> " + payload);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(System.currentTimeMillis() + ": BlockingPublisher was interrupted");
                    return;
                }
            }
        }
    }
	
//	 System.out.println(attributeName + " "
//           + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
//           + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
//           + (value.getB() == null ? "" : "B=[" + value.getB() + "]")
//           + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
//           + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
//           + (value.getBS() == null ? "" : "BS=[" + value.getBS() + "] \n"));
}
