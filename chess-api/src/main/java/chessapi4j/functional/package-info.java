/**
 * This package contains classes that are especially well-suited for functional programming
 * and for applications that operate in multithreaded environments. The core design principle
 * is to make immutable all entities that represent values in the game—such as positions,
 * moves, and similar constructs.
 * <p>
 * It is clearly established that the role of position generators, factory classes and constructors
 * is to produce these immutable objects, based on the rules of the game and specific validations.
 * This design choice leads to simpler, more predictable program flows, reducing ambiguity
 * and making reasoning about the system easier.
 * </p>
 * <p>
 * Some additional features has been added to the classes of this package. The Game class is more 
 * user friendly, more robust in terms of concurrency and game logic. The Position
 * class is immutable, thread-safe and has methods with better or more simple names. Generator class
 * is still accecssible but it becomes less necessary due to the {@link Position#children()} method.
 * List retrived from many of this classes are an immutable special implementation.
 * </p>
 * <p>
 * Position class does not require utility classes to determinate the game state (check, checkmate,
 * stalemate, lack of material, fifty moves). All of this is done in the Position class itself, making
 * it easier to the user.
 * </p>
 * <p>
 * Some classes may exhibit benign race conditions that, at worst, might impact performance,
 * but never compromise logical correctness.
 * </p>
 *
 * @author lunalobos
 * @since 1.2.9
 */
package chessapi4j.functional;