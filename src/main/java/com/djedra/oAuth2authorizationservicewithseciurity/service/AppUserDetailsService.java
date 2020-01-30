package com.djedra.oAuth2authorizationservicewithseciurity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.djedra.oAuth2authorizationservicewithseciurity.entity.Role;
import com.djedra.oAuth2authorizationservicewithseciurity.entity.User;
import com.djedra.oAuth2authorizationservicewithseciurity.repository.UserRepository;

@Component
public class AppUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(s);

		if (user == null) {
			throw new UsernameNotFoundException(String.format("The username %s doesn't exist", s));
		}

		user.getRoles().forEach(new Consumer<Role>() {
			public void accept(Role r) {
				authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
			}
		});

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(), authorities);

		return userDetails;
	}
}
