package ru.potemkin.coroutinestart

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {
    private val parentJob = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(LOG_TAG, "Exception caught $throwable")
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob + exceptionHandler)
    fun method() {
        val childJob1 = coroutineScope.launch {
            delay(3000)
            Log.d(LOG_TAG, "First coroutine finished")
        }

        val childJob2 = coroutineScope.launch {
            delay(2000)
            Log.d(LOG_TAG, "Second coroutine finished")
        }
        val childJob3 = coroutineScope.async {
            //async {
            delay(1000)
            error()
            Log.d(LOG_TAG, "Third coroutine finished")
        }
        coroutineScope.launch {
            try {
                childJob3.await()
            } catch (e:java.lang.Exception){

            }
        }

    }

    private fun error() {
        throw java.lang.RuntimeException()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }


    companion object {
        private const val LOG_TAG = "MainViewModel"
    }
}