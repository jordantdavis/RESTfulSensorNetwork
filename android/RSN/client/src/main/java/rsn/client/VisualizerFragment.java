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
public class VisualizerFragment extends Fragment {
    private View mRootView;
    private TextView mTextView;

    public VisualizerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_visualizer, container, false);
        mTextView = (TextView)(mRootView.findViewById(R.id.visualizer_textview));
        mTextView.setText("Coming Phase 6!");

        return mRootView;
    }
}
