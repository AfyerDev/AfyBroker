package net.afyer.afybroker.core;

import java.io.File;

/**
 * 抽象配置文件类
 *
 * @author Nipuru
 * @since 2022/7/28 17:43
 */
public abstract class FileConfig<T> {

    private final File file;

    public FileConfig(File file) {
        this.file = file;
    }

    /**
     * 获取配置文件File对象
     *
     * @return file
     */
    public File getFile() {
        return file;
    }

    /**
     * 保存配置文件
     */
    public abstract void save();

    /**
     * 读取配置文件
     */
    public abstract void reload();

    /**
     * 获取可使用的配置
     * bukkit: ConfigurationSection
     * bungee: Configuration
     *
     * @return config
     */
    public abstract T get();
}
