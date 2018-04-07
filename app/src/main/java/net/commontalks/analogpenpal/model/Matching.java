package net.commontalks.analogpenpal.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

/**
 * Created by imhwan on 2018. 2. 13..
 */

@Data
public class Matching implements Serializable, Comparable<Matching>{

    private String oppositeUserId, matchingId;
    private Mail lastMail;
    private long lastMailDate;
    private MatchingStatus matchingStatus = MatchingStatus.ACTIVE;
    private User matchingOppositeUser;

    @Override
    public int compareTo(@NonNull Matching o) {
        return String.valueOf(this.lastMailDate).compareTo(String.valueOf(o.getLastMailDate()));
    }

    public enum MatchingStatus{
        ACTIVE, DISABLED
    }
}
