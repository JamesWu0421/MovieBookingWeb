package tw.com.ispan.jwtTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.com.ispan.jwt.JsonWebTokenUtility;

@SpringBootTest
public class JsonWebTokenUtilityTest {
    @Autowired
    private JsonWebTokenUtility jwtUtility;
    @Test
    public void testSinged() {
        String token = jwtUtility.createToken("test123");
        
        String data = jwtUtility.validateToken(token);
        System.out.println("data="+data);


        System.out.println("token"+token);
        System.out.println("++++++++++++++++++++++");
    }
    @Test
    public void testEncrypted() {
        String token = jwtUtility.createEncryptedToken("test456");
        System.out.println("encryptedToken"+token);
        System.out.println("++++++++++++++++++++++");
    }
}
            