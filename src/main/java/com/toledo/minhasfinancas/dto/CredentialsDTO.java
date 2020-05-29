package com.toledo.minhasfinancas.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class to transfer only the client credentials.
 * 
 * @author Mateus Toledo <mateushtoledo@gmail.com>.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String email;
    private String password;
}
