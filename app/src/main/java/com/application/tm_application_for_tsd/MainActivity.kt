package com.application.tm_application_for_tsd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.application.tm_application_for_tsd.screen.ScannerFragment
import com.application.tm_application_for_tsd.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Загружаем первый фрагмент
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, ScannerFragment())
            }
        }
    }
}
