package com.fcb.porygon.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
open class SecurityConfig {
    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
