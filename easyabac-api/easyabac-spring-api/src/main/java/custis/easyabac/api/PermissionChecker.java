package custis.easyabac.api;

import org.springframework.stereotype.Indexed;

/**
 * @param <T> class for which objects permissions are checked
 * @param <A> class action
 * @author Anton Lapitskiy
 */
@Indexed
public interface PermissionChecker<T, A> {

}
