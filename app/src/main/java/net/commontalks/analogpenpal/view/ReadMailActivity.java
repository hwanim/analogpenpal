package net.commontalks.analogpenpal.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.Mail;
import net.commontalks.analogpenpal.model.Matching;
import net.commontalks.analogpenpal.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadMailActivity extends AppCompatActivity {

    private static final String TAG = "ReadMailActivity";
    @BindView(R.id.drawerBackBtn)
    ImageView drawerBackBtn;

    @BindView(R.id.readNewMailUserPhoto)
    ImageView readNewMailUserPhoto;

    @BindView(R.id.readNewMailUserName)
    TextView readNewMailUserName;

    @BindView(R.id.readNewMailUserNation)
    TextView readNewMailUserNation;

    @BindView(R.id.readLetterTv)
    TextView readLetterTv;

    @BindView(R.id.replyBtn)
    TextView replyBtn;

    private User mUser;
    private Matching mMatching;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        ButterKnife.bind(this);

        if (getIntent().getExtras().containsKey("user") && getIntent().getExtras().containsKey("matching")) {
            mUser = (User)getIntent().getSerializableExtra("user");
            mMatching = (Matching)getIntent().getSerializableExtra("matching");
        } else {
            finish();
        }


        mContext = this;

        Glide.with(mContext)
                .load(mMatching.getMatchingOppositeUser().getProfileUrl())
//                .placeholder(R.drawable.icon_noprofile)
                .into(readNewMailUserPhoto);
        readNewMailUserPhoto.setBackground(new ShapeDrawable(new OvalShape()));
        readNewMailUserPhoto.setClipToOutline(true);

        readNewMailUserName.setText(mMatching.getMatchingOppositeUser().getUsername());
        readNewMailUserNation.setText(mMatching.getMatchingOppositeUser().getNation());
        readLetterTv.setText(mMatching.getLastMail().getTxt());
    }


    @OnClick(R.id.drawerBackBtn)
    public void onClikcBackBtn(){
        onBackPressed();
    }

    @OnClick(R.id.replyBtn)
    public void onClickReplyBtn(){
        //적절한 정보를 담아서 write mail activity로 넘겨줍니다.
        Intent intent = new Intent(this, WriteMailActivity.class);
        intent.putExtra("respond",true);
        if (mMatching.getLastMail().getMailType()== Mail.MailType.INITIAL){
            intent.putExtra("initialResponse",true);
        }
        intent.putExtra("oppositeUserId", mMatching.getMatchingOppositeUser().getUid());
        intent.putExtra("matchingId",mMatching.getMatchingId());
        Log.i(TAG, intent.getExtras().toString());
        startActivity(intent);
    }

}
