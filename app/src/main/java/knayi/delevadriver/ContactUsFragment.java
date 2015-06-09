package knayi.delevadriver;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;


public class ContactUsFragment extends Fragment {

    /*WebView webView;
    View view;*/

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*view  = inflater.inflate(R.layout.activity_contact_us, container, false);

        webView = (WebView) view.findViewById(R.id.contactus_webview);
        webView.loadUrl("http://deleva.sg/contact_us.html");*/

        WebView webview = new WebView(getActivity());
        webview.loadUrl("http://deleva.sg/contact_us.html");

        return webview;
    }


}
