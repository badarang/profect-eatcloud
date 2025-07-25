package profect.eatcloud.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정
 * 테스트를 위해 Payment API를 허용합니다
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/payments/**").permitAll()     // 결제 UI 페이지 허용
                .requestMatchers("/payment/**").permitAll()      // 정적 결제 페이지 허용 (success.html 등)
                .requestMatchers("/api/payments/**").permitAll() // Payment API 허용
                .anyRequest().authenticated()  // 나머지는 인증 필요
            )
            .csrf(csrf -> csrf.disable())  // 테스트를 위해 CSRF 비활성화
            .httpBasic(httpBasic -> httpBasic.disable());  // 기본 인증 비활성화
            
        return http.build();
    }
} 