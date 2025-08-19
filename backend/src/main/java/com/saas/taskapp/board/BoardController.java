package com.saas.taskapp.board;

import com.saas.taskapp.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardRepository boardRepository;
    private final TaskItemRepository taskItemRepository;

    @GetMapping
    public List<Board> myBoards(@AuthenticationPrincipal User user) {
        return boardRepository.findByOwner(user);
    }

    @PostMapping
    public Board createBoard(@AuthenticationPrincipal User user, @RequestBody CreateBoardRequest req) {
        Board board = Board.builder().name(req.getName()).owner(user).build();
        return boardRepository.save(board);
    }

    @GetMapping("/{boardId}/tasks")
    public List<TaskItem> listTasks(@PathVariable Long boardId) {
        return taskItemRepository.findByBoardIdOrderByPositionAsc(boardId);
    }

    @PostMapping("/{boardId}/tasks")
    public TaskItem createTask(@PathVariable Long boardId, @RequestBody CreateTaskRequest req) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        TaskItem task = TaskItem.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .status("TODO")
                .position(req.getPosition() == null ? 0 : req.getPosition())
                .board(board)
                .build();
        return taskItemRepository.save(task);
    }

    @PatchMapping("/tasks/{taskId}")
    public TaskItem updateTask(@PathVariable Long taskId, @RequestBody Map<String, Object> updates) {
        TaskItem task = taskItemRepository.findById(taskId).orElseThrow();
        if (updates.containsKey("title")) task.setTitle((String) updates.get("title"));
        if (updates.containsKey("description")) task.setDescription((String) updates.get("description"));
        if (updates.containsKey("status")) task.setStatus((String) updates.get("status"));
        if (updates.containsKey("position")) task.setPosition((Integer) updates.get("position"));
        return taskItemRepository.save(task);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskItemRepository.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class CreateBoardRequest {
        @NotBlank
        private String name;
    }

    @Data
    public static class CreateTaskRequest {
        @NotBlank
        private String title;
        private String description;
        private Integer position;
    }
}

