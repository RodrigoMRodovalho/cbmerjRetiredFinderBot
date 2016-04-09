package br.com.rrodovalho.cbmerjretiredfinder.textprocessor;

import br.com.rrodovalho.cbmerjretiredfinder.domain.FinderResult;
import br.com.rrodovalho.cbmerjretiredfinder.domain.UserAccount;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rrodovalho on 25/03/16.
 */
public class PDFUserFinder {

    private Map<String,InputStream> streamMap;
    private List<UserAccount> content;
    private String regex;

    public PDFUserFinder(Map<String, InputStream> streamMap, List<UserAccount> content, String regex) {
        this.streamMap = streamMap;
        this.content = content;
        this.regex = regex;
    }

    public List<FinderResult> find(){

        if(content!= null  && content.size()>0){

            PDDocument pd = null;
            PDFTextStripper stripper = null;
            StringBuilder sb = null;
            Pattern pattern = Pattern.compile(regex);

            List<FinderResult> results = new ArrayList<>();

            for (Map.Entry<String, InputStream> entry : streamMap.entrySet()) {

                try {
                    pd = PDDocument.load(entry.getValue());
                    stripper = new PDFTextStripper();
                    sb = new StringBuilder();
                    sb.append(stripper.getText(pd));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Matcher m = pattern.matcher(sb);
                while (m.find()){

                    for(int i=0;i<content.size();i++){
                        if(m.group().equals(content.get(i).getRg())){
                            results.add(new FinderResult(content.get(i),entry));
                        }
                    }
                }

                if (pd != null) {
                    try {
                        pd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return results;
        }

        return null;
    }
}
