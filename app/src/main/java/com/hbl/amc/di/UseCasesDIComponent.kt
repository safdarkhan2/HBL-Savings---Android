package com.hbl.amc.di
import com.hbl.amc.domain.usecase.*
import org.koin.dsl.module

/**
 * Use case DI module.
 * Provide Use case dependency.
 */
val UseCaseDependency = module {

    factory {
        GenericUseCase()
    }

    factory {
        CustomerInfoUseCase()
    }

    factory {
        RegulatoryInfoUseCase()
    }

    factory {
        ProductInfoUseCase()
    }

    factory {
        DisclaimersUseCase()
    }

    factory {
        PreviewUseCase()
    }
    factory {
        SelfServiceUseCase()
    }
    factory {
        DashboardUseCase()
    }

    factory {
        LoginUsecase()
    }
}