package org.hxnry.rsp.watcher_server.api.util;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Hxnry
 * @since November 07, 2016
 */
public final class F {
    /**
     * When the returned {@code Function} is passed as an argument to
     * {@link Stream#flatMap}, the result is a stream of instances of
     * {@code cls}.
     */
    public static <E> Function<Object, Stream<E>> instancesOf(Class<E> cls) {
        return o -> cls.isInstance(o)
                ? Stream.of(cls.cast(o))
                : Stream.empty();
    }
}
