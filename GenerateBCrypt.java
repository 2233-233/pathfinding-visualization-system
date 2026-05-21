import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        System.out.println("BCrypt encoded password for '123456': " + encodedPassword);
        
        // Also generate for other passwords if needed
        System.out.println("BCrypt encoded password for 'user123456': " + encoder.encode("user123456"));
    }
}