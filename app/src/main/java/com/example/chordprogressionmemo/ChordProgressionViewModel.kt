package com.example.chordprogressionmemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class ChordProgressionViewModel(
    application: Application,
    private val chordInfoDao: ChordInfoDao
) : AndroidViewModel(application) {

    val chordListState: StateFlow<List<ChordInfo>> =
        chordInfoDao.getAllOrderedByIndex()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val currentPosition = MutableStateFlow<Int>(-1)
    var isPlaying = MutableStateFlow<Boolean>(false)


    val enableDelete: StateFlow<Boolean> = isPlaying.map {
        !(isPlaying.value)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun resetPlayback() {
        currentPosition.value = -1
    }

    fun deleteChordInfo(chordInfo :ChordInfo)
    {
        chordInfoDao.deleteWithOrderIndexUpdated(chordInfo)
    }

    fun incrementPosition(): Boolean {
        val chordList = chordListState.value
        currentPosition.value += 1
        if (currentPosition.value >= chordList.count()) {
            return false
        }
        return true
    }

    fun setPosition(position: Int)
    {
        val chordList = chordListState.value
        if (position >= chordList.count()) {
            return
        }
        currentPosition.value = position
    }

    suspend fun playNextChord(): Boolean {
        if (currentPosition.value < 0) {
            setPosition(0)
        }

        val chordList = chordListState.value
        val player = ChordPlayer(getApplication(), chordList[currentPosition.value])

        player.waitForReady()
        player.play()
        player.stop(1_500L)
        player.release()

        if (!incrementPosition()) {
            resetPlayback()
            return false
        }
        return true
    }

}