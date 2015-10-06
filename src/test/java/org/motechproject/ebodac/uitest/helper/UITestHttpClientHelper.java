package org.motechproject.ebodac.uitest.helper;

import org.motechproject.ebodac.client.EbodacHttpClient;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;

public class UITestHttpClientHelper extends EbodacHttpClient {

    private String SERVER_URL;
    private String ZETES_REGISTRATION_URL;
    public UITestHttpClientHelper(String serverUrl) {
        SERVER_URL = serverUrl.substring(0, serverUrl.lastIndexOf("/"));
        ZETES_REGISTRATION_URL = SERVER_URL + "/ebodac/registration/submit";
    }

    public void addParticipant(TestParticipant participant) {
        sendJson(ZETES_REGISTRATION_URL,
                "{\"subjectId\":\"" + participant.id + "\"," +
                        "\"name\":\"" + participant.name + "\"," +
                        "\"language\":\"" + participant.language + "\"," +
                        "\"phoneNumber\":\"" + participant.phoneNumber + "\"," +
                        "\"siteId\":\"" + participant.siteId + "\",\"headOfHousehold\":\"" + participant.headOfHousehold + "\"," +
                        "\"householdName\":\"" + participant.householdName + "\"," +
                        "\"community\":\"" + participant.community + "\",\"address\":\"" + participant.address + "\"" +
                        "}","motech","motech");
    }


}
