package dev.engel.api.internal

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreOptions
import dev.engel.api.ApplicationEnvironment

interface GoogleCloudContext {
    val datastore: Datastore

    companion object {
        operator fun invoke(applicationEnvironment: ApplicationEnvironment): GoogleCloudContext {
            return when (applicationEnvironment) {
                ApplicationEnvironment.LOCAL -> LocalGoogleCloudContext()
                ApplicationEnvironment.PRODUCTION -> ProductionGoogleCloudContext()
            }
        }
    }
}

internal class ProductionGoogleCloudContext : GoogleCloudContext {
    override val datastore: Datastore by lazy { DatastoreOptions.getDefaultInstance().service }
}

internal class LocalGoogleCloudContext : GoogleCloudContext {
    override val datastore: Datastore by lazy {
        DatastoreOptions.newBuilder()
            .setHost("http://localhost:8081")
            .setProjectId("my-project")
            .build().service
    }
}