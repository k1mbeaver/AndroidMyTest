package com.example.myapplication.Model

// 1. UI가 관찰할 상태 (WPF의 Binding 대상)
data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: String? = null,
    val isError: Boolean = false
) {
    // UI에 보여질 최종 문자열을 계산하는 프로퍼티
    val displayText: String
        get() = if (isError) "Error"
        else number1 + (operation ?: "") + number2.ifEmpty { "" }
}

// 2. 사용자가 할 수 있는 모든 행동 (WPF의 ICommand 역할)
sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operation(val op: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Calculate : CalculatorAction()
}