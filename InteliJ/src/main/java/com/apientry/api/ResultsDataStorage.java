package com.apientry.api;

import com.amazonaws.services.rekognition.model.BoundingBox;

import java.util.ArrayList;

public class ResultsDataStorage {

    ArrayList<ResultPerson> Results = new ArrayList<ResultPerson>();





    void Check(String name, Long time, float confidence, BoundingBox b)
    {
        for (ResultPerson R : Results) {
            if(R.Name == name)
            {
                if(R.Confidence < confidence)
                {
                    R.BB = b;
                    R.BestTimestamp = time;
                    R.Confidence = confidence;
                }
                return;
            }
        }
        ResultPerson RP = new ResultPerson();
        RP.BestTimestamp = time;
        RP.Confidence = confidence;
        RP.Name = name;
        RP.BB = b;
        Results.add(RP);



    }
    void print(){
        for (ResultPerson r : Results)
        {
            System.out.println("");
            System.out.println("Name: " + r.Name);
            System.out.println("Confidence: " + r.Confidence);
            System.out.println("Time: " + r.BestTimestamp);
        }
    }





}
