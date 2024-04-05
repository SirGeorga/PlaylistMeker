package com.example.playlistmaker.presentation.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.models.Track

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerTrackName: TextView
    private lateinit var playerArtistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var cover: ImageView
    private lateinit var backButton: TextView
    private lateinit var playButton: ImageButton
    private lateinit var tvElapsedTime: TextView
    private lateinit var playerInteractor: PlayerInteractor
    private var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        initListeners()

        val track = intent.getParcelableExtra<Track>("track")
        if (track != null) {
            parseTrack(track)
        }
        playerInteractor = PlayerInteractorImpl(playButton, url, tvElapsedTime)
        playerInteractor.preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.mediaPlayer.release()
    }

    private fun initViews() {
        playerTrackName = findViewById(R.id.playerTrackName)
        playerArtistName = findViewById(R.id.playerArtistName)
        trackTime = findViewById(R.id.time)
        album = findViewById(R.id.album)
        year = findViewById(R.id.year)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)
        cover = findViewById(R.id.trackCover)
        backButton = findViewById(R.id.backArrow4)
        playButton = findViewById(R.id.playButton)
        tvElapsedTime = findViewById(R.id.tvElapsedTime)
    }

    private fun initListeners() {
        backButton.setOnClickListener {
            finish()
        }
        playButton.setOnClickListener {
            playButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            playerInteractor.playbackControl()
            if (!playerInteractor.stopWatchStarted && playerInteractor.timeElapse <= 0L) {
                playerInteractor.handler.removeCallbacksAndMessages(null)
                playerInteractor.stopWatchStarted = true
                playerInteractor.handler.post(playerInteractor.startStopwatch())
            }
        }
    }

    private fun parseTrack(track: Track) {
        playerTrackName.text = track.trackName ?: getString(R.string.st_unknown_track)
        playerArtistName.text = track.artistName ?: getString(R.string.st_unknown_artist)
        trackTime.text = track.trackTimeNormal ?: getString(R.string.st_00_00)
        album.text = track.collectionName ?: getString(R.string.st_unknown_album)
        year.text = track.year ?: getString(R.string.st_unknown_year).take(4)
        genre.text = track.primaryGenreName ?: getString(R.string.st_unknown_genre)
        country.text = track.country ?: getString(R.string.st_unknown_country)
        url = track.previewUrl ?: ""
        val getImage = track.artworkUrl512 ?: getString(R.string.st_unknown_cover_url)
        Glide.with(this).load(getImage).placeholder(R.drawable.ic_album_placeholder).into(cover)
    }
}