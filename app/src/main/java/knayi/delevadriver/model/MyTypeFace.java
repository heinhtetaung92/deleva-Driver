package knayi.delevadriver.model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by heinhtetaung on 5/6/15.
 */
public class MyTypeFace {

    private static final String TAG = "Typefaces";
    public static final String BOLD = "bold";
    public static final String NORMAL = "normal";
    public static final String ITALIC = "italic";


    private static Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {

                    if(assetPath.equals(NORMAL)) {
                        Typeface tf = Typeface.createFromAsset(c.getAssets(),
                                "fonts/ciclesemi.ttf");

                        cache.put(assetPath, tf);
                    }else if(assetPath.equals(BOLD)) {
                        Typeface tf = Typeface.createFromAsset(c.getAssets(),
                                "fonts/ciclegordita.ttf");

                        cache.put(assetPath, tf);
                    }else if(assetPath.equals(ITALIC)) {
                        Typeface tf = Typeface.createFromAsset(c.getAssets(),
                                "fonts/ciclesemiitalic.ttf");

                        cache.put(assetPath, tf);
                    }




                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }

    }

}
