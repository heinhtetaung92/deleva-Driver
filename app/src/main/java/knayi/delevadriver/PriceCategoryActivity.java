package knayi.delevadriver;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;


public class PriceCategoryActivity extends Fragment {

    //WebView webView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*view = inflater.inflate(R.layout.activity_price_category, container, false);
        webView = (WebView) view.findViewById(R.id.pricecategory);


        //webView.loadData(customHtml, "text/html", "UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new MyWebClient());
        //String url ="http://www.google.com";// field.getText().toString();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.loadUrl("http://deleva.sg/prices.html");*/

        WebView webview = new WebView(getActivity());
        webview.loadUrl("http://deleva.sg/prices.html");
        return webview;
    }


    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem item = menu.add("Icon");
        item.setIcon(R.drawable.deleva_dispatcher_white_noeffects_04);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
