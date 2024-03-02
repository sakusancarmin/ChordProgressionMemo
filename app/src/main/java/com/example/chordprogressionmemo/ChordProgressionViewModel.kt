package com.example.chordprogressionmemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ChordProgressionViewModel(
    application: Application,
    private val chordInfoDao: ChordInfoDao,
    progInfoId: Long
) : AndroidViewModel(application) {

    val chordListState: StateFlow<List<ChordInfo>> =
        chordInfoDao.getAllOrderedByIndex(progInfoId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val currentPosition = MutableStateFlow<Int>(-1)
    private var isPlaying = MutableStateFlow<Boolean>(false)

    val enablePlay: StateFlow<Boolean> = chordListState.combine(isPlaying) { chordList, isPlaying ->
            chordList.isNotEmpty() && !(isPlaying)
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val enableDelete: StateFlow<Boolean> = isPlaying.map {
        !(isPlaying.value)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private fun resetPosition() {
        currentPosition.value = -1
    }

    fun deleteChordInfo(chordInfo :ChordInfo)
    {
        chordInfoDao.deleteWithOrderIndexUpdated(chordInfo)
    }

    private fun incrementPosition(): Boolean {
        val chordList = chordListState.value
        currentPosition.value += 1
        return currentPosition.value < chordList.count()
    }

    fun setPosition(position: Int)
    {
        val chordList = chordListState.value
        if (position >= chordList.count()) {
            return
        }
        currentPosition.value = position
    }

    fun playChordProgression()
    {
        if (!(enablePlay.value)) {
            return
        }

        viewModelScope.launch {
            isPlaying.value = true
            while(playNextChord()){
            }
            resetPosition()
            isPlaying.value = false
        }
    }

    private suspend fun playNextChord(): Boolean {
        if (currentPosition.value < 0) {
            setPosition(0)
        }

        val chordList = chordListState.value
        val player = ChordPlayer(getApplication(), chordList[currentPosition.value])

        val job = viewModelScope.launch {
            player.waitForReady()
            player.play()
            player.stop(1_500L)
            player.release()
        }
        job.join()

        if (!incrementPosition()) {
            resetPosition()
            return false
        }
        return true
    }

}