package benjaminmestdagh.expenses.entities;

import java.io.Serializable;

/**
 * Created by benjamin on 30/07/13.
 */
public class Expense implements Serializable {
    private long id;
    private String name;
    private long account_id;
    private Category category;
    private double amount;
    private Currency currency;
    private long date;
    private PaymentMethod paymentMethod;

    public Expense(long id, String name, long account_id,
                   Category category, double amount, Currency currency, long date, PaymentMethod p) {
        this.id = id;
        this.name = name;
        this.account_id = account_id;
        this.category = category;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.paymentMethod = p;
    }

    public Expense(String name, long account_id, Category category,
                   double amount, Currency currency, long date, PaymentMethod p) {
        this(0, name, account_id, category, amount, currency, date, p);
    }

    public Expense(String name, long account_id, Category category,
                   double amount, Currency currency, PaymentMethod p) {
        this(0, name, account_id, category, amount, currency, new java.util.Date().getTime(), p);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public String getCategoryName() {
        return category.getName();
    }

    public long getCategory_id() {
        return category.getId();
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountInUsd() {
        return this.amount / currency.getValue();
    }

    public double getAmountIn(Currency resultCurrency) {
        return this.getAmountInUsd() * resultCurrency.getValue();
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

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getPaymentMethod_id() {
        return this.paymentMethod.getId();
    }

    public String getPaymentMethodName() {
        return this.paymentMethod.getName();
    }

    public String getPaymentMethodIcon() {
        return this.paymentMethod.getTypeIcon();
    }

    public void setPaymentMethod(PaymentMethod p) {
        this.paymentMethod = p;
    }

    @Override
    public String toString() {
        return this.name + ": " + this.getCurrencyCode() + " " + this.amount;
    }

}
