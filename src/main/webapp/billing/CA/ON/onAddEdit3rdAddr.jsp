<%--

    Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

--%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin.billing,_admin" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_admin&type=_admin.billing");%>
</security:oscarSec>
<%@ page import="oscar.*" %>
<%--<jsp:useBean id="onAddEdit3rdAddrBean" scope="request" type="onAddEdit3rdAddrBean" />--%>
<%@page errorPage = "../../../appointment/errorpage.jsp" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%
    onAddEdit3rdAddrBean b = new onAddEdit3rdAddrBean();
    b.init(request, response, out, (String) session.getAttribute("userrole"),
                              (String) session.getAttribute("user"), authed, (String) request.getParameter("id"));
    b.writeHeader();
%>

<script type="text/javascript"
        src="../../../share/calendar/lang/<bean:message key="global.javascript.calendar"/>">
</script>
<% b.write(); %>
<tr>
    <td align="center" class="myGreen" colspan="2"><input
        type="hidden" name="action" value='<%=b.getAction()%>'> <input
        type="submit" name="submit"
        value="<bean:message key="admin.resourcebaseurl.btnSave"/>"
        onclick="javascript:return onSave();"> <input type="button"
        name="Cancel"
        value="<bean:message key="admin.resourcebaseurl.btnExit"/>"
        onClick="window.close()"> <input type="hidden" name="id"
        value="<%=b.getProp().getProperty("id", "")%>" /></td>
</tr>
<%
    b.writeFooter();
%>
