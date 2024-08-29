package changes;

import java.io.Serializable;

public record Change(String changeDetails, String username) implements Serializable {
}
