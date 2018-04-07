package net.commontalks.analogpenpal.view;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.User;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuggestFeedbackActivity extends AppCompatActivity {

    private static String DOMAIN = "sandbox38def3c3373341ccacfe75fa4c858959.mailgun.org";
    private static String API_KEY = "key-aa5f6c3a16e50f7cd808976e819b4812";
    private Configuration mConfiguration;


    @BindView(R.id.suggestFeedbackConstraintLayout)
    ConstraintLayout suggestFeedbackConstraintLayout;

    @BindView(R.id.suggestFeedback)
    TextView suggestFeedback;

    @BindView(R.id.suggestFeedbackEt1)
    EditText suggestFeedbackEt1;

    private static User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_feedback);
        ButterKnife.bind(this);

        if (getIntent()!= null) {
            mUser =(User) getIntent().getSerializableExtra("user");

        }

    }


    @OnClick(R.id.suggestSendBtn)
    public void onClickSuggestBtn(){
        if (suggestFeedbackEt1.getText().equals("")){
            return;
        }
        mConfiguration = new Configuration()
                .domain(DOMAIN)
                .apiKey(API_KEY)
                .from("ludus2018@gmail.com");

        Thread thread = new Thread() {
            @Override
            public void run() {
                Mail.using(mConfiguration)
                        .to("ludus2018@gmail.com")
                        .subject( mUser.getUsername() + " 님의 개선의견 전달")
                        .text("- User Info \n" + mUser.toString() + "\n\n\n" +
                                "- 내용 \n " + suggestFeedbackEt1.getText().toString())
                        .build()
                        .send();
                Snackbar.make(suggestFeedbackConstraintLayout, "의견이 접수되었습니다. 감사합니다.", Snackbar.LENGTH_LONG).show();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        thread.start();
    }
}
