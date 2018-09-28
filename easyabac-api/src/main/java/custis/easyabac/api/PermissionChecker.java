package custis.easyabac.api;

import custis.easyabac.pdp.AuthResponse;
import org.springframework.stereotype.Indexed;

/**
 * @param <T> класс, для объектов которого проверяются разрешения
 * @param <A> класс действий
 * @author Anton Lapitskiy
 */
@Indexed
public interface PermissionChecker<T, A> {

    AuthResponse.AuthResult authorize(T entity, A action);
}
