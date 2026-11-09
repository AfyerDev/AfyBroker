package net.afyer.afybroker.bukkit.util;

import net.afyer.afybroker.bukkit.folia.FoliaSchedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * 兼容 Bukkit 与 Folia 的任务调度。
 * <p>
 * Folia 没有主线程，{@code Bukkit.getScheduler()} 不可用：异步任务改用 AsyncScheduler，实体相关任务改用该实体的 EntityScheduler。
 * Folia 实现在 folia-support 子模块（{@link FoliaSchedulers}），直接调用 folia-api，没有反射；
 * 非 Folia 服务端只走 BukkitScheduler 分支，不会加载 {@link FoliaSchedulers}。
 */
public final class Schedulers {

    private static final boolean FOLIA = classExists("io.papermc.paper.threadedregions.RegionizedServer");

    private Schedulers() {
    }

    /** 当前服务端是否为 Folia */
    public static boolean isFolia() {
        return FOLIA;
    }

    /** 异步执行 */
    public static void runAsync(Plugin plugin, Runnable task) {
        if (FOLIA) {
            FoliaSchedulers.runAsync(plugin, task);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /** 在实体所属线程执行；Bukkit 上即主线程。实体已失效时 Folia 不执行任务 */
    public static void runForEntity(Plugin plugin, Entity entity, Runnable task) {
        if (FOLIA) {
            FoliaSchedulers.runForEntity(plugin, entity, task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
