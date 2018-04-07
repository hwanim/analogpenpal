package net.commontalks.analogpenpal.view.fragment;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.view.RegisterActivity;

import java.lang.reflect.Array;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by imhwan on 2018. 2. 14..
 */

public class register3 extends android.support.v4.app.Fragment {

    @BindView(R.id.signInBtn)
    TextView signInBtn;

    @BindView(R.id.interestTv)
    TextView interestTv;

    @BindView(R.id.interestTableLayout)
    TableLayout interestTableLayout;

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


    private static String TAG = "RegisterActivity";

    private HashMap<TextView,Integer> mInterestsHashMap;
    public HashMap<TextView,Integer> mInterestsChoicesHashMap;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterestsHashMap = new HashMap<>();
        mInterestsChoicesHashMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ConstraintLayout constraintLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_register3, container,false);

        ButterKnife.bind(this, constraintLayout);

        setInterestsContentsView();
        return constraintLayout;
    }

    private void setInterestsContentsView() {
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
                        if (mInterestsChoicesHashMap.size() == 3) {
                            Snackbar.make(getView(), R.string.interestLimitSnackbar_kr, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        mInterestsChoicesHashMap.put(Tv, mInterestsHashMap.get(Tv));
                        Tv.setBackgroundResource(R.drawable.button_hotdog_clicked);
                    }
                }
            });
        }
    }


    @OnClick(R.id.signInBtn)
    public void onClickSignInBtn(){
        if (mInterestsChoicesHashMap.size() != 3) {
            Snackbar.make(getView(), R.string.interestSnackbar_kr, Snackbar.LENGTH_LONG).show();
            return;
        }
        ((RegisterActivity)getActivity()).registerUser();

    }

    private void clickOff(View v){
     v.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

         }
     });
    }



}
