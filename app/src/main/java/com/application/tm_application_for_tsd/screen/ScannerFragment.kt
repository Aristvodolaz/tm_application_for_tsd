package com.application.tm_application_for_tsd.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.application.tm_application_for_tsd.R
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
        scannerViewModel.startScanning()

        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        binding.scanButton.setOnClickListener{
            navigateToScanTypeFragment()
        }

        // Подписка на данные сканера
        scannerViewModel.barcodeData.observe(viewLifecycleOwner) { barcode ->
            Toast.makeText(requireContext(), "Scanned: $barcode", Toast.LENGTH_SHORT).show()

            // Автоматическая отправка данных на сервер
            scannerViewModel.checkValidateBox(barcode, "")
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


        return binding.root
    }

    private fun navigateToScanTypeFragment() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, ScanTypeFragment())
            addToBackStack(null) // Добавляем в стек, чтобы можно было вернуться назад
        }
    }
    override fun onStart() {
        super.onStart()
        scannerViewModel.startScanning()
    }

    override fun onResume() {
        super.onResume()
        scannerViewModel.startScanning()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
