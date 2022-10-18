package nbbrd.console.picocli;

import java.util.function.Supplier;

public interface CommandSupporter<T> {

    void applyTo(T support);

    static <T> T create(Supplier<T> factory, CommandSupporter<? super T>... supporters) {
        T result = factory.get();
        for (CommandSupporter<? super T> supporter : supporters) {
            supporter.applyTo(result);
        }
        return result;
    }
}
