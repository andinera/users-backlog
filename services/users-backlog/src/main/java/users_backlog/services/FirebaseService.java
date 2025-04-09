package users_backlog.services;

import java.util.logging.Logger;

// import javax.annotation.PostConstruct;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseToken;

// import org.springframework.context.annotation.Scope;
// import org.springframework.stereotype.Service;

// @Service
// @Scope("session")
public class FirebaseService {
    
    private static final Logger log = Logger.getLogger(FirebaseService.class.getName());

    // @PostConstruct
    // public void initialize() {
    //     try {
    //         FirebaseOptions options = new FirebaseOptions.Builder()
    //             .setCredentials(GoogleCredentials.getApplicationDefault())
    //             .build();
    //         FirebaseApp.initializeApp(options);
    //     } catch (Exception e) {
    //         log.severe(e.getMessage());
    //     }
    // }

    // public void verifyToken(String idToken) {
    //     try {
    //         FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
    //         String uid = decodedToken.getUid();
    //         log.info(uid);
    //     } catch (Exception e) {
    //         log.severe(e.getMessage());
    //     }
    // }

}