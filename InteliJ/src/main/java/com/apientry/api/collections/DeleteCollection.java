package com.apientry.api.collections;

import com.apientry.api.ClientFactory;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;

public class DeleteCollection {
    public void run(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide a collection name.");
            return;
        }

        String collectionId = args[1];

        DeleteCollectionRequest request = new DeleteCollectionRequest()
                .withCollectionId(collectionId);
        AmazonRekognition rekognition = ClientFactory.createClient();
        DeleteCollectionResult result = rekognition.deleteCollection(request);

        Integer statusCode = result.getStatusCode();
        System.out.println("Status code: " + statusCode);
    }
}
