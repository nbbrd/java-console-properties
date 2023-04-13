package _demo;

import java.util.Locale;
import java.util.function.UnaryOperator;

@lombok.AllArgsConstructor
public enum TextOperation {

    NO_OP(UnaryOperator.identity()),
    UPPER_CASE(s -> s.toUpperCase(Locale.ROOT));

    private final UnaryOperator<String> operator;

    public String apply(String input) {
        return operator.apply(input);
    }
}
