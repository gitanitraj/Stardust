package com.zipcode.stardust.controller;

import org.springframework.stereotype.Controller;

import com.zipcode.stardust.service.ProfileService;

@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
}
