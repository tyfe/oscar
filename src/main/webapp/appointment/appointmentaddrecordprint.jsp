<%@ page import="java.sql.*, java.util.*, oscar.MyDateFormat, org.oscarehr.common.OtherIdManager,oscar.oscarDemographic.data.*, org.oscarehr.common.dao.BillingDao, java.text.SimpleDateFormat, org.caisi.model.Tickler,org.caisi.service.TicklerManager,org.oscarehr.util.SpringUtils"
	errorPage="errorpage.jsp"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ include file="/common/webAppContextAndSuperMgr.jsp"%>
<!--  
/*
 * 
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved. *
 * This software is published under the GPL GNU General Public License. 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version. * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details. * * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. * 
 * 
 * <OSCAR TEAM>
 * 
 * This software was written for the 
 * Department of Family Medicine 
 * McMaster University 
 * Hamilton 
 * Ontario, Canada 
 */
-->
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
</head>
<body background="../images/gray_bg.jpg"
	bgproperties="fixed">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="90%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
		<bean:message key="appointment.addappointment.msgMainLabel" /></font></th>
	</tr>
</table>
<%
	String[] param = new String[18];
	param[0]=request.getParameter("provider_no");
	param[1]=request.getParameter("appointment_date");
	param[2]=MyDateFormat.getTimeXX_XX_XX(request.getParameter("start_time"));
	param[3]=MyDateFormat.getTimeXX_XX_XX(request.getParameter("end_time"));
	param[4]=request.getParameter("keyword");
	param[5]=request.getParameter("notes");
	param[6]=request.getParameter("reason");
	param[7]=request.getParameter("location");
	param[8]=request.getParameter("resources");
	param[9]=request.getParameter("type");
	param[10]=request.getParameter("style");
	param[11]=request.getParameter("billing");
	param[12]=request.getParameter("status");
	param[13]=request.getParameter("createdatetime");
	param[14]=request.getParameter("creator");
	param[15]=request.getParameter("remarks");
	param[17]=(String)request.getSession().getAttribute("programId_oscarView");
	DemographicData.Demographic demo = null;
    if (request.getParameter("demographic_no") != null && !(request.getParameter("demographic_no").equals(""))) {
        param[16] = request.getParameter("demographic_no");
        DemographicData demData = new DemographicData();
        demo = demData.getDemographic(param[16]);
    } else {
        param[16] = "0";
    }

    int rowsAffected = oscarSuperManager.update("appointmentDao", request.getParameter("dboperation"), param);
	if (rowsAffected == 1) {
            if(demo != null && request.getParameter("dboperation").equals("add_apptrecord")) {
			BillingDao billingDao = (BillingDao) SpringUtils.getBean("billingDao");
			java.util.Date appointmentDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(param[1]);
			List<Integer> unpaidNumbers = billingDao.listUnpaidInvoices(Integer.parseInt(demo.getDemographicNo()), appointmentDate);
			if(unpaidNumbers == null || unpaidNumbers.size()>0) {
				Tickler tickler = new Tickler();
				tickler.setStatus('A');
				tickler.setCreator((String) request.getSession().getAttribute("user"));
				tickler.setDemographic_no(demo.getDemographicNo());
				tickler.setPriority("Normal");
				tickler.setService_date(appointmentDate);
				tickler.setTask_assigned_to(param[0]);
				tickler.setUpdate_date(new java.util.Date());
				String message = null;
				if(unpaidNumbers.size() == 1) message = "Patient "+demo.getChartNo()+" "+demo.getFirstName()+" "+demo.getLastName()+" needs to pay invoice #";
				else message = "Patient "+demo.getChartNo()+" "+demo.getFirstName()+" "+demo.getLastName()+" needs to pay invoices ##";
				for (Integer num : unpaidNumbers) {
					message += num.toString()+" ";
				}
				tickler.setMessage(message);

		        org.caisi.service.TicklerManager tcm = (org.caisi.service.TicklerManager) WebApplicationContextUtils.getWebApplicationContext(
			       		 pageContext.getServletContext()).getBean("ticklerManagerT");
		        tcm.addTickler(tickler);
		    }
		}
%>
<p>
<h1><bean:message key="appointment.addappointment.msgAddSuccess" /></h1>

<script LANGUAGE="JavaScript">
	self.opener.refresh();
	popupPage(350,750,'../report/reportdaysheet.jsp?dsmode=new&provider_no=<%=param[0]%>&sdate=<%=param[1]%>') ;
	self.close();
</script>
<%
		String[] param2 = new String[7];
		param2[0]=param[0]; //provider_no
		param2[1]=param[1]; //appointment_date
		param2[2]=param[2]; //start_time
		param2[3]=param[3]; //end_time
		param2[4]=param[13]; //createdatetime
		param2[5]=param[14]; //creator
		param2[6]=param[16]; //demographic_no

		List<Map> resultList = oscarSuperManager.find("appointmentDao", "search_appt_no", param2);
		if (resultList.size()>0) {
			Integer apptNo = (Integer)resultList.get(0).get("appointment_no");
			String mcNumber = request.getParameter("appt_mc_number");
			OtherIdManager.saveIdAppointment(apptNo, "appt_mc_number", mcNumber);
		}
	} else {
%>
<p>
<h1><bean:message key="appointment.addappointment.msgAddFailure" /></h1>

<%  
	}
%>
<p></p>
<hr width="90%"/>
<form>
<input type="button" value="<bean:message key="global.btnClose"/>" onClick="window.close();">
</form>
</center>
</body>
</html:html>