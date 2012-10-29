package com.example.whowants.model;

public class Result {
	private int score;
	private String name;
	
	public Result(int score, String name) {
		this.score = score;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
}
