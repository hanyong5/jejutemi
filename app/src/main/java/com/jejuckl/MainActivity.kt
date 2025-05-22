package com.jejuckl

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jejuckl.ui.theme.Jejuckl_temiTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jejuckl.viewmodel.MainViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import kotlinx.coroutines.delay



class MainActivity : ComponentActivity(), OnRobotReadyListener, OnGoToLocationStatusChangedListener {
    private var recognizedText by mutableStateOf("")
    private lateinit var robot: Robot
    private val viewModel: MainViewModel by viewModels()



//    val viewModel: MainViewModel by viewModel()

    // Google STT Launcherf
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                Log.d("GoogleSTT", "인식된 텍스트:f $spokenText")
                recognizedText = spokenText
                viewModel.fetchAIResponse(spokenText) // 🔥 서버에 JSON 요청
            }
        }
    }

    // Google STT 음성 인식 시작
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "대화 내용을 말씀해 주세요...")
        }

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("GoogleSTT", "Google 음성 인식 기능이 사용 불가능합니다.")
            Toast.makeText(this, "Google 음성 인식 기능이 사용 불가능합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        robot = Robot.getInstance()

        // ⬇️ 리스너 등록 필수
        robot.addOnGoToLocationStatusChangedListener(this)


        enableEdgeToEdge()
        setContent {
//            val viewModel: MainViewModel = viewModel()
            val aiResponse by viewModel.aiResponse.collectAsState()
            Jejuckl_temiTheme {
                TabletBackgroundScreen(
                    onStartListening = { startVoiceRecognition() },
                    recognizedText = recognizedText,
                    aiResponse = aiResponse,
                    onMoveToLocation = { locationName -> moveTemiToLocation(locationName) }
                )
            }
        }
    }

    private fun moveTemiToLocation(locationName: String) {
        try {
            // 테미 로봇이 준비되었는지 확인
            if (robot.isReady) {
                // 위치로 이동 명령 실행
                robot.goTo(locationName)
                Toast.makeText(this, "${locationName}으로 이동을 시작합니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "테미 로봇이 준비되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("TemiControl", "테미 이동 중 오류 발생: ${e.message}")
            Toast.makeText(this, "테미 이동 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            Log.d("TemiControl", "테미 로봇이 준비되었습니다.")
        }
    }


    private var hasCompleted = false

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        Log.d("TemiControl", "상태 수신: $status, 위치: $location")

        if (status == "start") {
            Log.d("TemiControl", "이동 시작: $location")
            hasCompleted = false  // 이동이 시작되면 완료 상태 초기화
        }
        else if (status == "complete") {
            Log.d("TemiControl", "상태: hasCompleted = $hasCompleted")

            if (!hasCompleted) {
                hasCompleted = true
                Log.d("TemiControl", "이동 완료: $location")

                // 로봇 음성 안내도 가능
                // Robot.getInstance().speak(TtsRequest.create("원하시는 위치에 도착하였습니다. 감사합니다.", false))

                Handler(Looper.getMainLooper()).postDelayed({
                    val target = "home"

                    if (robot.locations.contains(target)) {
                        // ✅ location == "home"이면 이동 생략
                        if (location == "home") {
                            Log.d("TemiControl", "이미 $target 위치에 있음 - 이동 생략")
                            hasCompleted = true
                        } else {
                            robot.goTo(target)
                            Log.d("TemiControl", "$target 위치로 이동 명령 실행")
                        }
                    } else {
                        Log.e("TemiControl", "$target 위치가 Temi에 등록되어 있지 않습니다.")
                    }

                    Log.d("TemiControl", "10초 후 홈베이스 복귀 로직 실행됨")
                }, 10000)
            }
        }
        else if (status == "abort") {
            Log.d("TemiControl", "이동 중단: $location")
        }
        else if (status == "error") {
            Log.e("TemiControl", "이동 오류: $location - $description")
        }
    }

}

@Composable
fun TabletBackgroundScreen(
    onStartListening: () -> Unit,
    recognizedText: String,
    aiResponse: String, // ✅ 추가
    onMoveToLocation: (String) -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    var showBtn02_02Dialog by remember { mutableStateOf(false) }
    var showBtn02_03Dialog by remember { mutableStateOf(false) }
    var showBtn03_01Dialog by remember { mutableStateOf(false) }

    var showEducationDialog by remember { mutableStateOf(false) }
    var showSTTDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg01),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        CustomGridMenu(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            onFirstButtonClick = {
                onStartListening()
                showSTTDialog = true
            },
            onEducationClick = { showEducationDialog = true },
            onBtn02_02Click = { showBtn02_02Dialog = true },
            onBtn02_03Click = { showBtn02_03Dialog = true },
            onBtn03_01Click = { showBtn03_01Dialog = true },
            onBtn04_01Click = { showInfoDialog = true }
        )

        if (showEducationDialog) {
            EducationDialog(
                url = "https://jejutemi.netlify.app/edu",
                onDismiss = { showEducationDialog = false },
                onMoveToLocation = onMoveToLocation // ✅ 추가
            )
        }
        if (showSTTDialog) {

            STTDialog(
                viewModel = viewModel,
                onDismiss = { showSTTDialog = false },
                onStartListening = onStartListening,
                recognizedText = recognizedText,
                aiResponse = aiResponse
            )
        }
        if (showBtn02_02Dialog) {
            EducationDialog(
                url = "https://jejutemi.netlify.app/room",
                onDismiss = { showBtn02_02Dialog = false },
                onMoveToLocation = onMoveToLocation // ✅ 추가
            )
        }
        if (showBtn02_03Dialog) {
            EducationDialog(
                url = "https://jejutemi.netlify.app/equip",
                onDismiss = { showBtn02_03Dialog = false },
                onMoveToLocation = onMoveToLocation // ✅ 추가
            )
        }
        if (showBtn03_01Dialog) {
            EducationDialog(
                url = "https://jejutemi.netlify.app/usage",
                onDismiss = { showBtn03_01Dialog = false },
                onMoveToLocation = onMoveToLocation // ✅ 추가
            )
        }
        if (showInfoDialog) {
            InfoDialog(onDismiss = { showInfoDialog = false }, onMoveToLocation = onMoveToLocation)
        }
    }
}

@Composable
fun CustomGridMenu(
    modifier: Modifier = Modifier,
    onFirstButtonClick: () -> Unit,
    onEducationClick: () -> Unit,
    onBtn02_02Click: () -> Unit,
    onBtn02_03Click: () -> Unit,
    onBtn03_01Click: () -> Unit,
    onBtn04_01Click: () -> Unit
) {
    Row(
        modifier = modifier.padding(start = 40.dp, end = 40.dp, top = 140.dp, bottom = 170.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽 파란 버튼 (세로로 긴 형태)
        MenuButton(
            iconResId = R.drawable.btn01_01,
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight(),
            bgColor =  Color(0xFF7B39F6),
            bgAlpha = 0.7f,
            onClick = onFirstButtonClick
        )

        // 가운데 버튼 그룹
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    iconResId = R.drawable.btn02_01,
                    modifier = Modifier.weight(0.9f),
                    onClick = onEducationClick
                )
            }
            Column(
                modifier = Modifier.weight(1f),

                ) {
                MenuButton(
                    iconResId = R.drawable.btn02_02,
                    modifier = Modifier.weight(1f),
                    onClick = onBtn02_02Click
                )
                Spacer(modifier = Modifier.height(16.dp))

                MenuButton(
                    iconResId = R.drawable.btn02_03,
                    modifier = Modifier.weight(1f),
                    onClick = onBtn02_03Click
                )
            }



        }

        // 오른쪽 하늘색 버튼 그룹 (2x2)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    iconResId = R.drawable.btn04_01,
                    modifier = Modifier.weight(0.8f),
                    onClick = onBtn03_01Click
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    iconResId = R.drawable.btn04_02,
                    modifier = Modifier.weight(0.8f),
                            onClick = onBtn04_01Click
                )
            }
        }
    }
}

@Composable
fun MenuButton(
    iconResId: Int,
    modifier: Modifier = Modifier,
    bgColor: Color = Color(0xFF47A5F1),
    bgAlpha: Float = 0.4f,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor.copy(alpha = bgAlpha)) // ✅ 배경색 + 투명도
            .let {
                if (onClick != null) it.clickable { onClick() } else it
            }
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}



@Composable
fun EducationDialog(
    url: String,
    onDismiss: () -> Unit,
    onMoveToLocation: (String) -> Unit // ✅ 추가
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {


        // 메인 다이얼로그 박스
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(start = 50.dp, end = 50.dp, top = 90.dp, bottom = 80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFDAEBFE))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // WebView
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            loadUrl(url)
                        }
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxWidth()
                )
            }
        }
        // ✅ 상단 오른쪽에 고정된 동그라미 닫기 버튼
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 70.dp, end = 30.dp)
                .size(60.dp) // 동그라미 크기
                .background(Color.LightGray, shape = CircleShape) // 원형 배경
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }



        RoomButtonSection(
            modifier = Modifier.align(Alignment.BottomCenter),
            onMoveToLocation = onMoveToLocation
        )


    }
}


@Composable
fun RoomButtonSection(
    modifier: Modifier = Modifier,
    onMoveToLocation: (String) -> Unit
) {
    Box(
        modifier = modifier
            .padding(bottom = 70.dp, start = 130.dp, end = 130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x5E2872F3))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(3.6f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoomButton("창작실", Modifier.weight(1f), onMoveToLocation)
                    RoomButton("회의실", Modifier.weight(1f), onMoveToLocation)
                    RoomButton("교육실1", Modifier.weight(1f), onMoveToLocation)
                    RoomButton("교육실2", Modifier.weight(1f), onMoveToLocation)
                }
                RoomButton(
                    "편집실 / 장비보관실 / 스튜디오 / 머들코지2",
                    modifier = Modifier.fillMaxWidth(),
                    onMoveToLocation = onMoveToLocation
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoomButton("콘텐츠공작소", modifier = Modifier.fillMaxWidth(), onMoveToLocation)
                RoomButton("정수기", modifier = Modifier.fillMaxWidth(), onMoveToLocation)
            }

            Column(
                modifier = Modifier.weight(0.8f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoomButton("입주실", modifier = Modifier.fillMaxWidth(), onMoveToLocation)
                RoomButton("사무실", modifier = Modifier.fillMaxWidth(), onMoveToLocation)
            }
        }
    }
}



@Composable
fun RoomButton(label: String, modifier: Modifier = Modifier,onMoveToLocation: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    
    Button(
        onClick = { showDialog = true },
        modifier = modifier.height(48.dp)
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("${label}로 이동 하시겠습니까?") },
            text = { 
                Column {
                    //Text("${label}으로 테미를 이동하시겠습니까?")
//                    Spacer(modifier = Modifier.height(8.dp))
                    Text("이동버튼을 클릭하시면 원하시는 곳으로 이동합니다.", color = Color.Black)
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("취소")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {

                            // TODO: 테미 이동 명령 실행
                            showDialog = false;

                            val destination = if (label == "편집실 / 장비보관실 / 스튜디오 / 머들코지2") {
                                "편집실"
                            } else {
                                label
                            }
                            onMoveToLocation(destination)



                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("이동")
                    }
                }
            }
        )
    }
}




@Composable
fun STTDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onStartListening: () -> Unit,
    recognizedText: String,
    aiResponse: String,
) {


    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        // 메인 다이얼로그 박스
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(start = 80.dp, end = 80.dp, top = 120.dp, bottom = 170.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFDAEBFE))
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    when {
                        aiResponse.isBlank() -> {
                            // ✨ 점멸 애니메이션: 응답 대기 중
                            val infiniteTransition = rememberInfiniteTransition()
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            Text(
                                text = "지금 응답 준비 중...",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray.copy(alpha = alpha)
                            )
                        }

                        aiResponse.contains("중단", ignoreCase = true) ||
                                aiResponse.contains("에러", ignoreCase = true) ||
                                aiResponse.length < 5 -> {
                            // ⚠️ 오류 또는 응답 멈춤으로 판단
                            Text(
                                text = "다시 말하기 버튼을 클릭 하세요",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Red
                            )
                        }

                        else -> {
                            // ✅ 정상 응답 출력
                            TypingText(fullText = aiResponse)

                            val ttsRequest = remember(aiResponse) {
                                TtsRequest.create(aiResponse, false)
                            }

                            LaunchedEffect(ttsRequest) {
                                Robot.getInstance().speak(ttsRequest)
                            }
                        }
                    }

                }

            }
        }

        // ✅ 이 부분을 BoxScope 안으로 이동
        IconButton(
            onClick = {
                forceStopTTS()
                viewModel.clearAIResponse() // ✅ aiResponse 초기화
                onDismiss()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 50.dp)
                .size(60.dp)
                .background(Color.LightGray, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }

        Button(
            onClick = onStartListening,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp, start = 150.dp, end = 150.dp)
        ) {
            Text("다시 말하기", color = Color.White, fontSize = 30.sp,modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
    }
}


fun forceStopTTS() {
    // TTS 중단을 위한 우회 덮어쓰기
    Robot.getInstance().speak(TtsRequest.create("중단합니다.", false))
}

@Composable
fun TypingText(
    fullText: String,
    typingDelay: Long = 130L // 한 글자당 지연 시간
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(fullText) {
        displayedText = "" // 처음부터 시작
        for (i in fullText.indices) {
            displayedText += fullText[i]
            delay(typingDelay)
        }
    }

    Text(
        text = displayedText,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        color = Color(0xFF888888),
        modifier = Modifier
            .padding(30.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        textAlign = TextAlign.Left,
        lineHeight = 70.sp
    )
}

@Composable
fun InfoDialog(onDismiss: () -> Unit, onMoveToLocation: (String) -> Unit) {
    val tabTitles = listOf("1층 안내", "B1층 안내", "이용방법")
    var selectedTab by remember { mutableStateOf(1) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent() // 💡 모든 터치 이벤트를 여기서 소비
                    }
                }
            }
//            .clickable(enabled = true, onClick = {}) // 💡 터치 이벤트 소모
    ) {

        // 메인 다이얼로그 박스
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(start = 50.dp, end = 50.dp, top = 50.dp, bottom = 80.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(16.dp, Color(0xFFDAEBFE), RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(40.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ✅ 타이틀과 밑줄 추가
                Column(
                    modifier = Modifier.fillMaxWidth(),

                ) {
                    Text(
                        text = "시설안내 및 에스코트",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(modifier = Modifier.fillMaxSize()) {

                    // 좌측 탭 메뉴 (세로 버튼 리스트)
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .width(200.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Button(
                                onClick = { selectedTab = index },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTab == index) Color(0xFF2196F3) else Color.White,
                                    contentColor = if (selectedTab == index) Color.White else Color.Black
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(title,fontSize = 28.sp)
                            }
                        }
                    }

                    // 우측 콘텐츠 (이미지 or 설명)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = 24.dp)
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        when (selectedTab) {
                            0 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.building01),
                                    contentDescription = "1층 안내",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            1 -> {




//                            Image(
//                                painter = painterResource(id = R.drawable.building02),
//                                contentDescription = "지하1층 안내",
//                                contentScale = ContentScale.Fit,
//                                modifier = Modifier.fillMaxSize()
//                            )



                                var showDialog by remember { mutableStateOf(false) }
                                var targetRoom by remember { mutableStateOf("") }

                                Box(modifier = Modifier.fillMaxSize()) {
                                    // 🖼 지하 1층 안내 이미지
                                    Image(
                                        painter = painterResource(id = R.drawable.building02),
                                        contentDescription = "지하1층 안내",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )



                                    // ✅ 위치 이동 확인 다이얼로그
                                    if (showDialog) {
                                        AlertDialog(
                                            onDismissRequest = { showDialog = false },
                                            title = { Text("테미 이동") },
                                            text = { Text("$targetRoom 으로 이동하시겠습니까?") },
                                            confirmButton = {
                                                Button(
                                                    onClick = {
                                                        showDialog = false
                                                        onMoveToLocation(targetRoom)
                                                    }
                                                ) {
                                                    Text("이동")
                                                }
                                            },
                                            dismissButton = {
                                                OutlinedButton(onClick = { showDialog = false }) {
                                                    Text("취소")
                                                }
                                            }
                                        )
                                    }
                                }

                                // 위치 버튼 1
                                LocationMarker(x = 410.dp, y = 120.dp, label = "1", room = "스튜디오", onMoveToLocation)
                                LocationMarker(x = 410.dp, y = 300.dp, label = "2", room = "편집실", onMoveToLocation)
                                LocationMarker(x = 620.dp, y = 180.dp, label = "3", room = "편집실", onMoveToLocation)
                                LocationMarker(x = 750.dp, y = 360.dp, label = "4", room = "편집실", onMoveToLocation)

                            }
                            2 -> {
                                Image(
                                    painter = painterResource(id = R.drawable.building_detail),
                                    contentDescription = "안내",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }


        }

        // 닫기 버튼
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 30.dp)
                .size(60.dp)
                .background(Color.LightGray, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }

        // 하단 룸 버튼들
//        RoomButtonSection(
//            modifier = Modifier.align(Alignment.BottomCenter),
//            onMoveToLocation = onMoveToLocation
//        )
    }
}



@Composable
fun LocationMarker(
    x: Dp,
    y: Dp,
    label: String,
    room: String,
    onMoveToLocation: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Red.copy(alpha = 0.8f))
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
    }



    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "$room 로 이동하시겠습니까?",
                    modifier = Modifier.width(600.dp) // ✅ 너비 설정
                )
            },
            text = { Text("이동버튼을 클릭하시면 원하시는 곳으로 이동합니다.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onMoveToLocation(room)
                }) {
                    Text("이동")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}







