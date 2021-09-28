package csumb.edu.project2.firebase;

import csumb.edu.project2.objects.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FirebaseServiceTest {
    @Autowired
    FirebaseService firebaseService;

    @Test
    public void testInsertShowUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test@test.com", "test");
        firebaseService.saveUserDetails(tempUser);

        User newUser = firebaseService.getUserDetails("test@test.com");

        assertEquals(newUser.getUsername(), tempUser.getUsername());
        assertEquals(newUser.getPassword(), tempUser.getPassword());

        firebaseService.deleteUser(newUser);
    }

    @Test
    public void testDeleteUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test1@test.com", "test1");
        firebaseService.saveUserDetails(tempUser);
        firebaseService.deleteUser(tempUser);
        assertEquals(null,firebaseService.getUserDetails("test1@test.com"));

    }

    @Test
    public void testUpdateUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test2@test.com", "wrong password");
        firebaseService.saveUserDetails(tempUser);

        User newUser = new User("test2@test.com", "test3");
        firebaseService.updateUserDetails(newUser);

        String mynewPassword = firebaseService.getUserDetails("test2@test.com").getPassword();
        assertEquals("test3", mynewPassword);
    }
}
