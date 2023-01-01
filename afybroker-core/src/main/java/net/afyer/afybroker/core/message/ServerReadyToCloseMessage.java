package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;


/**
 * @author Anjin
 * @since 2022-12-30 17:18:16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerReadyToCloseMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -5687906079526902058L;

    String server;

    Set<String> tags;
}
