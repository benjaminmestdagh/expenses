package benjaminmestdagh.expenses.entities;

import java.io.Serializable;

/**
 * Created by benjamin on 06/08/13.
 */
public class PaymentMethod implements Serializable {
    private long id;
    private String name;
    private PaymentMethodType type;

    public PaymentMethod(long id, String name, PaymentMethodType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public PaymentMethod(String name, PaymentMethodType type) {
        this(0, name, type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getType_id() {
        return this.type.getId();
    }

    public String getType() {
        return this.type.getName();
    }

    public String getTypeIcon() {
        return this.type.getIcon();
    }

    public void setType(PaymentMethodType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
