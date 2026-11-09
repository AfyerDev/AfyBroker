package net.afyer.afybroker.bukkit.folia;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Folia 调度实现，直接调用 folia-api。
 * <p>
 * 只由 {@code Schedulers} 在 Folia 上调用；其他服务端不会加载本类。
 * 方法签名只使用 Bukkit 类型，afybroker-bukkit 不需要 folia-api 即可编译。
 */
public final class FoliaSchedulers {

    private FoliaSchedulers() {
    }

    /** 使用 AsyncScheduler 异步执行 */
    public static void runAsync(Plugin plugin, Runnable task) {
        Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
    }

    /** 在实体所属区域线程执行；实体已失效时不执行 */
    public static void runForEntity(Plugin plugin, Entity entity, Runnable task) {
        entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
    }
}
