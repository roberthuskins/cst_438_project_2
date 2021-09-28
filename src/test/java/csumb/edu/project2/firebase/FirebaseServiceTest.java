package csumb.edu.project2.firebase;

import csumb.edu.project2.objects.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirebaseServiceTest {

    @Test
    public void testInsertShowUser() throws ExecutionException, InterruptedException {
        FirebaseInitialize init = new FirebaseInitialize();
        init.initialize();
        FirebaseService firebaseService = new FirebaseService();
        User tempUser = new User("test@test.com", "test");
        firebaseService.saveUserDetails(tempUser);

        User newUser = firebaseService.getUserDetails("test@test.com");

        assertEquals(newUser.getUsername(), tempUser.getUsername());
        assertEquals(newUser.getPassword(), tempUser.getPassword());
    }
}
