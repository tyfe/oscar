<!-- 
/*
* 
* Copyright (c) 2001-2002. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved. *
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
* This software was written for 
* Centre for Research on Inner City Health, St. Michael's Hospital, 
* Toronto, Ontario, Canada 
*/
 -->

<%@ include file="/survey/taglibs.jsp"%>

<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />


<html:form action="/SurveyManager" method="POST"
	enctype="multipart/form-data" styleId="surveyForm">
	<input type="hidden" name="method" value="import_survey" />
	<table width=100%">
		<tr>
			<td class="leftfield">File Name:&nbsp;&nbsp; <html:file
				property="web.importFile" /></td>
		</tr>
		<tr>
			<td><html:submit value="Import" /> <input type="button"
				value="Cancel"
				onclick="location.href='<html:rewrite action="/SurveyManager"/>'" />
			</td>
		</tr>
	</table>

</html:form>