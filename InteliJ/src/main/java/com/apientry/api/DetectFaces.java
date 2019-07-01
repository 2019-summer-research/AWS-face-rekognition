package com.apientry.api;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.rekognition.model.Image;
import com.apientry.api.faces.ClientFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DetectFaces {

    public void run(String[] args) {

        if (args.length < 2) {
            System.err.println("Please provide an image.");
            return;
        }

        String imgPath = args[1];
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(imgPath));
        } catch (IOException e) {
            System.err.println("Failed to load image: " + e.getMessage());
            return;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        AmazonRekognition rekognition = ClientFactory.createClient();

        DetectFacesRequest request = new DetectFacesRequest()
                .withImage(new Image().withBytes(byteBuffer))
                .withAttributes(Attribute.ALL);
        DetectFacesResult result = rekognition.detectFaces(request);

        String orientationCorrection = result.getOrientationCorrection();
        System.out.println("Orientation correction: " + orientationCorrection);




        List<FaceDetail> faceDetails = result.getFaceDetails();
        for (FaceDetail faceDetail : faceDetails) {

            printFaceDetails(faceDetail);
        }


        drawBoundingBoxes(bytes,result);
    }

    private void printFaceDetails(FaceDetail faceDetail) {
        System.out.println("###############");

        AgeRange ageRange = faceDetail.getAgeRange();
        System.out.println("Age range: " + ageRange.getLow() + "-" + ageRange.getHigh());

        Beard beard = faceDetail.getBeard();
        System.out.println("Beard: " + beard.getValue() + "; confidence=" + beard.getConfidence());

        BoundingBox bb = faceDetail.getBoundingBox();
        System.out.println("BoundingBox: left=" + bb.getLeft() +
                ", top=" + bb.getTop() + ", width=" + bb.getWidth() +
                ", height=" + bb.getHeight());

        Float confidence = faceDetail.getConfidence();
        System.out.println("Confidence: " + confidence);


        Eyeglasses eyeglasses = faceDetail.getEyeglasses();
        System.out.println("Eyeglasses: " + eyeglasses.getValue() +
                "; confidence=" + eyeglasses.getConfidence());

        EyeOpen eyesOpen = faceDetail.getEyesOpen();
        System.out.println("EyeOpen: " + eyesOpen.getValue() +
                "; confidence=" + eyesOpen.getConfidence());

        Gender gender = faceDetail.getGender();
        System.out.println("Gender: " + gender.getValue() +
                "; confidence=" + gender.getConfidence());

        List<Landmark> landmarks = faceDetail.getLandmarks();
        for (Landmark lm : landmarks) {
            System.out.println("Landmark: " + lm.getType()
                    + ", x=" + lm.getX() + "; y=" + lm.getY());
        }

        MouthOpen mouthOpen = faceDetail.getMouthOpen();
        System.out.println("MouthOpen: " + mouthOpen.getValue() +
                "; confidence=" + mouthOpen.getConfidence());

        Mustache mustache = faceDetail.getMustache();
        System.out.println("Mustache: " + mustache.getValue() +
                "; confidence=" + mustache.getConfidence());

        Pose pose = faceDetail.getPose();
        System.out.println("Pose: pitch=" + pose.getPitch() +
                "; roll=" + pose.getRoll() + "; yaw" + pose.getYaw());

        ImageQuality quality = faceDetail.getQuality();
        System.out.println("Quality: brightness=" +
                quality.getBrightness() + "; sharpness=" + quality.getSharpness());

        Smile smile = faceDetail.getSmile();
        System.out.println("Smile: " + smile.getValue() +
                "; confidence=" + smile.getConfidence());

        Sunglasses sunglasses = faceDetail.getSunglasses();
        System.out.println("Sunglasses=" + sunglasses.getValue() +
                "; confidence=" + sunglasses.getConfidence());

        System.out.println("###############");
    }
    private BoundingBox convertBoundingBox(BoundingBox bb, String orientationCorrection, int width, int height) {
        if (orientationCorrection == null) {
            System.out.println("No orientationCorrection available.");
            return null;
        } else {
            float left = -1;
            float top = -1;
            switch (orientationCorrection) {
                case "ROTATE_0":
                    left = width * bb.getLeft();
                    top = height * bb.getTop();
                    break;
                case "ROTATE_90":
                    left = height * (1 - (bb.getTop() + bb.getHeight()));
                    top = width * bb.getLeft();
                    break;
                case "ROTATE_180":
                    left = width - (width * (bb.getLeft() + bb.getWidth()));
                    top = height * (1 - (bb.getTop() + bb.getHeight()));
                    break;
                case "ROTATE_270":
                    left = height * bb.getTop();
                    top = width * (1 - bb.getLeft() - bb.getWidth());
                    break;
                default:
                    System.out.println("Orientation correction not supported: " +
                            orientationCorrection);
                    return null;
            }
            System.out.println("BoundingBox: left=" + (int)left + ", top=" +
                    (int)top + ", width=" + (int)(bb.getWidth()*width) +
                    ", height=" + (int)(bb.getHeight()*height));
            BoundingBox outBB = new BoundingBox();
            outBB.setHeight(bb.getHeight()*height);
            outBB.setWidth(bb.getWidth()*width);
            outBB.setLeft(left);
            outBB.setTop(top);
            return outBB;
        }
    }


    public BufferedImage drawBoundingBoxes(byte[] bytes, DetectFacesResult result) {
        int width;
        int height;
        BufferedImage img = null;
        Graphics2D graphics;
        try {
            img = ImageIO.read(new ByteArrayInputStream(bytes));
            width = img.getWidth();
            height = img.getHeight();
            graphics = img.createGraphics();
        } catch (IOException e) {
            System.err.println("Failed to read image: " + e.getLocalizedMessage());
            return img;
        }
        System.out.println("Image: width=" + width + ", height=" + height);

        String orientationCorrection = result.getOrientationCorrection();
        System.out.println("Orientation correction: " + orientationCorrection);

        List<FaceDetail> faceDetails = result.getFaceDetails();
        for (FaceDetail faceDetail : faceDetails) {
            drawBoundingBox(faceDetail, orientationCorrection, width, height, graphics);
        }

        try {
            ImageIO.write(img, "jpg", new File("img_bb.jpg"));
            return img;
        } catch (IOException e) {
            System.err.println("Failed to write image: " + e.getLocalizedMessage());
        }
        return img;
    }

    private void drawBoundingBox(FaceDetail faceDetail, String orientationCorrection, int width, int height,
                                 Graphics2D graphics) {

        BoundingBox bb = faceDetail.getBoundingBox();
        BoundingBox cbb = convertBoundingBox(bb, orientationCorrection, width, height);
        if (cbb == null) {
            graphics.setColor(Color.RED);
            graphics.setStroke(new BasicStroke(10));
            BoundingBox outBB = new BoundingBox();
            float left = width * bb.getLeft();
            float top = height * bb.getTop();
            outBB.setHeight(bb.getHeight()*height);
            outBB.setWidth(bb.getWidth()*width);
            outBB.setLeft(left);
            outBB.setTop(top);

            graphics.drawRect(outBB.getLeft().intValue(), outBB.getTop().intValue(),
                    outBB.getWidth().intValue(), outBB.getHeight().intValue());
            return;
        }

        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(10));
        graphics.drawRect(cbb.getLeft().intValue(), cbb.getTop().intValue(),
                cbb.getWidth().intValue(), cbb.getHeight().intValue());
    }

}
