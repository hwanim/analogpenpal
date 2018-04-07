package net.commontalks.analogpenpal.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import lombok.Data;

/**
 * Created by imhwan on 2018. 2. 13..
 */
@Data
public class Mail implements Serializable {
    private String id, title, txt, userId, sendUserTimezone;
    private Long sendDate;
    private MailType mailType;
    private int sendUserOffset;

    public enum MailType{
        INITIAL, RESPOND
    }

    private ArrayList<String> photoList;
}
