package custis.easyabac.demo.authz;

import custis.easyabac.demo.model.User;

/**
 * Провайдер данных текущего пользователя
 */
public class AuthenticationContext {
    public static User currentUser() {
        return new User();
    }
}
