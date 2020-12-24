package com.skrb7f16.petngo.Models;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportModel {
    private String report;
    private String doneOn;
    private String type;
    private String breed;
    private String pic;
    private int id;

    public ReportModel(String report, String doneOn, String type, String breed, String pic, int id) {
        this.report = report;
        this.doneOn = doneOn;
        this.type = type;
        this.breed = breed;
        this.pic = pic;
        this.id = id;
    }

    public ReportModel() {
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getDoneOn() {
        return doneOn;
    }

    public void setDoneOn(String doneOn) {
        this.doneOn = doneOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JSONObject serializedObject() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("report",this.report);
        jsonObject.put("type",this.type);
        jsonObject.put("breed",this.breed);
        jsonObject.put("id",this.id);
        jsonObject.put("pic",this.pic);
        jsonObject.put("doneOn",this.doneOn);
        return jsonObject;
    }

    public ReportModel(JSONObject json) throws JSONException {
        this.id=json.getInt("id");
        this.breed=json.getString("breed");
        this.report=json.getString("report");
        this.doneOn=json.getString("doneOn");
        this.pic=json.getString("pic");
        this.type=json.getString("type");

    }
}

