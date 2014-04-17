package rsn.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by jordan on 4/15/14.
 */
public class LoggerFragment extends Fragment {
    private View mRootView;
    private TextView mTextView;

    public LoggerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_logger, container, false);
        mTextView = (TextView)(mRootView.findViewById(R.id.logger_textview));
        mTextView.setText("Coming Soon!");

        return mRootView;
    }
}
