package com.hotel.management.dto;

import com.hotel.management.model.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private User.Role role;

    public static UserResponse fromEntity(User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole());
        return resp;
    }
}
