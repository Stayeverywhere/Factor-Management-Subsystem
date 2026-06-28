package com.factor.application.factor;

import com.factor.domain.factor.Formula;
import com.factor.domain.factor.FormulaItem;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

public class DerivedFactorCalculator {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    public BigDecimal calculate(Formula formula, Map<String, BigDecimal> factorValues) {
        if (formula == null || formula.items() == null || formula.items().isEmpty()) {
            throw new IllegalArgumentException("Formula items must not be empty");
        }

        BigDecimal result = BigDecimal.ZERO;
        boolean first = true;

        for (FormulaItem item : formula.items()) {
            BigDecimal value = resolveValue(item, factorValues);
            BigDecimal weighted = value.multiply(safeWeight(item.weight()), MATH_CONTEXT);
            String op = normalizeOperator(item.operator());
            if (first) {
                result = weighted;
                first = false;
            } else {
                result = apply(op, result, weighted);
            }
        }

        return result.setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveValue(FormulaItem item, Map<String, BigDecimal> factorValues) {
        BigDecimal value = factorValues.get(item.factorId());
        if (value == null) {
            throw new IllegalArgumentException("Missing factor value for factorId=" + item.factorId());
        }
        return value;
    }

    private BigDecimal safeWeight(BigDecimal weight) {
        return weight == null ? BigDecimal.ONE : weight;
    }

    private String normalizeOperator(String operator) {
        return operator == null ? "+" : operator.trim().toUpperCase();
    }

    private BigDecimal apply(String operator, BigDecimal left, BigDecimal right) {
        return switch (operator) {
            case "+" -> left.add(right, MATH_CONTEXT);
            case "-" -> left.subtract(right, MATH_CONTEXT);
            case "*" -> left.multiply(right, MATH_CONTEXT);
            case "/" -> right.compareTo(BigDecimal.ZERO) == 0 ? left : left.divide(right, MATH_CONTEXT);
            case "MAX" -> left.max(right);
            case "MIN" -> left.min(right);
            case "AVG" -> left.add(right, MATH_CONTEXT).divide(BigDecimal.valueOf(2), MATH_CONTEXT);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }
}
