package net.commontalks.analogpenpal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TimeZone;

import lombok.Data;

/**
 * Created by imhwan on 2018. 2. 13..
 */
@Data
public class User implements Serializable {

    private String uid,loginId,password, profileUrl, sex, username, nation;
    private int age, userTimeZoneOffset;
    private ArrayList<String> interests;
    private long lastLogin;
    private LaguageSelection.WhichLanguage whichLanguage;
}
