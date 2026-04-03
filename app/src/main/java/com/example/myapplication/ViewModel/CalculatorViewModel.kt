package com.example.myapplication.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.Model.CalculatorAction
import java.math.BigDecimal
import java.math.RoundingMode
import com.example.myapplication.Model.CalculatorState

class CalculatorViewModel : ViewModel() {
    // UI에서 관찰할 상태 (private set을 통해 외부에서 임의 수정 방지)
    var state by mutableStateOf(CalculatorState())
        private set

    // UI에서 발생하는 모든 이벤트를 수신하는 단일 진입점
    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Operation -> enterOperation(action.op)
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Calculate -> performCalculation()
        }
    }

    private fun enterNumber(number: String) {
        if (state.operation == null) {
            if (state.number1.length >= 15) return // 길이 제한
            state = state.copy(number1 = state.number1 + number)
        } else {
            if (state.number2.length >= 15) return
            state = state.copy(number2 = state.number2 + number)
        }
    }

    private fun enterOperation(op: String) {
        // 1. 첫 번째 숫자가 입력되어 있을 때만 연산자를 받을 수 있음
        if (state.number1.isNotBlank()) {

            // 2. 만약 두 번째 숫자까지 이미 입력된 상태에서 또 연산자를 눌렀다면?
            // 예: "1" "+" "2" 상태에서 "-" 를 누른 경우 -> 먼저 "1+2"를 계산해버림
            if (state.number2.isNotBlank()) {
                performCalculation()
            }

            // 3. 에러 상태(0으로 나누기 등)가 발생하지 않았다면, 방금 누른 새로운 연산자를 등록
            if (!state.isError) {
                state = state.copy(operation = op)
            }
        }
    }

    private fun performCalculation() {
        val number1 = state.number1.toBigDecimalOrNull()
        val number2 = state.number2.toBigDecimalOrNull()

        if (number1 != null && number2 != null && state.operation != null) {
            val result = try {
                when (state.operation) {
                    "+" -> number1 + number2
                    "-" -> number1 - number2
                    "*" -> number1 * number2
                    "/" -> number1.divide(number2, 8, RoundingMode.HALF_UP).stripTrailingZeros()
                    else -> return
                }
            } catch (e: ArithmeticException) {
                // 0으로 나누기 등의 에러 처리
                state = state.copy(isError = true)
                return
            }

            // 계산 결과를 number1에 넣고 나머지는 초기화 (연쇄 계산을 위해)
            state = CalculatorState(
                number1 = result.toPlainString(),
                number2 = "",
                operation = null
            )
        }
    }
}