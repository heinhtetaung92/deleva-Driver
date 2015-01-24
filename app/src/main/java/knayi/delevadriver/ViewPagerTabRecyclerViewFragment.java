package knayi.delevadriver;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;

import knayi.delevadriver.model.JobItem;
import knayi.delevadriver.model.Requester;

public class ViewPagerTabRecyclerViewFragment extends Fragment {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        Activity parentActivity = getActivity();
        final ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
        recyclerView.setHasFixedSize(false);
        ArrayList<JobItem> items = new ArrayList<JobItem>();
        JobItem jobItem;
        for (int i = 1; i <= 30; i++) {
            jobItem = new JobItem();
            if (i % 2 == 0) {
                jobItem.set_type("Medical");
                jobItem.set_address("Inyar myaing St, Bahan, Yangon.");
                jobItem.set_status("P");
                jobItem.set_price("150");
                jobItem.set_createAt("2015-01-12");

                Requester requester = new Requester();
                requester.set_name("Pizza Company");
                requester.set_email("pizzacompany@example.com");
                requester.set_business_type("pizza");
                requester.set_mobile_number("09501001231");
                requester.set_address("Dagon Center, Pyi Rd, May Ni Gone, Yangon");

                jobItem.set_requester(requester);

            } else {
                jobItem.set_type("Food");
                jobItem.set_address("Kabaaye Pagoda Rd, Bahan, Yangon.");
                jobItem.set_status("A");
                jobItem.set_price("200");
                jobItem.set_createAt("2015-01-12");

                Requester requester = new Requester();
                requester.set_name("Pizza Company");
                requester.set_email("pizzacompany@example.com");
                requester.set_business_type("pizza");
                requester.set_mobile_number("09501001231");
                requester.set_address("Dagon Center, Pyi Rd, May Ni Gone, Yangon");

                jobItem.set_requester(requester);
            }

            items.add(jobItem);
        }
        View headerView = LayoutInflater.from(parentActivity).inflate(R.layout.padding, null);
        recyclerView.setAdapter(new SimpleHeaderRecyclerAdapter(parentActivity, items, headerView));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified offset after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
                ViewTreeObserver vto = recyclerView.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        recyclerView.scrollVerticallyToPosition(initialPosition);
                    }
                });
            }
            recyclerView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
        return view;
    }
}
