package com.xebia.hr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.xebia.hr.constants.AppConstants;

/**
 * Created by jasleen on 12/05/17.
 */
@Entity
@Table(name = "ATTEMPT_ARCHIVE")
public class AttemptArchive extends AbstractPersistable<Long> implements Serializable, Comparable<AttemptArchive> {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private long id;

	@Id
	@GeneratedValue
	@Column(name = "TRY_ID")
	private long tryId;

	@Column(columnDefinition = "Integer(3)", name = "SCORE")
	private int score;

	@Column(columnDefinition = "Integer(3)", name = "MAX_SCORE")
	private int maxScore;

	@Column(name = "SCORE_PERCENT")
	private double scoreInPercent;

	/**
	 * Courses's result states
	 * 
	 * @see AppConstants.PASSED etc
	 */
	@Column(columnDefinition = "Varchar(20)", name = "RESULT")
	private String result;

	@Column(name = "START_TIME", nullable = false)
	private Timestamp startTime;

	@Column(name = "FINISH_TIME")
	private Timestamp finishTime;

	@Column(name = "POLICY_AGREED", columnDefinition = "tinyint(1) default 0")
	private boolean policyAgreed;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTryId() {
		return tryId;
	}

	public void setTryId(Long tryId) {
		this.tryId = tryId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(final int score) {
		this.score = score;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(final int maxScore) {
		this.maxScore = maxScore;
	}

	public String getResult() {
		return result;
	}

	public void setResult(final String result) {
		this.result = result;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}

	public double getScoreInPercent() {
		return scoreInPercent;
	}

	public void setScoreInPercent(double scoreInPercent) {
		this.scoreInPercent = scoreInPercent;
	}

	public boolean isPolicyAgreed() {
		return policyAgreed;
	}

	public void setPolicyAgreed(boolean policyAgreed) {
		this.policyAgreed = policyAgreed;
	}

	@Override
	public int compareTo(AttemptArchive o) {
		return this.startTime.compareTo(o.startTime);
	}
}