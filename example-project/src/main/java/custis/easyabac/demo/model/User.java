package custis.easyabac.demo.model;

import lombok.Getter;

/**
 * Сотрудник компании
 */
@Getter
public class User {

    private UserId id;
    private Branch branch;
    private String firstName;
    private String lastName;

    public boolean hasRole(String role) {
        return false;
    }

}
