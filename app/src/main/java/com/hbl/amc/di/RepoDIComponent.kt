package com.hbl.amc.di
import com.hbl.amc.domain.repository.*
import org.koin.dsl.module


/**
 * Repository DI module.
 * Provides Repo dependency.
 */
val RepoDependency = module {

    factory {
        GenericRepository()
    }

    factory {
        CustomerInfoRepository()
    }

    factory {
        RegulatoryInfoRepository()
    }

    factory {
        ProductInfoRepository()
    }

    factory {
        DisclaimersRepository()
    }

    factory {
        PreviewRepository()
    }

    factory {
        SelfServiceRepository()
    }

    factory {
        DashboardRepository()
    }

    factory {
        LoginRepository()
    }
}