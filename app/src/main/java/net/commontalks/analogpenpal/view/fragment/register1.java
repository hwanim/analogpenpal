package net.commontalks.analogpenpal.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.commontalks.analogpenpal.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by imhwan on 2018. 2. 14..
 */

public class register1 extends android.support.v4.app.Fragment {

    private static String TAG = "RegisterActivity";

    @BindView(R.id.idTv)
    TextView idTv;

    @BindView(R.id.idEt)
    public EditText idEt;

    @BindView(R.id.pwTv)
    TextView pwTv;

    @BindView(R.id.pwEt)
    public EditText pwEt;

    @BindView(R.id.comfirmPwTv)
    TextView comfirmPwTv;

    @BindView(R.id.comfirmPwEt)
    public EditText comfirmPwEt;

    @BindView(R.id.nameTv)
    TextView nameTv;

    @BindView(R.id.nameEt)
    public EditText nameEt;

    @BindView(R.id.ageTv)
    TextView ageTv;

    @BindView(R.id.ageEt)
    public EditText ageEt;

    @BindView(R.id.sexTv)
    TextView sexTv;

    @BindView(R.id.maleTv)
    TextView maleTv;

    @BindView(R.id.femaleTv)
    TextView femaleTv;

    @BindView(R.id.nationSpinner)
    public Spinner nationSpinner;

    public boolean maleClicked = false;
    public boolean femaleClicked = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Register1 Fragment onCreate");
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout constraintLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_register1, container,false);
        ButterKnife.bind(this, constraintLayout);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getAllLocale());
        nationSpinner.setAdapter(adapter);
        return constraintLayout;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Register1 Fragment onDestroy");
        super.onDestroy();
    }

    @OnClick(R.id.maleTv)
    public void onClickMaletv(){
        maleClicked = true;
        maleTv.setBackgroundResource(R.drawable.button_hotdog_clicked);
        if (femaleClicked){
            femaleTv.setBackgroundResource(R.drawable.hotdog_button);
            femaleClicked = false;
        }
    }

    @OnClick(R.id.femaleTv)
    public void onClickFemaletv(){
        femaleClicked = true;
        femaleTv.setBackgroundResource(R.drawable.button_hotdog_clicked);
        if (maleClicked){
            maleTv.setBackgroundResource(R.drawable.hotdog_button);
            maleClicked = false;
        }
    }


    private ArrayList<String> getAllLocale(){
        String[] locales = Locale.getISOCountries();
        Log.i(TAG, Arrays.toString(locales));
        ArrayList<String> localcountries=new ArrayList<String>();
        for(String countryCode: locales)
        {
            Locale obj = new Locale("", countryCode);
            localcountries.add(obj.getDisplayCountry());
//            System.out.println("Country Code = " + obj.getCountry()
//                    + ", Country Name = " + obj.getDisplayCountry());
        }
        return localcountries;
    }
}
