package benjaminmestdagh.expenses.entities;

import java.io.Serializable;

/**
 * Created by benjamin on 06/08/13.
 */
public class Currency implements Serializable {
    private long id;
    private double value;
    private String name;
    private String code;

    public Currency(long id, double value, String name, String code) {
        this.name = name;
        this.value = value;
        this.id = id;
        this.code = code;
    }

    public Currency(double value, String name, String code) {
        this(0, value, name, code);
    }

    public Currency(String name, String code) {
        this(0, 1F, name, code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
