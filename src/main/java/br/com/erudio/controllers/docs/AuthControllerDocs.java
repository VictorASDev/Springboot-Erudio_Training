package br.com.erudio.controllers.docs;

import br.com.erudio.data.dto.v1.security.TokenDTO;
import br.com.erudio.data.dto.v1.security.AccountCredentialsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint", description = "Endpoint For Managing Authentication")
public interface AuthControllerDocs {
    @Operation(
            summary = "Authenticates An User And Returns a Token",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = TokenDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_XML_VALUE,
                                            schema = @Schema(implementation = TokenDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_YAML_VALUE,
                                            schema = @Schema(implementation = TokenDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            }

    )
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials);

    @Operation(
            summary = "Refresh Token for authenticated user and returns a token!",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = TokenDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_XML_VALUE,
                                            schema = @Schema(implementation = TokenDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_YAML_VALUE,
                                            schema = @Schema(implementation = TokenDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            }

    )
    public ResponseEntity<?> refreshToken(@PathVariable("username") String username,
                                          @RequestHeader("Authorization") String refreshToken);

    @Operation(
            summary = "Creates a New User on Data!",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = AccountCredentialsDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_XML_VALUE,
                                            schema = @Schema(implementation = AccountCredentialsDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_YAML_VALUE,
                                            schema = @Schema(implementation = AccountCredentialsDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            }

    )
    public ResponseEntity<Void> create(@RequestBody AccountCredentialsDTO credentials);
}
