package com.sedefbas.sedef.controller;
import com.sedefbas.sedef.email.MailService;
import com.sedefbas.sedef.request.RegisterRequest;
import com.sedefbas.sedef.service.AuthService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")

public class AuthController {
    private AuthService authService;
    MailService mailService;

    public AuthController(AuthService authService, MailService mailService) {
        this.authService = authService;
        this.mailService = mailService;
    }


    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/comments")
    public String getcomments(@RequestBody RegisterRequest request){
        return "comments";
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        return authService.confirmToken(token);
    }


}
