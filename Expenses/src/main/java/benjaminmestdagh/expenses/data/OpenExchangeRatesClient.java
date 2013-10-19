package benjaminmestdagh.expenses.data;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import benjaminmestdagh.expenses.entities.Currency;

/**
 * Created by benjamin on 07/08/13.
 */
public class OpenExchangeRatesClient {

    private static OpenExchangeRatesClient instance;

    private OpenExchangeRatesClient() {}

    public static OpenExchangeRatesClient getInstance() {
        if(instance == null) {
            instance = new OpenExchangeRatesClient();
        }

        return instance;
    }

    public void updateExchangeRates(Context context) throws Exception {
        Map<String, Double> rates = getExchangeRates();
        DatabaseOtherManager dbman = DatabaseOtherManager.getInstance(context);

        for(Currency currency : dbman.getCurrencies()) {
            currency.setValue(rates.get(currency.getCode()));
            dbman.updateCurrency(currency);
        }
    }

    public Map<String, Double> getExchangeRates() throws Exception {
        Map<String, Double> result = new HashMap<String, Double>();
        String jsonFeed = readJsonFeed(OpenExchangeRatesApi.LATEST_RATES, null);
        JSONObject jsonObject = new JSONObject(jsonFeed);
        JSONObject rates = jsonObject.getJSONObject("rates");
        java.util.Iterator keys = rates.keys();
        String code;
        double value;

        while(keys.hasNext()) {
            code = (String)keys.next();
            value = rates.getDouble(code);

            result.put(code, value);
        }

        return result;
    }

    public List<Currency> getCurrencies() throws Exception {
        List<Currency> currencies = new LinkedList<Currency>();
        String jsonFeed = readJsonFeed(OpenExchangeRatesApi.ALL_CURRENCIES, null);
        JSONObject jsonObject = new JSONObject(jsonFeed);
        java.util.Iterator keys = jsonObject.keys();
        String code;
        String name;

        while(keys.hasNext()) {
            code = (String)keys.next();
            name = jsonObject.getString(code);
            currencies.add(new Currency(name, code));
        }

        return currencies;
    }

    private String readJsonFeed(String url, Map<String, String> parameters) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        if(parameters != null) url += buildQueryString(parameters);

        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private String buildQueryString(Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();

        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            sb.append("&" + entry.getKey() + "=" + entry.getValue());
        }

        return sb.toString();
    }
}
