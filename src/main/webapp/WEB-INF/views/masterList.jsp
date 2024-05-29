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
    <form action="/mList">
        <input type="text" name="search" value="${search.getSearch()}">
        <input type="submit" value="검색">
    </form>
    <p>총 검색 건수 : ${total}</p>


    <form action="/changeStatus" method="post">
    <table border="1" >
        <tr>
            <th> 그룹명<br>(TR_KEY) </th>
            <th> 현재 수신 건수 / 총 건수
                <br>
                (SEND_CNT) / (TOTAL_SEND_CNT)
            </th>
            <th> 현재 상태<br>(STATUS) </th>
            <th> 마지막 건수 수신 시각<br>(RECEIVED_TIME) </th>
            <th> 상태 변화</th>
        </tr>
        <% for(Master master : list){ %>
        <tr>
            <td><%= master.getMASTER_KEY() %></td>
            <td><%= master.getSEND_CNT() %> / <%= master.getTOTAL_SEND_CNT() %></td>
            <td><%= master.getSTATUS() %></td>
            <td><%= master.getRECEIVED_TIME() %></td>
            <td>
              <select name="${master.getMASTER_KEY()}" onchange="updateStatus('<%= master.getMASTER_KEY() %>', this.value)">
                <option value="1">1(수신중)</option>
                <option value="2">2(수신완료)</option>
                <option value="3">3(출력중)</option>
              </select>
            </td>
        </tr>
        <% } %>
        <tr>
            <td colspan="4"><input type="hidden" id="statusData" name="statusData"></td>
            <td><input type="submit" value="상태 저장" onclick="prepareData()"></td>
        </tr>
    </table>
    </form>

<hr>

    <table>
        <tr>
        <a href="/mList?page=1&search=${p.getSearch()}"> <<제일 앞으로<< </a>
        .
        <a href="/mList?page=${p.getPrev_()}&search=${p.getSearch()}"> <이전< </a>
        .
        <c:forEach var="i" begin="${p.getStartPage()}" end="${p.getStartPage()+(p.getEndPage()-p.getStartPage())}">
                    <a href="/mList?page=${i}&search=${p.getSearch()}">${i}</a>
                    .
        </c:forEach>
        <a href="/mList?page=${p.getNext_()}&search=${p.getSearch()}"> >다음> </a>
        .
        <a href="/mList?page=${p.getTotalPage()}&search=${p.getSearch()}"> >>제일 뒤로>> </a>
        </tr>
    </table>

<hr>
start<%= p.getStartPage() %>
end<%= p.getEndPage() %>
now<%= p.getPage() %>

<script>

    const statusMap = new Map();

    function updateStatus(key, value) {
        statusMap.set(key, value);
        console.log(`Updated ${key} to ${value}`);
        console.log(statusMap);
    }

    function prepareData() {
            const statusData = Object.fromEntries(statusMap);
            document.getElementById('statusData').value = JSON.stringify(statusData);
            console.log(statusData);
        }
</script>


</body>
</html>