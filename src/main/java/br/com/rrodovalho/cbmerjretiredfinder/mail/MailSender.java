package br.com.rrodovalho.cbmerjretiredfinder.mail;


import br.com.rrodovalho.cbmerjretiredfinder.domain.FinderResult;
import br.com.rrodovalho.cbmerjretiredfinder.processor.Executor;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by rrodovalho on 28/03/16.
 */
public class MailSender {

    private static final String fromEmail = "";
    private static final String fromEmailPassword = "";

    public static void send(List<FinderResult> results) throws MessagingException {

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, fromEmailPassword);
                    }
                });

        Message message;
        if(results!=null && results.size() > 0) {
            for (FinderResult f : results) {

                System.out.println("User " + f.getUser().getRg() + " was found in " + f.getFiles().getKey() + " file\n");
                message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail.concat("@gmail.com")));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(f.getUser().getUserEmail()));
                message.setSubject("CMBERJ_Retired_Finder");
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Good News your RG :" + f.getUser().getRg() + " was found. The file is sending as attachment");
                MimeMultipart multipart = new MimeMultipart("related");

                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                String filename = f.getFiles().getKey().concat(".pdf");
                ByteArrayDataSource rawData = null;
                try {
                    //rawData = new ByteArrayDataSource( IOUtils.toByteArray(fis),"application/pdf");
                    rawData = new ByteArrayDataSource(f.getFiles().getValue(), "application/pdf");
                } catch (IOException e) {
                    e.printStackTrace();
                }

//            File mockDir = new File("/home/rrodovalho/cbmerjBot/2016/03/");
//            FileInputStream fis = null;
//            if(mockDir.exists()){
//
//                File[] files = mockDir.listFiles();
//
//    //            for(int i=0;i<files.length;i++){
//                    try {
//                        fis = new FileInputStream(files[0]);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//    //            }
//
//            }
//
//            // Part two is attachment
//            messageBodyPart = new MimeBodyPart();
//            String filename = "/home/rrodovalho/cbmerj.pdf";
//            ByteArrayDataSource rawData= null;
//            try {
//                rawData = new ByteArrayDataSource( IOUtils.toByteArray(fis),"application/pdf");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            MimeBodyPart attachment= new MimeBodyPart();
//            ByteArrayDataSource ds = null;
//            try {
//                ds = new ByteArrayDataSource(f.getFiles().getValue(), "application/pdf");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            attachment.setDataHandler(new DataHandler(ds));
//            attachment.setFileName("Report.pdf");

                messageBodyPart.setHeader("Content-Type", "application/pdf");
                messageBodyPart.setHeader("Content-Transfer-Encoding", "base64");
                messageBodyPart.setHeader("Content-Disposition", "attachment;filename=" + filename);
//            DataSource dataSource = new InputStreamDataSource(f.getFiles().getValue(),filename);
                messageBodyPart.setDataHandler(new DataHandler(rawData));
//            messageBodyPart.setDataHandler(new DataHandler(rawData, "application/pdf"));

                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);

                // Send the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);
                System.out.println("Mail to " + f.getUser().getUserEmail() + " was sent successfully....");
            }
        }
        else{
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail.concat("@gmail.com")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Executor.USER_EMAIL));
            message.setSubject("CMBERJ_Retired_Finder");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Unfortunelly nothing was found :( ");
            MimeMultipart multipart = new MimeMultipart();

            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            System.out.println("Nothing found mail was sent successfully....");
        }

    }
}
