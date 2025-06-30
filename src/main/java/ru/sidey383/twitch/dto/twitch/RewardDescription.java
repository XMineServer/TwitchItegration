package ru.sidey383.twitch.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 *   broadcaster_id	String	The ID that uniquely identifies the broadcaster.
 *    broadcaster_login	String	The broadcaster’s login name.
 *    broadcaster_name	String	The broadcaster’s display name.
 *    clientId	String	The ID that uniquely identifies this custom reward.
 *    title	String	The title of the reward.
 *    prompt	String	The prompt shown to the viewer when they redeem the reward if user input is required (see the is_user_input_required field).
 *    cost	Integer	The cost of the reward in Channel Points.
 *    image	Object	A set of custom images for the reward. This field is null if the broadcaster didn’t upload images.
 *       url_1x	String	The URL to a small version of the image.
 *       url_2x	String	The URL to a medium version of the image.
 *       url_4x	String	The URL to a large version of the image.
 *    default_image	Object	A set of default images for the reward.
 *       url_1x	String	The URL to a small version of the image.
 *       url_2x	String	The URL to a medium version of the image.
 *       url_4x	String	The URL to a large version of the image.
 *    background_color	String	The background color to use for the reward. The color is in Hex format (for example, #00E5CB).
 *    is_enabled	Boolean	A Boolean value that determines whether the reward is enabled. Is true if enabled; otherwise, false. Disabled rewards aren’t shown to the user.
 *    is_user_input_required	Boolean	A Boolean value that determines whether the user must enter information when redeeming the reward. Is true if the user is prompted.
 *    max_per_stream_setting	Object	The settings used to determine whether to apply a maximum to the number of redemptions allowed per live stream.
 *       is_enabled	Boolean	A Boolean value that determines whether the reward applies a limit on the number of redemptions allowed per live stream. Is true if the reward applies a limit.
 *       max_per_stream	Int64	The maximum number of redemptions allowed per live stream.
 *    max_per_user_per_stream_setting	Object	The settings used to determine whether to apply a maximum to the number of redemptions allowed per user per live stream.
 *       is_enabled	Boolean	A Boolean value that determines whether the reward applies a limit on the number of redemptions allowed per user per live stream. Is true if the reward applies a limit.
 *       max_per_user_per_stream	Int64	The maximum number of redemptions allowed per user per live stream.
 *    global_cooldown_setting	Object	The settings used to determine whether to apply a cooldown period between redemptions and the length of the cooldown.
 *       is_enabled	Boolean	A Boolean value that determines whether to apply a cooldown period. Is true if a cooldown period is enabled.
 *       global_cooldown_seconds	Int64	The cooldown period, in seconds.
 *    is_paused	Boolean	A Boolean value that determines whether the reward is currently paused. Is true if the reward is paused. Viewers can’t redeem paused rewards.
 *    is_in_stock	Boolean	A Boolean value that determines whether the reward is currently in stock. Is true if the reward is in stock. Viewers can’t redeem out of stock rewards.
 *    should_redemptions_skip_request_queue	Boolean	A Boolean value that determines whether redemptions should be set to FULFILLED status immediately when a reward is redeemed. If false, status is set to UNFULFILLED and follows the normal request queue process.
 *    redemptions_redeemed_current_stream	Integer	The number of redemptions redeemed during the current live stream. The number counts against the max_per_stream_setting limit. This field is null if the broadcaster’s stream isn’t live or max_per_stream_setting isn’t enabled.
 *    cooldown_expires_at	String	The timestamp of when the cooldown period expires. Is null if the reward isn’t in a cooldown state. See the global_cooldown_setting field.
 *    Use JsonProperty to map the fields to the JSON response from Twitch API.
 * **/
public record RewardDescription (
        @JsonProperty("broadcaster_id")
        String broadcasterId,
        @JsonProperty("broadcaster_login")
        String broadcasterLogin,
        @JsonProperty("broadcaster_name")
        String broadcasterName,
        String id,
        String title,
        String prompt,
        int cost,
        RewardImage image,
        @JsonProperty("default_image")
        RewardImage defaultImage,
        @JsonProperty("background_color")
        String backgroundColor,
        @JsonProperty("is_enabled")
        boolean isEnabled,
        @JsonProperty("is_user_input_required")
        boolean isUserInputRequired,
        @JsonProperty("max_per_stream_setting")
        MaxPerStreamSetting maxPerStreamSetting,
        @JsonProperty("max_per_user_per_stream_setting")
        MaxPerUserPerStreamSetting maxPerUserPerStreamSetting,
        @JsonProperty("global_cooldown_setting")
        GlobalCooldownSetting globalCooldownSetting,
        @JsonProperty("is_paused")
        boolean isPaused,
        @JsonProperty("is_in_stock")
        boolean isInStock,
        @JsonProperty("should_redemptions_skip_request_queue")
        boolean shouldRedemptionsSkipRequestQueue,
        @JsonProperty("redemptions_redeemed_current_stream")
        Integer redemptionsRedeemedCurrentStream,
        @JsonProperty("cooldown_expires_at")
        Instant cooldownExpiresSt

) {

    public record RewardImage(
            @JsonProperty("url_1x")
            String url1,
            @JsonProperty("url_2x")
            String url2,
            @JsonProperty("url_3x")
            String url4
    ) {
    }

    private record MaxPerStreamSetting(
            @JsonProperty("is_enabled")
            boolean isEnabled,
            @JsonProperty("max_per_stream")
            Long maxPerStream
    ){}

    private record MaxPerUserPerStreamSetting(
            @JsonProperty("is_enabled")
            boolean isEnabled,
            @JsonProperty("max_per_user_per_stream")
            Long maxPerUserPerStream
    ){}

    private record GlobalCooldownSetting(
            @JsonProperty("is_enabled")
            boolean isEnabled,
            @JsonProperty("global_cooldown_seconds")
            Long globalCooldownSeconds
    ){}

}
