
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static String baseUrl;
    private static String apiVersion;
    private static String baseDb;
    private static String baseUser;
    private static String basePassword;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("test-config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find test-config.properties");
                return;
            }
            properties.load(input);
            baseUrl = properties.getProperty("base.url");
            baseDb = properties.getProperty("base.db");
            baseUser = properties.getProperty("base.user");
            basePassword = properties.getProperty("base.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getApiVersion() {
        return basePassword;
    }

    public static String getBasePassword() {
        return basePassword;
    }

    public static String getBaseUser() {
        return baseUser;
    }
}
