package custis.easyabac.pdp;

import java.util.UUID;

/**
 * FIXME Переписать нафиг
 */
public class RequestId {

    private String id = UUID.randomUUID().toString();

    public static RequestId newRandom() {
        return new RequestId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
