package com.example.Catalogo.security;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.stream.Collectors;
import com.example.Catalogo.dto.AuthResponse;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {



    private final RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String url = "http://localhost:8081/auth/user-data/" + email;

        try {

            AuthResponse userData = restTemplate.getForObject(url, AuthResponse.class);

            if (userData == null || userData.getEmail() == null) {
                throw new UsernameNotFoundException("Usuario no encontrado: " + email);
            }


            Collection<SimpleGrantedAuthority> authorities = userData.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());


            return new org.springframework.security.core.userdetails.User(
                    userData.getEmail(),
                    "",
                    authorities
            );

        } catch (Exception e) {

            throw new UsernameNotFoundException("Error de autenticación o usuario no encontrado: " + email, e);
        }
    }
}