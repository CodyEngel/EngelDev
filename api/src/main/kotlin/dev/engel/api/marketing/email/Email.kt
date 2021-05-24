package dev.engel.api.marketing.email

import dev.engel.api.internal.di.DependencyGraph
import dev.engel.api.internal.extensions.skribe
import dev.engel.api.internal.networking.NetworkRequests
import dev.engel.api.internal.networking.request.MailchimpMemberRequest
import dev.engel.api.internal.networking.response.MailchimpMergeFields
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

fun Application.registerEmailRoutes() {
    routing {
        addEmailRecipientRoute()
    }
}

inline class MailchimpApiKey(val key: String) {
    override fun toString(): String = key
}

fun Route.addEmailRecipientRoute() {
    val apiKey = DependencyGraph.instance.mailchimpApiKey
    val skribe = DependencyGraph.instance.skribe
    val networkRequests = NetworkRequests()

    route("/marketing/email/recipient") {
        post {
            skribe.info("POST -- Email Recipient")

            val recipientRequest = call.receive<RecipientRequest>()

            val mailchimpRequest = MailchimpMemberRequest(
                status = "pending",
                emailAddress = recipientRequest.emailAddress,
                mergeFields = MailchimpMergeFields(
                    firstName = recipientRequest.firstName,
                    lastName = recipientRequest.lastName
                )
            ).also { application.skribe.info("REQUEST -- $it") }

            val response = networkRequests.postMailchimpMember(apiKey, mailchimpRequest)
            when {
                response.first != null -> {
                    val mailchimpResponse = response.first!!
                    call.respond(
                        RecipientResponse(
                            id = mailchimpResponse.id,
                            emailAddress = mailchimpResponse.emailAddress,
                            status = mailchimpResponse.status,
                            firstName = mailchimpResponse.mergeFields.firstName,
                            lastName = mailchimpResponse.mergeFields.lastName
                        )
                    ).also { skribe.info("RESPONSE -- $it") }
                }
                response.second != null -> {
                    val mailchimpError = response.second!!.copy(status = response.second!!.title.asErrorCode.value)
                    skribe.info("RESPONSE -- $mailchimpError")
                    call.response.status(mailchimpError.title.asErrorCode)
                    call.respond(mailchimpError)
                }
                else -> {
                    skribe.error("Unexpected response: $response")
                    call.response.status(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}

private val String.asErrorCode: HttpStatusCode
    get() {
        return when (toLowerCase()) {
            "member exists" -> HttpStatusCode.Conflict
            "invalid resource" -> HttpStatusCode.Forbidden
            else -> HttpStatusCode.BadRequest
        }
    }

@Serializable
data class RecipientResponse(
    val id: String,
    val emailAddress: String,
    val status: String,
    val firstName: String,
    val lastName: String,
)

@Serializable
data class RecipientRequest(
    val emailAddress: String,
    val firstName: String,
    val lastName: String,
)
