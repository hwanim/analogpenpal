package net.commontalks.analogpenpal.model;

import lombok.Data;

/**
 * Created by imhwan on 2018. 2. 21..
 */

@Data
public class LaguageSelection {

    private WhichLanguage whichLanguage;

    

    public enum WhichLanguage{
        KR, EN
    }



}
