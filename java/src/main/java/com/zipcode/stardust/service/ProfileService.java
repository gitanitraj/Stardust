package com.zipcode.stardust.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.UserRepository;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Load a user profile by username
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // Update profile fields (displayName, bio, etc.)
    public User updateProfile(User user, String displayName, String bio) {
        user.setDisplayName(displayName);
        user.setBio(bio);
        return userRepository.save(user);
    }

    // Optional: handle avatar upload
    public String saveAvatar(MultipartFile file) {
        // TODO: implement file validation + save logic
        return null;
    }
}
