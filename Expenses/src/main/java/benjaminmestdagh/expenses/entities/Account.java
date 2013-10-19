package benjaminmestdagh.expenses.entities;

import java.io.Serializable;

/**
 * Created by benjamin on 30/07/13.
 */
public class Account implements Serializable {
    private long id;
    private String name;
    private Currency currency;
    private long date;

    public Account(long id, String name, Currency currency, long date) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.date = date;
    }

    public Account(String name, Currency currency, long date) {
        this(0, name, currency, date);
    }

    public Account(String name, Currency currency) {
        this(0, name, currency, new java.util.Date().getTime());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrencyCode() {
        return currency.getCode();
    }

    public long getCurrency_id() {
        return currency.getId();
    }

    public double getCurrencyValue() {
        return currency.getValue();
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
