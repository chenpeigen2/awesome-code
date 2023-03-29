package org.peter;

import com.google.auto.service.AutoService;

@AutoService(UserService.class)
public class LocalUserService implements UserService {

    @Override
    public String userName() {
        return "local user";
    }
}