package net.commontalks.analogpenpal.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.commontalks.analogpenpal.CustomView.CustomDialog;
import net.commontalks.analogpenpal.CustomView.RecyclerViewItemClickListener;
import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.adapter.MailListAdapter;
import net.commontalks.analogpenpal.model.Mail;
import net.commontalks.analogpenpal.model.Matching;
import net.commontalks.analogpenpal.model.User;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.PorterDuff.Mode.ADD;
import static net.commontalks.analogpenpal.CustomView.CustomDialog.DialogType.SHOWLETTER;

public class MainDrawerActivity extends AppCompatActivity {

    private static String TAG = "MainDrawerActivity";

    @BindView(R.id.mainMailRecyclerView)
    RecyclerView mainMailRecyclerView;

    @BindView(R.id.drawerOpenBtn)
    ImageView drawerOpenBtn;

    @BindView(R.id.mainActivityToolbarTitleTxt)
    TextView mainActivityToolbarTitleTxt;

    @BindView(R.id.mainActivityToolbarTitleImage)
    ImageView mainActivityToolbarTitleImage;

    @BindView(R.id.writeNewMailBtn)
    ImageView writeNewMailBtn;

    ImageView NavigationimageView;
    TextView NavigationName;
    TextView NavigationNation;



    private LinearLayoutManager mLayoutManager;
    private MailListAdapter mMailListAdapter;
    private CustomDialog mCustomDialog;
    private Context mContext;
    private DatabaseReference mUserRef;
    private DatabaseReference mMatchingRef;
    public static User mUser;
    private String mUserId;
    private static int SENDING_TIME = 3 * 24 * 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        mContext = this;
        mLayoutManager = new LinearLayoutManager(mContext);
        mMailListAdapter = new MailListAdapter(mContext);
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
        mUserId = getIntent().getStringExtra("uid");
        Log.i(TAG, getIntent().getExtras().toString() );
        mUser = (User)getIntent().getSerializableExtra("user");
        setViews(navigationView);
        mMatchingRef = mUserRef.child(mUserId).child("matchings");
        mMailListAdapter.setUserUid(mUser);
        Log.i(TAG, mUserId);



        mUserRef.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (mUser.getUserTimeZoneOffset() != TimeZone.getDefault().getRawOffset()) {
                        mUser.setUserTimeZoneOffset(TimeZone.getDefault().getRawOffset());
                        dataSnapshot.child("userTimeZoneOffset").getRef().setValue(TimeZone.getDefault().getRawOffset());
                    }
                    dataSnapshot.child("lastLogin").getRef().setValue(mUser.getLastLogin());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        setToolbar(drawer);

        mainMailRecyclerView.setLayoutManager(mLayoutManager);
        mainMailRecyclerView.setAdapter(mMailListAdapter);
        mainMailRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Matching matching = mMailListAdapter.getItem(position);
                //1. 만약 나에게 온 메일이라면 팝업으로 띄워줍니다.
                if (!matching.getLastMail().getUserId().equals(mUserId)){
                    //팝업으로 띄워줍니다.
                    Intent intent = new Intent(mContext, ReadMailActivity.class);
                    intent.putExtra("user",mUser);
                    intent.putExtra("matching", matching);
                    startActivity(intent);
                }
            }
        }));
        setLetterListener();

    }

    private void setViews(NavigationView navigationView) {

        NavigationimageView = navigationView.findViewById(R.id.NavigationimageView);
        NavigationName = navigationView.findViewById(R.id.NavigationName);
        NavigationNation = navigationView.findViewById(R.id.NavigationNation);
        Glide.with(mContext)
                    .load(mUser.getProfileUrl())
                    .placeholder(R.drawable.icon_noprofile)
                    .into(NavigationimageView);
        NavigationimageView.setBackground(new ShapeDrawable(new OvalShape()));
        NavigationimageView.setClipToOutline(true);
        NavigationName.setText(mUser.getUsername());
        NavigationNation.setText(mUser.getNation());

        navigationView.findViewById(R.id.settingLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingActivity.class);
                intent.putExtra("user",mUser);
                startActivity(intent);
            }
        });

        navigationView.findViewById(R.id.letterBoxLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LetterBoxActivity.class);
                startActivity(intent);
            }
        });

        navigationView.findViewById(R.id.upgradeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpgradeActivity.class);
                startActivity(intent);
            }
        });

        navigationView.findViewById(R.id.LogoutLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences loginInfo = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                if (loginInfo.contains("loginId")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("loginId");
                    editor.remove("uid");
                    editor.remove("password");
                    editor.apply();


                    Intent intent = new Intent(mContext, StartActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    private void setLetterListener() {
        mMatchingRef.addChildEventListener(matchingEventListener);
    }



    private void updateUI(DataSnapshot dataSnapshot, UIUpdateType uiUpdateType ) {
        Matching matching;
        if (dataSnapshot.exists()) {
            matching = dataSnapshot.getValue(Matching.class);
            Log.i(TAG,matching.toString());
        } else {
            return;
        }

        if (matching.getLastMail().getMailType() == Mail.MailType.RESPOND
                && !matching.getLastMail().getUserId().equals(mUser.getUid())) {
            long standardMailSendDate = matching.getLastMail().getSendDate() + matching.getLastMail().getSendUserOffset();
            long standardUserDate = System.currentTimeMillis() + mUser.getUserTimeZoneOffset();
            long mailSend = (standardMailSendDate - standardUserDate) / (1000*60*60);
            Log.i(TAG,"nowTime : " + System.currentTimeMillis());
            Log.i(TAG,"standardMailSendDate : " + standardMailSendDate+ "/ standardUserDate : " + standardUserDate + "/ mailSend : " + mailSend);
            if(mailSend < 3) {
                //3일이상 지났으면 메일을 보여줍니다.
                return;
            }
        }


        switch (uiUpdateType) {
            case ADD:
                //첫 메일이 왔을때
                Log.i(TAG, "childAdd");
                mMailListAdapter.addItem(matching);
                break;

            case UPDATE:
                //해당되는 친구에게 애니메이션을 설정해줍니다
                Log.i(TAG, "childUpdate");
                mMailListAdapter.updateItem(matching);
                //답장이 오거나 메일을 답장을 보냈을때 업데이트 됨.
                break;
        }
    }

    public enum UIUpdateType{
        ADD, UPDATE, REMOVE
    }


    private void setToolbar(final DrawerLayout drawer){
        //드로어 오픈은 세팅해주고
        drawerOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        //메일쓰기 버튼 추가. 처음보는 사람들에게 보내는 것이므로 extra 추가 없이 보내줍니다.
        writeNewMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WriteMailActivity.class);
                startActivity(intent);
            }
        });
        mainActivityToolbarTitleTxt.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private ChildEventListener matchingEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.i(TAG, "onChildAdded");
            updateUI(dataSnapshot,UIUpdateType.ADD);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            updateUI(dataSnapshot,UIUpdateType.UPDATE);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            updateUI(dataSnapshot,UIUpdateType.REMOVE);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
