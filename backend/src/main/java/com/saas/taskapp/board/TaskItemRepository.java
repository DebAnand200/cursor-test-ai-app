package com.saas.taskapp.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByBoardIdOrderByPositionAsc(Long boardId);
}

