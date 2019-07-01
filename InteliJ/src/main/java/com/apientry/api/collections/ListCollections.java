package com.apientry.api.collections;

import com.apientry.api.ClientFactory;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;

import java.util.List;

public class ListCollections {
    public void run(String[] args) {

        ListCollectionsRequest request = new ListCollectionsRequest()
                .withMaxResults(100);

        AmazonRekognition rekognition = ClientFactory.createClient();
        ListCollectionsResult result = rekognition.listCollections(request);

        List<String> collectionIds = result.getCollectionIds();
        while (collectionIds != null) {
            for (String id : collectionIds) {
                System.out.println(id);
            }

            String token = result.getNextToken();
            if (token != null) {
                result = rekognition.listCollections(request.withNextToken(token));
            } else {
                collectionIds = null;
            }
        }
    }
}
