package net.commontalks.analogpenpal.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.BCrypt;
import net.commontalks.analogpenpal.model.Matching;
import net.commontalks.analogpenpal.model.User;
import net.commontalks.analogpenpal.view.MainDrawerActivity;
import net.commontalks.analogpenpal.view.StartActivity;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by imhwan on 2018. 2. 14..
 */

public class CustomDialog extends Dialog {

    private static final String TAG = "CustomDialog";
    private Context mContext;
    private View.OnClickListener mClickListener;
    private Matching mMatching;
    private View.OnClickListener mLeftListener;
    private View.OnClickListener mRightListener;
    private DialogType dialogType;

    @BindView(R.id.sendLetterConstLayout)
    ConstraintLayout sendLetterConstLayout;

    @BindView(R.id.yesBtn)
    TextView yesBtn;

    @BindView(R.id.noBtn)
    TextView noBtn;

    //login Dialog
    @BindView(R.id.loginLayout)
    ConstraintLayout loginLayout;

    @BindView(R.id.loginIdTv)
    TextView loginIdTv;

    @BindView(R.id.loginPwTv)
    TextView loginPwTv;

    @BindView(R.id.loginIdEt)
    EditText loginIdEt;

    @BindView(R.id.loginPwEt)
    EditText loginPwEt;

    @BindView(R.id.loginBtn)
    TextView loginBtn;

    //interests table dialog
    @BindView(R.id.interestsConstLayout)
    ConstraintLayout interestsConstLayout;
    @BindView(R.id.interestTableLayout)
    TableLayout interestTableLayout;
    @BindView(R.id.setInterestsLayout)
    ConstraintLayout setInterestsLayout;
    @BindView(R.id.interest1)
    TextView interest1;
    @BindView(R.id.interest2)
    TextView interest2;
    @BindView(R.id.interest3)
    TextView interest3;
    @BindView(R.id.interest4)
    TextView interest4;
    @BindView(R.id.interest5)
    TextView interest5;
    @BindView(R.id.interest6)
    TextView interest6;
    @BindView(R.id.interest7)
    TextView interest7;
    @BindView(R.id.interest8)
    TextView interest8;
    @BindView(R.id.interest9)
    TextView interest9;
    @BindView(R.id.interest10)
    TextView interest10;
    @BindView(R.id.interest11)
    TextView interest11;
    @BindView(R.id.interest12)
    TextView interest12;
    @BindView(R.id.interest13)
    TextView interest13;
    @BindView(R.id.interest14)
    TextView interest14;
    @BindView(R.id.interest15)
    TextView interest15;
    @BindView(R.id.interest16)
    TextView interest16;
    @BindView(R.id.interest17)
    TextView interest17;
    @BindView(R.id.interest18)
    TextView interest18;
    @BindView(R.id.interest19)
    TextView interest19;
    @BindView(R.id.interest20)
    TextView interest20;
    @BindView(R.id.interest21)
    TextView interest21;
    @BindView(R.id.settingCompleteBtn)
    TextView  settingCompleteBtn;
    private HashMap<TextView,Integer> mInterestsHashMap;
    public HashMap<TextView,Integer> mInterestsChoicesHashMap;


    public CustomDialog(Context context, Matching matching, View.OnClickListener clickListener,DialogType dialogType) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.mClickListener = clickListener;
        this.mMatching = matching;
        this.dialogType = dialogType;

    }

    public CustomDialog(Context context, View.OnClickListener clickListener,DialogType dialogType) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.mClickListener = clickListener;
        this.dialogType = dialogType;

    }


    public CustomDialog(Context context, View.OnClickListener leftListener, View.OnClickListener rightListener, DialogType dialogType) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.mLeftListener = leftListener;
        this.mRightListener = rightListener;
        this.dialogType = dialogType;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.activity_custom_dialog);
        ButterKnife.bind(this);


        switch (dialogType){
            case SENDLETTER:
                sendLetterConstLayout.setVisibility(View.VISIBLE);
                yesBtn.setOnClickListener(mLeftListener);
                noBtn.setOnClickListener(mRightListener);
                break;

            case SHOWLETTER:

                break;

            case LOGIN:
                loginLayout.setVisibility(View.VISIBLE);
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkPw();
                        CustomDialog.this.dismiss();
                    }
                });

                break;

            case SETINTEREST:
                setInterestsLayout.setVisibility(View.VISIBLE);
                settingCompleteBtn.setOnClickListener(mClickListener);
                setInterestsContentsView();
                break;
        }


    }

    private void checkPw(){
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean success = false;
                if (dataSnapshot.exists()){
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    while (dataSnapshotIterator.hasNext()){
                        User user = dataSnapshotIterator.next().getValue(User.class);
                        if (user.getLoginId().equals(loginIdEt.getText().toString())) {
                            if (BCrypt.checkpw(loginPwEt.getText().toString(), user.getPassword())){
                                Intent intent = new Intent(mContext, MainDrawerActivity.class);
                                intent.putExtra("user",user);
                                intent.putExtra("uid",user.getUid());
                                success = true;
                                mContext.startActivity(intent);

                                //sharedpreferences에 아이디랑 비밀번호를 저장해서 자동 로그인 구현
                                SharedPreferences sharedPreferences = mContext.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("loginId", user.getLoginId());
                                editor.putString("password", user.getPassword());
                                editor.putString("uid", user.getUid());
                                editor.apply();

                                ((StartActivity)mContext).finish();
                            } else {
                                Toast.makeText(mContext, "비밀번호를 확인해 주세요.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    if (!success) {
                        Toast.makeText(mContext, "아이디를 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public enum DialogType{
        SENDLETTER, SHOWLETTER, LOGIN, SETINTEREST
    }

    private void setInterestsContentsView() {
        mInterestsHashMap = new HashMap<>();
        mInterestsChoicesHashMap = new HashMap<>();
        Log.i(TAG, "setInterestsContentsView");
        mInterestsHashMap.put(interest1, R.string.movie_kr);
        mInterestsHashMap.put(interest2,R.string.hotplace_kr);
        mInterestsHashMap.put(interest3,R.string.photo_kr);
        mInterestsHashMap.put(interest4,(R.string.classic_kr));
        mInterestsHashMap.put(interest5,(R.string.travel_kr));
        mInterestsHashMap.put(interest6,(R.string.religion_kr));
        mInterestsHashMap.put(interest7,(R.string.sports_kr));
        mInterestsHashMap.put(interest8,(R.string.science_kr));
        mInterestsHashMap.put(interest9,(R.string.game_kr));
        mInterestsHashMap.put(interest10,(R.string.arts_kr));
        mInterestsHashMap.put(interest11,(R.string.drama_kr));
        mInterestsHashMap.put(interest12,(R.string.music_kr));
        mInterestsHashMap.put(interest13,(R.string.theater_kr));
        mInterestsHashMap.put(interest14,(R.string.electronics_kr));
        mInterestsHashMap.put(interest15,(R.string.poem_kr));
        mInterestsHashMap.put(interest16,(R.string.IT_kr));
        mInterestsHashMap.put(interest17,(R.string.fashion_kr));
        mInterestsHashMap.put(interest18,(R.string.fitness_kr));
        mInterestsHashMap.put(interest19,(R.string.beauty_kr));
        mInterestsHashMap.put(interest20,(R.string.novel_kr));
        mInterestsHashMap.put(interest21,(R.string.philosophy_kr));
        mInterestsChoicesHashMap = new HashMap<>();
        for (final TextView Tv : mInterestsHashMap.keySet()){
            Tv.setText(mInterestsHashMap.get(Tv));
            Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            if (mInterestsChoicesHashMap.containsKey(Tv)){
                mInterestsChoicesHashMap.put(Tv, mInterestsHashMap.get(Tv));
                Tv.setBackgroundResource(0);
            }
            Tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInterestsChoicesHashMap.containsKey(Tv)) {
                        mInterestsChoicesHashMap.remove(Tv);
                        Tv.setBackgroundResource(R.drawable.hotdog_button);
                    } else {
                        if (mInterestsChoicesHashMap.size() == 2) {
                            Toast.makeText(mContext, R.string.interestLimitSnackbar2_kr, Toast.LENGTH_LONG).show();
                            return;
                        }
                        mInterestsChoicesHashMap.put(Tv, mInterestsHashMap.get(Tv));
                        Tv.setBackgroundResource(R.drawable.button_hotdog_clicked);
                    }
                }
            });
        }
    }
}
