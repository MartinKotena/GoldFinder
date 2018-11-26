package cz.unknown.domain.simpletracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class PermissionUtils {

    public static String mCurrentPhotoPath1 = "";
    public static int REQUEST_TAKE_PHOTO = 1;

        public static void requestPermission(Activity activity, String permissionType, int requestCode) {
                if (!checkPermission(activity, permissionType)) {
                ActivityCompat.requestPermissions(activity, new String[]{permissionType}, requestCode);
            }
        }

        public static boolean checkPermission(Context context, String permission){
            int permissionState = ActivityCompat.checkSelfPermission(context, permission);
            return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static File createImageFile1() throws IOException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = df.format(Calendar.getInstance().getTime());
        String imageFileName = "JPEG_" + date + "_";
        String p = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ Environment.DIRECTORY_PICTURES + File.separator + "SimpleTracker";
        File konec = new File(p);
        konec.mkdirs();
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                konec    /* directory */
        );
        mCurrentPhotoPath1 = image.getAbsolutePath();
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void dispatchTakePictureIntent(Fragment activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile1();
            } catch (IOException e) {
                Toast.makeText(activity.getActivity().getApplicationContext(), "Nejede", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity.getActivity().getApplicationContext(),
                        "cz.unknown.domain.simpletracker",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);

            }
        }
    }

}

