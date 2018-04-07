package net.commontalks.analogpenpal.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.commontalks.analogpenpal.R;
import net.commontalks.analogpenpal.model.Mail;
import net.commontalks.analogpenpal.model.Matching;
import net.commontalks.analogpenpal.model.User;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by imhwan on 2018. 2. 13..
 */

public class MailListAdapter extends RecyclerView.Adapter<MailListAdapter.ViewHolder> {

    private ArrayList<Matching> mMatchingList;
    private User mUser;
    private Context mContext;

    public MailListAdapter(Context context){
        mMatchingList = new ArrayList<>();
        mContext = context;
    }

    public void addItem(Matching item){
        mMatchingList.add(item);
        notifyDataSetChanged();
    }

    public void updateItem(Matching item){
        int position = getPosition(item);
        mMatchingList.set(position,item);
        notifyItemChanged(position);
    }

    public void sort(){
        Collections.sort(mMatchingList);
        Collections.reverse(mMatchingList);
    }


    public Matching getItem(int position) {
        return mMatchingList.get(position);
    }

    public int getPosition(Matching item){
        int count = 0;
        for (Matching matching: mMatchingList){
            if (matching.getMatchingId().equals(item.getMatchingId())){
                return count;
            }
            count++;
        }
        return -1;
    }


    public void removeItem(Matching item){
        mMatchingList.remove(item);
        notifyItemRemoved(getPosition(item));
    }

    @Override
    public MailListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mail_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MailListAdapter.ViewHolder holder, int position) {
        Matching matching = mMatchingList.get(position);
        View[] recieveViews = new View[] {holder.mailstampImage,
                holder.sendDateDay, holder.sendDateMonth, holder.sendDateYear};
        View[] sendViews = new View[] {
                holder.postOfficeImage, holder.postOfficeDate, holder.postBoxImage, holder.postBoxDate,
                holder.airplaneImage, holder.airplaneDate
        };


        if (matching.getLastMail().getUserId().equals(mUser.getUid())) {
            //내가 마지막메일을 보낸거면 홀더 설정을 해줍니다.
            //내가 보낸 메일
            for (View view : recieveViews) {
                view.setVisibility(View.GONE);
            }
            Glide.with(mContext)
                    .load(mUser.getProfileUrl())
                    .placeholder(R.drawable.icon_noprofile)
                    .into(holder.sendUserProfilePhoto);
            holder.sendUserProfilePhoto.setBackground(new ShapeDrawable(new OvalShape()));
            holder.sendUserProfilePhoto.setClipToOutline(true);
            holder.sendUserName.setText(mUser.getUsername());
            holder.sendUserNation.setText((mUser.getNation() != null)?mUser.getNation():"Unknown");


            Glide.with(mContext)
                    .load(matching.getMatchingOppositeUser().getProfileUrl())
                    .placeholder(R.drawable.icon_noprofile)
                    .into(holder.receiveUserProfilePhoto);
            holder.receiveUserProfilePhoto.setBackground(new ShapeDrawable(new OvalShape()));
            holder.receiveUserProfilePhoto.setClipToOutline(true);
            holder.receiveUserName.setText(matching.getMatchingOppositeUser().getUsername());
            holder.receiveUserNation.setText((matching.getMatchingOppositeUser().getNation() != null)
                    ? matching.getMatchingOppositeUser().getNation():"Unknown");



            long sendDate = matching.getLastMail().getSendDate();
            long currTime = System.currentTimeMillis();
            long timeGapHour = (currTime - sendDate)/ (1000*60*60) ;
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(1000); //You can manage the time of the blink with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);

            if (timeGapHour > 3 ){


            } else if (timeGapHour > 2 ) {

            } else if (timeGapHour > 1) {

            } else {
                holder.postOfficeImage.startAnimation(anim);
            }




        } else{
            //내가 받은것이라면 받은 상태에 따라 다른 홀더 설정을 해줍니다.
            //내가 받은 메일

            for (View view : sendViews){
                view.setVisibility(View.GONE);
            }
            Glide.with(mContext)
                    .load(mUser.getProfileUrl())
                    .placeholder(R.drawable.icon_noprofile)
                    .into(holder.receiveUserProfilePhoto);
            holder.receiveUserProfilePhoto.setBackground(new ShapeDrawable(new OvalShape()));
            holder.receiveUserProfilePhoto.setClipToOutline(true);
            holder.receiveUserName.setText(mUser.getUsername());
            holder.receiveUserNation.setText((mUser.getNation() != null)?mUser.getNation():"Unknown");

            //상대방
            Glide.with(mContext)
                    .load(matching.getMatchingOppositeUser().getProfileUrl())
                    .placeholder(R.drawable.icon_noprofile)
                    .into(holder.sendUserProfilePhoto);
            holder.sendUserProfilePhoto.setBackground(new ShapeDrawable(new OvalShape()));
            holder.sendUserProfilePhoto.setClipToOutline(true);
            holder.sendUserName.setText(matching.getMatchingOppositeUser().getUsername());
            holder.sendUserNation.setText(matching.getMatchingOppositeUser().getNation());


        }
    }

    @Override
    public int getItemCount() {
        return mMatchingList.size();
    }

    public void setUserUid(User user) {
        mUser = user;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mailItemLayout)
        ConstraintLayout mailItemLayout;

        //send User information
        @BindView(R.id.sendUserProfilePhoto)
        ImageView sendUserProfilePhoto;

        @BindView(R.id.fromTv)
        TextView fromTv;

        @BindView(R.id.sendUserName)
        TextView sendUserName;

        @BindView(R.id.sendUserNation)
        TextView sendUserNation;

        //receive user information
        @BindView(R.id.receiveUserProfilePhoto)
        ImageView receiveUserProfilePhoto;

        @BindView(R.id.toTv)
        TextView toTv;

        @BindView(R.id.receiveUserName)
        TextView receiveUserName;

        @BindView(R.id.receiveUserNation)
        TextView receiveUserNation;

        //mail stamp view
        @BindView(R.id.mailstampImage)
        ImageView mailstampImage;

        @BindView(R.id.sendDateDay)
        TextView sendDateDay;

        @BindView(R.id.sendDateMonth)
        TextView sendDateMonth;

        @BindView(R.id.sendDateYear)
        TextView sendDateYear;

        //mail sending views
        @BindView(R.id.postOfficeImage)
        ImageView postOfficeImage;

        @BindView(R.id.postOfficeDate)
        TextView postOfficeDate;

        @BindView(R.id.postBoxImage)
        ImageView postBoxImage;

        @BindView(R.id.postBoxDate)
        TextView postBoxDate;

        @BindView(R.id.airplaneImage)
        ImageView airplaneImage;

        @BindView(R.id.airplaneDate)
        TextView airplaneDate;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }

    private enum SendingStatus{
        FIRST, SENCOND, THIRD, SENDED
    }
}
