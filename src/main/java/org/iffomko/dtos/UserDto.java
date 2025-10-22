package org.iffomko.dtos;

import org.iffomko.domain.User;

public class UserDto {
    private int id;
    private String phone;
    private String firstName;
    private String lastName;

    public UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.phone = user.getPhone();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public User toEntity() {
        return new User(id, phone, null, firstName, lastName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
