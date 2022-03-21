package im.dacer.jetcurrency.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalculatorTest {

    @Test
    fun add_1_to_0_and_Return_1() {
        assertThat(Calculator.performAdd("0", '1'))
            .isEqualTo(
                Calculator.Result(
                    expression = "1",
                    value = 1.0,
                )
            )
    }

    @Test
    fun add_division_sign_to_0_and_Return_0() {
        assertThat(Calculator.performAdd("0", '÷'))
            .isEqualTo(
                Calculator.Result(
                    expression = "0÷",
                    value = 0.0,
                )
            )
    }

    @Test
    fun add_equals_sign_to_10_plus_2_and_Return_12() {
        assertThat(Calculator.performAdd("10+2", '='))
            .isEqualTo(
                Calculator.Result(
                    expression = "12.0",
                    value = 12.0,
                )
            )
    }

    @Test
    fun calculate_a_long_math_expression_and_Return_correct_answer() {
        assertThat(Calculator.performAdd("1.0x12.2÷10+2-12+36.987x2", '='))
            .isEqualTo(
                Calculator.Result(
                    expression = "65.194",
                    value = 65.194,
                )
            )
    }

    @Test
    fun calculate_a_math_expression_with_unsupported_symbol_and_Return_0() {
        assertThat(Calculator.performAdd("1.0minus2", '='))
            .isEqualTo(
                Calculator.Result(
                    expression = "0.0",
                    value = 0.0,
                )
            )
    }

    @Test
    fun calculate_a_wrong_math_expression_and_Return_0() {
        assertThat(Calculator.performAdd("1+-", '2'))
            .isEqualTo(
                Calculator.Result(
                    expression = "1+-2",
                    value = 0.0,
                )
            )
    }

    @Test
    fun perform_0_divided_by_0_and_Return_0() {
        assertThat(Calculator.performAdd("0÷", '0'))
            .isEqualTo(
                Calculator.Result(
                    expression = "0÷0",
                    value = 0.0,
                )
            )
    }
}
