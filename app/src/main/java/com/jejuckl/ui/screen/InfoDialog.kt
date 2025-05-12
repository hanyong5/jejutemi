package com.jejuckl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

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
                            "편집실 / 장비보관실 / 스튜디오 / 머들코지",
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

@Composable
fun RoomButton(label: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* TODO: 클릭 이벤트 처리 */ },
        modifier = modifier.height(48.dp)
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
} 