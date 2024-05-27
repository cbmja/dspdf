<%@page import="com.daishin.pdf.dto.Master"%>
<%@page import="com.daishin.pdf.page.Page"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <style>
    table{
    	margin-left : auto;
    	margin-right : auto;
    	margin-top : 50px;
    	margin-bottom : 50px;
        width : 80%;
    }

    body{
            text-align: center;
        }
    </style>

</head>
<body>
    <%
        List<Master> list = (List<Master>)request.getAttribute("masterList");
        Page p = (Page)(request.getAttribute("p"));
    %>


<hr>

    <table border="1" >
        <tr>
            <th> 그룹명(TR_KEY) </th>
            <th> 총 건수(TOTAL_SEND_CNT) </th>
            <th> 현재 수신 건수(SEND_CNT) </th>
            <th> 상태(STATUS) </th>
            <th> 마지막 건수 수신 시각(RECEIVED_TIME) </th>
        </tr>
        <% for(Master master : list){ %>
        <tr>
            <td><%= master.getMASTER_KEY() %></td>
            <td><%= master.getTOTAL_SEND_CNT() %></td>
            <td><%= master.getSEND_CNT() %></td>
            <td><%= master.getSTATUS() %></td>
            <td><%= master.getRECEIVED_TIME() %></td>
        </tr>
        <% } %>


    </table>

<hr>

    <table>
        <tr>
        <a href="/mList?page=1"> <<제일 앞으로<< </a>
        .
        <a href="/mList?page=${p.getPrev_()}"> <이전< </a>
        .
        <c:forEach var="i" begin="${p.getStartPage()}" end="${p.getStartPage()+(p.getEndPage()-p.getStartPage())}">
                    <a href="/mList?page=${i}">${i}</a>
                    .
        </c:forEach>
        <a href="/mList?page=${p.getNext_()}"> >다음> </a>
        .
        <a href="/mList?page=${p.getTotalPage()}"> >>제일 뒤로>> </a>
        </tr>
    </table>

<hr>
start<%= p.getStartPage() %>
end<%= p.getEndPage() %>
now<%= p.getPage() %>


</body>
</html>