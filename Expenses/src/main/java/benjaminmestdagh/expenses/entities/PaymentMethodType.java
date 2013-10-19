package benjaminmestdagh.expenses.entities;

import java.io.Serializable;

/**
 * Created by benjamin on 10/08/13.
 */
public class PaymentMethodType implements Serializable {
    private long id;
    private String name;
    private String icon;

    public PaymentMethodType(long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public PaymentMethodType(String name, String icon) {
        this(0, name, icon);
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
