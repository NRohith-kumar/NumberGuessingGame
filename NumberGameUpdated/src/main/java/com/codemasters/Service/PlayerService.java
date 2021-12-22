package com.codemasters.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codemasters.Constants.Constant;
import com.codemasters.DAO.PlayerRepository;
import com.codemasters.Model.Players;


@Service
public class PlayerService {
	 
	@Autowired
	PlayerRepository pRepo;
	
	public Players RegisterUser(String userName,String password)
	{
		Players player = new Players();
		player.setUserName(userName);
		player.setPassword(password);
		pRepo.save(player);
		return player;
		
	}
	
	public boolean login(String userName,String password){
		
		Players player = pRepo.findByUserName(userName);
		if(player != null && player.getPassword().equals(password))
		{
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public  int generateNumber() {
		
		return ThreadLocalRandom.current().nextInt(Constant.min,Constant.max);
	}
	
	public Map<String,String> checkUserGuess(int guess , int generated , int buffer){
		Map<String,String> map = new HashMap<>();
		if(guess < Constant.min || guess > Constant.max) {
			map.put("outOfRange", "Guessed Number is out of Range ("+Constant.min+"-"+Constant.max+")");
			return map;
		}
		else if(generated-buffer <= guess && guess <= generated+buffer) {
			map.put("correctGuess", "Guess Is Correct");
			return map;
		}
		else if(generated < guess)
		{
			map.put("lessThanGenerated","Magic number is less than "+guess);
			
			return map;
		}
		else {
			map.put("greaterThanGenerated","Magic Number is greater than "+guess);
			return map;
		}
	}
	
	public int calculateBuffer(int max,int min) {
		if((max-min)<=100)
		{
			int buffer=2;
			return buffer;
		}
		else
		{
			int multiplier = 5;
			int index = (max-min)/100;
			int buffer = multiplier*index; 
			return buffer;
		}
	}
	
	public int calculateScore(int count) {
		switch(count) {
		case 2:return 100;
		case 1:return 50;
		case 0:return 25;
		default:return 0;
		}
	}
	
	public Players calculateTotalScore(String userName,int score)
	{
		Players player = pRepo.findByUserName(userName);
		
		player.setTotalScore(player.getTotalScore()+score);
		pRepo.save(player);
		return player;
	}
	
	public List<Players> getLeaderBoard()
	{
		return pRepo.findTop5ByOrderByTotalScoreDesc();
	}
}
