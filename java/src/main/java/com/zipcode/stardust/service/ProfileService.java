package com.zipcode.stardust.service;
import java.io.File;

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

    public String saveAvatar(MultipartFile file, String username) {
    try {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Create a folder inside /static/avatars/
        String uploadDir = "avatars/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Build a unique filename: username + original extension
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String filename = username + extension;
        File destination = new File(uploadDir + filename);

        // Save file to disk
        file.transferTo(destination);

        // Return the URL path used by the browser
        return "/avatars/" + filename;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}
