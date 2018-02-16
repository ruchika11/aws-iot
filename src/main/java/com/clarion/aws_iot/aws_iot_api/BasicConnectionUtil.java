package com.clarion.aws_iot.aws_iot_api;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iotdata.AWSIotDataClient;


public class BasicConnectionUtil {
	
	public static final String access_key_id = credentials.access_key_id;
    public static final String secret_access_key = credentials.secret_access_key;
    public static final String clientId = credentials.clientId;
	public static final String clientEndpoint = credentials.clientEndpoint;
	public static final String certificateFile = App.fileLocation+App.thingName+"_cert.txt";
	public static final String privateKeyFile = App.fileLocation+App.thingName+"_key.txt";
	

	public static AWSIotClient initIotClientUsingAccessKeyAndSecretKey(){
		 BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, secret_access_key);
    	 AWSIotClient iotClient = new AWSIotClient(awsCreds);
    	 com.amazonaws.regions.Region usEast2 = com.amazonaws.regions.Region.getRegion(Regions.US_EAST_2);
    	 iotClient.setRegion(usEast2);
    	 return iotClient;
	}
	
	public static AmazonDynamoDB initIotDynemoDbClientUsingAccessKeyAndSecretKey(){
		AWSCredentialsProvider awsCreds = new AWSStaticCredentialsProvider(new BasicAWSCredentials(access_key_id, secret_access_key));
		AmazonDynamoDB dynamoDbClient = AmazonDynamoDBClientBuilder.standard().withCredentials(awsCreds).withRegion(Regions.US_EAST_2)
		.build();	
		return dynamoDbClient;
	}
	
	public static AWSIotDataClient initIotDataClientUsingAccessKeyAndSecretKey(){
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id,secret_access_key);
      	AWSIotDataClient IotDataClient = new AWSIotDataClient(awsCreds);
      	com.amazonaws.regions.Region usEast2 = com.amazonaws.regions.Region.getRegion(Regions.US_EAST_2);
      	IotDataClient.setRegion(usEast2);
      	return IotDataClient;
	}
	
	public static AWSIotMqttClient initMqttClient() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		
		util util = new util();
		com.clarion.aws_iot.aws_iot_api.util.KeyStorePasswordPair pair = util.getKeyStorePasswordPair(certificateFile,privateKeyFile,null);
		
		AWSIotMqttClient awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        
        if (awsIotClient == null) {
           if (access_key_id != null && secret_access_key != null) {
                awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, access_key_id, secret_access_key);
            }
        }

        if (awsIotClient == null) {
            throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
        }
        
        return awsIotClient;
    }
}
