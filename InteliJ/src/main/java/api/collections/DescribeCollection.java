package api.collections;

import api.ClientFactory;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.DescribeCollectionRequest;
import com.amazonaws.services.rekognition.model.DescribeCollectionResult;

public class DescribeCollection {
    public void run(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide a collection name.");
            return;
        }

        DescribeCollectionRequest request = new DescribeCollectionRequest()
                .withCollectionId(args[1]);

        AmazonRekognition rekognition = ClientFactory.createClient();
        DescribeCollectionResult result = rekognition.describeCollection(request);

        System.out.println("ARN: " + result.getCollectionARN()
                + "\nFace Model Version: " + result.getFaceModelVersion()
                + "\nFace Count: " + result.getFaceCount()
                + "\nCreated: " + result.getCreationTimestamp());
    }
}
