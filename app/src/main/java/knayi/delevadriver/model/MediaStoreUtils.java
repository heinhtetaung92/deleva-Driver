package knayi.delevadriver.model;

import android.content.Context;
import android.content.Intent;

/**
 * Created by heinhtetaung on 4/7/15.
 */
public class MediaStoreUtils {

    private MediaStoreUtils() {
    }


    public static Intent getPickImageIntent(final Context context) {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);


        return Intent.createChooser(intent, "Select picture");
    }
}
