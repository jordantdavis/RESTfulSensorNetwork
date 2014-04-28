package rsn.client.scheduler;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by jordan on 4/24/14.
 */
public class ScheduleAccessor {
    private static final String SCHEDULE_FILENAME = "schedules.csv";

    private Context context;

    public ScheduleAccessor(Context context) {
        this.context = context;
    }

    public ArrayList<Schedule> getAllSchedules() {
        ArrayList<Schedule> schedules = new ArrayList<Schedule>();

        synchronized (ScheduleAccessor.class) {
            try {
                Scanner scanner = new Scanner(context.openFileInput(SCHEDULE_FILENAME));

                while (scanner.hasNext()) {
                    String[] tokens = scanner.nextLine().split(",");
                    String sensorName = tokens[0];
                    int startTime = Integer.parseInt(tokens[1]);
                    int endTime = Integer.parseInt(tokens[2]);
                    double frequency = Double.parseDouble(tokens[3]);
                    schedules.add(new Schedule(sensorName, startTime, endTime, frequency));
                }

                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return schedules;
    }

    public void appendSchedule(Schedule schedule) {
        synchronized (ScheduleAccessor.class) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(SCHEDULE_FILENAME,
                        Context.MODE_PRIVATE | Context.MODE_APPEND);

                fileOutputStream.write((schedule.toString() + "\n").getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
