package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Nipuru
 * @since 2022/8/3 18:03
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerConnectOtherMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 6146541679234629563L;

    UUID uid;
    String server;

}
