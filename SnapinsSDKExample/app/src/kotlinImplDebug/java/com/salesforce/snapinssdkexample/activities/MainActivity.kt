/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.snapinssdkexample.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.salesforce.android.chat.core.ChatCore
import com.salesforce.android.chat.core.model.AvailabilityState
import com.salesforce.android.service.common.analytics.ServiceAnalytics
import com.salesforce.android.service.common.utilities.control.Async
import com.salesforce.androidsdk.app.SalesforceSDKManager
import com.salesforce.androidsdk.rest.RestClient
import com.salesforce.snapinssdkexample.ChatLauncher
import com.salesforce.snapinssdkexample.R
import com.salesforce.snapinssdkexample.activities.settings.ChatSettingsActivity
import com.salesforce.snapinssdkexample.utils.ServiceSDKUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.reflect.KClass

/**
 * Main test activity supporting basic functionality:
 * <ul>
 *     <li>Authentication (Logging in or out)</li>
 * </ul>
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initializeServiceSDK()

        setupButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chat_settings -> startActivityFor(ChatSettingsActivity::class)
            R.id.action_version_page -> startActivityFor(VersionActivity::class)
            R.id.action_check_chat_agent_availability -> showLiveAgentChatAvailability()
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun startActivityFor(activityClass: KClass<out AppCompatActivity>): Boolean {
        startActivity(Intent(applicationContext, activityClass.java))
        return true
    }

    private fun showLiveAgentChatAvailability(): Boolean {
        val chatConfig = ServiceSDKUtils.getChatConfigurationBuilder(applicationContext).build()

        // Create an agent availability client
        ChatCore.configureAgentAvailability(chatConfig).check()
                .onResult { _: Async<*>?, state: AvailabilityState ->
                    run {
                        // Display a toast when any agent availability state is changed
                        Toast.makeText(this,
                                String.format(getString(R.string.chat_availability_change_message), state.status.toString()),
                                Toast.LENGTH_SHORT).show()
                    }
                }

        return true
    }

    /**
     * Initializes configurations and listeners which should happen at startup.
     */
    private fun initializeServiceSDK() {
        setupServiceSDKListeners()
    }

    /**
     * An example of how an application can listen to the Service SDK for analytic type events.
     */
    private fun setupServiceSDKListeners() {
        // You can listen to user-driven events from the Snap-ins SDK using the ServiceAnalytics system.
        // Implement ServiceAnalyticsListener and add your listener to ServiceAnalytics to start receiving events.
        ServiceAnalytics.addListener({ behaviorId, eventData ->
            // Left blank intentionally
        })
    }

    /**
     * Adds click listeners to the main activity buttons.
     */
    private fun setupButtons() {
        chat_launch_button.setOnClickListener({ startChat() })
        login_button.setOnClickListener({ login() })
        logout_button.setOnClickListener({ logout() })
    }

    /**
     * Initializes Chat.
     */
    private fun startChat() {
        val chat = ChatLauncher()
        chat.launchChat(this)
    }

    /**
     * Initiates user login process.
     */
    private fun login() {
        SalesforceSDKManager.getInstance().clientManager.getRestClient(this) { client: RestClient ->
            run {
                // left blank intentionally
            }
        }
    }

    /**
     * Logs out authenticated users.
     */
    private fun logout() {
        SalesforceSDKManager.getInstance().logout(this, true)
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
    }
}
