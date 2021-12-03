/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.snapinssdkexample.utils

import android.content.Context
import com.salesforce.android.chat.core.ChatConfiguration
import com.salesforce.android.chat.ui.ChatUIConfiguration
import com.salesforce.androidsdk.accounts.UserAccount
import com.salesforce.androidsdk.app.SalesforceSDKManager
import com.salesforce.androidsdk.rest.ClientManager
import com.salesforce.snapinssdkexample.R
import com.salesforce.snapinssdkexample.activities.settings.ChatSettingsActivity
import com.salesforce.snapinssdkexample.auth.MobileSDKAuthTokenProvider
import com.salesforce.snapinssdkexample.auth.MobileSdkUser
import com.salesforce.snapinssdkexample.utils.Utils.getBooleanPref
import com.salesforce.snapinssdkexample.utils.Utils.getStringPref

/**
 * Helper object to encapsulate building configurations of some of the Snap-ins SDKs
 */
object ServiceSDKUtils {
    /**
     * Creates a chat configuration based on chat settings or uses sensible default values.
     */
    fun getChatConfigurationBuilder(context: Context): ChatConfiguration.Builder {
        // Create a core configuration instance
        return ChatConfiguration.Builder(
                getStringPref(context, ChatSettingsActivity.KEY_ORG_ID,
                        context.getString(R.string.pref_chat_org_id_default)),
                getStringPref(context, ChatSettingsActivity.KEY_BUTTON_ID,
                        context.getString(R.string.pref_chat_button_id_default)),
                getStringPref(context, ChatSettingsActivity.KEY_DEPLOYMENT_ID,
                        context.getString(R.string.pref_chat_deployment_id_default)),
                getStringPref(context, ChatSettingsActivity.KEY_LIVE_AGENT_POD,
                        context.getString(R.string.pref_chat_la_pod_default))
        )
    }

    /**
     * Return ChatUIConfigurationBuilder configured with optional ChatBot banner and avatar.
     */
    fun getChatUIConfigurationBuilder(context: Context, chatConfiguration: ChatConfiguration): ChatUIConfiguration.Builder {
        // Check if the banner and/or avatar are enabled in settings
        val chatbotBannerEnabled = getBooleanPref(context, ChatSettingsActivity.KEY_CHATBOT_BANNER_ENABLED)
        val chatbotAvatarEnabled = getBooleanPref(context, ChatSettingsActivity.KEY_CHATBOT_AVATAR_ENABLED)
        val defaultToMinimized = getBooleanPref(context, ChatSettingsActivity.KEY_DEFAULT_TO_MINIMIZED_ENABLED)

        val chatUIConfigurationBuilder: ChatUIConfiguration.Builder = ChatUIConfiguration.Builder().chatConfiguration(chatConfiguration)

        if (chatbotBannerEnabled) {
            // Set the ChatBot banner to use via layout ID
            chatUIConfigurationBuilder.enableChatBotBanner(R.layout.chatbot_banner)
        }

        if (chatbotAvatarEnabled) {
            // Set the ChatBot avatar to use via drawable ID
            chatUIConfigurationBuilder.chatBotAvatar(R.drawable.ic_chatbot_avatar)
        }

        if (!defaultToMinimized) {
            chatUIConfigurationBuilder.defaultToMinimized(false)
        }

        return chatUIConfigurationBuilder
    }

    /**
     * Returns a nullable value of the current authenticated user
     */
    fun getAuthenticatedUser(): UserAccount? {
        return SalesforceSDKManager.getInstance().userAccountManager.currentUser
    }

    /**
     * Returns an MobileSDKAuthTokenProvider for the provided UserAccount.
     */
    private fun getAuthTokenProvider(user: UserAccount): MobileSDKAuthTokenProvider {
        val accMgrAuthTokenProvider = ClientManager.AccMgrAuthTokenProvider(
                SalesforceSDKManager.getInstance().clientManager,
                user.instanceServer,
                user.authToken,
                user.refreshToken)

        return MobileSDKAuthTokenProvider(accMgrAuthTokenProvider, user.authToken)
    }
}
