package com.example.chordprogressionmemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

enum class PlaybackState {
    UNPLAYABLE, PLAYABLE, PLAYING
}

class ChordProgressionViewModel(
    application: Application,
    private val chordInfoDao: ChordInfoDao,
    progInfoId: Long
) : AndroidViewModel(application) {

    val chordListState: StateFlow<List<ChordInfo>> =
        chordInfoDao.getAllOrderedByIndex(progInfoId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val currentPosition = MutableStateFlow<Int>(-1)
    private var chordPlayerJob: Job? = null
    private var isPlaying = MutableStateFlow<Boolean>(false)

    val playbackState: StateFlow<PlaybackState> =
        combine(chordListState, isPlaying) { chordList, isPlaying ->
            when {
                chordList.isEmpty() -> PlaybackState.UNPLAYABLE
                !isPlaying -> PlaybackState.PLAYABLE
                else -> PlaybackState.PLAYING
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, PlaybackState.UNPLAYABLE)

    val enableDelete: StateFlow<Boolean> = playbackState.map {state ->
        when {
            state == PlaybackState.PLAYING -> false
            else -> true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private fun resetPosition() {
        currentPosition.value = -1
    }

    fun deleteChordInfo(chordInfo: ChordInfo) {
        chordInfoDao.deleteWithOrderIndexUpdated(chordInfo)
    }

    private fun incrementPosition() {
        currentPosition.value += 1
    }

    private fun hasNextPosition(): Boolean
    {
        val chordList = chordListState.value
        return currentPosition.value < chordList.count()
    }

    fun setPosition(position: Int) {
        val chordList = chordListState.value
        if (position >= chordList.count()) {
            return
        }
        currentPosition.value = position
    }

    fun playChordProgression() {
        if (playbackState.value != PlaybackState.PLAYABLE) {
            return
        }

        chordPlayerJob = viewModelScope.launch {
            isPlaying.value = true
            while (playNextChord()) {
                /* DO NOTHING */
            }

            // 一時停止ボタンが押されてジョブがキャンセルされた場合も、必ず実行される
            if(!hasNextPosition()) {
                resetPosition()
            }
            chordPlayerJob = null
            isPlaying.value = false
        }
    }

    fun stopChordProgression() {
        viewModelScope.launch {
            chordPlayerJob?.cancelAndJoin()
            chordPlayerJob = null
        }
    }

    private suspend fun playNextChord(): Boolean {
        assert(hasNextPosition())
        if (currentPosition.value < 0) {
            setPosition(0)
        }

        val chordList = chordListState.value
        val player = ChordPlayer(getApplication(), chordList[currentPosition.value])
        var hasNextChord = false

        //val job = viewModelScope.launch {
        try {
            player.waitForReady()
            player.play()
            player.stop(1_500L)
            incrementPosition()
            hasNextChord = hasNextPosition()
        } catch (e: CancellationException) {
            player.stop(0L)
        } finally {
            player.release()
        }

        return hasNextChord
    }
}