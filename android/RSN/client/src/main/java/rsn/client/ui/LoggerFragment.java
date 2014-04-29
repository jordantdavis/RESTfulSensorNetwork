package rsn.client.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import rsn.client.scheduler.Schedule;
import rsn.client.scheduler.ScheduleAccessor;

/**
 * Created by jordan on 4/15/14.
 */
public class LoggerFragment extends ListFragment {
    private View mRootView;
    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScheduleAccessor scheduleAccessor = new ScheduleAccessor(getActivity());
        ArrayList<Schedule> schedules = scheduleAccessor.getAllSchedules();
//        ArrayList<Schedule> schedules = new ArrayList<Schedule>();
//        schedules.add(new Schedule("accelerometer", 0, 60, 1));
//        schedules.add(new Schedule("gyroscope", 0, 60, 1));
//        schedules.add(new Schedule("magnetometer", 0, 60, 1));

        ArrayAdapter<Schedule> adapter = new ArrayAdapter<Schedule>(inflater.getContext(),
                android.R.layout.simple_list_item_1, schedules);

        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
