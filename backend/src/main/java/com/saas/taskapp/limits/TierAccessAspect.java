package com.saas.taskapp.limits;

import com.saas.taskapp.user.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TierAccessAspect {

    @Around("execution(* com.saas.taskapp.board.BoardController.createBoard(..)) && args(user, ..)")
    public Object enforceFreeTierBoardLimit(ProceedingJoinPoint pjp, @AuthenticationPrincipal User user) throws Throwable {
        // Placeholder for real enforcement. For now allow all.
        return pjp.proceed();
    }
}

