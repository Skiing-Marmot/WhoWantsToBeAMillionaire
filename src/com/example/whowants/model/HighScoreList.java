package com.example.whowants.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreList {

    private List<HighScore> scores;

    public HighScoreList() {
	scores = new ArrayList<HighScore>();
    }

    public List<HighScore> getScores() {
	sortScore();
	return scores;
    }

    public void setScores(List<HighScore> scores) {
	this.scores = scores;
    }

    private void sortScore() {
	Collections.sort(scores, Collections.reverseOrder());
    }
}
