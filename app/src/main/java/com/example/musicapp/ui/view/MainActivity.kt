package com.example.musicapp.ui.view

import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.musicapp.R
import com.example.musicapp.data.model.SongListItem
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.ui.viewmodel.HomeViewModel
import com.example.musicapp.ui.viewmodel.MainViewModel
import com.google.android.exoplayer2.util.Log
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        connectToPlayerService()

        addOnClick()
        observeCurrentSong()
        observePlaybackState()
    }

    private fun observePlaybackState() {
        mainViewModel.playbackState.observe(this, Observer {
            it?.let {
                Log.d("MusicService ", it.playbackState.toString())
                if ((it.playbackState as PlaybackState).state == PlaybackState.STATE_PLAYING) {
                    updateUI("pause")

                } else if ((it.playbackState as PlaybackState).state == PlaybackState.STATE_PAUSED) {
                    updateUI("play")
                }
            }
        })
    }

    private fun updateUI(state: String) {
        Log.d("MusicService ", "updateUI")

        runOnUiThread {
            if (state.equals("pasue")) {
                binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            } else {
                binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            }
        }
    }


    private fun observeCurrentSong() {
        mainViewModel.currentSong.observe(this, Observer {
            binding.headerTitle.text = it.name
            Picasso.get().load(it.imageUrl)
                .into(binding.imageView)
        })
    }

    private fun addOnClick() {
        binding.playPause.setOnClickListener(View.OnClickListener {

            /*"sid": "",
            "album": "",
            "name": "All The People Said Amen",
            "artist": "Matt Maher",
            "image_url": "https://is2-ssl.mzstatic.com/image/thumb/Music125/v4/33/16/35/33163578-83ed-06db-6cd1-8430fdaa1ac8/source/600x600bb.jpg",
            "link_url": "",
            "preview_url": "",
            "played_at": ""*/

            var song = SongListItem(
                linkUrl = "https://securestreams8.autopo.st:3005/1",
                artist = "Matt Mahe",
                imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music125/v4/33/16/35/33163578-83ed-06db-6cd1-8430fdaa1ac8/source/600x600bb.jpg",
                name = "All The People Said Amen"
            )
            //  var song = SongListItem(linkUrl =  "https://rfcmedia.streamguys1.com/70hits.aac",artist = "Matt Mahe", imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music125/v4/33/16/35/33163578-83ed-06db-6cd1-8430fdaa1ac8/source/600x600bb.jpg", name = "All The People Said Amen")
            //  var song = SongListItem(linkUrl =  "https://live.wostreaming.net/direct/agmedia28-kkalfmaac-ibc3?source=MyNewSource",artist = "Matt Mahe", imageUrl = "https://is2-ssl.mzstatic.com/image/thumb/Music125/v4/33/16/35/33163578-83ed-06db-6cd1-8430fdaa1ac8/source/600x600bb.jpg", name = "All The People Said Amen")
            mainViewModel.playRadio(song)
        })
    }

    private fun connectToPlayerService() {
        //  TODO("Not yet implemented")
    }


    private fun setupViewModel() {
//        mainViewModel = ViewModelProvider.of(
//            this,
//            ViewModelFactory(ApiHelper(ApiServiceImpl()))
//        ).get(MainViewModel::class.java)
    }
}