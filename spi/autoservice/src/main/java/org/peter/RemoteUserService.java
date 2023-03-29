package org.peter;

import com.google.auto.service.AutoService;

@AutoService(UserService.class)
public class RemoteUserService implements UserService {

    @Override
    public String userName() {
        return "remote user";
    }
}
