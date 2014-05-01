package rsn.client.scheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by jordan on 4/28/14.
 */
public class ScheduleAccessor extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "Schedules";
    private static final int DB_VERSION = 1;

    private static final String COL_SENSOR_NAME = "sensorName";
    private static final String COL_START_TIME = "startTime";
    private static final String COL_END_TIME = "endTime";
    private static final String COL_FREQUENCY = "frequency";

    public ScheduleAccessor(Context context) {
        super(context, TABLE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableCreateString =
                "CREATE TABLE " + TABLE_NAME + " (" +
                COL_SENSOR_NAME + " VARCHAR(50)," +
                COL_START_TIME + " INT," +
                COL_END_TIME + " INT," +
                COL_FREQUENCY + " REAL," +
                "PRIMARY KEY (" +
                COL_SENSOR_NAME + "," +
                COL_START_TIME + "," +
                COL_END_TIME + "," +
                COL_FREQUENCY + "));";

        db.execSQL(tableCreateString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Schedule> getAllSchedules() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
            new String[]{ COL_SENSOR_NAME, COL_START_TIME, COL_END_TIME, COL_FREQUENCY },
            null, null, null, null, null
        );

        ArrayList<Schedule> schedules = new ArrayList<Schedule>();

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String sensorName = cursor.getString(0);
                long startTime = cursor.getLong(1);
                long endTime = cursor.getLong(2);
                double frequency = cursor.getDouble(3);
                schedules.add(new Schedule(sensorName, startTime, endTime, frequency));
                cursor.moveToNext();
            }

            cursor.close();
        }

        db.close();

        return schedules;
    }

    public void addSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SENSOR_NAME, schedule.getSensorName());
        contentValues.put(COL_START_TIME, schedule.getStartTime());
        contentValues.put(COL_END_TIME, schedule.getEndTime());
        contentValues.put(COL_FREQUENCY, schedule.getFrequency());

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public void removeSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_SENSOR_NAME + "='" +
                schedule.getSensorName() + "' AND " + COL_START_TIME + "=" +
                schedule.getStartTime() + " AND " + COL_END_TIME + "=" +
                schedule.getEndTime() + " AND " + COL_FREQUENCY + "=" +
                schedule.getFrequency() + ";");


        db.close();
    }
}
