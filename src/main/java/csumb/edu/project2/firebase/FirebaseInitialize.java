package csumb.edu.project2.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;
import org.apache.commons.io.IOUtils;


import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;

/***
 * This class initializes the connection to Firestore. It is run automatically because of the @PostConstruct method.
 * Autoruns when the spring app starts or when a unit test with @SpringBootTest is ran.
 */
@Service
public class FirebaseInitialize {
    @PostConstruct
    public void initialize() {

        if (System.getenv("FIREBASE_TYPE") == null) {
            try {
                FileInputStream serviceAccount =
                        new FileInputStream("./laundrylist-687e2-firebase-adminsdk-staqg-86c89737bc.json");

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://laundrylist-687e2-default-rtdb.firebaseio.com")
                        .build();

                FirebaseApp.initializeApp(options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("DID WE EVEN MAKE IT HERE");
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(createFirebaseCredential()))
                        .setDatabaseUrl("https://laundrylist-687e2-default-rtdb.firebaseio.com")
                        .build();

                FirebaseApp.initializeApp(options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private InputStream createFirebaseCredential() throws Exception {
        String privateKey = System.getenv("FIREBASE_PRIVATE_KEY").replace("\\n", "\n");

        FirebaseCredential firebaseCredential = new FirebaseCredential();
        firebaseCredential.setType(System.getenv("FIREBASE_TYPE"));
        firebaseCredential.setProject_id(System.getenv("FIREBASE_PROJECT_ID"));
        firebaseCredential.setPrivate_key_id("FIREBASE_PRIVATE_KEY_ID");
        firebaseCredential.setPrivate_key(privateKey);
        firebaseCredential.setClient_email(System.getenv("FIREBASE_CLIENT_EMAIL"));
        firebaseCredential.setClient_id(System.getenv("FIREBASE_CLIENT_ID"));
        firebaseCredential.setAuth_uri(System.getenv("FIREBASE_AUTH_URI"));
        firebaseCredential.setToken_uri(System.getenv("FIREBASE_TOKEN_URI"));
        firebaseCredential.setAuth_provider_x509_cert_url(System.getenv("FIREBASE_AUTH_PROVIDER_X509_CERT_URL"));
        firebaseCredential.setClient_x509_cert_url(System.getenv("FIREBASE_CLIENT_X509_CERT_URL"));

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(firebaseCredential);

        return IOUtils.toInputStream(jsonString);
    }
}
