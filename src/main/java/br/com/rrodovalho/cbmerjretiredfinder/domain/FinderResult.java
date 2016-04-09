package br.com.rrodovalho.cbmerjretiredfinder.domain;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by rrodovalho on 31/03/16.
 */
public class FinderResult {

    private UserAccount user;
    private Map.Entry<String,InputStream> files;

    public FinderResult(UserAccount user, Map.Entry<String, InputStream> files) {
        this.user = user;
        this.files = files;
    }

    public UserAccount getUser() {
        return user;
    }

    public Map.Entry<String, InputStream> getFiles() {
        return files;
    }

}
