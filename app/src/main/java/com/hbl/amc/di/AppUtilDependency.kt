package com.hbl.amc.di

import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * DI module for App Util dependency.
 */
val AppUtilDependency = module {
//    factory { AppUtils(androidContext()) }
}
