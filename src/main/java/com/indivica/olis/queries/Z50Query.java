package com.indivica.olis.queries;

import com.indivica.olis.parameters.PID51;
import com.indivica.olis.parameters.PID52;
import com.indivica.olis.parameters.PID7;
import com.indivica.olis.parameters.PID8;

/**
 * Z50 - Identify Patient by Name, Sex, and Date of Birth
 * @author jen
 *
 */
public class Z50Query implements Query {

	private PID52 firstName = new PID52(); // mandatory
	private PID51 lastName = new PID51(); // mandatory
	private PID8 sex = new PID8(); // mandatory
	private PID7 dateOfBirth = new PID7(); // mandatory
	
	@Override
	public String getQueryHL7String() {
		String query = "";
		
		if (firstName != null)
			query += firstName.toOlisString() + "~";
		
		if (lastName != null)
			query += lastName.toOlisString() + "~";
		
		if (sex != null)
			query += sex.toOlisString() + "~";
		
		if (dateOfBirth != null)
			query += dateOfBirth.toOlisString() + "~";
		
		return query;
	}

	public void setFirstName(PID52 firstName) {
    	this.firstName = firstName;
    }

	public void setLastName(PID51 lastName) {
    	this.lastName = lastName;
    }

	public void setSex(PID8 sex) {
    	this.sex = sex;
    }

	public void setDateOfBirth(PID7 dateOfBirth) {
    	this.dateOfBirth = dateOfBirth;
    }

	@Override
	public QueryType getQueryType() {
		return QueryType.Z50;
	}

}