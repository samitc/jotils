package Utils.function;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result but can throw an exception.  This is the three-arity specialization of {@link Consumer}.
 * Unlike most other functional interfaces, {@code BiConsumerException} is expected
 * to operate via side-effects.
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <E> the type of the throwable to the operation
 * @see Consumer
 * @since 1.0
 */
@FunctionalInterface
public interface BiConsumerException<T, U, E extends Throwable> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u) throws E;
}
