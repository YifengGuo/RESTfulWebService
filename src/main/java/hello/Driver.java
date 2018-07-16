package hello;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by guoyifeng on 7/16/18.
 */
public class Driver {
    private long id;
    private String firstName;
    private String lastName;

    @JsonProperty
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Driver() {}

    public Driver(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
