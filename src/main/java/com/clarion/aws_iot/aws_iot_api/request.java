package com.clarion.aws_iot.aws_iot_api;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.amazonaws.services.iot.model.AttachPolicyRequest;
import com.amazonaws.services.iot.model.AttachThingPrincipalRequest;
import com.amazonaws.services.iot.model.AttributePayload;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreatePolicyRequest;
import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.CreateThingTypeRequest;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;

public class request {
	
	public CreateThingRequest createThingRequest(String thingName, String thingTypeName, HashMap<String,String> attributes){
		CreateThingRequest request = new CreateThingRequest();
        request.setThingName(thingName);
        request.setThingTypeName(thingTypeName);
        
        AttributePayload attributePayload = new AttributePayload();
        attributePayload.setAttributes(attributes);
		request.setAttributePayload(attributePayload);
		
		return request;
	}
	
	public ListThingsRequest getThingRequest(){
		ListThingsRequest listOfThingRequest = new ListThingsRequest();
		return listOfThingRequest;
	}
	
	public CreateThingTypeRequest createThingTypeRequest(String thingTypeName){
		CreateThingTypeRequest requestCreateThingType = new CreateThingTypeRequest();
      	requestCreateThingType.setThingTypeName(thingTypeName);
      	return requestCreateThingType;
	}
	
	public CreateKeysAndCertificateRequest createCertificateRequest(boolean setAsActive){
		CreateKeysAndCertificateRequest requestCreateKeysAndCertificate = new CreateKeysAndCertificateRequest();
      	requestCreateKeysAndCertificate.setSetAsActive(setAsActive);
      	return requestCreateKeysAndCertificate;
	}
	
	public AttachThingPrincipalRequest attachCertificatewithCertificateArnRequest(String thingName, String certificateArn){
		AttachThingPrincipalRequest requestAttachThingPrincipal = new AttachThingPrincipalRequest();
		requestAttachThingPrincipal.setThingName(thingName);
		requestAttachThingPrincipal.setPrincipal(certificateArn);
		return requestAttachThingPrincipal;
	}
	
	public CreatePolicyRequest createPolicyRequest(String json, String policyName){
		CreatePolicyRequest requestCreatePolicy = new CreatePolicyRequest();
		requestCreatePolicy.setPolicyDocument(json);
	    requestCreatePolicy.setPolicyName(policyName);
	    return requestCreatePolicy;
	}
	
	public AttachPolicyRequest attachPolicyToThing(String policyName, String Target){
		AttachPolicyRequest requestAttachPolicy = new AttachPolicyRequest();
      	requestAttachPolicy.setPolicyName(policyName);
      	requestAttachPolicy.setTarget(Target);
      	return requestAttachPolicy;
	}
	
	public UpdateThingShadowRequest createThingShadowRequest(String thingName, String payloadInJson){
		UpdateThingShadowRequest updateThingShadowRequest = new UpdateThingShadowRequest();
        updateThingShadowRequest.setThingName(thingName);
        byte[] b = payloadInJson.getBytes(StandardCharsets.UTF_8);
        updateThingShadowRequest.setPayload( ByteBuffer.wrap(b));
        return updateThingShadowRequest;
	}
	
	public GetThingShadowRequest getThingShadowRequest(String thingName){
		GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest();
      	getThingShadowRequest.setThingName(thingName);
      	return getThingShadowRequest;
	}
}
