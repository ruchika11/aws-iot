package com.clarion.aws_iot.aws_iot_api;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;


import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.model.AttachPolicyResult;
import com.amazonaws.services.iot.model.AttachThingPrincipalResult;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.amazonaws.services.iot.model.CreatePolicyResult;
import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.CreateThingTypeResult;
import com.amazonaws.services.iot.model.ListThingsResult;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;

public class App
{

	public static final String thingName = "euro";
	private static final String TestTopic = "Topic1";
	private static request request = new request();
	public static final String fileLocation =  "C:\\Users\\Ruchika\\Desktop\\credentials\\";

	public static void main(String[] args)throws Exception {
		
		AWSIotClient iotClient = BasicConnectionUtil.initIotClientUsingAccessKeyAndSecretKey();
		AWSIotDataClient iotDataClient = BasicConnectionUtil.initIotDataClientUsingAccessKeyAndSecretKey();
//		createThingEndToEnd(iotClient);
//		publishToTheTopic();
//		createThingShadow(iotDataClient);
		getThingShadow(iotDataClient);


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
	
	public static void publishToTheTopic() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, AWSIotException{
		AWSIotMqttClient iotClient = BasicConnectionUtil.initMqttClient();
		iotClient.connect();
		
		int counter = 0;
		while(true){
			String payload = "hello from blocking publisher - " + (counter++);
			iotClient.publish(TestTopic,payload);
			System.out.println("hello from blocking publisher - " + (counter++));
		}
	}

}
