package rsn.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by jordan on 4/15/14.
 */
public class SchedulerFragment extends Fragment {
    private View mRootView;
    private WebView mWebView;

    public SchedulerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_scheduler, container, false);
        mWebView = (WebView)(mRootView.findViewById(R.id.web_view));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://hnat-server.cs.memphis.edu/~jdavis17/rsn/scheduler.html");

        return mRootView;
    }
}
