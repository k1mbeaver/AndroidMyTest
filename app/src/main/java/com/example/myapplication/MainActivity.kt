package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.Model.CalculatorAction
import com.example.myapplication.Model.CalculatorState
import com.example.myapplication.ViewModel.CalculatorViewModel

// 1. 하단 탭에 들어갈 5가지 실험실 메뉴 정의 (C#의 enum과 유사)
enum class LabTab(val title: String, val icon: ImageVector) {
    CALCULATOR("계산기", Icons.Default.Edit),
    SENSOR("센서", Icons.Default.LocationOn),
    GRAPHICS("그래픽", Icons.Default.Create),
    NETWORK("통신", Icons.Default.Share),
    SYSTEM("시스템", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LabAppShell()
            }
        }
    }
}

@Composable
fun LabAppShell() {
    // WPF의 INotifyPropertyChanged처럼, 이 값이 변하면 화면이 자동으로 갱신됨
    var currentTab by remember { mutableStateOf(LabTab.CALCULATOR) }

    // Scaffold: 상단바, 하단바, 메인 콘텐츠 영역을 자동으로 잡아주는 '앱의 뼈대' 컨테이너
    Scaffold(
        bottomBar = {
            NavigationBar {
                LabTab.values().forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = currentTab == tab, // 현재 선택된 탭인지 확인 (RadioButton의 IsChecked)
                        onClick = { currentTab = tab } // 클릭 시 상태 변경
                    )
                }
            }
        }
    ) { paddingValues ->
        // 하단바 높이만큼의 패딩을 피해서 메인 컨텐츠 배치
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // C#의 switch 패턴 매칭과 동일
            when (currentTab) {
                LabTab.CALCULATOR -> CalculatorScreenRoot()
                LabTab.SENSOR -> PlaceholderScreen("센서 데이터 읽기 (준비중)")
                LabTab.GRAPHICS -> PlaceholderScreen("커스텀 2D 렌더링 (준비중)")
                LabTab.NETWORK -> PlaceholderScreen("백그라운드 통신 (준비중)")
                LabTab.SYSTEM -> PlaceholderScreen("디바이스 제어 (준비중)")
            }
        }
    }
}

// 순수 UI 컴포저블: 프리뷰(Preview)나 테스트가 매우 쉬워짐
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = state.displayText.ifEmpty { "0" }, // ViewModel의 상태를 읽기만 함
            fontSize = 48.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                row.forEach { btn ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .background(Color.LightGray)
                            .clickable {
                                // 로직 대신 '어떤 액션이 일어났는지'만 ViewModel로 던짐
                                when (btn) {
                                    "C" -> onAction(CalculatorAction.Clear)
                                    "=" -> onAction(CalculatorAction.Calculate)
                                    "+", "-", "*", "/" -> onAction(CalculatorAction.Operation(btn))
                                    else -> onAction(CalculatorAction.Number(btn))
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) { Text(btn, fontSize = 24.sp) }
                }
            }
        }
    }
}

// 최상위 컴포저블: ViewModel을 주입받고 State와 Event만 하위로 넘김 (State Hoisting)
@Composable
fun CalculatorScreenRoot(
    viewModel: CalculatorViewModel = viewModel() // 안드로이드 기본 DI 제공
) {
    CalculatorScreen(
        state = viewModel.state,
        onAction = viewModel::onAction // 메서드 레퍼런스 전달
    )
}

// 3. 아직 구현되지 않은 탭을 위한 임시 화면
@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title, fontSize = 20.sp, color = Color.Gray)
    }
}