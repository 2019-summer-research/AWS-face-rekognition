package api.faces;

import api.ClientFactory;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SearchFacesByImage {
    public void run(String[] args) {

        if (args.length < 3) {
            System.err.println("Please provide a collection and images:  ");
            return;
        }

        String collectionId = args[1];
        String imageArg = args[2];

        Path path = Paths.get(imageArg);
        ByteBuffer byteBuffer;
        try {
            byte[] bytes = Files.readAllBytes(path);
            byteBuffer = ByteBuffer.wrap(bytes);
        } catch (IOException e) {
            System.err.println("Failed to read file '" + imageArg + "': " + e.getMessage());
            return;
        }

        SearchFacesByImageRequest request = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(new Image().withBytes(byteBuffer));

        AmazonRekognition rekognition = ClientFactory.createClient();
        SearchFacesByImageResult result = rekognition.searchFacesByImage(request);

        List<FaceMatch> faceMatches = result.getFaceMatches();
        for (FaceMatch match : faceMatches) {
            Float similarity = match.getSimilarity();
            Face face = match.getFace();
            System.out.println("MATCH:" +
                    "\nSimilarity: " + similarity +
                    "\nFace-ID: " + face.getFaceId() +
                    "\nImage ID: " + face.getImageId() +
                    "\nExternal Image ID: " + face.getExternalImageId() +
                    "\nConfidence: " + face.getConfidence());
        }

        if(result.getFaceMatches().size() < 1)
        {
            System.out.println("No Matching Face Found");
        }
    }
}
