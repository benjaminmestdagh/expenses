package benjaminmestdagh.expenses.data;

import benjaminmestdagh.expenses.App;
import benjaminmestdagh.expenses.R;

/**
 * A class containing the strings for making requests to the Open Exchange Rate API.
 * Created by benjamin on 08/08/13.
 */
public final class OpenExchangeRatesApi {

    private static final String APP_ID = App.getContext().getString(R.string.open_exchange_rates_api_key);
    private static final String BASE_URL = "http://openexchangerates.org/api/";

    public static final String LATEST_RATES = BASE_URL + "latest.json?app_id=" + APP_ID;
    public static final String ALL_CURRENCIES = BASE_URL + "currencies.json";
}
