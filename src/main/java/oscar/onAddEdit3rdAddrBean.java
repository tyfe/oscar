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

package oscar;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import java.util.*;
import java.sql.*;
// import oscar.*;
import java.net.*;
// import org.apache.commons.lang.StringEscapeUtils;
import oscar.oscarBilling.ca.on.data.*;

public class onAddEdit3rdAddrBean {
    private JspWriter out;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String userRole;
    private String user;
    private boolean authed;
    private Properties prop;
    private String action;
    private String id;

    public onAddEdit3rdAddrBean() {

    }

    public void init(HttpServletRequest req, HttpServletResponse res, JspWriter o, String userRole, String user, boolean authed, String id) {
        out = o;
        request = req;
        response = res;
        this.userRole = userRole;
        this.user = user;
        this.authed = authed;
        this.id = id;
    }

    private String getOptionList(JdbcBilling3rdPartImpl dbObj) {
        String s = "";
        List sL = dbObj.get3rdAddrNameList();
        for (int i = 0; i < sL.size(); i++) {
            Properties propT = (Properties) sL.get(i);
            s += "\t\t\t<option value=\"" + propT.getProperty("company_name", "") + "\">" + propT.getProperty("company_name", "") + "</option>\n";
        }
        return s;
    }

    public Properties getProp() {
        return prop;
    }

    public String getAction() {
        return action;
    }

    public void writeHeader() throws IOException {
        out.println("<html:html locale=\"true\">\n" +
                "<head>\n" +
                "<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/global.js\"></script>\n" +
                "<title>Add/Edit Service Code</title>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"billingON.css\" />\n" +
                "<link rel=\"StyleSheet\" type=\"text/css\" href=\"../web.css\" />\n" +
                "<!-- calendar stylesheet -->\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\"\n" +
                "\thref=\"../../../share/calendar/calendar.css\" title=\"win2k-cold-1\" />\n" +
                "<!-- main calendar program -->\n" +
                "<script type=\"text/javascript\" src=\"../../../share/calendar/calendar.js\"></script>\n" +
                "<!-- language for the calendar -->\n");
    }

    public void write() throws IOException {
        if (!authed) {
            return;
        }

        if (user == null) {
            response.sendRedirect("../logout.jsp");
        }
        int serviceCodeLen = 5;
        String msg = "Type in a name and search first to see if it is available.";
        action = "search"; // add/edit
        prop = new Properties();
        JdbcBilling3rdPartImpl dbObj = new JdbcBilling3rdPartImpl();
        if (request.getParameter("submit") != null && request.getParameter("submit").equals("Save")) {
            if (request.getParameter("action").startsWith("edit")) {
                // update the service code
                String company_name = request.getParameter("company_name");
                if (company_name.equals(request.getParameter("action").substring("edit".length()))) {
                    String list = "";
                    Properties val = new Properties();
                    val.setProperty("id", id);
                    val.setProperty("attention", request.getParameter("attention"));
                    val.setProperty("company_name", request.getParameter("company_name"));
                    val.setProperty("address", request.getParameter("address"));
                    val.setProperty("city", request.getParameter("city"));
                    val.setProperty("province", request.getParameter("province"));
                    val.setProperty("postcode", request.getParameter("postcode"));
                    val.setProperty("telephone", request.getParameter("telephone"));
                    val.setProperty("fax", request.getParameter("fax"));

                    boolean ni = dbObj.update3rdAddr(id, val);
                    if (ni) {
                        msg = company_name + " is updated.<br>"
                                + "Type in a name and search first to see if it is available.";
                        action = "search";
                        prop.setProperty("company_name", company_name);
                    } else {
                        msg = company_name + " is <font color='red'>NOT</font> updated. Action failed! Try edit it again.";
                        action = "edit" + company_name;
                        prop.setProperty("company_name", company_name);
                        prop.setProperty("id", id);
                        prop.setProperty("attention", request.getParameter("attention"));
                        prop.setProperty("company_name", request.getParameter("company_name"));
                        prop.setProperty("address", request.getParameter("address"));
                        prop.setProperty("city", request.getParameter("city"));
                        prop.setProperty("province", request.getParameter("province"));
                        prop.setProperty("postcode", request.getParameter("postcode"));
                        prop.setProperty("telephone", request.getParameter("telephone"));
                        prop.setProperty("fax", request.getParameter("fax"));
                    }
                } else {
                    msg = "You can <font color='red'>NOT</font> save the name - " + company_name
                            + ". Please search the name first.";
                    action = "search";
                    prop.setProperty("company_name", company_name);
                }

            } else if (request.getParameter("action").startsWith("add")) {
                String company_name = request.getParameter("company_name");
                if (company_name.equals(request.getParameter("action").substring("add".length()))) {
                    Properties val = new Properties();
                    val.setProperty("attention", request.getParameter("attention"));
                    val.setProperty("company_name", request.getParameter("company_name"));
                    val.setProperty("address", request.getParameter("address"));
                    val.setProperty("city", request.getParameter("city"));
                    val.setProperty("province", request.getParameter("province"));
                    val.setProperty("postcode", request.getParameter("postcode"));
                    val.setProperty("telephone", request.getParameter("telephone"));
                    val.setProperty("fax", request.getParameter("fax"));
                    int ni = dbObj.addOne3rdAddrRecord(val);
                    if (ni > 0) {
                        msg = company_name + " is added.<br>"
                                + "Type in a name and search first to see if it is available.";
                        action = "search";
                        prop.setProperty("company_name", company_name);
                    } else {
                        msg = company_name + " is <font color='red'>NOT</font> added. Action failed! Try edit it again.";
                        action = "add" + company_name;
                        prop.setProperty("company_name", company_name);
                        prop.setProperty("attention", request.getParameter("attention"));
                        prop.setProperty("company_name", request.getParameter("company_name"));
                        prop.setProperty("address", request.getParameter("address"));
                        prop.setProperty("city", request.getParameter("city"));
                        prop.setProperty("province", request.getParameter("province"));
                        prop.setProperty("postcode", request.getParameter("postcode"));
                        prop.setProperty("telephone", request.getParameter("telephone"));
                        prop.setProperty("fax", request.getParameter("fax"));
                    }
                } else {
                    msg = "You can <font color='red'>NOT</font> save the name - " + company_name
                            + ". Please search the name first.";
                    action = "search";
                    prop.setProperty("company_name", company_name);
                }
            } else {
                msg = "You can <font color='red'>NOT</font> save the name. Please search the name first.";
            }
        } else if (request.getParameter("submit") != null && request.getParameter("submit").equals("Search")) {
            // check the input data
            if (request.getParameter("company_name") == null) {
                msg = "Please type in a right name.";
            } else {
                String company_name = request.getParameter("company_name");
                Properties ni = dbObj.get3rdAddrProp(company_name);
                if (!ni.getProperty("company_name", "").equals("")) {
                    prop = ni; //.setProperty("company_name", company_name);
                    int n = 0;
                    msg = "You can edit the name.";
                    action = "edit" + company_name;

                } else {
                    prop.setProperty("company_name", company_name);
                    msg = "It is a NEW name. You can add it.";
                    action = "add" + company_name;
                }
            }
        }

        out.println("<!-- the following script defines the Calendar.setup helper function, which makes\n" +
                        "       adding a calendar a matter of 1 or 2 lines of code. -->\n" +
                        "<script type=\"text/javascript\"\n" +
                        "\tsrc=\"../../../share/calendar/calendar-setup.js\"></script>\n" +
                        "<script language=\"JavaScript\">\n" +
                        "\n" +
                        "      <!--\n" +
                        "\t\tfunction setfocus() {\n" +
                        "\t\t  this.focus();\n" +
                        "\t\t  document.forms[0].company_name.focus();\n" +
                        "\t\t  document.forms[0].company_name.select();\n" +
                        "\t\t}\n" +
                        "\t    function onSearch() {\n" +
                        "\t        //document.forms[0].submit.value=\"Search\";\n" +
                        "\t        var ret = checkServiceCode();\n" +
                        "\t        return ret;\n" +
                        "\t    }\n" +
                        "\t    function onSave() {\n" +
                        "\t        //document.forms[0].submit.value=\"Save\";\n" +
                        "\t        var ret = checkServiceCode();\n" +
                        "\t        if(ret==true) {\n" +
                        "\t\t\t\tret = checkAllFields();\n" +
                        "\t\t\t}\n" +
                        "\t        if(ret==true) {\n" +
                        "\t            ret = confirm(\"Are you sure you want to save?\");\n" +
                        "\t        }\n" +
                        "\t        return ret;\n" +
                        "\t    }\n" +
                        "\t\tfunction checkServiceCode() {\n" +
                        "\t        var b = true;\n" +
                        "\t        if(document.forms[0].service_code.value.length!=5 || !isServiceCode(document.forms[0].service_code.value)){\n" +
                        "\t            b = false;\n" +
                        "\t            alert (\"You must type in a service code with 5 (upper case) letters/digits. The service code ends with \\'A\\' or \\'B\\'...\");\n" +
                        "\t        }\n" +
                        "\t        return b;\n" +
                        "\t    }\n" +
                        "    function isServiceCode(s){\n" +
                        "        // temp for 0.\n" +
                        "    \tif(s.length==0) return true;\n" +
                        "    \tif(s.length!=5) return false;\n" +
                        "        if((s.charAt(0) < \"A\") || (s.charAt(0) > \"Z\")) return false;\n" +
                        "        if((s.charAt(4) < \"A\") || (s.charAt(4) > \"Z\")) return false;\n" +
                        "\n" +
                        "        var i;\n" +
                        "        for (i = 1; i < s.length-1; i++){\n" +
                        "            // Check that current character is number.\n" +
                        "            var c = s.charAt(i);\n" +
                        "            if (((c < \"0\") || (c > \"9\"))) return false;\n" +
                        "        }\n" +
                        "        return true;\n" +
                        "    }\n" +
                        "\t\tfunction checkAllFields() {\n" +
                        "\t        var b = true;\n" +
                        "\t        for(var i=0; i<10; i++) {\n" +
                        "\t        \tvar fieldItem = eval(\"document.forms[1].serviceCode\" + i);\n" +
                        "\t        \tif(fieldItem.value.length>0) {\n" +
                        "\t\t\t        if(!isServiceCode(fieldItem.value)){\n" +
                        "\t\t\t            b = false;\n" +
                        "\t\t\t            alert (\"You must type in a Service Code in the field!\");\n" +
                        "\t\t\t        }\n" +
                        "\t        \t}\n" +
                        "\t        \tvar fieldItem1 = eval(\"document.forms[1].serviceUnit\" + i);\n" +
                        "\t        \tvar fieldItem2 = eval(\"document.forms[1].serviceAt\" + i);\n" +
                        "\t        \tif(fieldItem1.value.length>0) {\n" +
                        "\t\t\t        if(!isNumber(fieldItem1.value)){\n" +
                        "\t\t\t            b = false;\n" +
                        "\t\t\t            alert (\"You must type in a number in the field!\");\n" +
                        "\t\t\t        }\n" +
                        "\t        \t}\n" +
                        "\t        \tif(fieldItem2.value.length>0) {\n" +
                        "\t\t\t        if(!isNumber(fieldItem2.value)){\n" +
                        "\t\t\t            b = false;\n" +
                        "\t\t\t            alert (\"You must type in a number in the field!\");\n" +
                        "\t\t\t        }\n" +
                        "\t        \t}\n" +
                        "\t        }\n" +
                        "\t        var fieldItemDx = eval(\"document.forms[1].dx\");\n" +
                        "\t        if(fieldItemDx.value.length>0){\n" +
                        "\t\t\t        if(!isNumber(fieldItemDx.value) || fieldItemDx.value.length!=3){\n" +
                        "\t\t\t            b = false;\n" +
                        "\t\t\t            alert (\"You must type in a number in the right Dx field!\");\n" +
                        "\t\t\t        }\n" +
                        "\t        } \n" +
                        "\t\t\treturn b;\n" +
                        "\t    }\n" +
                        "\t    function isNumber(s){\n" +
                        "\t        var i;\n" +
                        "\t        for (i = 0; i < s.length; i++){\n" +
                        "\t            // Check that current character is number.\n" +
                        "\t            var c = s.charAt(i);\n" +
                        "\t            if (c == \".\") continue;\n" +
                        "\t            if (((c < \"0\") || (c > \"9\"))) return false;\n" +
                        "\t        }\n" +
                        "\t        // All characters are numbers.\n" +
                        "\t        return true;\n" +
                        "\t    }\n" +
                        "\t    function upCaseCtrl(ctrl) {\n" +
                        "\t\t\tctrl.value = ctrl.value.toUpperCase();\n" +
                        "\t\t}\n" +
                        "\t    \n" +
                        "//-->\n" +
                        "\n" +
                        "      </script>\n" +
                        "</head>\n" +
                        "<body bgcolor=\"ivory\" onLoad=\"setfocus()\" topmargin=\"0\" leftmargin=\"0\"\n" +
                        "\trightmargin=\"0\">\n" +
                        "<center>\n" +
                        "<table BORDER=\"1\" CELLPADDING=\"0\" CELLSPACING=\"0\" WIDTH=\"100%\">\n" +
                        "\t<tr class=\"myDarkGreen\">\n" +
                        "\t\t<th><font color=\"white\">" + msg + "</font></th>\n" +
                        "\t</tr>\n" +
                        "</table>\n" +
                        "</center>\n" +
                        "\n" +
                        "<table BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\" WIDTH=\"100%\"\n" +
                        "\tclass=\"myYellow\">\n" +
                        "\t<form method=\"post\" name=\"baseur0\" action=\"onAddEdit3rdAddr.jsp\">\n" +
                        "\t\n" +
                        "\t<tr>\n" +
                        "\t\t<td align=\"right\" width=\"50%\"><select name=\"company_name\"\n" +
                        "\t\t\tid=\"company_name\">\n" +
                        "\t\t\t<option selected=\"selected\" value=\"\">- choose one -</option>\n" +
                        getOptionList(dbObj) +
                        "\t\t</select></td>\n" +
                        "\t\t<td><input type=\"hidden\" name=\"submit\" value=\"Search\"> <input\n" +
                        "\t\t\ttype=\"submit\" name=\"action\" value=\" Edit \"></td>\n" +
                        "\t</tr>\n" +
                        "\t</form>\n" +
                        "</table>\n" +
                        "<table width=\"100%\" border=\"0\" cellspacing=\"2\" cellpadding=\"2\">\n" +
                        "\t<form method=\"post\" name=\"baseurl\" action=\"onAddEdit3rdAddr.jsp\">\n" +
                        "\t<tr class=\"myGreen\">\n" +
                        "\t\t<td align=\"right\"><b>Company Name</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"company_name\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("company_name", "") + "\" size='40'\n" +
                        "\t\t\tmaxlength='50' /> <input type=\"submit\" name=\"submit\" value=\"Search\"\n" +
                        "\t\t\tonclick=\"javascript:return onSearch();\"></td>\n" +
                        "\t</tr>\n" +
                        "\t<tr class=\"myIvory\">\n" +
                        "\t\t<td align=\"right\"><b>Attention</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"attention\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("attention", "") + "\" size='40'\n" +
                        "\t\t\tmaxlength='50' /></td>\n" +
                        "\t</tr>\n" +
                        "\t<tr class=\"myGreen\">\n" +
                        "\t\t<td align=\"right\"><b>Address</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"address\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("address", "") + "\" size='40' maxlength='50' />\n" +
                        "\t\t</td>\n" +
                        "\t</tr>\n" +
                        "\t<tr class=\"myIvory\">\n" +
                        "\t\t<td align=\"right\"><b>City</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"city\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("city", "") + "\" size='40' maxlength='50' />\n" +
                        "\t\t</td>\n" +
                        "\t</tr>\n" +
                        "\t<tr class=\"myGreen\">\n" +
                        "\t\t<td align=\"right\"><b>Province</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"province\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("province", "") + "\" size='20'\n" +
                        "\t\t\tmaxlength='20' /></td>\n" +
                        "\t</tr>\n" +
                        "\t<tr class=\"myIvory\">\n" +
                        "\t\t<td align=\"right\"><b>postcode</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"postcode\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("postcode", "") + "\" size='10'\n" +
                        "\t\t\tmaxlength='10' /></td>\n" +
                        "\t</tr>\n" +
                        "\t<tr class=\"myGreen\">\n" +
                        "\t\t<td align=\"right\"><b>Tel.</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"telephone\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("attention", "") + "\" size='40'\n" +
                        "\t\t\tmaxlength='50' /></td>\n" +
                        "\t</tr>\n" +
                        "\t<tr class=\"myIvory\">\n" +
                        "\t\t<td align=\"right\"><b>Fax</b></td>\n" +
                        "\t\t<td><input type=\"text\" name=\"fax\"\n" +
                        "\t\t\tvalue=\"" + prop.getProperty("fax", "") + "\" size='40' maxlength='50' />\n" +
                        "\t\t</td>\n" +
                        "\t</tr>");
    }

    public void writeFooter() throws IOException {
        out.println(
                "\t</form>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>");
    }
}