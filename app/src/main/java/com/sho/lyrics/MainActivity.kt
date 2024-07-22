package com.sho.lyrics

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sho.lyrics.ui.theme.LyricsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LyricsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun App() {
    var url by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("Lyrics will appear here") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            ClickableText(result)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 输入框
//        Box(
//            modifier = Modifier
//                .weight(0.2f)
//                .fillMaxWidth()
//        ) {
//        }


        InputTextField(url) {
            newUrl -> url = newUrl
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            ClearButton {
                url = ""
                result = "Lyrics will appear here"  // Reset result text
            }
            // 按钮
            ScrapeButton(url) { newResult -> result = newResult }
        }
    }
}


@Composable
fun ScrapeButton(url: String, onGenerateResult: (String) -> Unit) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scrapeLyrics(url, onGenerateResult, scope)
        }
    ) {
        Text("Scrape")
    }
}


@Composable
fun ClickableText(result: String) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = result,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { copyToClipboard(context, result) },
            overflow = TextOverflow.Ellipsis
        )
    }
}



@Composable
fun InputTextField(url: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = url,
        onValueChange = onValueChange,
        label = { Text("Enter URL") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ClearButton(onClear: () -> Unit) {
    IconButton(
        onClick = onClear,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}


fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Copied Text", text))
}


fun scrapeLyrics(url: String, onGenerateResult: (String) -> Unit, scope: CoroutineScope) {
    val (scraper, validUrlPrefix) = when {
        url.startsWith("https://www.uta-net.com") -> {
            Pair(UtaNetSingle()::scrapeLyrics, "https://www.uta-net.com")
        }
        url.startsWith("https://genius.com") || url.startsWith("https://m.genius.com") -> {
            Pair(GeniusSingle()::scrapeLyrics, "https://genius.com")
        }
        else -> {
            onGenerateResult("Invalid URL")
            return
        }
    }

    scope.launch {
        try {
            val result = withContext(Dispatchers.IO) {
                scraper(url)
            }
            onGenerateResult(result)
        } catch (e: Exception) {
            onGenerateResult("Failed to retrieve lyrics: ${e.message}")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AppPreview() {
    LyricsTheme {
        App()
    }
}