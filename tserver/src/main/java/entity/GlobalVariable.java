package entity;

/**
 * Created by Ageev Evgeny on 24.03.2016.
 */
//@Entity
//@Table(name = "global_variables")
public class GlobalVariable {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String key;

    private String value;

    public GlobalVariable() {}

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
