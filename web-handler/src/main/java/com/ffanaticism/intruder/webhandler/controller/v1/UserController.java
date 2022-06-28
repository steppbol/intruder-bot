package com.ffanaticism.intruder.webhandler.controller.v1;

import com.ffanaticism.intruder.serviceprovider.model.StoredUser;
import com.ffanaticism.intruder.serviceprovider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ffanaticism.intruder.webhandler.controller.ApiPath.API_PATH;
import static com.ffanaticism.intruder.webhandler.controller.ApiPath.USERS_PATH;
import static com.ffanaticism.intruder.webhandler.controller.ApiPath.V1_PATH;
import static com.ffanaticism.intruder.webhandler.controller.ApiPath.WEB_HANDLER_PATH;

@RestController
@RequestMapping(value = API_PATH + V1_PATH + WEB_HANDLER_PATH + USERS_PATH)
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<StoredUser> getAll() {
        return userService.getAll();
    }
}
