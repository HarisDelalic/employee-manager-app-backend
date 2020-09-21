package com.dela.employeemanagerapp.service;

import com.dela.employeemanagerapp.domain.User;
import com.dela.employeemanagerapp.domain.UserPrincipal;
import com.dela.employeemanagerapp.exception.domain.UserNotFoundException;
import com.dela.employeemanagerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
@Qualifier("userServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User foundUser = userRepository.findUserByUsername(username).map(user -> {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(LocalDate.now());
            return userRepository.save(user);
        }).orElseThrow(() ->  {
            log.error("User with username: " + username + " not found");
            throw new UserNotFoundException("User with username: " + username + " not found");
        });

        return new UserPrincipal(foundUser);
    }
}