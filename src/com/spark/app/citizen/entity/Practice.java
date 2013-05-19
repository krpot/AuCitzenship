package com.spark.app.citizen.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author sunghun
 *
 */
@DatabaseTable(tableName="practices")
public class Practice{

	@DatabaseField(generatedId=true) public int id = -1;
	@DatabaseField public String dateTime;
	@DatabaseField public int total;    //Total question number
	@DatabaseField public int picked;	//Number of picked question
	@DatabaseField public int correct;	//Number of right answer
	
	public Practice(){}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Practice [id=" + id + ", dateTime=" + dateTime + ", total="
				+ total + ", picked=" + picked + ", correct=" + correct + "]";
	}

}
