package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;


/**
 * Create by Walter on 2018/10/23
 */
@RestController
public class UserController {

    @Autowired
    private RedisUserDetailsService userService;

    @GetMapping("/user")
    public ResponseEntity<? extends Collection> user(@RequestParam(defaultValue = "0") Long currentPage,
                                                     @RequestParam(defaultValue = "10") Long pageSize){
        List<? extends UserDetails> users = userService.user(currentPage,pageSize);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/user/add")
    public ResponseEntity<String> add(User user){
        if (userService.userExists(user.getUsername())){
            return ResponseEntity.ok("用户名已存在");
        }
        userService.createUser(user);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/user/delete")
    public ResponseEntity<String> delete(String username){
        userService.deleteUser(username);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/user/update")
    public ResponseEntity<String> update(User user,String oldUsername){
        userService.deleteUser(oldUsername);
        userService.createUser(user);
        return ResponseEntity.ok("success");
    }
}
