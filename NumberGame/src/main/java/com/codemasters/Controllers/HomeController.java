package com.codemasters.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemasters.Constants.Constant;
import com.codemasters.Model.Players;
import com.codemasters.Service.PlayerService;

@Controller
public class HomeController {
	
	int attempts;
	int generatedNumber;
	int buffer;
	int score;
	String userName;
	@Autowired
	PlayerService pService;
	
	@RequestMapping("/")
	public String home() {
		return "Index";
	}
	
	@RequestMapping("/register")
	public String getPlayer() {
		return "Signup";
	}
	
	@RequestMapping("/save")
	public String saveUser(@RequestParam String userName,@RequestParam String password)
	{
		pService.RegisterUser(userName, password);
		return "Index";
	}
	@RequestMapping("/login")
	public String login()
	{
		return "login";
	}
	
	@RequestMapping("/authenticate")
	public String authenticate(@RequestParam String userName,@RequestParam String password) {
		boolean evaluate = pService.login(userName,password);
		if(evaluate) {
			this.userName = userName;
			return "Range";
		}
		else {
			return "AccessDenied";
		}
	}
	
	@RequestMapping("/restart")
	public String restart()
	{
		return "Range";
	}
	
	@RequestMapping("/retry")
	public String retry()
	{
		return"EnterNumber";
	}
	
	@RequestMapping("/pretasks")
	public String OnClickStart(@RequestParam String maximum,@RequestParam String minimum) {
		Constant.setMax(Integer.parseInt(maximum));
		Constant.setMin(Integer.parseInt(minimum));
		if((Constant.max == Constant.min || Constant.min > Constant.max))
		{
			return "Error1";
		}
		else
		{
			generatedNumber = pService.generateNumber();
			buffer=pService.calculateBuffer(Constant.max,Constant.min);
			System.out.println("generated  "+generatedNumber);
			attempts=3;
			return "EnterNumber";

		}
	}
	
	@RequestMapping("/startGame")
	public String startGame(@RequestParam int userGuess,Model model) {
		attempts--;
		Map<String, String> checkUserGuess = pService.checkUserGuess(userGuess, generatedNumber,buffer);
		System.out.println("..."+generatedNumber);
		if(checkUserGuess.keySet().toArray()[0].equals("outOfRange") && attempts>0) {
			model.addAttribute("message",checkUserGuess.get("outOfRange"));
			model.addAttribute("attempts", attempts);
			return "OutOfRange";
			
		}
		else if(checkUserGuess.keySet().toArray()[0].equals("lessThanGenerated")&& attempts >0){
		
			model.addAttribute("message",checkUserGuess.get("lessThanGenerated"));
			model.addAttribute("attempts", attempts);
			return "Retry";
		}
		else if(checkUserGuess.keySet().toArray()[0].equals("greaterThanGenerated")&& attempts >0)
		{
		
			model.addAttribute("message",checkUserGuess.get("greaterThanGenerated"));
			model.addAttribute("attempts", attempts);
			return "Retry";
		}
		else if(checkUserGuess.keySet().toArray()[0].equals("correctGuess")){
			model.addAttribute("message",checkUserGuess.get("true"));
			int score=pService.calculateScore(attempts);
			model.addAttribute("score", score);
			Players players = pService.calculateTotalScore(userName, score);
			model.addAttribute("totalScore",players.getTotalScore());
			model.addAttribute("magicNumber", generatedNumber);
			return "userWon";
			
		}
		else {
			model.addAttribute("message", "Maximum attempts Reached");
			model.addAttribute("score", 0);
			Players players = pService.calculateTotalScore(userName, 0);
			model.addAttribute("totalScore",players.getTotalScore());
			model.addAttribute("magicNumber", generatedNumber);
			return "UserLost";
		}
		
	}
	
	@RequestMapping("/leaderBoard")
	public String leaderBoard(Model model)
	{
		List<Players> leaderBoard = pService.getLeaderBoard();
		model.addAttribute("players", leaderBoard);
		return "leaderBoard";
	}

}
