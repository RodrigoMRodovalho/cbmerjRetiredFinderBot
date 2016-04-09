package br.com.rrodovalho.cbmerjretiredfinder.bot;

import br.com.rrodovalho.cbmerjretiredfinder.domain.LoginElementEnum;
import br.com.rrodovalho.cbmerjretiredfinder.domain.UserAccount;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.*;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rrodovalho on 25/03/16.
 */
public class StreamCBMERJBot {

    private final static String BASE_URL = "http://www.cbmerj.rj.gov.br";
    private final static String BULLETINS_URL = "/index.php/boletins/boletins-ostensivos/";
    private final static String BULLETIN_PREFIX = "BOL";
    private final static boolean MOCK_STREAM = false;
    private final static String MOCK_STREAM_DIR = "/home/rrodovalho/cbmerjBot/2016/03/";
    private final static String fileReadPath = "bulletins.txt";
    private UserAccount userAccount;
    private Map<String,String> webLoginComponents;
    private WebDriver mDriver;

    public StreamCBMERJBot(UserAccount userAccount, Map<String, String> webLoginComponents) {
        this.userAccount = userAccount;
        this.webLoginComponents = webLoginComponents;
        mDriver = new HtmlUnitDriver();
    }

    private void connect() throws UnknownHostException {

        mDriver.get(BASE_URL);
        if(mDriver.getPageSource().equalsIgnoreCase("Unknown host")){
            throw new UnknownHostException(BASE_URL);
        }
    }

    private void logIn() throws InvalidObjectException {

        WebElement wUserName = mDriver.findElement(By.id(webLoginComponents.get(LoginElementEnum.USER_NAME_INPUT_ID.name())));
        WebElement wUserPassword = mDriver.findElement(By.id(webLoginComponents.get(LoginElementEnum.USER_PASSWORD_INPUT_ID.name())));
        WebElement wSubmit = mDriver.findElement(By.name(webLoginComponents.get(LoginElementEnum.SUBMIT_INPUT_NAME.name())));

        if(!(wUserName!=null && wUserPassword!=null && wSubmit!=null)){
            throw new InvalidObjectException("Invalid login web elements");
        }

        wUserName.sendKeys(userAccount.getUserLogin());
        wUserPassword.sendKeys(userAccount.getUserPassword());
        wSubmit.click();
    }

    private void obtainBulletinsPage() throws UnknownHostException {

        String currentYear = new SimpleDateFormat( "yyyy" ).format(new java.util.Date());
        mDriver.get(BASE_URL+ BULLETINS_URL +currentYear);

        if(mDriver.getPageSource().equalsIgnoreCase("Unknown host")){
            throw new UnknownHostException(BASE_URL+ BULLETINS_URL +currentYear);
        }

    }

    private void goToCurrentMonthPage() throws InvalidObjectException {

        String currentMonth = new SimpleDateFormat( "MM" ).format(new java.util.Date());
        WebElement month = mDriver.findElement(By.linkText(currentMonth));

        if(month==null){
            throw new InvalidObjectException("Invalid month web link");
        }

        month.click();
    }

    private InputStream getStreamFromUrl(String url) throws IOException {

        CookieStore cookieStore = seleniumCookiesToCookieStore();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCookieStore(cookieStore);

        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
//            InputStream i = entity.getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = entity.getContent().read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

            InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
            return is1;
        }

        return null;
    }

    private CookieStore seleniumCookiesToCookieStore() {

        Set<Cookie> seleniumCookies = mDriver.manage().getCookies();
        CookieStore cookieStore = new BasicCookieStore();

        for(Cookie seleniumCookie : seleniumCookies){
            BasicClientCookie basicClientCookie =
                    new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
            basicClientCookie.setDomain(seleniumCookie.getDomain());
            basicClientCookie.setExpiryDate(seleniumCookie.getExpiry());
            basicClientCookie.setPath(seleniumCookie.getPath());
            cookieStore.addCookie(basicClientCookie);
        }

        return cookieStore;
    }

    private Map<String,InputStream> mockStreams() throws FileNotFoundException {

        File mockDir = new File(MOCK_STREAM_DIR);

        if(mockDir.exists()){

            Map<String,InputStream> results = new HashMap<String, InputStream>();
            FileInputStream fis = null;
            File[] files = mockDir.listFiles();

            for(int i=0;i<files.length;i++){
                fis = new FileInputStream(files[i]);
                results.put("[ "+files[i].getName().replace(".pdf"," ]"),fis);
            }
            return results;
        }
        return null;

    }

    public Map<String,InputStream> obtainBulletinsStreamList() throws IOException {

        if(MOCK_STREAM){

            return mockStreams();
        }
        else{

            connect();
            logIn();
            obtainBulletinsPage();
            goToCurrentMonthPage();

            Map<String, InputStream> streamMap = null;
            List<WebElement> bulletins = mDriver.findElements(By.partialLinkText(BULLETIN_PREFIX));

            if(bulletins==null){
                throw new InvalidObjectException("Invalid bulletins stream link list");
            }
            else{

                streamMap = new HashMap<>();
                for (int i = 0; i < bulletins.size(); i++) {
                    if(!isFileAlreadyRead(bulletins.get(i).getText())){
                        streamMap.put(bulletins.get(i).getText().replaceAll(" ",""), getStreamFromUrl(bulletins.get(i).getAttribute("href")));
                    }
                }

                return streamMap;
            }

        }
    }

    private boolean isFileAlreadyRead(String name){
//        if(!findName(name)){
//            write(name);
//            return false;
//        }
//        else {
//            return true;
//        }
        return false;
    }

//    public boolean findName(String name){
//        System.out.println("Find Name "+name);
//        String line = null;
//
//        try {
//            // FileReader reads text files in the default encoding.
//            FileReader fileReader =
//                    new FileReader(fileReadPath);
//
//            // Always wrap FileReader in BufferedReader.
//            BufferedReader bufferedReader =
//                    new BufferedReader(fileReader);
//            boolean flag = false;
//            while((line = bufferedReader.readLine()) != null) {
//                if(line.contains(name)){
//                    flag = true;
//                    break;
//                }
//            }
//            // Always close files.
//            bufferedReader.close();
//            return flag;
//        }
//        catch(Exception ex) {
//            ex.printStackTrace();
//        }
//       return false;
//    }
//
//    public void write(String name){
//        try {
//            // Assume default encoding.
//            FileWriter fileWriter =
//                    new FileWriter(fileReadPath);
//
//            // Always wrap FileWriter in BufferedWriter.
//            BufferedWriter bufferedWriter =
//                    new BufferedWriter(fileWriter);
//
//            // Note that write() does not automatically
//            // append a newline character.
//            bufferedWriter.write(name);
//            bufferedWriter.newLine();
//
//            // Always close files.
//            bufferedWriter.close();
//        }
//        catch(IOException ex) {
//            ex.printStackTrace();
//        }
//    }
}
