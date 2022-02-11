package com.example.musicapp.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.example.musicapp.data.model.SongListItem
import com.example.musicapp.ui.player.MusicServiceConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(private val musicServiceConnection: MusicServiceConnection) : ViewModel() {


    private val _activityName = MutableLiveData<String>()
    val activityName: LiveData<String> get() = _activityName


    private val mCurrentSong = MutableLiveData<SongListItem>()
    val currentSong: LiveData<SongListItem> get() = mCurrentSong

    private val mPlaybackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = mPlaybackState

    var observer : Observer<SongListItem>
    lateinit var playbackStateBbserver : Observer<PlaybackStateCompat?>

    init {

        musicServiceConnection.subscribe("root_id",
            _activityName.value ?: "",
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>,
                ) {
                    //  Timber.d("onChildrenLoaded - $children")
                    super.onChildrenLoaded(parentId, children)
                }
            })

        observer = Observer<SongListItem> {mCurrentSong.postValue(it) }
        musicServiceConnection.currentSong.observeForever(observer)

        playbackStateBbserver = Observer<PlaybackStateCompat?> {mPlaybackState.postValue(it) }
        musicServiceConnection.playbackState.observeForever(playbackStateBbserver)




    }



    fun playRadio(song: SongListItem) {
        musicServiceConnection.playRadio(song)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.currentSong.removeObserver(observer)
        musicServiceConnection.playbackState.removeObserver(playbackStateBbserver)
        musicServiceConnection.unsubscribe("root_id",
            object : MediaBrowserCompat.SubscriptionCallback() {})

    }
}