package net.afyer.afybroker.server.plugin;


/**
 * @author Nipuru
 * @since 2022/7/31 10:49
 */
public abstract class Command {

    /**
     * 命令名称
     */
    private final String name;
    /**
     * 命令用法
     */
    private String usage;
    /**
     * 命令别名
     */
    private final String[] aliases;

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getUsage() {
        return usage;
    }

    public Command(String name, String... aliases) {
        this.name = name;
        this.usage = name;
        this.aliases = aliases;
    }

    protected void setUsage(String usage) {
        this.usage = (usage == null || usage.trim().isEmpty()) ? this.name : usage;
    }

    public abstract void execute(String[] args);
}
