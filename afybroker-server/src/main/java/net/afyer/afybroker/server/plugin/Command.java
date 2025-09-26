package net.afyer.afybroker.server.plugin;


/**
 * @author Nipuru
 * @since 2022/7/31 10:49
 */
public abstract class Command {

    /** 命令名称 */
    private final String name;
    /** 命令别名 */
    private final String[] aliases;

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Command(String name, String... aliases)
    {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void execute(String[] args);
}
