/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.common.dao.ContactTypeDao;
import org.oscarehr.common.dao.ProgramContactTypeDao;
import org.oscarehr.common.model.ContactType;
import org.oscarehr.common.model.ProgramContactType;
import org.oscarehr.common.model.ProgramContactTypePK;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oscar.log.LogAction;

@Service
public class ContactManager {
	
	@Autowired
	ContactTypeDao contactTypeDao;
	
	@Autowired
	ProgramContactTypeDao programContactTypeDao;
	
	
	@Autowired
	SecurityInfoManager securityInfoManager;
	
	
	public List<ContactType> getContactTypes(LoggedInInfo loggedInInfo) {
		List<ContactType> results = contactTypeDao.findAll(0, AbstractDao.MAX_LIST_RETURN_SIZE);
		
		 //--- log action ---
		LogAction.addLogSynchronous(loggedInInfo,"ContactManager.getContactTypes", "");
		
		return results;
	}
	
	public void archiveContactType(LoggedInInfo loggedInInfo, Integer id) {
		
		if( ! securityInfoManager.hasPrivilege(loggedInInfo, "_demographic", SecurityInfoManager.WRITE, null ) ) {	
			LogAction.addLog(loggedInInfo, "ContactManager.archiveContactType", null, null, null, "User missing _demographic role with write access");
			return;
        }
		
		ContactType ct = contactTypeDao.find(id);
		if(ct != null) {
			LogAction.addLogSynchronous(loggedInInfo,"ContactManager.archiveContactType", "id="+id);
			
			ct.setActive(false);
			contactTypeDao.merge(ct);
		} else {
			throw new RuntimeException("ContactType does not exist");
		}
		
	}
	
	public void restoreContactType(LoggedInInfo loggedInInfo, Integer id) {
		
		if( ! securityInfoManager.hasPrivilege(loggedInInfo, "_demographic", SecurityInfoManager.WRITE, null ) ) {	
			LogAction.addLog(loggedInInfo, "ContactManager.restoreContactType", null, null, null, "User missing _demographic role with write access");
			return;
        }
		
		ContactType ct = contactTypeDao.find(id);
		if(ct != null) {
			LogAction.addLogSynchronous(loggedInInfo,"ContactManager.restoreContactType", "id="+id);
			
			ct.setActive(true);
			contactTypeDao.merge(ct);
		} else {
			throw new RuntimeException("ContactType does not exist");
		}
		
	}
	
	public void saveContactType(LoggedInInfo loggedInInfo, ContactType contactType) {
		
		if( ! securityInfoManager.hasPrivilege(loggedInInfo, "_demographic", SecurityInfoManager.WRITE, null ) ) {	
			LogAction.addLog(loggedInInfo, "ContactManager.saveContactType", null, null, null, "User missing _demographic role with write access");
			return;
        }
		
		if(contactType.getId() == null || contactType.equals("0")) {
			contactTypeDao.persist(contactType);
		} else {
			contactTypeDao.merge(contactType);
		}
		
		
		LogAction.addLogSynchronous(loggedInInfo,"ContactManager.saveContactType", "id="+contactType.getId());
		
	}
	
	public Map<String,List<ProgramContactType>> getProgramContactTypes(LoggedInInfo loggedInInfo) {
		Map<String,List<ProgramContactType>> results = new HashMap<String,List<ProgramContactType>> ();
		
		 
		if( ! securityInfoManager.hasPrivilege(loggedInInfo, "_admin", SecurityInfoManager.READ, null ) ) {	
			LogAction.addLog(loggedInInfo, "ContactManager.getProgramContactTypes", null, null, null, "User missing _admin role with read access");
			return null;
        }
		
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		
		
		List<Program> programs = programManager.getAllPrograms(loggedInInfo);
		for(Program program:programs) {
			List<ProgramContactType> types = getContactTypesForProgram(loggedInInfo,program.getId());
			results.put(String.valueOf(program.getId()), types);
		}
		
		return results;
	}
	
	
	public List<ProgramContactType> getContactTypesForProgram(LoggedInInfo loggedInInfo, Integer programId) {
		if( ! securityInfoManager.hasPrivilege(loggedInInfo, "_admin", SecurityInfoManager.WRITE, null ) ) {	
			LogAction.addLog(loggedInInfo, "ContactManager.getContactTypesForProgram", null, null, null, "User missing _admin role with write access");
			return null;
        }
		
		List<ProgramContactType> results2 = new ArrayList<ProgramContactType>();
		
		List<ProgramContactType> results = programContactTypeDao.findByProgram(programId);
		
		for(ProgramContactType pct:results) {
			ContactType ct = contactTypeDao.find(pct.getId().getContactTypeId());
			if(ct != null) {
				if(ct.isActive()) {
					pct.setContactType(ct);
					results2.add(pct);
				}
			}
		}
		LogAction.addLogSynchronous(loggedInInfo,"ContactManager.getContactTypesForProgram", "programId="+programId);
		
		
		return results2;
	}
	
	public void removeProgramContactTypes(LoggedInInfo loggedInInfo, Integer programId) {
		if( ! securityInfoManager.hasPrivilege(loggedInInfo, "_admin", SecurityInfoManager.WRITE, null ) ) {	
			LogAction.addLog(loggedInInfo, "ContactManager.removeProgramContactTypes", null, null, null, "User missing _admin role with write access");
			return;
        }
		
		List<ProgramContactType> pctList = programContactTypeDao.findByProgram(programId);
		
		for(ProgramContactType pct: pctList) {
			programContactTypeDao.remove(pct.getId());
		}
		
		LogAction.addLogSynchronous(loggedInInfo,"ContactManager.removeProgramContactTypes", "programId="+programId);
		
	}
	
	public void addProgramContactType(LoggedInInfo loggedInInfo, Integer programId, Integer contactTypeId, String category) {
		if( ! securityInfoManager.hasPrivilege(loggedInInfo, "_admin", SecurityInfoManager.WRITE, null ) ) {	
			LogAction.addLog(loggedInInfo, "ContactManager.addProgramContactType", null, null, null, "User missing _admin role with write access");
			return;
        }
		
		ProgramContactType pct = new ProgramContactType();
		ProgramContactTypePK pk = new ProgramContactTypePK();
		pk.setProgramId(programId);
		pk.setContactTypeId(contactTypeId);
		pct.setId(pk);
		pct.getId().setCategory(category);
		
		programContactTypeDao.persist(pct);
		
		LogAction.addLogSynchronous(loggedInInfo,"ContactManager.addProgramContactType", "programId="+programId + ",contactTypeId="+contactTypeId);
		
	}
	
}
