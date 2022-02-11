package com.example.musicapp.ui.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.data.model.SongListItem
import com.example.musicapp.ui.player.PlayerConstants.Action.PLAY_RADIO
import com.example.musicapp.ui.viewmodel.MainViewModel
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MusicServiceConnection(
    val context: Context,
)  {

    var scope = CoroutineScope(Job() + Dispatchers.Main)
    lateinit var mediaController: MediaControllerCompat
    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mIsConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = mIsConnected

    private val mPlaybackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = mPlaybackState

    private val _activityName = MutableLiveData<String>()
    val activityName: LiveData<String> get() = _activityName

    private val mCurrentSong = MutableLiveData<SongListItem>()
    val currentSong: LiveData<SongListItem> get() = mCurrentSong

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }

    fun subscribe(parentId: String, name : String, callback: MediaBrowserCompat.SubscriptionCallback) {
        Log.d("MusicService ", "subscribe")
        scope.launch{ _activityName.postValue(name) }
        mediaBrowser.subscribe(parentId, callback)
        scope = CoroutineScope(Job() + Dispatchers.Main)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
        scope.cancel()
    }

    fun playRadio(song: SongListItem) {
        if(mediaBrowser.isConnected) {
            mCurrentSong.postValue(song)
            mediaBrowser.sendCustomAction(PLAY_RADIO, bundleOf(Pair("name", song.name),Pair("linkUrl", song.linkUrl),Pair("imageUrl", song.imageUrl),Pair("artist", song.artist)), null)
        }
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context,
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            Log.d("MusicService ", "onConnected")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
                context
            }
            scope.launch { mIsConnected.postValue(true) }
        }

        override fun onConnectionSuspended() {
            Log.d("MusicService ", "onConnectionSuspended")
            scope.launch {
                mIsConnected.postValue(false)            }
        }

        override fun onConnectionFailed() {
            Log.d("MusicService ", "onConnectionFailed")
            scope.launch {
                mIsConnected.postValue(false)            }
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            scope.launch {
                mPlaybackState.postValue(state)
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            scope.launch {

            }
        }

        @SuppressWarnings("ComplexMethod")
        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
//            Timber.d("chk onSessionEvent - $extras - $event")
            scope.launch {
                when (event) {

                }
            }
        }


        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

    }
}