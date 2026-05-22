package com.zipcode.stardust.config;

import com.zipcode.stardust.model.Subforum;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.SubforumRepository;
import com.zipcode.stardust.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private SubforumRepository subforumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin@localhost", "admin", "password", passwordEncoder);
            userRepository.save(admin);
        }

        if (subforumRepository.count() == 0) {
            Subforum forum = new Subforum("Forum",
                    "Announcements, bug reports, and general discussion about the forum belongs here", null);
            subforumRepository.save(forum);

            Subforum announcements = new Subforum("Announcements",
                    "View forum announcements here", forum);
            subforumRepository.save(announcements);

            Subforum bugReports = new Subforum("Bug Reports",
                    "Report bugs with the forum here", forum);
            subforumRepository.save(bugReports);

            Subforum general = new Subforum("General Discussion",
                    "Use this subforum to post anything you want", null);
            subforumRepository.save(general);

            Subforum other = new Subforum("Other",
                    "Discuss other things here", null);
            subforumRepository.save(other);
        }
    }
}
