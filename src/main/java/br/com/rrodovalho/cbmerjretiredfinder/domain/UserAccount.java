package br.com.rrodovalho.cbmerjretiredfinder.domain;

/**
 * Created by rrodovalho on 25/03/16.
 */
public class UserAccount {

    private String userLogin;
    private String userPassword;
    private String rg;
    private String userEmail;

    public UserAccount(String userLogin, String userPassword, String rg, String userEmail) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.rg = rg;
        this.userEmail = userEmail;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getRg() {
        return rg;
    }
}
