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
public class SensorSamplesAccessor extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "SensorSamples";
    private static final int DB_VERSION = 1;

    private static final String COL_SENSOR_NAME = "sensorName";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_SENSOR_VALUE = "sensorValue";

    public SensorSamplesAccessor(Context context) {
        super(context, TABLE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableCreateString =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COL_SENSOR_NAME + " VARCHAR(50)," +
                        COL_TIMESTAMP+ " INT," +
                        COL_SENSOR_VALUE + " REAL," +
                        "PRIMARY KEY (" +
                        COL_SENSOR_NAME + "," +
                        COL_TIMESTAMP + "," +
                        COL_SENSOR_VALUE + "));";

        db.execSQL(tableCreateString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<SensorSample> getAllSensorSamples() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{ COL_SENSOR_NAME, COL_TIMESTAMP, COL_SENSOR_VALUE },
                null, null, null, null, null
        );

        ArrayList<SensorSample> sensorSamples = new ArrayList<SensorSample>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String sensorName = cursor.getString(0);
            long timestamp = cursor.getLong(1);
            double sensorValue = cursor.getDouble(2);
            sensorSamples.add(new SensorSample(sensorName, timestamp, sensorValue));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return sensorSamples;
    }

    public void addSensorSample(SensorSample sensorSample) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SENSOR_NAME, sensorSample.getSensorName());
        contentValues.put(COL_TIMESTAMP, sensorSample.getTimestamp());
        contentValues.put(COL_SENSOR_VALUE, sensorSample.getSensorValue());

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public void removeSensorSample(SensorSample sensorSample) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COL_SENSOR_NAME + "=?," + COL_TIMESTAMP + "=?" +
                COL_SENSOR_VALUE + "=?";
        String[] whereArgs = { sensorSample.getSensorName(), Long.toString(sensorSample.getTimestamp()),
                Double.toString(sensorSample.getSensorValue())};

        db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
    }
}
