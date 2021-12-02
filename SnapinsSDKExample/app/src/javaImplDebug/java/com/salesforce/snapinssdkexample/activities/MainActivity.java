package com.salesforce.snapinssdkexample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.salesforce.android.chat.core.ChatConfiguration;
import com.salesforce.android.chat.core.ChatCore;
import com.salesforce.android.chat.core.model.AvailabilityState;
import com.salesforce.android.service.common.analytics.ServiceAnalytics;
import com.salesforce.android.service.common.analytics.ServiceAnalyticsListener;
import com.salesforce.android.service.common.utilities.control.Async;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.snapinssdkexample.ChatLauncher;
import com.salesforce.snapinssdkexample.R;
import com.salesforce.snapinssdkexample.activities.settings.ChatSettingsActivity;
import com.salesforce.snapinssdkexample.utils.ServiceSDKUtils;

import java.util.Map;

/**
 * Main test activity supporting basic functionality:
 * <ul>
 * <li>Authentication (Logging in or out)</li>
 * </ul>
 */
public class MainActivity extends AppCompatActivity {
    private Button chatButton;
    private Button loginButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        chatButton = findViewById(R.id.chat_launch_button);
        loginButton = findViewById(R.id.login_button);
        logoutButton = findViewById(R.id.logout_button);

        setSupportActionBar(toolbar);

        initializeServiceSDK();

        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat_settings:
                return startActivityFor(ChatSettingsActivity.class);
            case R.id.action_version_page:
                return startActivityFor(VersionActivity.class);
            case R.id.action_check_chat_agent_availability:
                return showLiveAgentChatAvailability();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean startActivityFor(Class<? extends AppCompatActivity> activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
        return true;
    }

    private Boolean showLiveAgentChatAvailability() {

        ChatConfiguration chatConfig = ServiceSDKUtils.getChatConfigurationBuilder(getApplicationContext()).build();

        // Create an agent availability client
        ChatCore.configureAgentAvailability(chatConfig).check()
                .onResult(new Async.ResultHandler<AvailabilityState>() {
                    @Override
                    public void handleResult(Async<?> async, @NonNull AvailabilityState availabilityState) {
                        // Display a toast when any agent availability state is changed
                        Toast.makeText(MainActivity.this,
                                String.format(getString(R.string.chat_availability_change_message), availabilityState.getStatus().toString()),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        return true;
    }

    /**
     * Initializes configurations and listeners which should happen at startup.
     */
    private void initializeServiceSDK() {
        setupServiceSDKListeners();
    }

    /**
     * An example of how an application can listen to the Service SDK for analytic type events.
     */
    private void setupServiceSDKListeners() {
        // You can listen to user-driven events from the Snap-ins SDK using the ServiceAnalytics system.
        // Implement ServiceAnalyticsListener and add your listener to ServiceAnalytics to start receiving events.
        ServiceAnalytics.addListener(new ServiceAnalyticsListener() {
            @Override
            public void onServiceAnalyticsEvent(String behaviourId, Map<String, Object> eventData) {
                // Left blank intentionally
            }
        });
    }

    /**
     * Adds click listeners to the main activity buttons.
     */
    private void setupButtons() {
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchChat();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    /**
     * Launches Chat.
     */
    private void launchChat() {
        ChatLauncher chat = new ChatLauncher();
        chat.launchChat(this);
    }

    /**
     * Initiates user login process.
     */
    private void login() {
        SalesforceSDKManager.getInstance()
                .getClientManager()
                .getRestClient(this, new ClientManager.RestClientCallback() {
                    @Override
                    public void authenticatedRestClient(RestClient client) {
                        // left blank intentionally
                    }
                });
    }

    /**
     * Logs out authenticated users.
     */
    private void logout() {
        SalesforceSDKManager.getInstance().logout(this, true);
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }

}
