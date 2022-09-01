package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.afyer.afybroker.core.util.LazyLocation;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Anjin
 * @since 2022-9-1 16:09:24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerTeleportToMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = -5262186177968036442L;

    UUID who;
    LazyLocation location;
}
