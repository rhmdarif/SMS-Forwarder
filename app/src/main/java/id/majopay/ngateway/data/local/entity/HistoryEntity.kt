package id.majopay.ngateway.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.datetime.Instant

/**
 * Database entity representing a forwarding attempt history log for notifications.
 * 
 * @property id Unique identifier for the history entry
 * @property ruleId Foreign key reference to the rule that triggered this forwarding (null if no rule matched)
 * @property matchedRule Whether this message/notification matched any forwarding rule
 * @property senderNumber Legacy: nomor pengirim SMS (sumber SMS sudah dihapus, selalu null)
 * @property messageBody The message content (notification text)
 * @property sourceType Source type - "NOTIFICATION"
 * @property sourcePackage Package name (for notifications)
 * @property sourceAppName App name (for notifications)
 * @property notificationTitle Notification title (for notifications)
 * @property notificationText Notification text (for notifications)
 * @property endpoint The HTTP endpoint used for forwarding
 * @property method The HTTP method used for forwarding
 * @property requestHeaders JSON string of request headers
 * @property requestBody The request body sent to the API
 * @property responseCode HTTP response code (null if request failed or no rule matched)
 * @property responseBody HTTP response body (null if request failed or no rule matched)
 * @property status Success or failure status
 * @property errorMessage Error details if forwarding failed
 * @property timestamp Timestamp when the message/notification was received
 * @property forwardedAt Timestamp when the forwarding was attempted (null if no forwarding)
 */
@Entity(
    tableName = "forwarding_history",
    foreignKeys = [
        ForeignKey(
            entity = RuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["rule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["rule_id"]),
        Index(value = ["timestamp"]),
        Index(value = ["source_type"]),
        Index(value = ["status"])
    ]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "rule_id")
    val ruleId: Long? = null, // Nullable to support non-matching messages
    
    @ColumnInfo(name = "matched_rule")
    val matchedRule: Boolean = false, // Whether message matched any rule
    
    // Legacy (sumber SMS sudah dihapus)
    @ColumnInfo(name = "sender_number")
    val senderNumber: String? = null, // Selalu null untuk data baru
    
    @ColumnInfo(name = "message_body")
    val messageBody: String, // Notification content
    
    // Source information
    @ColumnInfo(name = "source_type")
    val sourceType: String, // "NOTIFICATION"
    
    @ColumnInfo(name = "source_package")
    val sourcePackage: String? = null, // Package name for notifications
    
    @ColumnInfo(name = "source_app_name")
    val sourceAppName: String? = null, // App name for notifications
    
    // Notification-specific fields
    @ColumnInfo(name = "notification_title")
    val notificationTitle: String? = null, // Title for notifications
    
    @ColumnInfo(name = "notification_text")
    val notificationText: String? = null, // Text for notifications
    
    // Forwarding details
    val endpoint: String? = null,
    val method: String? = null,
    
    @ColumnInfo(name = "request_headers")
    val requestHeaders: String = "{}", // JSON string of headers
    
    @ColumnInfo(name = "request_body")
    val requestBody: String? = null,
    
    @ColumnInfo(name = "response_code")
    val responseCode: Int? = null,
    
    @ColumnInfo(name = "response_body")
    val responseBody: String? = null,
    
    val status: String, // ForwardingStatus enum as string
    
    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null,
    
    val timestamp: Instant, // When message/notification was received
    
    @ColumnInfo(name = "forwarded_at")
    val forwardedAt: Instant? = null // When forwarding was attempted
) 