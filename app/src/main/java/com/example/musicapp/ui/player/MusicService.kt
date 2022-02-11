package com.example.musicapp.ui.player

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.net.toUri
import androidx.media.MediaBrowserServiceCompat
import com.example.musicapp.data.model.SongListItem
import com.example.musicapp.ui.player.PlayerConstants.Action.PLAY_RADIO
import com.example.musicapp.ui.player.PlayerConstants.BundleData.CLOSE_MINI_PLAYER
import com.example.musicapp.ui.player.PlayerConstants.MEDIA_ROOT_ID
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.PlaybackStatsListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import org.koin.android.ext.android.inject
import java.util.*
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager


class MusicService : MediaBrowserServiceCompat() {

    val musicServiceConnection by inject<MusicServiceConnection>()
    var isForegroundService = false
    private lateinit var dataSourceFactory: DefaultDataSource.Factory
    private lateinit var context: Context
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private var playbackStatsListener: PlaybackStatsListener? = null
    private lateinit var musicNotificationManager: MusicNotificationManager

    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        Log.d("MusicService ", "onCreate()")
        createMediaSession()
        sessionToken = mediaSession.sessionToken
        setUpNotificationManger()
        dataSourceFactory = getDefaultDataSourceFactory()
        createExoplayerInstance()
        createMediaSessionConnector()
        musicNotificationManager.setPlayerToNotification(exoPlayer)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_ROOT_ID -> result.detach()
        }
    }

    @Suppress("LongMethod", "ComplexMethod", "UnusedPrivateMember")
    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        super.onCustomAction(action, extras, result)
        when (action) {
            PLAY_RADIO -> {
                val name = extras?.getString("name") as String
                val artist = extras.getString("artist") as String
                val linkUrl = extras.getString("linkUrl") as String

                playRadio(SongListItem(name = name, artist = artist, linkUrl = linkUrl));
            }
        }
    }

    private fun playRadio(song: SongListItem) {
        exoPlayer.setMediaSource(asMediaSource(song))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        musicNotificationManager.setPlayerToNotification(exoPlayer)
    }

    private fun asMediaSource(
        songs: SongListItem,
    ): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.let { song ->
            val mediaSource =
                song.linkUrl.let {
                    buildMediaSourceType(
                        getMediaSourceType(it.toUri()),
                        it.toUri()
                    )
                }
            concatenatingMediaSource.addMediaSource(mediaSource as MediaSource)
        }
        return concatenatingMediaSource
    }

    private fun getMediaSourceType(mediaUri: Uri?): Int {
        Log.d("MusicService ", "getMediaSourceType")
        return when {
            mediaUri.toString().contains(".m3u8") -> C.TYPE_HLS
            mediaUri.toString().contains(".aac") -> C.TYPE_RTSP
            else -> C.TYPE_OTHER
        }
    }

    private fun buildMediaSourceType(type: Int, mediaUri: Uri): Any {
        Log.d("MusicService ", "buildMediaSourceType" + " -type "+type)
        return when (type) {

            C.TYPE_HLS -> HlsMediaSource.Factory(getDefaultDataSourceFactory())
                .setAllowChunklessPreparation(true)
                .createMediaSource(
                    MediaItem.Builder()
                        .setMediaId(mediaUri.toString())
                        .setMimeType(MimeTypes.APPLICATION_M3U8)
                        .setUri(mediaUri)
                        .build()
                )

            C.TYPE_RTSP ->
            RtspMediaSource.Factory()
                .setForceUseRtpTcp(true)
                .setUserAgent("ZEE5")
                .createMediaSource( MediaItem.Builder()
                    .setUri(mediaUri)
                    .build())

            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.Builder()
                        .setUri(mediaUri)
                        .build()
                )

            else -> Unit
        }
    }

    private fun getDefaultDataSourceFactory(): DefaultDataSource.Factory {

        return DefaultDataSource.Factory(context).setTransferListener(getDefaultBandWidth())

        /*return DefaultDataSourceFactory(context,
            getDefaultBandWidth(),
            DefaultHttpDataSource.Factory()
                .setUserAgent(Util.getUserAgent(context, "musicapp"))
                .setAllowCrossProtocolRedirects(true)
        )*/
    }

    private fun getDefaultBandWidth(): DefaultBandwidthMeter {
        return DefaultBandwidthMeter.Builder(context).build()
    }

    private fun setUpNotificationManger() {
        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this),
        ) {

        }
    }

    private fun createMediaSession() {
        Log.d("MusicService ", "createMediaSession")
        val pendingIntent: PendingIntent = try {
            val activityName: String = musicServiceConnection.activityName.value.toString()
            if (activityName.isEmpty().not()) {
                val activityClass = Class.forName(activityName)
                val activityIntent = Intent(this, activityClass)
                PendingIntent.getActivity(context, 0, activityIntent, 0)
            } else {
                packageManager?.getLaunchIntentForPackage(packageName)?.setPackage(null).let {
                    PendingIntent.getActivity(this, 0, it, 0)
                }
            }
        } catch (e: ClassNotFoundException) {
            packageManager?.getLaunchIntentForPackage(packageName)?.setPackage(null).let {
                PendingIntent.getActivity(this, 0, it, 0)
            }
        }

        mediaSession = MediaSessionCompat(this, "SERVICE_TAG").apply {
            setSessionActivity(pendingIntent)
            isActive = true
        }
    }

    private fun createMediaSessionConnector() {

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)
    }

    private fun createExoplayerInstance() {
        Log.d("MusicService ", "createExoplayerInstance")
        exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(getLoader())
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setTrackSelector(getDefaultTrackSelector(context)).build().apply {
                setAudioAttributes(audioAttributes, true)
                setHandleAudioBecomingNoisy(true)
            }
    }

    private fun getLoader(): LoadControl {
        Log.d("MusicService ", "getLoader")
        return DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(
                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .setTargetBufferBytes(DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES)
            .setPrioritizeTimeOverSizeThresholds(DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS)
            .build()
    }

    private fun getDefaultTrackSelector(context: Context): DefaultTrackSelector {
        Log.d("MusicService ", "getDefaultTrackSelector")

        return DefaultTrackSelector(context)
    }

    fun closeMiniPlayer() {
        mediaSessionConnector.mediaSession.sendSessionEvent(CLOSE_MINI_PLAYER, null)
    }


}