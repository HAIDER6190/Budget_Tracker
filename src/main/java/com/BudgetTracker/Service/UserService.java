package com.BudgetTracker.Service;

import com.BudgetTracker.dto.UserRequestDTO;
import com.BudgetTracker.dto.UserResponseDTO;
import com.BudgetTracker.Entity.User;
import com.BudgetTracker.Exception.EmailAlreadyExistsException;
import com.BudgetTracker.Exception.UsernameAlreadyExistsException;
import com.BudgetTracker.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserProvider authUserProvider;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        // Check for duplicate username
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + dto.getUsername() + "' is already taken");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email '" + dto.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return mapToResponse(userRepository.save(user));
    }

    public UserResponseDTO getMyProfile() {
        User user = authUserProvider.getCurrentUser();
        return mapToResponse(user);
    }

    @Transactional
    public UserResponseDTO updateMyProfile(UserRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        // Check if new username is taken by another user
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new UsernameAlreadyExistsException("Username '" + dto.getUsername() + "' is already taken");
            }
            user.setUsername(dto.getUsername());
        }

        // Check if new email is taken by another user
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyExistsException("Email '" + dto.getEmail() + "' is already registered");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteMyAccount() {
        User user = authUserProvider.getCurrentUser();
        userRepository.delete(user);
    }

    private UserResponseDTO mapToResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
