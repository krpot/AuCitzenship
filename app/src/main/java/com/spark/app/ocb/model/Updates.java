package com.spark.app.ocb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sunghun
 *
 */
public class Updates {

	public String fileName;   //file name
	public String version;    //version number
	public long lastUpdated;

	public Updates(){}


    @Override
    public String toString() {
        return "Updates{" +
                ", fileName='" + fileName + '\'' +
                ", version='" + version + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    public static Updates toObject(JSONObject json) throws JSONException {
        Updates o = new Updates();
        if (json==null)
            return o;

        o.fileName = json.optString("f");
        o.version  = json.optString("v");
        o.lastUpdated = new Date().getTime();
        return o;
    }

    public static List<Updates> loadFromJson(String jsonStr) throws JSONException{
        List<Updates> list = new ArrayList<Updates>();
        JSONArray jArray = new JSONArray(jsonStr);
        for (int i=0; i<jArray.length(); i++){
            JSONObject json = jArray.getJSONObject(i);
            list.add(Updates.toObject(json));
        }

        return list;
    }
}
