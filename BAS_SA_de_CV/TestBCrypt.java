import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "password123";
        String hash = "$2a$10$8.UnVuG9HLpUsdBXwWrVG.5R0z3M/I3Ua/XyW6PZqjK.6YtG8Y/5K";
        System.out.println("Matches: " + encoder.matches(raw, hash));
        System.out.println("New hash: " + encoder.encode(raw));
    }
}
