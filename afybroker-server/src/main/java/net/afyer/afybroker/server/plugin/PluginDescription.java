package net.afyer.afybroker.server.plugin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 代表 broker.yml
 *
 * @author Nipuru
 * @since 2022/7/31 10:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PluginDescription {

    /**
     * 插件名称.
     */
    String name;
    /**
     * 插件主类 继承自{@link Plugin}.
     */
   String main;
    /**
     * 插件版本.
     */
    String version;
    /**
     * 插件作者.
     */
    String author;
    /**
     * 插件硬依赖.
     */
    Set<String> depends = new HashSet<>();
    /**
     * 插件软依赖.
     */
    Set<String> softDepends = new HashSet<>();
    /**
     * 插件源文件.
     */
    File file = null;
    /**
     * 可选 插件简介.
     */
    String description = null;
}
