package web;

import web.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Communication {

    @PostConstruct
    private void initData() {
        ResponseEntity<List<User>> usersList = getUsersList();
        System.out.println(usersList.getBody());
        
        User newUser = new User(3, "James", "Brown", 25);
        ResponseEntity<String> responseSaveUser = saveUser(newUser);
        System.out.println(responseSaveUser.getBody());

        newUser.setName("Thomas");
        newUser.setLastName("Shelby");
        ResponseEntity<String> responseEditUser = editUser(newUser);
        System.out.println(responseEditUser.getBody());

        ResponseEntity<String> responseDeleteUser = deleteUser(3);
        System.out.println(responseDeleteUser.getBody());

    }

    @Autowired
    private RestTemplate restTemplate;
    private List<String> cookies;

    private final String URL = "http://94.198.50.185:7081/api/users";

    public ResponseEntity<List<User>> getUsersList() {
        ResponseEntity<List<User>> responseEntity =
                restTemplate.exchange(URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<User>>() {});

        cookies = responseEntity.getHeaders().get("Set-Cookie");
        System.out.println(cookies);
        return responseEntity;
    }

    public ResponseEntity<String> saveUser(User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookies.stream().collect(Collectors.joining(";")));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        System.out.println(headers);

        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(URL, httpEntity, String.class);
        return responseEntity;
    }

    public ResponseEntity<String> editUser(User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookies.stream().collect(Collectors.joining(";")));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.PUT, httpEntity, String.class);
        return responseEntity;
    }

    public ResponseEntity<String> deleteUser(int id) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookies.stream().collect(Collectors.joining(";")));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<User> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, httpEntity, String.class);
        return responseEntity;
    }
}
