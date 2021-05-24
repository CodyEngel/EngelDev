package dev.engel.api.internal.networking.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MailchimpMemberResponse(
	val id: String,
	@SerialName("email_address")
	val emailAddress: String,
	val status: String,
	@SerialName("merge_fields")
	val mergeFields: MailchimpMergeFields,
)

@Serializable
data class MailchimpMergeFields(
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
