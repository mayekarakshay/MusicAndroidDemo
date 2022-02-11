package com.example.musicapp.ui.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicapp.R
import com.example.musicapp.ui.player.PlayerConstants.Notification.NOTIFICATION_CHANNEL_ID
import com.example.musicapp.ui.player.PlayerConstants.Notification.NOTIFICATION_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.squareup.picasso.Picasso
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso.LoadedFrom
import java.lang.Exception


class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {
    private val notificationManager: PlayerNotificationManager
    private val mNotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        createChannel()

        notificationManager =
            PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
                .setNotificationListener(notificationListener)
                .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
                .build().apply {
                    setMediaSessionToken(sessionToken)
                    setUseNextActionInCompactView(true)
                    setUsePreviousActionInCompactView(true)
                    setUsePlayPauseActions(false)
                    setUsePreviousAction(false)
                    setUseNextAction(false)
                    setSmallIcon(R.drawable.ic_baseline_audiotrack_24)
                    setUseRewindAction(false)
                    setUseFastForwardAction(false)
                    setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                }
    }


    fun setPlayerToNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) return

            val mChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.resources.getString(R.string.app_name)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(false)
                vibrationPattern = longArrayOf(0L)
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    fun refreshNotification() {
        notificationManager.invalidate()
    }

    fun cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID)
    }


    private inner class DescriptionAdapter(
        private val mediaController: MediaControllerCompat,
    ) : PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): CharSequence {
            newSongCallback()
            context as MusicService

            return context.musicServiceConnection.currentSong.value?.name.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): String? {
            context as MusicService
            return context.musicServiceConnection.currentSong.value?.name.toString()
        }

        @Suppress("ReturnCount")
        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback,
        ): Bitmap? {
            context as MusicService
            var _bitmap: Bitmap? = null
            Picasso.get().load(context.musicServiceConnection.currentSong.value?.imageUrl)
                .into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                        _bitmap = bitmap
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }
                })

            return _bitmap
        }
    }
}