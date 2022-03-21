package im.dacer.jetcurrency.utils

import com.notkamui.keval.Keval

class Calculator {
    companion object {
        /**
         * it will combine str and str2 as math expression and try to calculate the result.
         */
        fun performAdd(str: String, c: Char): Result {
            var expression = str

            expression = if (c == '=') {
                calculate(expression).toString()
            } else if (str == "0" && c.isDigit()) {
                c.toString()
            } else {
                expression + c
            }

            return Result(
                expression = expression,
                value = calculate(expression)
            )
        }

        fun performBackspace(expressionStr: String): Result {
            val expression = expressionStr.dropLast(1)
            return Result(
                expression = expression,
                value = calculate(expression)
            )
        }

        private fun calculate(expression: String): Double {
            return try {
                var expressionForEval = expression
                    .replace('x', '*')
                    .replace('÷', '/')
                val calculateSymbols = arrayOf('+', '-', '*', '/')
                // TODO it seems can be optimized
                while (calculateSymbols.contains(expressionForEval.last())) {
                    expressionForEval = expressionForEval.dropLast(1)
                }
                Keval.eval(expressionForEval)
            } catch (e: Exception) {
                0.0
            }
        }

        /**
         * expression can only include 1,2,3,4,5,6,7,8,9,0,.,+,-,x,÷
         *
         * TODO: DELETE IT. T_T
         */
        private fun calculate_Deprecated(expression: String): Double {
            return try {
                val calculateSymbols = arrayOf("+", "-", "x", "÷")

                // validate expression
                expression.forEachIndexed { index, c ->
                    if (index >= expression.length) return@forEachIndexed
                    if (!c.isDigit() && !expression[index + 1].isDigit()) {
                        throw Exception("wrong expression!")
                    }
                }

                var result = 0.0
                val numbers = expression.split(*calculateSymbols)
                val symbols = mutableListOf<Char>()

                var tmpIndex = 0
                var tmpNumbersIndex = 0
                while (tmpIndex < expression.length) {
                    if (!expression[tmpIndex].isDigit()) {
                        symbols += expression[tmpIndex]
                        tmpIndex++
                    } else {
                        symbols += ' '
                    }
                    tmpIndex += numbers[tmpNumbersIndex].length
                    tmpNumbersIndex++
                }

                numbers.forEachIndexed { index, s ->
                    val num = s.toDouble()
                    val symbol = symbols[index]
                    if (index == 0) {
                        result = if (symbol == '-') {
                            -num
                        } else {
                            num
                        }
                    } else {
                        when (symbol) {
                            '+' -> result += num
                            '-' -> result -= num
                            'x' -> result *= num
                            '÷' -> result /= num
                        }
                    }
                }
                result
            } catch (e: Exception) {
                0.0
            }
        }
    }

    data class Result(val expression: String, val value: Double)
}
