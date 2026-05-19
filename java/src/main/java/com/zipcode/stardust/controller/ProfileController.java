package com.zipcode.stardust.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.zipcode.stardust.model.User;
import com.zipcode.stardust.service.ProfileService;


@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // GET /users/{username}
    @GetMapping("/users/{username}")
    public String viewProfile(@PathVariable String username, Model model) {
        User profileUser = profileService.getUserProfile(username);
        if (profileUser == null) {
            return "error/404";
        }
        model.addAttribute("profileUser", profileUser);
        return "profile/view";
    }

    // GET /settings/profile
    @GetMapping("/settings/profile")
    public String editProfileForm(@AuthenticationPrincipal UserDetails currentUser,
                                  Model model) {
        User user = profileService.getUserProfile(currentUser.getUsername());
        model.addAttribute("user", user);
        return "profile/edit";
    }

    // POST /settings/profile
    @PostMapping("/settings/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails currentUser,
                                @RequestParam String displayName,
                                @RequestParam String bio) {

        User user = profileService.getUserProfile(currentUser.getUsername());
        profileService.updateProfile(user, displayName, bio);

        return "redirect:/users/" + user.getUsername();
    }

    // POST /settings/profile/avatar
    @PostMapping("/settings/profile/avatar")
    public String uploadAvatar(@AuthenticationPrincipal UserDetails currentUser,
                               @RequestParam("avatar") MultipartFile file) {

        User user = profileService.getUserProfile(currentUser.getUsername());
        String avatarUrl = profileService.saveAvatar(file);

        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
            profileService.updateProfile(user, user.getDisplayName(), user.getBio());
        }

        return "redirect:/users/" + user.getUsername();
    }
}

