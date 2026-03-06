package br.com.erudio.controllers;

import br.com.erudio.controllers.docs.AuthControllerDocs;
import br.com.erudio.data.dto.v1.security.AccountCredentialsDTO;
import br.com.erudio.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthController implements AuthControllerDocs {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Override
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials) {
        if (credentialsIsInvalid(credentials))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Client Request!");

        var token = service.signIn(credentials);

        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Client Request!");

        return ResponseEntity.ok().body(token);
    }

    @Override
    @PutMapping("/refresh/{username}")
    public ResponseEntity<?> refreshToken(@PathVariable("username") String username,
                                          @RequestHeader("Authorization") String refreshToken) {

        if (parametersAreInvalid(username, refreshToken))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Client Request!");
        
        var token = service.refreshToken(username, refreshToken);

        return ResponseEntity.ok().body(token);
    }

    @Override
    @PostMapping(value = "/createUser",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    public ResponseEntity<AccountCredentialsDTO> create(@RequestBody AccountCredentialsDTO credentials) {
        var response = service.create(credentials);

        return ResponseEntity.ok(response);
    }



    private static boolean parametersAreInvalid(String username, String refreshToken) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(refreshToken);
    }

    private static boolean credentialsIsInvalid(AccountCredentialsDTO credentials) {
        return credentials == null ||
                StringUtils.isBlank(credentials.getPassword()) ||
                StringUtils.isBlank(credentials.getUsername());
    }
}
