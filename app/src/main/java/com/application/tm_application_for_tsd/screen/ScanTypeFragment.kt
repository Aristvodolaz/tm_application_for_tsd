package com.application.tm_application_for_tsd.screen

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.application.tm_application_for_tsd.R
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.platform.ComposeView

@AndroidEntryPoint
class ScanTypeFragment : Fragment() {

    private val scannerViewModel: ScannerViewModel by viewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent {
                ScanTypeScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        scannerViewModel.startScanning() // Запуск сканирования автоматически при старте фрагмента
    }
}

@Composable
fun ScanTypeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Картинка
        Image(
            painter = painterResource(id = R.drawable.scan_image), // Замените на вашу картинку
            contentDescription = "Scan Box",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp)
        )

        // Подпись
        Text(
            text = "Отсканируйте нужный короб",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScanTypeScreenPreview() {
    ScanTypeScreen()
}
