package com.example.chordprogressionmemo

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.*

@OptIn(UnstableApi::class)
class ChordPlayer(
    private val context: Context,
    private val chordInfo: ChordInfo
) {
    private var playerList = arrayListOf<ExoPlayer>()
    private var readyCount = 0

    init {
        val soundFileList = getSoundFileList()
        soundFileList.forEach { soundFile ->
            val player = ExoPlayer.Builder(context).build()
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        readyCount++
                    }
                }
            })

            val mediaItem = MediaItem.fromUri("file:///android_asset/sound/$soundFile")
            // assetフォルダからの読み込みのため、メディアソースに変換する
            val dataSourceFactory = DefaultDataSource.Factory(context)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            player.setMediaSource(mediaSource)
            player.prepare()
            playerList.add(player)
        }

        Log.d("ChordPlayer", soundFileList.joinToString(","))
    }

    fun isReady(): Boolean {
        return readyCount == playerList.count()
    }

    suspend fun waitForReady() {
        while (!isReady()) {
            delay(100)
        }
    }

    fun play() {
        // 和音を鳴らすため、すべての音声の準備ができてから再生する
        if (!isReady()) {
            return
        }

        playerList.forEach {
            // 複数回再生する場合を考え、再生位置を初期化する
            it.seekTo(0)
            it.playWhenReady = true
        }
    }

    suspend fun stop(delayMilliSecs: Long) {
        delay(delayMilliSecs)
        playerList.forEach {
            // 複数回再生されることを考え、playerがリセットされないように
            // pause()を用いる
            it.pause()
        }
    }

    fun release() {
        playerList.forEach {
            it.release()
        }
        playerList.clear()
    }

    private fun getSoundFileList(): List<String> {
        val rootNote = NoteName.lookup(chordInfo.rootNote)
        val quality = ChordQuality.lookup(chordInfo.quality)
        val bassNote = NoteName.lookup(chordInfo.bassNote)
        val octave = chordInfo.octave

        // コードを構成する音の高さのリスト(相対音)を生成する
        val bassPitch = bassNote.value()
        var rootPitch = rootNote.value()
        if (rootPitch < bassPitch) {
            // ベース音を最低音にするため、1オクターブ上げて補正する
            rootPitch += 12
        }
        val chordPitchList = arrayListOf(bassPitch)
        quality.pitchList.forEach { pitch ->
            val chordPitch = rootPitch + pitch
            // ベース音と被っている場合は対象外とする
            if (chordPitch % 12 == bassPitch % 12) {
                return@forEach
            }
            chordPitchList.add(chordPitch)
        }

        // 相対音をもとにファイル名を生成する
        return chordPitchList.map() { pitch ->
            toAbsPitch(pitch, octave).toString() + ".flac"
        }
    }
}

fun toAbsPitch(pitch: Int, octave: Int): Int {
    // pitchが0～11となるように正規化する
    val normalizedPitch = pitch % 12
    val normalizedOctave = octave + pitch / 12

    // C3が0となるように絶対音程を求める
    val diff = normalizedPitch - NoteName.C.value()
    return 12 * (normalizedOctave - 3) + diff
}