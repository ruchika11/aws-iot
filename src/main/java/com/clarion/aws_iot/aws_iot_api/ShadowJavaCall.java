package com.clarion.aws_iot.aws_iot_api;

import java.net.URI;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class ShadowJavaCall {

	public static final String certificateFile = App.fileLocation+"0ddb6ec476-certificate.pem.crt";
	public static final String privateKeyFile = App.fileLocation+"0ddb6ec476-private.pem.key";
	public static void main(String args[])
	{
		try{
			
		
		util util = new util();
		com.clarion.aws_iot.aws_iot_api.util.KeyStorePasswordPair pair = util.getKeyStorePasswordPair(certificateFile,privateKeyFile,null);
		
//		String keyPassphrase = "";
//
//		KeyStore keyStore = KeyStore.getInstance("PKCS12");
//		keyStore.load(new FileInputStream("cert-key-pair.pfx"), keyPassphrase.toCharArray());

		SSLContext sslContext = SSLContexts.custom()
		        .loadKeyMaterial(pair.keyStore,pair.keyPassword.toCharArray())
		        .build();

		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,new String[]{"TLSv1.2"},null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
		
		URI uri = new URI("https://a1s44wlly1l2e0.iot.us-east-2.amazonaws.com:8443/things/euro/shadow");
		
		
		String JSON_STRING=
				"{\"state\":{\"reported\":{\"device_id\":\"B827EB8CB947878\",\"motion_info\":\"stop\"}}}";
		
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
		catch (Exception e) {
			// TODO: handle exception
		}
        
	}
}
