package com.hbl.amc.di
import com.hbl.amc.ui.CustomerInformation.CustomerInfoViewModel
import com.hbl.amc.ui.self_service.DashboardViewModel
import com.hbl.amc.ui.Disclaimers.DisclaimersViewModel
import com.hbl.amc.ui.GenericViewModel
import com.hbl.amc.ui.Preview.PreviewViewModel
import com.hbl.amc.ui.productInformation.ProductInfoViewModel
import com.hbl.amc.ui.RegulatoryInformation.RegulatoryInfoViewModel
import com.hbl.amc.ui.Start.LoginViewModel
import com.hbl.amc.ui.self_service.SelfServiceViewModel
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        GenericViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        CustomerInfoViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        RegulatoryInfoViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        ProductInfoViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        DisclaimersViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        PreviewViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        SelfServiceViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        DashboardViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }

    viewModel { (mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) ->
        LoginViewModel(mainDispatcher = mainDispatcher, ioDispatcher = ioDispatcher, useCase = get())
    }
}