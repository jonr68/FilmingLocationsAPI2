package com.example.FilmingLacationsAPI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    
    @GetMapping("/helloworld")
    public ResponseEntity<String> helloWorld(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            // Get username from OAuth2 provider
            String username = principal.getAttribute("name");
            String email = principal.getAttribute("email");
            
            return ResponseEntity.ok("Hello " + username + "! Your email is: " + email);
        }
        
        return ResponseEntity.ok("Hello World!");
    }
    
    @GetMapping("/")
    public String home() {
        return "Welcome! <a href='/oauth2/authorization/github'>Login with GitHub</a> | " +
               "<a href='/oauth2/authorization/google'>Login with Google</a>";
    }
}