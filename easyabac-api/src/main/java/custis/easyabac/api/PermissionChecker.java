package custis.easyabac.api;

import org.springframework.stereotype.Indexed;

/**
 * @param <T> класс, для объектов которого проверяются разрешения
 * @param <A> класс действий
 * @author Anton Lapitskiy
 */
@Indexed
public interface PermissionChecker<T, A> {

}
