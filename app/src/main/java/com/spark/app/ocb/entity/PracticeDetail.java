package com.spark.app.ocb.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author sunghun
 *
 */
//@DatabaseTable(tableName="practicedetail")
public class PracticeDetail{

	@DatabaseField(id=true) private String id;
	@DatabaseField public int practiceId;
	@DatabaseField public int questionId;
	@DatabaseField public String aId;	//Answer id of a
	@DatabaseField public String bId;   //Answer id of b
	@DatabaseField public String cId;   //Answer id of c
	@DatabaseField public String yourAnswerId;  //Answer user picked
	
	public PracticeDetail(){}
	
	public PracticeDetail(String id, int practiceId, int questionId,
			String aId, String bId, String cId, String yourAnswerId) {
		super();
		this.id = id;
		this.practiceId = practiceId;
		this.questionId = questionId;
		this.aId = aId;
		this.bId = bId;
		this.cId = cId;
		this.yourAnswerId = yourAnswerId;
	}

	public String getId(){
		return "" + practiceId + "-" + questionId;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PracticeDetail [id=" + id + ", practiceId=" + practiceId
				+ ", questionId=" + questionId + ", aId=" + aId + ", bId="
				+ bId + ", cId=" + cId + ", yourAnswerId=" + yourAnswerId + "]";
	}

}
