package com.example.musicapp.ui.player

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.musicapp.ui.player.PlayerConstants.Notification.NOTIFICATION_ID
import com.google.android.exoplayer2.ui.PlayerNotificationManager

/**
 * This class is used to listen Music Player Notification callbacks/events.
 */
class MusicPlayerNotificationListener(
    private val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        if(dismissedByUser) {
            musicService.apply {
                stopForeground(true)
                isForegroundService = false
                stopSelf()
            }
            musicService.closeMiniPlayer()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if(ongoing && isForegroundService.not()) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                startForeground(NOTIFICATION_ID, notification)
                isForegroundService = true
            }
        }
    }
}