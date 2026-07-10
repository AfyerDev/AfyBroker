package net.afyer.afybroker.core.util;

/**
 * Utilities for propagating failures without changing their type or identity.
 */
public final class ThrowableUtils {

    private ThrowableUtils() {
    }

    /**
     * Throws {@code throwable} itself, including checked exceptions, without wrapping it.
     *
     * @return never returns; the generic return type lets callers use this in return statements
     */
    @SuppressWarnings("unchecked")
    public static <R, T extends Throwable> R throwUnchecked(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
