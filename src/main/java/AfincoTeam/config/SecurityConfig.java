package AfincoTeam.config;

/**
Classe de configuração de segurança.
Configura o filtro de segurança usando Spring Security,
permitindo todas as requisições e desabilitando CSRF para facilitar
o desenvolvimento com autenticação baseada em sessão.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // permite todas as requisições sem autenticação
            )
            .csrf(csrf -> csrf.disable());  // desabilita a proteção CSRF para facilitar o desenvolvimento com autenticação baseada em sessão

        return http.build();
    }
}