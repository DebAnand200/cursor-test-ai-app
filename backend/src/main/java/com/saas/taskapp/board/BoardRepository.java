package com.saas.taskapp.board;

import com.saas.taskapp.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwner(User owner);
}

