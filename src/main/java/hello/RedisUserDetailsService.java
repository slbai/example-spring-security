package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.core.BulkMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by Walter on 2018/10/23
 */
@Component
public class RedisUserDetailsService implements UserDetailsManager {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Map<Object, Object> map = redisTemplate.opsForHash().entries("user:name:" + username);
        List<SimpleGrantedAuthority> roles = Arrays.stream(map.get("role").toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new User(username,encoder.encode(map.get("pwd").toString()),roles);
    }

    @Override
    public void createUser(UserDetails user) {
        redisTemplate.opsForList().leftPush("user:name",user.getUsername());
        redisTemplate.opsForHash().put("user:name:"+user.getUsername(),"pwd",user.getPassword());
        List<String> strings = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        redisTemplate.opsForHash().put("user:name:"+user.getUsername(),"role",String.join(",",strings));
    }

    @Override
    public void updateUser(UserDetails user) {
    }

    @Override
    public void deleteUser(String username) {
        redisTemplate.delete("user:name:"+username);
        redisTemplate.opsForList().remove("user:name",1,username);
    }

    @Override
    public void changePassword(String oldPassword,String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return redisTemplate.hasKey("user:name:"+username);
    }

    /** 自定义方法 **/
    public List<? extends UserDetails> user(Long currentPage,Long pageSize){
        return redisTemplate.sort(
                SortQueryBuilder
                        .sort("user:name")
                        .get("#")
                        .get("user:name:*->pwd")
                        .get("user:name:*->role")
                        .alphabetical(true)
                        .order(SortParameters.Order.ASC)
                        .limit(currentPage*pageSize,pageSize).build(),(BulkMapper<UserDetails, String>) tuple -> {
                            List<SimpleGrantedAuthority> roles = Arrays.stream(tuple.get(2).split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                            return new User(tuple.get(0),tuple.get(1),roles);
                        });
    }
}
