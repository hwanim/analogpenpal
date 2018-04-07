package net.commontalks.analogpenpal.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfilePhotoRegisterActivity extends AppCompatActivity {

    private static final String TAG = "PhotoRegisterActivity";
    private User mUser;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private static final int MULTIPLE_PERMISSIONS = 101;
    private Uri mTemporaryUri;
    private Uri photoUri;
    private String mCurrentPhotoPath;
    private DatabaseReference mUserRef;
    private StorageReference mStoreageRef;
    private boolean mRegisterPhoto = false;
    private Context mContext;


    @BindView(R.id.profilePhotoRegister)
    ImageView profilePhotoRegister;

    @BindView(R.id.registerProflePhotoTv)
    TextView registerProflePhotoTv;

    @BindView(R.id.registerProfiePhotoBtn)
    ImageView registerProfiePhotoBtn;


    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_register);
        ButterKnife.bind(this);
        mUser = (User)getIntent().getSerializableExtra("user");
        Log.i(TAG, mUser.toString());
        mUserRef = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid());
        mStoreageRef = FirebaseStorage.getInstance().getReference("users").child(mUser.getUid())
                .child(String.valueOf(System.currentTimeMillis()));
        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.i("POSTWRITE", "not granted permission add.");
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            Log.i("POSTWRITE", "permission request." + permissionList.toString());
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;

    }

    @OnClick(R.id.profilePhotoRegister)
    public void onClickprofilePhotoRegister(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        new AlertDialog.Builder(this).setTitle("업로드할 이미지 선택")
//                .setPositiveButton("사진 촬영", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        takePhoto();
//
//                    }
//                })
                .setPositiveButton("앨범 선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToAlbum();
                    }
                }).show();
    }

    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "net.ludus.analogpenpal.provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            mTemporaryUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mTemporaryUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "cm_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/analogpenpal/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    Log.i("POSTWRITE", "grantResults : " + grantResults.length);
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            Log.i("POSTWRITE", "READ_EXTERNAL_STORAGE equal to request Permission : " + grantResults[i]);
                            Log.i("POSTWRITE", "Pemissions : " + permissions[i]);
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            Log.i("POSTWRITE", "WRITE_EXTERNAL_STORAGE equal to request Permission : " + grantResults[i]);
                            Log.i("POSTWRITE", "Pemissions : " + permissions[i]);
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            Log.i("POSTWRITE", "CAMERA equal to request Permission" + grantResults[i]);
                            Log.i("POSTWRITE", "Pemissions : " + permissions[i]);
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }


    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "Permission Denied. please accept the permission.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                photoUri = mTemporaryUri;
            }
            if (photoUri != null) {
                cropImage();
            } else {
                Toast.makeText(this, "이미지 처리 오류!", Toast.LENGTH_SHORT).show();
                return;
            }
            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            profilePhotoRegister.setImageURI(photoUri);
            profilePhotoRegister.setBackground(new ShapeDrawable(new OvalShape()));
            profilePhotoRegister.setClipToOutline(true);
            //버튼도 함께 설정해주어야함.
            registerProfiePhotoBtn.setImageResource(R.drawable.button_start_kor);
            mRegisterPhoto = true;

        }
    }


    //Android N crop image
    public void cropImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.grantUriPermission("com.android.camera", photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(photoUri, "image/*");

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            int size = list.size();
            if (size == 0) {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                File croppedFileName = null;
                try {
                    croppedFileName = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File folder = new File(Environment.getExternalStorageDirectory() + "/analogpenpal/");
                File tempFile = new File(folder.toString(), croppedFileName.getName());

                photoUri = FileProvider.getUriForFile(this,
                        "net.ludus.analogpenpal.provider", tempFile);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    grantUriPermission(res.activityInfo.packageName, photoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(i, CROP_FROM_CAMERA);
            }

        } else {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(photoUri, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            photoUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CROP_FROM_CAMERA);
        }
    }


    @OnClick(R.id.registerProfiePhotoBtn)
    public void onClickRegisterBtn(){
        registerProfiePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //등록이 되었다면 데이터베이스에도 등록해줍니다.
        if (mRegisterPhoto) {
            mStoreageRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downLoadUri = taskSnapshot.getDownloadUrl();

                    mainActivityStart(mContext, mUser, downLoadUri);
                }
            });
        } else {
            mainActivityStart(mContext, mUser, null);
            finish();
        }
    }


    private void mainActivityStart(Context context, User user, Uri uri){
        mUser.setProfileUrl(String.valueOf(uri));
        mUserRef.child("profileUrl").setValue(mUser.getProfileUrl()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ((ProfilePhotoRegisterActivity)mContext).finish();
            }
        });
        Intent intent = new Intent(mContext, MainDrawerActivity.class);
        intent.putExtra("uid",user.getUid());
        intent.putExtra("user", user);
        startActivity(intent);
    }
}

