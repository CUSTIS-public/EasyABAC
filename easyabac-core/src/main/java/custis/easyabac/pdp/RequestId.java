package custis.easyabac.pdp;

import java.util.Objects;
import java.util.UUID;

/**
 * FIXME Переписать нафиг
 */
public class RequestId {

    private String id;

    public RequestId(String id) {
        this.id = id;
    }

    public static RequestId newRandom() {
        return new RequestId(UUID.randomUUID().toString());
    }

    public static RequestId of(String serialized) {
        return new RequestId(serialized);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestId requestId = (RequestId) o;
        return Objects.equals(id, requestId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
