package com.djedra.oAuth2authorizationservicewithseciurity.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthConfiguration extends AuthorizationServerConfigurerAdapter {

	public static String REALM = "CRM_REALM";

	private static final int THIRTY_DAYS = 60 * 60 * 24 * 30;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

//	{noop} oznacza, że hasło będzie przechowywane jako " plain text" czyli nie zaszyfrowane (nie zalecane w kodzie produkcyjnym). Używa deprecjonowane NoOpPasswordEncoder zamiast DelegatingPasswordEncoder do walidacji hasła
//		User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build(); 
//		User.withUsername("user").password("{noop}user").roles("USER").build();
//	Powinno się używać PasswordEncoder 

//	zwraca token z odpowiednimi credencialami tj. role itp
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("diego").secret("{noop}haslo")
				.authorizedGrantTypes("password", "refresh_token", "client_credentials")
				.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("read", "write", "trust")
				.accessTokenValiditySeconds(300).refreshTokenValiditySeconds(THIRTY_DAYS);

//		wywala: java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
//		clients.inMemory().withClient("diego").secret(passwordEncoder().encode("haslo")).authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("all").authorizedGrantTypes("password", "refresh_token", "client_credentials")
//				.and()
//				.withClient("diegoRoleClient").secret(passwordEncoder().encode("hasloRoleClient")).authorities("ROLE_CLIENT").scopes("all").authorizedGrantTypes("client_credentials")
//				.accessTokenValiditySeconds(300).refreshTokenValiditySeconds(THIRTY_DAYS);

//		nie pozwala z grant_type: password
//		PasswordEncoder encoder =
//				PasswordEncoderFactories.createDelegatingPasswordEncoder();
//		
//		clients.inMemory().withClient("diego").secret(encoder.encode("haslo")).authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("all").authorizedGrantTypes("password", "refresh_token", "client_credentials")
//		.and()
//		.withClient("diegoRoleClient").secret(encoder.encode("hasloRoleClient")).authorities("ROLE_CLIENT").scopes("all").authorizedGrantTypes("client_credentials")
//		.accessTokenValiditySeconds(300).refreshTokenValiditySeconds(THIRTY_DAYS);
	}

//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder(4);
//	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
				.authenticationManager(authenticationManager);
	}

//	The only goal of this method is to define the realm in the sense of the HTTP/1.1:
//		The "realm" authentication parameter is reserved for use by authentication schemes that wish to indicate a scope of protection. [...] These realms allow the protected resources on a server to be partitioned into a set of protection spaces, each with its own authentication scheme and/or authorization database.

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.realm(REALM);
		oauthServer.checkTokenAccess("permitAll()");
	}

}
