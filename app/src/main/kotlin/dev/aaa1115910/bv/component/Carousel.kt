package dev.aaa1115910.bv.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Carousel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeCarousel(
    modifier: Modifier = Modifier
) {
    val backgrounds = listOf(
        Color.Red.copy(alpha = 0.3f),
        Color.Yellow.copy(alpha = 0.3f),
        Color.Green.copy(alpha = 0.3f)
    )

    Carousel(
        itemCount = backgrounds.size,
        modifier = modifier
            .height(300.dp)
            .fillMaxWidth(),
    ) { itemIndex ->
        Box(
            modifier = Modifier
                .background(backgrounds[itemIndex])
                .border(2.dp, Color.White.copy(alpha = 0.5f))
                .fillMaxSize()
        ) {
            CarouselCard()
        }
    }
}

@Composable
private fun CarouselCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .padding(40.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        var isFocused by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = if (isFocused) Color.Red else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
        ) {
            Button(
                onClick = { },
                modifier = Modifier
                    .onFocusChanged { isFocused = it.isFocused }
                    .padding(vertical = 2.dp, horizontal = 5.dp)
            ) {
                Text(text = "Play")
            }
        }
    }
}