<%@page import="com.daishin.pdf.dto.Master"%>
<%@page import="com.daishin.pdf.page.Page"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix ="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <form action="/mList" method="get">
        <input type="hidden" name="sort" value="${p.getSort()}">
        <input type="hidden" name="sortCate" value="${p.getSortCate()}">
        <select name="cate">
            <%
                if ( p.getCate().equals("MASTER_KEY") ){
            %>
                <option value="MASTER_KEY" selected >MASTER_KEY</option>
                <option value="STATUS">STATUS(1,2,3)</option>
                <option value="TYPE">배치(AR) / 실시간(RE)</option>
            <%
                } else if( p.getCate().equals("STATUS") ) {
            %>
                <option value="MASTER_KEY">MASTER_KEY</option>
                <option value="STATUS" selected >STATUS(1,2,3)</option>
                <option value="TYPE">배치(AR) / 실시간(RE)</option>
            <%
            } else {
            %>
                <option value="MASTER_KEY">MASTER_KEY</option>
                <option value="STATUS">STATUS(1,2,3)</option>
                <option value="TYPE" selected >배치(AR) / 실시간(RE)</option>
            <% } %>
        </select>
        <input type="text" name="search" value="${search.getSearch()}">
        <input type="submit" value="검색">
    </form>
    <p>총 검색 건수 : ${p.getTotal()}</p>


    <form action="/changeStatus" method="post">
    <table border="1" >
        <tr>
            <th> 그룹명<br>(TR_KEY) </th>
            <th> 현재 수신 건수 / 총 건수
                <br>
                (SEND_CNT) / (TOTAL_SEND_CNT)
            </th>
            <th> 마지막 건수 수신 시각<br>(RECEIVED_TIME) </th>
            <th> 현재 상태 시작 시각</th>
            <th> 현재 상태<br>(STATUS) </th>
            <th> 상태 변화</th>
        </tr>
        <% for(Master master : list){ %>
        <tr>
            <td><%= master.getMASTER_KEY() %></td>
            <td><%= master.getSEND_CNT() %> / <%= master.getTOTAL_SEND_CNT() %></td>
            <td><%= master.getRECEIVED_TIME() %></td>
            <td><%= master.getSTATUS_TIME() %></td>
            <td><%= master.getSTATUS() %></td>
            <td>
              <select name="${master.getMASTER_KEY()}" onchange="updateStatus('<%= master.getMASTER_KEY() %>', this.value)">
                <option value="0" disabled selected>선택</option>
                <option value="1">1(수신중)</option>
                <option value="2">2(수신완료)</option>
                <option value="3">3(출력중)</option>
              </select>
            </td>
        </tr>
        <% } %>
        <tr>
            <td colspan="5">
                <input type="hidden" id="statusData" name="statusData">
                <input type="hidden" name="search" value="${p.getSearch()}">
                <input type="hidden" name="page" value="${p.getPage()}">
                <input type="hidden" name="cate" value="${p.getCate()}">
                <input type="hidden" name="sort" value="${p.getSort()}">
                <input type="hidden" name="sortCate" value="${p.getSortCate()}">
            </td>
            <td><input type="submit" value="상태 저장" onclick="prepareData()"></td>
        </tr>
    </table>
    </form>

    <form action="/mList" method="get">
        <input type="hidden" name="search" value="${p.getSearch()}">
        <input type="hidden" name="page" value="${p.getPage()}">
        <input type="hidden" name="cate" value="${p.getCate()}">

        <select name="sortCate">
        <% if( p.getSortCate().equals("MASTER_KEY") ){ %>
            <option value="MASTER_KEY" selected>MASTER_KEY</option>
            <option value="STATUS">STATUS</option>
            <option value="RECEIVED_TIME">RECEIVED_TIME</option>
            <option value="STATUS_TIME">STATUS_TIME</option>
        <% } else if( p.getSortCate().equals("STATUS") ){ %>
        <option value="MASTER_KEY" selected>MASTER_KEY</option>
            <option value="MASTER_KEY" >MASTER_KEY</option>
            <option value="STATUS" selected>STATUS</option>
            <option value="RECEIVED_TIME">RECEIVED_TIME</option>
            <option value="STATUS_TIME">STATUS_TIME</option>
        <% } else if( p.getSortCate().equals("RECEIVED_TIME") ) { %>
            <option value="MASTER_KEY" >MASTER_KEY</option>
            <option value="STATUS" >STATUS</option>
            <option value="RECEIVED_TIME" selected>RECEIVED_TIME</option>
            <option value="STATUS_TIME">STATUS_TIME</option>
        <% } else { %>
            <option value="MASTER_KEY" >MASTER_KEY</option>
            <option value="STATUS" >STATUS</option>
            <option value="RECEIVED_TIME" >RECEIVED_TIME</option>
            <option value="STATUS_TIME" selected>STATUS_TIME</option>
        <% } %>
        </select>

        <select name="sort">
        <%
        if ( p.getSort().equals("ASC") ){
        %>
        <option value="ASC" selected>오름 차순</option>
        <option value="DESC">내림 차순</option>
        <%
        } else {
        %>
        <option value="ASC">오름 차순</option>
        <option value="DESC" selected>내림 차순</option>
        <% } %>
        </select>
        <input type="submit" value="정렬">
    </form>
<hr>

    <table>
        <tr>
        <a href="/mList?page=1&search=${p.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}"> <<제일 앞으로<< </a>
        .
        <a href="/mList?page=${p.getPage()-1}&search=${p.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}"> <이전 페이지< </a>
        .
        <c:forEach var="i" begin="${p.getStartPage()}" end="${p.getStartPage()+(p.getEndPage()-p.getStartPage())}">
                    <a href="/mList?page=${i}&search=${p.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}">${i}</a>
                    .
        </c:forEach>
        <a href="/mList?page=${p.getPage()+1}&search=${p.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}"> >다음 페이지> </a>
        .
        <a href="/mList?page=${p.getTotalPage()}&search=${p.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}"> >>제일 뒤로>> </a>
        </tr>
    </table>

<hr>
start : <c:out value="${p.getStartPage()}"/> / now : <c:out value="${p.getPage()}"/> / end : <c:out value="${p.getEndPage()}"/>

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
        }
</script>


</body>
</html>