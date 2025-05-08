package com.jejuckl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.viewinterop.AndroidView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Jejuckl_temiTheme {
                TabletBackgroundScreen()
            }
        }
    }
}

@Composable
fun TabletBackgroundScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var showEducationDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg01),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        // 바둑판 형태 커스텀 배치
        CustomGridMenu(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            onFirstButtonClick = { showDialog = true },
            onEducationClick = { showEducationDialog = true }
        )

        if (showDialog) {
            AskAnythingDialog(onDismiss = { showDialog = false })
        }
        if (showEducationDialog) {
            EducationDialog(
                url = "https://원하는_교육안내_URL", // 실제 URL로 교체
                onDismiss = { showEducationDialog = false }
            )
        }
    }
}

@Composable
fun CustomGridMenu(
    modifier: Modifier = Modifier,
    onFirstButtonClick: () -> Unit,
    onEducationClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(start = 30.dp, end = 30.dp, top = 120.dp, bottom = 170.dp),
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
                    modifier = Modifier.weight(1f),
                    onClick = onEducationClick
                )
            }
            Column(
                modifier = Modifier.weight(1f),

            ) {
                MenuButton(
                    iconResId = R.drawable.btn02_02,
                    modifier = Modifier.weight(1f)
                )

                MenuButton(
                    iconResId = R.drawable.btn02_03,
                    modifier = Modifier.weight(1f)
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
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    iconResId = R.drawable.btn04_02,
                    modifier = Modifier.weight(1f)
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
fun AskAnythingDialog(onDismiss: () -> Unit) {
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
                .padding(start = 50.dp, end = 50.dp, top = 120.dp, bottom = 170.dp) // ✅ 여기에 패딩 추가
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp) // 내부 여백
                .fillMaxWidth() // 전체 너비 사용 (패딩 안쪽)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("무엇 이든 물어보세요...", color = Color.Gray, fontSize = 16.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "무엇이든지 물어보세요. 오늘도 좋은 하루 되세요",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("다시 질문하기", color = Color.White)
                }
            }
        }
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
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(start = 50.dp, end = 50.dp, top = 90.dp, bottom = 100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "교육안내",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
                // WebView 영역
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            loadUrl("https://www.naver.com")
                        }
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Jejuckl_temiTheme {
//        Greeting("Android")
    }
}