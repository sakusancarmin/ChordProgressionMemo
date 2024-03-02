package com.example.chordprogressionmemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chordprogressionmemo.data.ChordProgressionInfo
import com.example.chordprogressionmemo.data.ChordProgressionInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChordProgressionManagerViewModel(
    application: Application,
    private val progInfoDao: ChordProgressionInfoDao
) : AndroidViewModel(application) {

    // 新規コード進行名の入力ダイアログに関する状態管理
    val inputText = MutableStateFlow<String>("")
    val showInputDialog = MutableStateFlow<Boolean>(false)
    val enableAddButton = inputText.map {
        inputText.value.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun toggleInputDialog(show: Boolean) {
        showInputDialog.value = show
    }

    fun updateInputText(text: String) {
        inputText.value = text
    }

    // コード進行の削除ダイアログに関する状態管理
    val showDeleteDialog = MutableStateFlow<Boolean>(false)
    private var deleteTarget = MutableStateFlow<ChordProgressionInfo>(ChordProgressionInfo())
    fun openDeleteDialog(target: ChordProgressionInfo)
    {
        deleteTarget.value = target
        showDeleteDialog.value = true
    }

    fun closeDeleteDialog()
    {
        deleteTarget.value = ChordProgressionInfo()
        showDeleteDialog.value = false
    }

    val progListState: StateFlow<List<ChordProgressionInfo>> =
        progInfoDao.getAllInDescendingOrder()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addItem() {
        if (inputText.value.isEmpty()) {
            return
        }
        val progInfo = ChordProgressionInfo(name = inputText.value)
        viewModelScope.launch(Dispatchers.IO) {
            progInfoDao.insert(progInfo)
        }
    }

    fun deleteItemAndCloseDialog() {
        if (deleteTarget.value.name.isEmpty()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            progInfoDao.delete(deleteTarget.value)
            closeDeleteDialog()
        }
    }
}