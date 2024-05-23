package br.com.ero.sendemailhtml.service;

import br.com.ero.sendemailhtml.domain.User;

public interface UserService {
    User saveUser(User user);

    Boolean verifyToken(String token);
}
