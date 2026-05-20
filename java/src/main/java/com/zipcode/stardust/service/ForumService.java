package com.zipcode.stardust.service;

import com.zipcode.stardust.model.Subforum;
import com.zipcode.stardust.repository.SubforumRepository;
import com.zipcode.stardust.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ForumService {

    @Autowired
    private SubforumRepository subforumRepository;

    @Autowired
    private UserRepository userRepository;

    public String generateLinkPath(Long subforumId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" / <a href='/'>Forum Index</a>");
        Optional<Subforum> opt = subforumRepository.findById(subforumId);
        if (opt.isEmpty()) return sb.toString();
        Subforum current = opt.get();
        java.util.LinkedList<Subforum> chain = new java.util.LinkedList<>();
        while (current != null) {
            chain.addFirst(current);
            current = current.getParent();
        }
        for (Subforum sf : chain) {
            sb.append(" / <a href='/subforum?sub=").append(sf.getId()).append("'>")
              .append(escapeHtml(sf.getTitle())).append("</a>");
        }
        return sb.toString();
    }

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
        "https?://(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([\\w-]+)(?:\\S*)?",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile(
        "https?://\\S+\\.(?:jpg|jpeg|png|gif|webp)(?:\\?\\S*)?",
        Pattern.CASE_INSENSITIVE
    );

    public String embedMedia(String content) {
        if (content == null) return "";
        String escaped = escapeHtml(content);
        Matcher yt = YOUTUBE_PATTERN.matcher(escaped);
        String withYoutube = yt.replaceAll(m ->
            "<iframe width=\"560\" height=\"315\" " +
            "src=\"https://www.youtube.com/embed/" + m.group(1) + "\" " +
            "frameborder=\"0\" allowfullscreen></iframe>"
        );
        Matcher img = IMAGE_URL_PATTERN.matcher(withYoutube);
        return img.replaceAll(
            "<img src=\"$0\" style=\"max-width:100%;display:block;margin:8px 0;\" alt=\"image\">"
        );
    }

    private String escapeHtml(String text) {
        return HtmlUtils.htmlEscape(text != null ? text : "");
    }

    public boolean validTitle(String title) {
        return title != null && title.length() > 4 && title.length() < 140;
    }

    public boolean validContent(String content) {
        return content != null && content.length() > 10 && content.length() < 5000;
    }

    public boolean validUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9!@#%&]{4,40}$");
    }

    public boolean validPassword(String password) {
        return password != null && password.matches("^[a-zA-Z0-9!@#%&]{6,40}$");
    }

    public boolean usernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
