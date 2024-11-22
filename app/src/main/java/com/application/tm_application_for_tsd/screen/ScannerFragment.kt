package com.application.tm_application_for_tsd.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.application.tm_application_for_tsd.databinding.FragmentScannerBinding
import com.application.tm_application_for_tsd.viewModel.ScannerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    private val scannerViewModel: ScannerViewModel by viewModels()
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        // Подписка на данные сканера
        scannerViewModel.barcodeData.observe(viewLifecycleOwner) { barcode ->
            binding.barcodeTextView.text = barcode
            Toast.makeText(requireContext(), "Scanned: $barcode", Toast.LENGTH_SHORT).show()

            // Автоматическая отправка данных на сервер
            scannerViewModel.checkValidateBox(barcode)
        }

        // Подписка на результаты API
        scannerViewModel.apiResponse.observe(viewLifecycleOwner) { response ->
            if (response.valid) {
                Toast.makeText(requireContext(), "Barcode sent successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to send barcode: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Подписка на ошибки
        scannerViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
        }

        // Кнопка для начала сканирования
        binding.startScanButton.setOnClickListener {
            scannerViewModel.startScanning()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
