package br.com.ero.sendemailhtml.service;

import br.com.ero.sendemailhtml.domain.Confirmation;
import br.com.ero.sendemailhtml.domain.User;
import br.com.ero.sendemailhtml.repository.ConfirmationRepository;
import br.com.ero.sendemailhtml.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }
        user.setEnabled(false);
        userRepository.save(user);

        Confirmation confirmation = new Confirmation();
        confirmationRepository.save(confirmation);

//        TODO Send email to user with token

        return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);
//     Optional ->   confirmationRepository.delete(confirmation);
        return true;
    }
}
