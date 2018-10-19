package custis.easyabac.api.impl;

import java.util.Optional;

public interface SubjectEntityProvider<T> {

    Optional<T> provide();
}
