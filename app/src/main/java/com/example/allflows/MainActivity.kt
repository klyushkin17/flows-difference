package com.example.allflows

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.liveDataButton).setOnClickListener{
            viewModel.triggerLiveData()
        }

        findViewById<Button>(R.id.stateFlowButton).setOnClickListener{
            viewModel.triggerStateFlow()
        }

        findViewById<Button>(R.id.flowButton).setOnClickListener{
            lifecycleScope.launch {
                viewModel.triggerFlow().collectLatest {
                    findViewById<TextView>(R.id.tvFlow).text = it
                }
            }
        }

        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        viewModel.liveData.observe(this){
            findViewById<TextView>(R.id.tvLiveData).text = it
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stateFlow.collectLatest {
                    findViewById<TextView>(R.id.tvStateFlow).text = it
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.sharedFlow.collectLatest {
                    Snackbar.make(
                        findViewById(R.id.main),
                        it,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}