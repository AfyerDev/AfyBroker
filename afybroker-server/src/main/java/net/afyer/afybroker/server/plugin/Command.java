package net.afyer.afybroker.server.plugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author Nipuru
 * @since 2022/7/31 10:49
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Command {

    /** 命令名称 */
    final String name;
    /** 命令别名 */
    final String[] aliases;

    public Command(String name, String... aliases)
    {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void execute(String[] args);
}
