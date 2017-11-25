package com.smartiot.hamzajguerim.challenge.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hamzajguerim on 2017-11-25.
 */

public class User extends RealmObject {
    @PrimaryKey
    private int user_id;
    private String user_login;
    private String user_password;
    private String user_first_name;
    private String user_last_name;
}
