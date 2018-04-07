package net.commontalks.analogpenpal.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.commontalks.analogpenpal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by imhwan on 2018. 2. 14..
 */

public class register2 extends android.support.v4.app.Fragment {


    private boolean maleClicked = false;
    private boolean femaleClicked = false;
    private static String TAG = "RegisterActivity";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ConstraintLayout constraintLayout = (ConstraintLayout)inflater.inflate(R.layout.fragment_register2, container,false);
        ButterKnife.bind(this, constraintLayout);

        return constraintLayout;
    }




}
