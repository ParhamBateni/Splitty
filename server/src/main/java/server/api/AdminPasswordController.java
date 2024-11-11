package server.api;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/admin")
public class AdminPasswordController {
    public static String password = UUID.randomUUID().toString().substring(0, 8);;

    @GetMapping("")
    public boolean checkAdminPassword(@RequestParam String password) {
        return this.password.equals(password);
    }
    @GetMapping("/print")
    public void print() {
        System.out.println("**************************************\n" +
                "The admin password is " + password + " \n" +
                "**************************************");
    }

    @GetMapping("/check")
    public void check() {

    }

}

