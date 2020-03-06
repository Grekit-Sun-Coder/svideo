package cn.weli.svideo.module.mine.model.bean;

import cn.weli.svideo.module.mine.model.UserModel;

/**
 * 用户信息bean
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-18
 * @see UserModel
 * @since [1.0.0]
 */
public class UserInfoBean {

    /**
     * 性别，1-男，2-女
     */
    public static final String SEX_MAN = "1";
    public static final String SEX_WOMAN = "2";

    /**
     * 黑名单用户  0-否，1-是
     */
    public static final int NOT_IN_BLACKLIST = 0;
    public static final int IN_BLACKLIST = 1;

    public String access_token;

    public String id;

    public String uid;

    public String open_uid;

    public String phone_no;

    public String nick_name;

    public String avatar;

    public String sex;

    public String invite_code;

    public long create_time;

    public long update_time;

    public int bind_invite_code;

    public BlacklistInfo blacklist_info;

    /**
     * 首次登录奖励金币
     */
    public long gold;

    public boolean hasBindInviteCode() {
        return bind_invite_code == 1;
    }

    public static class BlacklistInfo{

        public int blacklist;   //1-黑名单用户 0-否

        public String avatar_tip;
    }
}
