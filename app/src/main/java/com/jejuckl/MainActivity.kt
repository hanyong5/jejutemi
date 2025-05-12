package com.jejuckl

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.ui.viewinterop.AndroidView


class MainActivity : ComponentActivity() {
    private var recognizedText by mutableStateOf("버튼을 눌러 말하세요.")



    // Google STT Launcher
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                Log.d("GoogleSTT", "인식된 텍스트: $spokenText")
                recognizedText = spokenText
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
        enableEdgeToEdge()
        setContent {
            Jejuckl_temiTheme {
                TabletBackgroundScreen(
                    onStartListening = { startVoiceRecognition() },
                    recognizedText = recognizedText
                )
            }
        }
    }
}

@Composable
fun TabletBackgroundScreen(
    onStartListening: () -> Unit,
    recognizedText: String
) {
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
            modifier = Modifier.fillMaxSize().padding(24.dp),
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
                url = "https://www.naver.com",
                onDismiss = { showEducationDialog = false }
            )
        }
        if (showSTTDialog) {
            STTDialog(
                onDismiss = { showSTTDialog = false },
                onStartListening = onStartListening,
                recognizedText = recognizedText
            )
        }
        if (showBtn02_02Dialog) {
            EducationDialog(
                url = "https://winloc.netlify.app/",
                onDismiss = { showBtn02_02Dialog = false }
            )
        }
        if (showBtn02_03Dialog) {
            EducationDialog(
                url = "https://example.com/edu02_03",
                onDismiss = { showBtn02_03Dialog = false }
            )
        }
        if (showBtn03_01Dialog) {
            EducationDialog(
                url = "https://example.com/edu03_01",
                onDismiss = { showBtn03_01Dialog = false }
            )
        }
        if (showInfoDialog) {
            InfoDialog(onDismiss = { showInfoDialog = false })
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
            bgColor = Color.Blue,
            bgAlpha = 0.7f,
            onClick = onFirstButtonClick
        )

        // 가운데 보라색 버튼 그룹
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
    bgColor: Color = Color(0xFF2196F3),
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
    onDismiss: () -> Unit
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
                .padding(top = 70.dp, end = 50.dp)
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




        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp, start = 130.dp, end = 130.dp)
                .background(Color.Yellow)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(8.dp)
//                    .border(1.dp, Color.Black)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(3.6f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoomButton("창작실", Modifier.weight(1f))
                        RoomButton("회의실", Modifier.weight(1f))
                        RoomButton("교육실1", Modifier.weight(1f))
                        RoomButton("교육실2", Modifier.weight(1f))
                    }
                    RoomButton(
                        "편집실 / 장비보관실 / 스튜디오 / 머들코지2",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoomButton("콘텐츠공작소", modifier = Modifier.fillMaxWidth())
                    RoomButton("정수기", modifier = Modifier.fillMaxWidth())
                }

                Column(
                    modifier = Modifier.weight(0.8f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoomButton("입주실", modifier = Modifier.fillMaxWidth())
                    RoomButton("사무실", modifier = Modifier.fillMaxWidth())
                }
            }
        }

    }
}

    @Composable
    fun RoomButton(label: String, modifier: Modifier = Modifier) {
        Button(
            onClick = { /* TODO: 클릭 이벤트 처리 */ },
            modifier = modifier.height(48.dp)
        ) {
            Text(text = label, fontSize = 16.sp,fontWeight = FontWeight.Bold)
        }
    }




@Composable
fun STTDialog(
    onDismiss: () -> Unit,
    onStartListening: () -> Unit,
    recognizedText: String
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
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(start = 50.dp, end = 50.dp, top = 120.dp, bottom = 170.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "음성 인식 결과",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = recognizedText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onStartListening,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("다시 말하기", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    val tabTitles = listOf("1층 안내", "2층 안내", "시설안내")
    var selectedTab by remember { mutableStateOf(0) }
    val tabContents = listOf(
        "1층에는 안내데스크, 로비, 카페가 있습니다.",
        "2층에는 회의실, 교육실, 사무실이 있습니다.",
        "시설 이용시간은 09:00~18:00입니다."
    )
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
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(start = 50.dp, end = 50.dp, top = 90.dp, bottom = 80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFDAEBFE))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // 좌측 탭
                Column(
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top
                ) {
                    tabTitles.forEachIndexed { idx, title ->
                        Button(
                            onClick = { selectedTab = idx },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == idx) Color(0xFF2196F3) else Color.White,
                                contentColor = if (selectedTab == idx) Color.White else Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(title, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                // 우측 내용
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 24.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = tabContents[selectedTab],
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                }
            }
            // 하단 RoomButton 레이어 (교육안내와 동일)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp, start = 80.dp, end = 80.dp)
                    .background(Color.Yellow)
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
                            RoomButton("창작실", Modifier.weight(1f))
                            RoomButton("회의실", Modifier.weight(1f))
                            RoomButton("교육실1", Modifier.weight(1f))
                            RoomButton("교육실2", Modifier.weight(1f))
                        }
                        RoomButton(
                            "편집실 / 장비보관실 / 스튜디오 / 머들코지1",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoomButton("콘텐츠공작소", modifier = Modifier.fillMaxWidth())
                        RoomButton("정수기", modifier = Modifier.fillMaxWidth())
                    }
                    Column(
                        modifier = Modifier.weight(0.8f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoomButton("입주실", modifier = Modifier.fillMaxWidth())
                        RoomButton("사무실", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            // 상단 닫기 버튼
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(48.dp)
                    .background(Color.LightGray, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}






