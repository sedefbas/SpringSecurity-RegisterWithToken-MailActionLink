package com.sedefbas.sedef.service;
import com.sedefbas.sedef.email.EmailTestService;
import com.sedefbas.sedef.email.MailService;
import com.sedefbas.sedef.model.Role;
import com.sedefbas.sedef.model.User;
import com.sedefbas.sedef.request.RegisterRequest;
import com.sedefbas.sedef.security.token.Token;
import com.sedefbas.sedef.security.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    UserService userService;
    EmailTestService emailService;

    TokenService tokenService;

    private MailService mailService;

    public AuthService(UserService userService, EmailTestService emailService, TokenService tokenService, MailService mailService) {
        this.userService = userService;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.mailService = mailService;
    }

    public String register(RegisterRequest request) {
        boolean isValidEmail = emailService.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        String token = userService.signUpUser(
                new User(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        Role.USER
                )
        );

        String link = "http://localhost:8080/auth/confirm?token=" + token;
        mailService.send(
                request.getEmail(),
                buildEmail(request.getFirstName(), link));
        return token;
    }


    @Transactional
    public String confirmToken(String token) {
        Token confirmationToken = tokenService.getToken(token).orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        tokenService.setConfirmedAt(token);
        userService.enableUser(
                confirmationToken.getUser().getEmail());
        return "confirmed";
    }
    private String buildEmail(String name, String link) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Helvetica', Arial, sans-serif;\n" +
                "            font-size: 16px;\n" +
                "            margin: 0;\n" +
                "            color: #0b0c0c;\n" +
                "        }\n" +
                "        table {\n" +
                "            border-collapse: collapse;\n" +
                "            width: 100%;\n" +
                "            max-width: 580px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "        td {\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #0b0c0c;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .header-content {\n" +
                "            padding: 10px;\n" +
                "            font-size: 28px;\n" +
                "            font-weight: 700;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            vertical-align: top;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "        .blue-bar {\n" +
                "            background-color: #1D70B8;\n" +
                "            height: 10px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 30px 10px;\n" +
                "        }\n" +
                "        .content-paragraph {\n" +
                "            margin: 0 0 20px 0;\n" +
                "            font-size: 19px;\n" +
                "            line-height: 25px;\n" +
                "            color: #0b0c0c;\n" +
                "        }\n" +
                "        .link-block {\n" +
                "            border-left: 10px solid #b1b4b6;\n" +
                "            padding: 15px 0 0.1px 15px;\n" +
                "            font-size: 19px;\n" +
                "            line-height: 25px;\n" +
                "            background-color: #eaeaea; /* Gri arka plan */\n" +
                "        }\n" +
                "        .link {\n" +
                "            color: #1D70B8; /* Mavi renk */\n" +
                "            text-decoration: none; /* Alt çizgiyi kaldır */\n" +
                "        }\n" +
                "        .closing-paragraph {\n" +
                "            height: 30px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <table>\n" +
                "        <tr class=\"header\">\n" +
                "            <td>\n" +
                "                <span class=\"header-content\">Confirm your email</span>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"blue-bar\"></td>\n" +
                "        </tr>\n" +
                "        <tr class=\"content\">\n" +
                "            <td>\n" +
                "                <p class=\"content-paragraph\">Hi " + name + ",</p>\n" +
                "                <p class=\"content-paragraph\">Thank you for registering. Please click on the below link to activate your account:</p>\n" +
                "                <blockquote class=\"link-block\">\n" +
                "                    <p class=\"content-paragraph\"><a href=\"" + link + "\" class=\"link\">Activate Now</a></p>\n" +
                "                </blockquote>\n" +
                "                <p class=\"content-paragraph\">Link will expire in 15 minutes.</p>\n" +
                "                <p>See you soon</p>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr class=\"closing-paragraph\">\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";
    }


}