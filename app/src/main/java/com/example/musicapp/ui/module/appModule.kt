package com.example.musicapp.ui.module

import com.example.musicapp.data.api.ApiService
import com.example.musicapp.data.api.ApiServiceImpl
import com.example.musicapp.data.repository.HomeRepository
import com.example.musicapp.data.repository.HomeRepositoryImpl
import com.example.musicapp.data.repository.RecentlyPlayedRepository
import com.example.musicapp.data.repository.RecentlyPlayedRepositoryImpl
import com.example.musicapp.ui.player.MusicServiceConnection
import com.example.musicapp.ui.viewmodel.HomeViewModel
import com.example.musicapp.ui.viewmodel.MainViewModel
import com.example.musicapp.ui.viewmodel.RecentlyPlayedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit


val appModule = module {

    single {
      MusicServiceConnection(
            context = get()
        )
    }
   /* single(named("music_base_url")) { "https://api.itmwpb.com/" }

    single<ApiService> {
        get<Retrofit.Builder>()
            .build()
            .create(ApiService::class.java)
    }*/

    viewModel {
        MainViewModel(
            musicServiceConnection = get(),
        )
    }
    viewModel {
        HomeViewModel(
            homeRepository = get(),
        )
    }
    viewModel {
        RecentlyPlayedViewModel(
            repository = get(),
        )
    }



    factory<HomeRepository> {
        HomeRepositoryImpl(
            apiService = get(),
        )
    }
    factory<RecentlyPlayedRepository> {
        RecentlyPlayedRepositoryImpl(
            apiService = get(),
        )
    }

    factory<ApiService> {
        ApiServiceImpl()
    }


}