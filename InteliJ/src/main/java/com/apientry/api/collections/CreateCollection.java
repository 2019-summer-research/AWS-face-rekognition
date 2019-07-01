package com.apientry.api.collections;

import com.apientry.api.ClientFactory;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;

public class CreateCollection {
    public void run(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide a collection name.");
            return;
        }

        String collectionName = args[1];

        CreateCollectionRequest request = new CreateCollectionRequest()
                .withCollectionId(collectionName);

        AmazonRekognition rekognition = ClientFactory.createClient();
        CreateCollectionResult result = rekognition.createCollection(request);

        Integer statusCode = result.getStatusCode();
        String collectionArn = result.getCollectionArn();
        String faceModelVersion = result.getFaceModelVersion();
        System.out.println("statusCode=" + statusCode + "\nARN="
                + collectionArn + "\nface model version=" + faceModelVersion);
    }
}
