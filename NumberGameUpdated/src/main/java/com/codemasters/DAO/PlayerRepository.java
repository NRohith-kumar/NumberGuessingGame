package com.codemasters.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codemasters.Model.Players;

public interface PlayerRepository extends JpaRepository<Players, Integer> {
	
	Players findByUserName(String usernamefromHtml);
	List<Players> findTop5ByOrderByTotalScoreDesc();

}
