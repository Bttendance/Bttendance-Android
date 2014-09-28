package com.uservoice.uservoicesdk.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.UserVoice;
import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestResult;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class ClientConfig extends BaseModel {
    private boolean ticketsEnabled;
    private boolean feedbackEnabled;
    private boolean whiteLabel;
    private int defaultForumId;
    private List<CustomField> customFields;
    private String defaultSort;
    private String subdomainId;
    private String key;
    private String secret;
    private String accountName;
    private boolean displaySuggestionsByRank;

    public static void loadClientConfig(final Callback<ClientConfig> callback) {
        Config config = Session.getInstance().getConfig();
        if (config == null) {
            // TODO the config should be stored on disk or something
            RestResult restResult = new RestResult(new Exception("Uservoice config not loaded."));
            callback.onError(restResult);
            return;
        }
        String path = Session.getInstance().getConfig().getKey() == null ? "/clients/default.json" : "/client.json";
        final String cacheKey = String.format("uv-client-%s-%s-%s", UserVoice.getVersion(), Session.getInstance().getConfig().getSite(), Session.getInstance().getConfig().getKey());
        final SharedPreferences prefs = Session.getInstance().getSharedPreferences();
        // cache the client config and then request it in the background
        ClientConfig clientConfig = load(prefs, cacheKey, "client", ClientConfig.class);
        if (clientConfig != null) {
//            Log.d("UV", "client config from cache");
            callback.onModel(clientConfig);
            // background refresh
            doGet(apiPath(path), new RestTaskCallback(callback) {
                @Override
                public void onComplete(JSONObject result) throws JSONException {
                    ClientConfig clientConfig = deserializeObject(result, "client", ClientConfig.class);
                    clientConfig.persist(prefs, cacheKey, "client");
                }
            });
        } else {
//            Log.d("UV", "loading client config");
            doGet(apiPath(path), new RestTaskCallback(callback) {
                @Override
                public void onComplete(JSONObject result) throws JSONException {
                    ClientConfig clientConfig = deserializeObject(result, "client", ClientConfig.class);
                    clientConfig.persist(prefs, cacheKey, "client");
                    callback.onModel(clientConfig);
                }
            });
        }
    }

    @Override
    public void load(JSONObject object) throws JSONException {
        super.load(object);

        ticketsEnabled = object.getBoolean("tickets_enabled");
        feedbackEnabled = object.getBoolean("feedback_enabled");
        whiteLabel = object.getBoolean("white_label");
        if (object.has("display_suggestions_by_rank")) {
            displaySuggestionsByRank = object.getBoolean("display_suggestions_by_rank");
        }
        defaultForumId = object.getJSONObject("forum").getInt("id");
        customFields = deserializeList(object, "custom_fields", CustomField.class);
        defaultSort = getString(object.getJSONObject("subdomain"), "default_sort");
        subdomainId = getString(object.getJSONObject("subdomain"), "id");
        accountName = getString(object.getJSONObject("subdomain"), "name");
        key = object.getString("key");
        // secret will only be available for the default client
        secret = object.has("secret") ? object.getString("secret") : null;
    }

    @Override
    public void save(JSONObject object) throws JSONException {
        super.save(object);
        object.put("tickets_enabled", ticketsEnabled);
        object.put("feedback_enabled", feedbackEnabled);
        object.put("white_label", whiteLabel);
        object.put("display_suggestions_by_rank", displaySuggestionsByRank);
        JSONObject forum = new JSONObject();
        forum.put("id", defaultForumId);
        object.put("forum", forum);
        JSONArray jsonCustomFields = new JSONArray();
        for (CustomField customField : customFields) {
            JSONObject jsonCustomField = new JSONObject();
            customField.save(jsonCustomField);
            jsonCustomFields.put(jsonCustomField);
        }
        object.put("custom_fields", jsonCustomFields);
        JSONObject subdomain = new JSONObject();
        subdomain.put("id", subdomainId);
        subdomain.put("default_sort", defaultSort);
        subdomain.put("name", accountName);
        object.put("subdomain", subdomain);
        object.put("key", key);
        if (secret != null) {
            object.put("secret", secret);
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public boolean isTicketSystemEnabled() {
        return ticketsEnabled;
    }

    public boolean isFeedbackEnabled() {
        return feedbackEnabled;
    }

    public boolean isWhiteLabel() {
        return whiteLabel;
    }

    public boolean shouldDisplaySuggestionsByRank() {
        return displaySuggestionsByRank;
    }

    public int getDefaultForumId() {
        return defaultForumId;
    }

    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public String getSuggestionSort() {
        return defaultSort.equals("new") ? "newest" : (defaultSort.equals("hot") ? "hot" : "votes");
    }

    public String getSubdomainId() {
        return subdomainId;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }
}
