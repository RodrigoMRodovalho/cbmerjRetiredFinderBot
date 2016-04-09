package br.com.rrodovalho.cbmerjretiredfinder.processor;

import br.com.rrodovalho.cbmerjretiredfinder.bot.StreamCBMERJBot;
import br.com.rrodovalho.cbmerjretiredfinder.domain.FinderResult;
import br.com.rrodovalho.cbmerjretiredfinder.domain.LoginElementEnum;
import br.com.rrodovalho.cbmerjretiredfinder.domain.UserAccount;
import br.com.rrodovalho.cbmerjretiredfinder.mail.MailSender;
import br.com.rrodovalho.cbmerjretiredfinder.textprocessor.PDFUserFinder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rrodovalho on 28/03/16.
 */
public class Executor {

    public final static String USER = "";
    public final static String USER_PASS = "";
    public final static String USER_RG = "";
    public final static String USER_EMAIL = "";

    public final static String USER_LOGIN_INPUT_ELEMENT_ID = "modlgn-username";
    public final static String USER_PASSWORD_LOGIN_INPUT_ELEMENT_ID = "modlgn-passwd";
    public final static String LOGIN_SUBMIT_ELEMENT_NAME = "Submit";
    public final static String SIMPLE_RG_REGEX = "[\\d]{2}[.]*[\\d]{3}";
                                                //00.000
                                                //00000
    public final static String FULL_RG_REGEX = "([\\d]+[.]*)+[-]*[/]*[\\d]+";
                                               //00.000
                                               //00000
                                               //00000000-0
                                               //00000000/00
                                               //00.000.000-0
    private static UserAccount userAccount;

    // TODO: 26/03/16 make a name regex

    public static void execute(){

        System.out.println("");
        userAccount = new UserAccount(USER, USER_PASS,USER_RG,USER_EMAIL);

        Map<String, String> webComponents = new HashMap<>();
        webComponents.put(LoginElementEnum.USER_NAME_INPUT_ID.name(), USER_LOGIN_INPUT_ELEMENT_ID);
        webComponents.put(LoginElementEnum.USER_PASSWORD_INPUT_ID.name(), USER_PASSWORD_LOGIN_INPUT_ELEMENT_ID);
        webComponents.put(LoginElementEnum.SUBMIT_INPUT_NAME.name(), LOGIN_SUBMIT_ELEMENT_NAME);

        StreamCBMERJBot streamCBMERJBot = new StreamCBMERJBot(userAccount, webComponents);

        Map<String, InputStream> streamMap = null;
        try {
            streamMap = streamCBMERJBot.obtainBulletinsStreamList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<FinderResult> results;
        List<UserAccount> contentToFind;
        PDFUserFinder pdfUserFinder;

        if (streamMap != null && streamMap.size() > 0) {
            contentToFind = getUsersToFind();
            pdfUserFinder = new PDFUserFinder(streamMap, contentToFind, SIMPLE_RG_REGEX);

            results = pdfUserFinder.find();

            try {
                MailSender.send(results);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        } else {
            // TODO: 26/03/16 improve error threatments
            System.out.println("Something went wrong");
        }
    }

    private static ArrayList<UserAccount> getUsersToFind() {

        // TODO: 25/03/16 buscar de maneira dinamica, talvez de um arquivo ou bd
        ArrayList<UserAccount> users = new ArrayList<>();
        users.add(userAccount);
        return users;
    }
}
