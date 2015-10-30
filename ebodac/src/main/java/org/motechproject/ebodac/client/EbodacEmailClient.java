package org.motechproject.ebodac.client;

import org.motechproject.ebodac.constants.EbodacConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;

@Component
public class EbodacEmailClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEmailClient.class);

    public Boolean hasNewJobCompletionMessage(String host, String user, String password) {
        LOGGER.info("Started checking for job completion emails");
        Properties properties = new Properties();

        Session session = Session.getDefaultInstance(properties);

        Boolean jobCompletion = false;
        try {
            Store store = session.getStore("imap");

            store.connect(host, user, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            Flags fetched = new Flags(EbodacConstants.FETCHED_EMAIL_FLAG);
            FlagTerm notFetched = new FlagTerm(fetched, false);
            Message[] messages = emailFolder.search(notFetched);

            for (Message message : messages) {
                String subject = message.getSubject();
                if (subject.contains(EbodacConstants.JOB_FAILURE_STATUS)) {
                    try {
                        LOGGER.error("Job failure, message content:\n" + message.getContent());
                    } catch (IOException e) {
                        LOGGER.error("Job failure, could not get the message content: " + subject);
                    }
                } else if (subject.contains(EbodacConstants.JOB_SUCCESS_STATUS)) {
                    LOGGER.info("New job completion message: " + subject);
                    jobCompletion = true;
                } else {
                    LOGGER.error("Message subject did not contain job status: " + subject);
                }
            }
            emailFolder.setFlags(messages, fetched, true);
            emailFolder.close(false);

            store.close();
        } catch (NoSuchProviderException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (MessagingException e) {
            LOGGER.error("Exception occurred when fetching new emails: " + e.getMessage(), e);
        }

        LOGGER.info("Finished checking for job completion emails, result: " + jobCompletion);
        return jobCompletion;
    }

}
