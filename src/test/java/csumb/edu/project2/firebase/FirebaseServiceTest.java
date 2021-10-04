package csumb.edu.project2.firebase;

import csumb.edu.project2.objects.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test for our database. The timers are to prevent flakiness due to latency.
 */
@SpringBootTest
public class FirebaseServiceTest {
    @Autowired
    FirebaseService firebaseService;

    @Test
    public void testInsertShowUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test@test.com", "test");
        firebaseService.saveUserDetails(tempUser);
        TimeUnit.SECONDS.sleep(5);

        User newUser = firebaseService.getUserDetails("test@test.com");

        TimeUnit.SECONDS.sleep(5);

        assertEquals(newUser.getUsername(), tempUser.getUsername());
        assertEquals(newUser.getPassword(), tempUser.getPassword());

        firebaseService.deleteUser(newUser);
    }

    @Test
    public void testDeleteUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test1@test.com", "test1");
        firebaseService.saveUserDetails(tempUser);

        TimeUnit.SECONDS.sleep(5);

        firebaseService.deleteUser(tempUser);
        TimeUnit.SECONDS.sleep(5);

        assertEquals(null,firebaseService.getUserDetails("test1@test.com"));


    }

    @Test
    public void testUpdateUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test2@test.com", "wrong password");
        firebaseService.saveUserDetails(tempUser);
        TimeUnit.SECONDS.sleep(5);
        User newUser = new User("test2@test.com", "test3");
        firebaseService.updateUserDetails(newUser);
        TimeUnit.SECONDS.sleep(5);
        String mynewPassword = firebaseService.getUserDetails("test2@test.com").getPassword();
        assertEquals("test3", mynewPassword);

        firebaseService.deleteUser(newUser);
    }
}
