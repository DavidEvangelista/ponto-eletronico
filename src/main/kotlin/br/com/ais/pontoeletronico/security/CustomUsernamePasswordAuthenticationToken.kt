package br.com.ais.pontoeletronico.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class CustomUsernamePasswordAuthenticationToken : UsernamePasswordAuthenticationToken {

    var userId: Long? = null
    constructor(principal: Any, credentials: Any, userId: Long) : super(principal, credentials) {
        this.userId = userId
    }

    constructor(principal: Any, credentials: Any, authorities: Collection<GrantedAuthority>, userId: Long) : super(principal, credentials, authorities) {
        this.userId = userId
    }
}
