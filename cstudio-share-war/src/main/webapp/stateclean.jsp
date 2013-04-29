<%@ page import="java.util.List" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.ArrayList" %>
<%
    org.springframework.web.context.WebApplicationContext ac = org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
    java.util.Map map = ac.getBeansOfType(org.craftercms.cstudio.alfresco.wcm.service.impl.StateCleaner.class);
    org.craftercms.cstudio.alfresco.wcm.service.impl.StateCleaner cleaner = (org.craftercms.cstudio.alfresco.wcm.service.impl.StateCleaner)map.get("cstudioStateCleaner");
    java.util.List<String> submitted = new java.util.ArrayList<String>();
    java.util.List<String> inProgress = new java.util.ArrayList<String>();
    java.util.List<String> scheduled = new java.util.ArrayList<String>();
    if ("true".equals(request.getParameter("clean"))) {
        submitted = cleaner.clean("Submitted", "cstudio", "cstudio--cstudioadmin");
        inProgress = cleaner.clean("In Progress", "cstudio", "cstudio--cstudioadmin");
        scheduled = cleaner.clean("Scheduled", "cstudio", "cstudio--cstudioadmin");
    } else {
        submitted = cleaner.search("Submitted", "cstudio");
        inProgress = cleaner.search("In Progress", "cstudio");
        scheduled = cleaner.search("Scheduled", "cstudio");
    }
    out.print("IN PROGRESS<br/>");
    out.print("---------------------------<br/>");
    print(out, inProgress);
    out.print("SUBMITTED<br/>");
    out.print("---------------------------<br/>");
    print(out, submitted);
    out.print("SCHEDULED<br/>");
    out.print("---------------------------<br/>");
    print(out, scheduled);
    
%><%!
    private void print(JspWriter out, java.util.List<String> inProgress) throws java.io.IOException {
        for (String inProgres : inProgress) {
            out.println(inProgres);
            out.println("<br/>");
        }
    }
%>
