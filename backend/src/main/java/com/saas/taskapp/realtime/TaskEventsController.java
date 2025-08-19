package com.saas.taskapp.realtime;

import com.saas.taskapp.board.TaskItem;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TaskEventsController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("task.updated")
    public void broadcastTaskUpdate(TaskItem task) {
        messagingTemplate.convertAndSend("/topic/tasks", task);
    }
}

