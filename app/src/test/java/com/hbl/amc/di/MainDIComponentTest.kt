package com.hbl.amc

import com.hbl.amc.di.RepoDependency
import com.hbl.amc.di.UseCaseDependency
import com.hbl.amc.di.ViewModelModule


/**
 * Main Koin DI component.
 * Helps to configure
 * 1) Mockwebserver
 * 2) Usecase
 * 3) Repository
 */
fun configureTestAppComponent(baseApi: String)
        = listOf(
    MockWebServerDIPTest,
    configureNetworkModuleForTest(baseApi),
    UseCaseDependency,
    RepoDependency,
    ViewModelModule
    )

