package custis.easyabac.pdp;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * FIXME Переписать нафиг
 */
@Getter
@Setter
public class RequestId {

    private String id = UUID.randomUUID().toString();

    public static RequestId newRandom() {
        return new RequestId();
    }
}
