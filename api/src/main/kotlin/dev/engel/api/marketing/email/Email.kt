package dev.engel.api.marketing.email

import dev.engel.api.internal.extensions.skribe
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

fun Application.registerEmailRoutes(apiKey: String) {
    routing {
        addEmailRecipientRoute(apiKey)
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

fun Route.addEmailRecipientRoute(apiKey: String) {
    route("/marketing/email/recipient") {
        post {
            application.skribe.info("POST -- Email Recipient")

            val recipientRequest = call.receive<RecipientRequest>()
            val requestUrl = "https://us1.api.mailchimp.com/3.0/lists/76fc4db729/members"
            val client = HttpClient(CIO) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    })
                }
            }

            val mailchimpRequest = MailchimpMemberRequest(
                status = "pending",
                emailAddress = recipientRequest.emailAddress,
                mergeFields = MailchimpMergeFields(
                    firstName = recipientRequest.firstName,
                    lastName = recipientRequest.lastName
                )
            ).also { application.skribe.info("REQUEST -- $it") }

            val mailchimpResponse = try {
                client.post<MailchimpMemberResponse>(requestUrl) {
                    accept(ContentType.Application.Json)
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Basic ${encodeAuthorizationHeader(apiKey)}")
                    body = mailchimpRequest
                }
            } catch (ex: ResponseException) {
                val text = ex.response.readText()
                application.skribe.error("ResponseError -- $text")
                var mailchimpError = Json { ignoreUnknownKeys = true }
                    .decodeFromString<MailchimpError>(text)
                mailchimpError = mailchimpError.copy(status = mailchimpError.title.asErrorCode.value)
                application.skribe.info("RESPONSE -- $mailchimpError")
                call.response.status(mailchimpError.title.asErrorCode)
                call.respond(mailchimpError)
                return@post
            }

            val response = RecipientResponse(
                id = mailchimpResponse.id,
                emailAddress = mailchimpResponse.emailAddress,
                status = mailchimpResponse.status,
                firstName = mailchimpResponse.mergeFields.firstName,
                lastName = mailchimpResponse.mergeFields.lastName
            ).also { application.skribe.info("RESPONSE -- $it") }

            call.respond(response)
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

private fun encodeAuthorizationHeader(apiKey: String): String {
    return Base64.getEncoder().encodeToString("user:$apiKey".toByteArray())
}

@Serializable
private data class MailchimpMemberRequest(
    @SerialName("email_address")
    val emailAddress: String,
    val status: String,
    @SerialName("merge_fields")
    val mergeFields: MailchimpMergeFields,
)

@Serializable
private data class MailchimpMemberResponse(
    val id: String,
    @SerialName("email_address")
    val emailAddress: String,
    val status: String,
    @SerialName("merge_fields")
    val mergeFields: MailchimpMergeFields,
)

@Serializable
private data class MailchimpMergeFields(
    @SerialName("FNAME")
    val firstName: String,
    @SerialName("LNAME")
    val lastName: String
)

@Serializable
data class MailchimpError(
    val type: String? = null,
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String
)