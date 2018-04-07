package net.commontalks.analogpenpal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.commontalks.analogpenpal.CustomView.CustomDialog;
import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.Mail;
import net.commontalks.analogpenpal.model.Matching;
import net.commontalks.analogpenpal.model.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static net.commontalks.analogpenpal.CustomView.CustomDialog.DialogType.SENDLETTER;
import static net.commontalks.analogpenpal.CustomView.CustomDialog.DialogType.SETINTEREST;

public class WriteMailActivity extends AppCompatActivity {

    private static final String TAG = "WriteMailActivity";
    private DatabaseReference mMatchingRef;
    private DatabaseReference mUserRef;
    private String mMatchingId;
    private User mUser;
    private Mail.MailType mMailType;
    private String mOppositeUserId;
    private FirebaseDatabase mFirebaseDb;
    private Boolean mInitialRespond = false;

    private Matching matching;
    private Mail mail;
    private ArrayList<String> mInterestUser;
    private ArrayList<String> mMessageUser;
    private Context mContext;
    private CustomDialog mCustomDialog;
    private ArrayList<String> mInterestsList;
    private User mOppositeUser;

    @BindView(R.id.drawerBackBtn)
    ImageView drawerBackBtn;

    @BindView(R.id.sendLetterBtn)
    TextView sendLetterBtn;

    @BindView(R.id.writeLetterEt)
    TextView writeLetterEt;

    @BindView(R.id.setInterestsBtn)
    TextView setInterestsBtn;

    @BindView(R.id.writeNewMailUserName)
    TextView writeNewMailUserName;

    @BindView(R.id.writeNewMailUserNation)
    TextView writeNewMailUserNation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_mail);

        ButterKnife.bind(this);
        mUser = MainDrawerActivity.mUser;
        mFirebaseDb = FirebaseDatabase.getInstance();
        mContext = this;

        mMatchingRef = mFirebaseDb.getReference("mailLists");
        mUserRef = mFirebaseDb.getReference("users");
        mail = new Mail();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("respond")) {
                //답장일 경우
                mOppositeUserId = bundle.getString("oppositeUserId");
                if (bundle.containsKey("initialResponse")) {
                    //첫 대답일 경우에는 mailLists를 추가해줘야함.
                    mInitialRespond = true;
                }
                    mMatchingId = bundle.getString("matchingId");
                mMailType = Mail.MailType.RESPOND;
                setInterestsBtn.setVisibility(View.GONE);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) sendLetterBtn
                        .getLayoutParams();
                mlp.setMargins(0, 30, 0, 20);
                sendLetterBtn.setLayoutParams(mlp);
            }
        } else {
            //낯선 사람들에게 보내는 경우
            mMailType = Mail.MailType.INITIAL;
            writeNewMailUserName.setText("???");
            writeNewMailUserNation.setText("???");
        }
        mInterestsList = new ArrayList<>();
    }

    private String mailId;
    private String matchingId;

    @OnClick(R.id.setInterestsBtn)
    public void setOnClikcInterests(){
        View.OnClickListener leftListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomDialog.mInterestsChoicesHashMap.size() != 2) {
                    Toast.makeText(mContext, "관심사는 두개를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (View interest: mCustomDialog.mInterestsChoicesHashMap.keySet()){
                    Log.i(TAG,interest.getTag().toString());
                    mInterestsList.add(interest.getTag().toString());
                }
                mCustomDialog.dismiss();
            }
        };

        mCustomDialog = new CustomDialog(mContext, leftListener, SETINTEREST);
        mCustomDialog.show();
    }

    @OnClick(R.id.drawerBackBtn)
    public void onClickDrawerBackBtn(){
        onBackPressed();
    }

    @OnClick(R.id.sendLetterBtn)
    public void onClickSendLetterBtn(){

        if (mMailType== Mail.MailType.INITIAL
                && mInterestsList.size() != 2) {
            Toast.makeText(mContext, "관심사를 설정해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (writeLetterEt.getText().toString().isEmpty()) {
            Toast.makeText(mContext, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        mCustomDialog = new CustomDialog(mContext,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMailBtn();
                        mCustomDialog.dismiss();
                    }
                },
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCustomDialog.dismiss();
                    }
                },
                SENDLETTER);
        mCustomDialog.show();
    }


    public void sendMailBtn(){
        mail.setSendDate(System.currentTimeMillis() - mUser.getUserTimeZoneOffset());
        mail.setTitle("");
        mail.setTxt(writeLetterEt.getText().toString());
        mail.setUserId(mUser.getUid());
        mail.setSendUserOffset(mUser.getUserTimeZoneOffset());

        switch (mMailType){
            case INITIAL:
                mail.setMailType(mMailType);
                mInterestUser = new ArrayList<>();
                mMessageUser = new ArrayList<>();
                //초기 메세지를 추가해주는 프로세스
                //1. 관심사가 매치되는 사람들을 검색
                //2. 그 중에 일부를 랜덤 추출
                //3. 그들에게 메세지를 추가해주고
                //4. 끝내준다.

                mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                User user = iterator.next().getValue(User.class);
                                if (user.getInterests().contains(mInterestsList.get(0))
                                        || user.getInterests().contains(mInterestsList.get(1))) {
                                    //마지막 접속일 기준 15일 이하인 사람만
                                    if (!user.getUid().equals(mUser.getUid())){
                                        mInterestUser.add(user.getUid());
                                    }
                                }
                            }

                            if (mInterestUser.size() > 10) {
                                int a[] = new int[10];
                                Random r = new Random();
                                for(int i =0; i<=9; i++) {
                                    a[i]=r.nextInt(mInterestUser.size());
                                    for(int j=0;j<i;j++) {
                                        if(a[i]==a[j]) {
                                            i--;
                                        }
                                    }
                                }
                                for (int i: a){
                                    mMessageUser.add(mInterestUser.get(i));
                                }
                            } else{
                                for (String userId: mInterestUser){
                                    mMessageUser.add(userId);
                                }
                            }
                            matching = new Matching();
                            matching.setOppositeUserId(mUser.getUid());
                            matching.setMatchingOppositeUser(mUser);

                            //메일을 받은 사람한테 먼저 매칭만 추가해주고, 추후에 첫 답장일 경우에 본인한테도 매칭을 추가시켜줍니다.
                            for (String uid: mMessageUser){
                                matchingId = mUserRef.child(uid).child("matchings").push().getKey();
                                matching.setLastMailDate(mail.getSendDate());
                                matching.setMatchingId(matchingId);
                                matching.setLastMail(mail);
                                mUserRef.child(uid).child("matchings").child(matchingId).setValue(matching);
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;


            case RESPOND:
                mMatchingRef = mMatchingRef.child(mMatchingId);
                mail.setMailType(mMailType);
                if (mInitialRespond) {
                    mUserRef
                            .child(mUser.getUid())
                            .child("matchings")
                            .child(mMatchingId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                //1. 첫 메일을 mailLists/{matching_id}/{mail_id}에 등록
                                matching = dataSnapshot.getValue(Matching.class);
                                mOppositeUser = matching.getMatchingOppositeUser();
                                mailId = mMatchingRef.push().getKey();
                                mMatchingRef
                                        .child(mailId)
                                        .setValue(matching.getLastMail())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //2. 답장메일을 각 유저의 matchings와 mailList에 등
                                            mailId = mMatchingRef.push().getKey();
                                            mail.setId(mailId);
                                            matching.setLastMail(mail);
                                            ArrayList<String> uidList = new ArrayList<>();
                                            uidList.add(mOppositeUserId);
                                            uidList.add(mUser.getUid());
                                            boolean lastUpdate = false;

                                            for (String userId : uidList){
                                                if (lastUpdate) {
                                                    mMatchingRef.child(mailId).setValue(mail);
                                                }
                                                mUserRef.child(userId).child("matchings").child(mMatchingId)
                                                        .child("lastMail").setValue(mail);
                                                if (userId.equals(mUser.getUid())) {
                                                    mUserRef
                                                            .child(userId)
                                                            .child(matching.getMatchingId())
                                                            .child("lastMailDate")
                                                            .setValue(mail.getSendDate());
                                                } else {
                                                    matching.setOppositeUserId(mUser.getUid());
                                                    matching.setMatchingOppositeUser(mUser);
                                                    matching.setLastMailDate(mail.getSendDate());
                                                    mUserRef.child(userId).child("matchings").child(matching.getMatchingId()).setValue(matching);
                                                }
                                                lastUpdate = true;
                                            }

                                            finish();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    mailId = mMatchingRef.push().getKey();
                    mail.setId(mailId);
                    mMatchingRef.child(mailId).setValue(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ArrayList<String> uidList = new ArrayList<>();
                                uidList.add(mOppositeUserId);
                                uidList.add(mUser.getUid());

                                for (String userId : uidList){
                                    mUserRef.child(userId).child("matchings").child(mMatchingId)
                                            .child("lastMail").setValue(mail);
                                    mUserRef.child(userId).child("matchings").child(mMatchingId)
                                            .child("lastMailDate").setValue(mail.getSendDate());
                                }

                                finish();
                            }
                        }
                    });
                }
                break;
        }
    }
}
