package net.afyer.afybroker.core.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Anjin
 * @since 2022年9月1日15:58:48
 */
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class LazyLocation implements Serializable {
    @Serial
    private static final long serialVersionUID = -5335150473408110718L;

    private final String serverName;
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
}
