package com.example.chordprogressionmemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


class ChordProgressionViewModel(
    application: Application,
    private val chordInfoDao: ChordInfoDao
) : AndroidViewModel(application) {

    val chordListState: StateFlow<List<ChordInfo>> =
        chordInfoDao.getAllOrderedByIndex()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private var currentIndex = 0


    fun resetPlayback() {
        currentIndex = 0
    }

    fun deleteChordInfo(chordInfo :ChordInfo)
    {
        chordInfoDao.deleteWithOrderIndexUpdated(chordInfo)
    }

    suspend fun playNextChord(): Boolean {
        val chordList = chordListState.value

        if (currentIndex == chordList.count()) {
            return false
        }

        val player = ChordPlayer(getApplication(), chordList[currentIndex])
        player.waitForReady()
        player.play()
        player.stop(1_500L)
        player.release()

        currentIndex++
        return true
    }

}