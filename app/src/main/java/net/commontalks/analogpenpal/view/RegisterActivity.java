package net.commontalks.analogpenpal.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.BCrypt;
import net.commontalks.analogpenpal.model.User;
import net.commontalks.analogpenpal.view.fragment.register1;
import net.commontalks.analogpenpal.view.fragment.register2;
import net.commontalks.analogpenpal.view.fragment.register3;
import net.commontalks.analogpenpal.view.fragment.register4;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import butterknife.OnClick;

public class RegisterActivity extends FragmentActivity {

    private DatabaseReference mUserRef;
    private int MAX_PAGES = 2;
    private DotsIndicator mDotsIndicator;
    private ViewPager mViewPager;
    Fragment mFragment = new Fragment();
    private static String TAG = "RegisterActivity";
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "activity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUserRef = FirebaseDatabase.getInstance().getReference("users");

        mViewPager = (ViewPager) findViewById(R.id.registerViewPager);
        mDotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
        mViewPager.setAdapter(new registerAdapter(getSupportFragmentManager()));
        mDotsIndicator.setViewPager(mViewPager);
        mContext = this;

    }

    private class registerAdapter extends FragmentPagerAdapter {
        public registerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, mFragment.toString());
            if (position<0 || MAX_PAGES < position) {
                return null;
            }
            switch (position){
                case 0:
                    mFragment = new register1();
                    Log.i(TAG, "register1");
                    break;

                case 1:
                    mFragment = new register3();
                    Log.i(TAG, getSupportFragmentManager().getFragments().toString());
                    Log.i(TAG, "register3");
                    break;


            }
            return mFragment;
        }

        @Override
        public int getCount() {
            return MAX_PAGES;
        }


    }

    public void registerUser() {
        String id = null, pw = null, name = null, sex = "-1", nation = null;
        int age = 0;
        ArrayList<String> interestArray = new ArrayList<>();
        Log.i(TAG, getSupportFragmentManager().getFragments().toString());
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof register1){
                Log.i(TAG, "fragment1");
                id = ((register1) fragment).idEt.getText().toString();
                pw = ((register1) fragment).pwEt.getText().toString();
                nation = ((register1)fragment).nationSpinner.getSelectedItem().toString();
                if (nation.isEmpty()) {
                    Snackbar.make(mFragment.getView(), "빈 작성란을 채워주세요.",Snackbar.LENGTH_LONG).show();
                    return;
                }
                String agestr = ((register1) fragment).ageEt.getText().toString();
                if (!agestr.isEmpty())  {
                    age = Integer.parseInt(agestr);
                }
                name = ((register1) fragment).nameEt.getText().toString();
                if (((register1) fragment).femaleClicked && !((register1) fragment).maleClicked) {
                    sex ="0" ;
                } else if  (!((register1) fragment).femaleClicked && ((register1) fragment).maleClicked){
                    sex ="1";
                }
                String pwConfirm = ((register1) fragment).comfirmPwEt.getText().toString();

                if (id.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty() || agestr.isEmpty() || name.isEmpty() || sex.equals("-1") ) {
                    Snackbar.make(mFragment.getView(), "빈 작성란을 채워주세요.",Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!pw.equals(pwConfirm)) {
                    Snackbar.make(mFragment.getView(), "비밀번호가 일치하지 않습니다.",Snackbar.LENGTH_LONG).show();
                    return;
                }

            } else if (fragment instanceof register3) {
                Log.i(TAG, "fragment3" + fragment.getId());
                int count = 0;
                for (TextView tv : ((register3) fragment).mInterestsChoicesHashMap.keySet() ) {
                    interestArray.add(count, (String) tv.getTag());
                    count++;
                }
                Log.i(TAG, "fragment3 " + interestArray.toString() );
            }
        }
        String userId = mUserRef.push().getKey();
        Log.i(TAG, "push key : " + userId  );

        //hash 암호화
        String hashpw = BCrypt.hashpw(pw, BCrypt.gensalt(4));
        Log.i(TAG, "hashed pw : " + hashpw  );
        Log.i(TAG, "password check1. true : " + String.valueOf(BCrypt.checkpw(pw, hashpw)));
        Log.i(TAG, "password check2. false : " + String.valueOf(BCrypt.checkpw("1", hashpw)));


        final User user = new User();
        user.setUid(userId);
        user.setLastLogin(System.currentTimeMillis());
        user.setAge(age);
        user.setSex(sex);
        user.setPassword(hashpw);
        user.setUsername(name);
        user.setLoginId(id);
        user.setInterests(interestArray);
        user.setNation(nation);
        user.setUserTimeZoneOffset(TimeZone.getDefault().getRawOffset());
        mUserRef.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(mContext, ProfilePhotoRegisterActivity.class);
                    intent.putExtra("uid",user.getUid());
                    intent.putExtra("user", user);

                    startActivity(intent);
                    finish();
                }
            }
        });
//
        //sharedpreferences에 아이디랑 비밀번호를 저장해서 자동 로그인 구현
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", userId);
        editor.putString("password", pw);
        editor.putString("loginId", id);
        editor.apply();
    }

}