package custis.easyabac.demo.authn;

/**
 * Current User Data Provider
 */
public class AuthenticationContext {

    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal();

    public static String currentUserId() {
        return THREAD_LOCAL.get();
    }


    public static void setup(String userId) {
        THREAD_LOCAL.set(userId);
    }

    public static void cleanup() {
        THREAD_LOCAL.set(null);
    }
}
