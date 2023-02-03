package com.hbl.amc.di

/**
 * Main dependency component.
 * This will create and provide required dependencies with sub dependencies.
 */
val appComponent = listOf(
    UseCaseDependency,
    AppUtilDependency,
    NetworkDependency,
    RepoDependency,
    ViewModelModule
)