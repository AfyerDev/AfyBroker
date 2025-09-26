package net.afyer.afybroker.server.plugin;


import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 代表 broker.yml
 *
 * @author Nipuru
 * @since 2022/7/31 10:40
 */
public class PluginDescription {

    /**
     * 插件名称.
     */
    private String name;
    /**
     * 插件主类 继承自{@link Plugin}.
     */
    private String main;
    /**
     * 插件版本.
     */
    private String version;
    /**
     * 插件作者.
     */
    private String author;
    /**
     * 插件硬依赖.
     */
    private Set<String> depends = new HashSet<>();
    /**
     * 插件软依赖.
     */
    private Set<String> softDepends = new HashSet<>();
    /**
     * 插件源文件.
     */
    private File file = null;
    /**
     * 可选 插件简介.
     */
    private String description = null;

    public PluginDescription() {
    }

    public PluginDescription(String name, String main, String version, String author, Set<String> depends, Set<String> softDepends, File file, String description) {
        this.name = name;
        this.main = main;
        this.version = version;
        this.author = author;
        this.depends = depends;
        this.softDepends = softDepends;
        this.file = file;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<String> getDepends() {
        return depends;
    }

    public void setDepends(Set<String> depends) {
        this.depends = depends;
    }

    public Set<String> getSoftDepends() {
        return softDepends;
    }

    public void setSoftDepends(Set<String> softDepends) {
        this.softDepends = softDepends;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginDescription that = (PluginDescription) o;
        return java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(main, that.main) &&
                java.util.Objects.equals(version, that.version) &&
                java.util.Objects.equals(author, that.author) &&
                java.util.Objects.equals(depends, that.depends) &&
                java.util.Objects.equals(softDepends, that.softDepends) &&
                java.util.Objects.equals(file, that.file) &&
                java.util.Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, main, version, author, depends, softDepends, file, description);
    }

    @Override
    public String toString() {
        return "PluginDescription{" +
                "name='" + name + '\'' +
                ", main='" + main + '\'' +
                ", version='" + version + '\'' +
                ", author='" + author + '\'' +
                ", depends=" + depends +
                ", softDepends=" + softDepends +
                ", file=" + file +
                ", description='" + description + '\'' +
                '}';
    }
}
