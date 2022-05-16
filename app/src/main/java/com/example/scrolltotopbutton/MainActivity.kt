package com.example.scrolltotopbutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scrolltotopbutton.ui.theme.ScrollToTopButtonTheme
import kotlinx.coroutines.launch

enum class ButtonState {
    Collapsed,
    Expanded
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScrollToTopButtonTheme {

                val itemModels = buildList {
                    repeat(8) {
                        this += ListItemModel(title = "Title #$it")
                    }
                }

                val listState = rememberLazyListState()
                val currentState by remember {
                    derivedStateOf {
                        if (listState.firstVisibleItemIndex > 0) ButtonState.Expanded
                        else ButtonState.Collapsed
                    }
                }

                val scope = rememberCoroutineScope()
                val buttonHeight = 64.dp
                val buttonPadding = 32.dp
                val transition = updateTransition(currentState, label = "")
                val buttonOffset by transition.animateDp(label = "") {
                    when (it) {
                        ButtonState.Collapsed -> buttonHeight + buttonPadding
                        ButtonState.Expanded -> 0.dp
                    }
                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Box(modifier = Modifier
                        .fillMaxSize()) {

                        LazyColumn(
                            modifier = Modifier.fillMaxHeight(),
                            state = listState,
//                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = VerticalArrangementWithFooter,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(itemModels) { item -> ListItem(model = item) }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = buttonOffset)
                        ) {

                            Button(
                                onClick = {
                                    scope.launch {
                                        listState.animateScrollToItem(index = 0)
                                    }
                                },
                                modifier = Modifier
                                    .size(buttonHeight)
                                    .clip(CircleShape)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = ""
                                )
                            }
                            
                            Spacer(modifier = Modifier.size(buttonPadding))
                        }

                    }
                }
            }
        }
    }
}

object VerticalArrangementWithFooter: Arrangement.Vertical {

    override val spacing: Dp
        get() = 8.dp

    override fun Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
        var y = 0

        sizes.forEachIndexed { index, size ->
            outPositions[index] = y
            y += size
        }

        if (y < totalSize) {
            outPositions[outPositions.lastIndex] =
                totalSize - sizes.last()
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListItem(model: ListItemModel) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = model.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScrollToTopButtonTheme {

    }
}

data class ListItemModel(val title: String)