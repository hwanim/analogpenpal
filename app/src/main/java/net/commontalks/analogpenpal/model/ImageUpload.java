package net.commontalks.analogpenpal.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * Created by imhwan on 2018. 2. 25..
 */
@Data
@RequiresApi(api = Build.VERSION_CODES.M)
public class ImageUpload {

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private static final int MULTIPLE_PERMISSIONS = 101;
    private Uri mTemporaryUri;
    private Uri photoUri;
    private String mCurrentPhotoPath;
    private String[] permissions;

    public void imageUploadStart(Context context, String[] needPermissions){
        profilePhotoAdd(context,needPermissions);

    }

    public boolean checkPermissions(Context context, String[] needPermissions) {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : needPermissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.i("POSTWRITE", "not granted permission add.");
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            Log.i("POSTWRITE", "permission request." + permissionList.toString());
            ActivityCompat.requestPermissions((Activity) context, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public void profilePhotoAdd(final Context context, String[] needPermissions) {
        checkPermissions(context, needPermissions);
        new AlertDialog.Builder(context).setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진 촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePhoto(context);

                    }
                })
                .setNegativeButton("앨범 선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToAlbum(context);
                    }
                }).show();
    }

    private void takePhoto(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(context, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                e.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(context,
                        "com.ludus.commontalks.provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                ((Activity) context).startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            mTemporaryUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mTemporaryUri);
            ((Activity) context).startActivityForResult(intent, PICK_FROM_CAMERA);
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "cm_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/commontalks/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private void goToAlbum(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        ((Activity) context).startActivityForResult(intent, PICK_FROM_ALBUM);
    }





}
