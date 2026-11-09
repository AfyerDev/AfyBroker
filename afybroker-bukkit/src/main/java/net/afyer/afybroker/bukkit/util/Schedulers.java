package net.afyer.afybroker.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * 兼容 Bukkit 与 Folia 的任务调度。
 * <p>
 * Folia 没有主线程，{@code Bukkit.getScheduler()} 不可用：异步任务改用 AsyncScheduler，实体相关任务改用该实体的 EntityScheduler。
 * 为了继续以 Java 8 和旧版 Bukkit API 编译，Folia 的调度器通过反射调用；非 Folia 服务端仍使用 BukkitScheduler。
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
        if (!FOLIA) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            return;
        }
        try {
            Object scheduler = Bukkit.class.getMethod("getAsyncScheduler").invoke(null);
            Method runNow = scheduler.getClass().getMethod("runNow", Plugin.class, Consumer.class);
            runNow.invoke(scheduler, plugin, (Consumer<Object>) t -> task.run());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to schedule async task on Folia", e);
        }
    }

    /** 在实体所属线程执行；Bukkit 上即主线程。实体已失效时 Folia 不执行任务 */
    public static void runForEntity(Plugin plugin, Entity entity, Runnable task) {
        if (!FOLIA) {
            Bukkit.getScheduler().runTask(plugin, task);
            return;
        }
        try {
            Object scheduler = entity.getClass().getMethod("getScheduler").invoke(entity);
            Method run = scheduler.getClass().getMethod("run", Plugin.class, Consumer.class, Runnable.class);
            run.invoke(scheduler, plugin, (Consumer<Object>) t -> task.run(), null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to schedule entity task on Folia", e);
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
