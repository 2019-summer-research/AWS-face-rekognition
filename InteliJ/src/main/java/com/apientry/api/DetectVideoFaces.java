package com.apientry.api;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
public class DetectVideoFaces {



    String bucket = "faceseast1";
    String video = "Test.MOV";
    String queueUrl =  "https://sqs.us-east-2.amazonaws.com/131136880645/RekogQue";
    String topicArn="arn:aws:sns:us-east-2:131136880645:AmazonRekognitionTopic";
    String roleArn="arn:aws:iam::131136880645:role/RekVid";
    String CollectionID = "vidtestclass";
    private static AmazonSQS sqs = null;
    private static AmazonRekognition rek = null;

    private NotificationChannel channel= new NotificationChannel()
            .withSNSTopicArn(topicArn)
            .withRoleArn(roleArn);


    private static String startJobId = null;
    private void StartFaceSearchCollection(String bucket, String video) throws Exception{


        StartFaceSearchRequest req = new StartFaceSearchRequest()
                .withCollectionId("vidtestclass")
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                                .withBucket(bucket)
                                .withName(video)))
                .withNotificationChannel(channel);



        StartFaceSearchResult startPersonCollectionSearchResult = rek.startFaceSearch(req);
        startJobId=startPersonCollectionSearchResult.getJobId();

    }


    public DetectVideoFaces (String collectionID,String Video)  throws Exception{
        video = Video;
        CollectionID = collectionID;
        System.out.println("|"+CollectionID+"|");
        sqs = AmazonSQSClientBuilder.defaultClient();
        rek = AmazonRekognitionClientBuilder.defaultClient();

        //=================================================
        StartFaceSearchCollection(bucket, video);
        //=================================================
        System.out.println("Waiting for job: " + startJobId);
        //Poll queue for messages
        List<Message> messages=null;
        int dotLine=0;
        boolean jobFound=false;

        //loop until the job status is published. Ignore other messages in queue.
        do{
            messages = sqs.receiveMessage(queueUrl).getMessages();
            if (dotLine++<20){
            }else{
                dotLine=0;
            }

            if (!messages.isEmpty()) {
                //Loop through messages received.
                for (Message message: messages) {
                    String notification = message.getBody();

                    // Get status and job id from notification.
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonMessageTree = mapper.readTree(notification);
                    JsonNode messageBodyText = jsonMessageTree.get("Message");
                    ObjectMapper operationResultMapper = new ObjectMapper();
                    JsonNode jsonResultTree = operationResultMapper.readTree(messageBodyText.textValue());
                    JsonNode operationJobId = jsonResultTree.get("JobId");
                    JsonNode operationStatus = jsonResultTree.get("Status");
                    System.out.println("Job found was " + operationJobId);
                    // Found job. Get the results and display.
                    if(operationJobId.asText().equals(startJobId)){
                        jobFound=true;
                        System.out.println("Job id: " + operationJobId );
                        System.out.println("Status : " + operationStatus.toString());
                        if (operationStatus.asText().equals("SUCCEEDED")){
                            //============================================
                            GetResultsFaceSearchCollection();
                            //============================================
                        }
                        else{
                            System.out.println("Video analysis failed");
                        }

                        sqs.deleteMessage(queueUrl,message.getReceiptHandle());
                    }

                    else{
                        System.out.println("Job received was not job " +  startJobId);
                        //Delete unknown message. Consider moving message to dead letter queue
                        sqs.deleteMessage(queueUrl,message.getReceiptHandle());
                    }
                }
            }
        } while (!jobFound);


        System.out.println("Done!");
    }


    void StartLabels(String bucket, String video) throws Exception{

        StartLabelDetectionRequest req = new StartLabelDetectionRequest()
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                                .withBucket(bucket)
                                .withName(video)))
                .withMinConfidence(50F)
                .withJobTag("DetectingLabels")
                .withNotificationChannel(channel);

        StartLabelDetectionResult startLabelDetectionResult = rek.startLabelDetection(req);
        startJobId=startLabelDetectionResult.getJobId();


    }


    void GetResultsLabels() throws Exception{

        int maxResults=10;
        String paginationToken=null;
        GetLabelDetectionResult labelDetectionResult=null;

        do {
            if (labelDetectionResult !=null){
                paginationToken = labelDetectionResult.getNextToken();
            }

            GetLabelDetectionRequest labelDetectionRequest= new GetLabelDetectionRequest()
                    .withJobId(startJobId)
                    .withSortBy(LabelDetectionSortBy.TIMESTAMP)
                    .withMaxResults(maxResults)
                    .withNextToken(paginationToken);


            labelDetectionResult = rek.getLabelDetection(labelDetectionRequest);

            VideoMetadata videoMetaData=labelDetectionResult.getVideoMetadata();

            System.out.println("Format: " + videoMetaData.getFormat());
            System.out.println("Codec: " + videoMetaData.getCodec());
            System.out.println("Duration: " + videoMetaData.getDurationMillis());
            System.out.println("FrameRate: " + videoMetaData.getFrameRate());


            //Show labels, confidence and detection times
            List<LabelDetection> detectedLabels= labelDetectionResult.getLabels();

            for (LabelDetection detectedLabel: detectedLabels) {
                long seconds=detectedLabel.getTimestamp();
                Label label=detectedLabel.getLabel();
                System.out.println("Millisecond: " + Long.toString(seconds) + " ");

                System.out.println("   Label:" + label.getName());
                System.out.println("   Confidence:" + detectedLabel.getLabel().getConfidence().toString());

                List<Instance> instances = label.getInstances();
                System.out.println("   Instances of " + label.getName());
                if (instances.isEmpty()) {
                    System.out.println("        " + "None");
                } else {
                    for (Instance instance : instances) {
                        System.out.println("        Confidence: " + instance.getConfidence().toString());
                        System.out.println("        Bounding box: " + instance.getBoundingBox().toString());
                    }
                }
                System.out.println("   Parent labels for " + label.getName() + ":");
                List<Parent> parents = label.getParents();
                if (parents.isEmpty()) {
                    System.out.println("        None");
                } else {
                    for (Parent parent : parents) {
                        System.out.println("        " + parent.getName());
                    }
                }
                System.out.println();
            }
        } while (labelDetectionResult !=null && labelDetectionResult.getNextToken() != null);

    }

    private static void GetResultsFaceSearchCollection() throws Exception{
        ResultsDataStorage DS = new ResultsDataStorage();
        GetFaceSearchResult faceSearchResult=null;
        int maxResults=10;
        String paginationToken=null;

        do {

            if (faceSearchResult !=null){
                paginationToken = faceSearchResult.getNextToken();
            }


            faceSearchResult  = rek.getFaceSearch(
                    new GetFaceSearchRequest()
                            .withJobId(startJobId)
                            .withMaxResults(maxResults)
                            .withNextToken(paginationToken)
                            .withSortBy(FaceSearchSortBy.TIMESTAMP)
            );


            VideoMetadata videoMetaData=faceSearchResult.getVideoMetadata();




            //Show search results
            List<PersonMatch> matches=
                    faceSearchResult.getPersons();

            for (PersonMatch match: matches) {
                long milliSeconds=match.getTimestamp();

                List <FaceMatch> faceMatches = match.getFaceMatches();
                if (faceMatches != null) {
                    for (FaceMatch faceMatch: faceMatches){
                        Face face=faceMatch.getFace();


                        DS.Check(face.getExternalImageId(),milliSeconds,faceMatch.getSimilarity(),face.getBoundingBox());


                    }
                }
            }


        } while (faceSearchResult !=null && faceSearchResult.getNextToken() != null);


        DS.print();





    }

}
