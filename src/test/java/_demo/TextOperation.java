package _demo;

import java.util.function.UnaryOperator;

@lombok.AllArgsConstructor
public enum TextOperation {

    NO_OP(UnaryOperator.identity()),
    UPPER_CASE(String::toUpperCase);

    private final UnaryOperator<String> operator;

    public String apply(String input) {
        return operator.apply(input);
    }
}
