package com.apientry.api;

import com.amazonaws.services.rekognition.model.BoundingBox;

public class ResultPerson {

    String Name;
    Long BestTimestamp;
    float Confidence;
    BoundingBox BB;
}
