package dev.engel.api.internal.networking.request

import dev.engel.api.internal.networking.response.MailchimpMergeFields
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MailchimpMemberRequest(
	@SerialName("email_address")
	val emailAddress: String,
	val status: String,
	@SerialName("merge_fields")
	val mergeFields: MailchimpMergeFields,
)
