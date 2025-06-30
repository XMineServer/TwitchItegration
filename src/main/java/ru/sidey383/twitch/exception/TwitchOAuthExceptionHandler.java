package ru.sidey383.twitch.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class TwitchOAuthExceptionHandler {

    @ExceptionHandler(TwitchInvalidRefreshTokenException.class)
    public ModelAndView handleInvalidRefreshToken(
            TwitchInvalidRefreshTokenException ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        ModelAndView mav = new ModelAndView("redirect:/");
        mav.addObject("error", "Ваша сессия истекла, пожалуйста, авторизуйтесь снова.");
        return mav;
    }
}

