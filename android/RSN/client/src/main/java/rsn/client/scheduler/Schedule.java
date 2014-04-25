package rsn.client.scheduler;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by jordan on 4/22/14.
 */
public class Schedule {
    private String sensorName;
    private int startTime;
    private int endTime;
    private double frequency;

    public Schedule(String name, int start, int end, double freq) {
        sensorName = name;
        startTime = start;
        endTime = end;
        frequency = freq;
    }

    public String getSensorName() {
        return sensorName;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public double getFrequency() {
        return frequency;
    }

    public String toString() {
        return sensorName + "," + startTime + "," + endTime + "," + frequency;
    }
}
