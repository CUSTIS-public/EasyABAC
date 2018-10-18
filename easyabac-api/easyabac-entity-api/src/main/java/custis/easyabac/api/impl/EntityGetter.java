package custis.easyabac.api.impl;

import java.util.Optional;

public interface EntityGetter<T> {

    Optional<T> get();
}
