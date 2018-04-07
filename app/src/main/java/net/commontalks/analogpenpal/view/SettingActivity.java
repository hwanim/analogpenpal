package net.commontalks.analogpenpal.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.commontalks.analogpenpal.view.MainDrawerActivity.mUser;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.settingProfileImage)
    ImageView settingProfileImage;

    @BindView(R.id.settingNameTv)
    TextView settingNameTv;

    @BindView(R.id.settingNationTv)
    TextView settingNationTv;

    @BindView(R.id.settingNotificationSwitch)
    Switch settingNotificationSwitch;

    @BindView(R.id.termsOfUseBtn)
    ImageView termsOfUseBtn;

    @BindView(R.id.sendRequestBtn)
    ImageView sendRequestBtn;

    @BindView(R.id.makersBtn)
    ImageView makersBtn;

    @BindView(R.id.drawerOpenBtn)
    ImageView drawerOpenBtn;

    @BindView(R.id.writeNewMailBtn)
    ImageView writeNewMailBtn;

    private static User mUser;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mContext = this;
        mUser = (User)getIntent().getSerializableExtra("user");
        Glide.with(mContext)
                .load(mUser.getProfileUrl())
                .placeholder(R.drawable.icon_noprofile)
                .into(settingProfileImage);
        settingProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        settingProfileImage.setClipToOutline(true);
        settingNameTv.setText(mUser.getUsername());
        settingNationTv.setText(mUser.getNation());
        drawerOpenBtn.setVisibility(View.GONE);
        writeNewMailBtn.setVisibility(View.GONE);

    }


    @OnClick(R.id.sendRequestBtn)
    public void onClickSendFeedbackBtn(){
        Intent intent = new Intent(this, SuggestFeedbackActivity.class);
        intent.putExtra("user", mUser);
        startActivity(intent);
    }


    @OnClick(R.id.makersBtn)
    public void onClickMakersBtn(){
        Intent intent = new Intent(this, MakersActivity.class);
        startActivity(intent);
    }


}
