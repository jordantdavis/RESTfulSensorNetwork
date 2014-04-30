package rsn.client.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import rsn.client.scheduler.SensorSample;

/**
 * Created by jordan on 4/29/14.
 */
public class RsnRequestHandler {
    private static final String REGISTER_DEVICE_RESOURCE = "http://hnat-server.cs.memphis.edu:9263/device/register";
    private static final String UNREGISTER_DEVICE_RESOURCE = "http://hnat-server.cs.memphis.edu:9263/device/unregister";
    private static final String SAMPLES_UPLOAD_RESOURCE = "http://hnat-server.cs.memphis.edu:9263/samples/upload";

    public RsnRequestHandler() {

    }

    public void registerDevice(String registrationId, String[] availableSensors) {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(REGISTER_DEVICE_RESOURCE);
        HttpResponse response;
        JSONObject registerDeviceObject = new JSONObject();
        JSONArray availableSensorsArray  = new JSONArray();
        StringEntity stringEntity = null;

        try {
            registerDeviceObject.put("registrationId", registrationId);

            for (String sensor : availableSensors) {
                availableSensorsArray.put(sensor);
            }

            registerDeviceObject.put("availableSensors", availableSensorsArray);
            stringEntity = new StringEntity(registerDeviceObject.toString());
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(stringEntity);
            response = client.execute(request);
            // validate hnat-server response
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unregisterDevice(String registrationId) {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(UNREGISTER_DEVICE_RESOURCE);
        HttpResponse response;
        JSONObject unregisterDeviceObject = new JSONObject();
        StringEntity stringEntity = null;

        try {
            unregisterDeviceObject.put("registrationId", registrationId);
            stringEntity = new StringEntity(unregisterDeviceObject.toString());
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(stringEntity);
            response = client.execute(request);
            // validate hnat-server response
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void samplesUpload(String registrationId, SensorSample[] sensorSamples) {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(SAMPLES_UPLOAD_RESOURCE);
        HttpResponse response;
        JSONObject samplesUploadObject = new JSONObject();
        JSONArray sensorSamplesArray = new JSONArray();
        StringEntity stringEntity = null;

        try {
            samplesUploadObject.put("registrationId", registrationId);

            for (SensorSample sensorSample : sensorSamples) {
                JSONObject sensorSampleObject = new JSONObject();
                sensorSampleObject.put("sensorName", sensorSample.getSensorName());
                sensorSampleObject.put("timestamp", sensorSample.getTimestamp());
                sensorSampleObject.put("sampleValue", sensorSample.getSensorValue());
                sensorSamplesArray.put(sensorSampleObject);
            }

            samplesUploadObject.put("samples", sensorSamplesArray);
            stringEntity = new StringEntity(samplesUploadObject.toString());
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(stringEntity);
            response = client.execute(request);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
