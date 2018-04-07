package net.commontalks.analogpenpal.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.commontalks.analogpenpal.CustomView.CustomDialog;
import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.BCrypt;
import net.commontalks.analogpenpal.model.User;

import java.util.ArrayList;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    private static String TAG = "StartActivity";

    @BindView(R.id.startSignInBtn)
    TextView startSignInBtn;

    @BindView(R.id.koreanBtn)
    TextView koreanBtn;

    @BindView(R.id.englishBtn)
    TextView englishBtn;

    @BindView(R.id.startLogInBtn)
    TextView startLogInBtn;

    @BindView(R.id.loadingLayout)
    FrameLayout loadingLayout;

    @BindView(R.id.startActivityToolbar)
    Toolbar startActivityToolbar;


    private boolean mKoreanLanguage = true;
    private CustomDialog mCustomDialog;
    private Context mContext;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "activity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mContext = this;
        ButterKnife.bind(this);

        SharedPreferences loginInfo = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        //만약에 로그인 정보가 있으면 바로 앱으로 접속시켜줍니다.
        if (loginInfo.contains("loginId")) {
            mUid = loginInfo.getString("uid", "");
            FirebaseDatabase.getInstance().getReference("users").child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        User user = dataSnapshot.getValue(User.class);

                        Intent intent = new Intent(mContext, MainDrawerActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("uid", mUid);
                        startActivity(intent);

                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("uid", user.getUid());
                        editor.putString("password", user.getPassword());
                        editor.putString("loginId", user.getLoginId());
                        editor.apply();
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            loadingLayout.setVisibility(View.GONE);
            startActivityToolbar.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.startSignInBtn)
    public void onClickAppStart(){
        Log.i(TAG, "onClickAppStart");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.startLogInBtn)
    public void onClickStartLogin(){
        mCustomDialog = new CustomDialog(mContext,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                },
                CustomDialog.DialogType.LOGIN
        );
        mCustomDialog.show();
    }

    @OnClick(R.id.englishBtn)
    public void onClickEnglishBtn(){
        if (mKoreanLanguage){
            mKoreanLanguage = false;
            englishBtn.setBackgroundResource(R.drawable.button_hotdog_cliked_language);
            koreanBtn.setBackgroundResource(R.drawable.button_hotdog_language);
            startLogInBtn.setText(R.string.login_en);
            startSignInBtn.setText(R.string.signin_en);
        }
    }

    @OnClick(R.id.koreanBtn)
    public void onClickKoreanBtn(){
        if (!mKoreanLanguage) {
            mKoreanLanguage = true;
            koreanBtn.setBackgroundResource(R.drawable.button_hotdog_cliked_language);
            englishBtn.setBackgroundResource(R.drawable.button_hotdog_language);
            startLogInBtn.setText(R.string.login_kr);
            startSignInBtn.setText(R.string.signin_kr);
        }
    }



    private void getCal(){
        String[]TZ = TimeZone.getAvailableIDs();
        ArrayList<String> TZ1 = new ArrayList<>();
        for(int i = 0; i < TZ.length; i++) {
            if(!(TZ1.contains(TimeZone.getTimeZone(TZ[i]).getDisplayName()))) {
                TZ1.add(TimeZone.getTimeZone(TZ[i]).getDisplayName());
            }
        }
    }
}
