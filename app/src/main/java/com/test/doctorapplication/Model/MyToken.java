package com.test.doctorapplication.Model;

import com.test.doctorapplication.Common.Common;

public class MyToken {
    private String token, userName;
    private Common.TOKEN_TYPE tokenType;

    public MyToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user) {
        this.userName = user;
    }

    public Common.TOKEN_TYPE getTokenType() {
        return tokenType;
    }

    public void setTokenType(Common.TOKEN_TYPE tokenType) {
        this.tokenType = tokenType;
    }
}
