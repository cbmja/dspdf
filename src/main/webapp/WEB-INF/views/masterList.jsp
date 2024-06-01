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
        List<String> cateList = (List<String>)request.getAttribute("cateList");
        List<String> statusList = (List<String>)request.getAttribute("statusList");
        List<String> sortCateList = (List<String>)request.getAttribute("sortCateList");
        List<String> sortList = (List<String>)request.getAttribute("sortList");
        Page p = (Page)(request.getAttribute("p"));
    %>


<hr>
    <form action="/mList" method="get">
        <input type="hidden" name="sort" value="${p.getSort()}">
        <input type="hidden" name="sortCate" value="${p.getSortCate()}">
        <select name="cate">
            <c:forEach var="cate" items="${cateList}">
                <option value="${cate}" <c:if test="${p.getCate() eq cate}">selected</c:if> >${cate}</option>
            </c:forEach>
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
            <th> TYPE(배치/실시간)</th>
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
                <c:forEach var="status" items="${statusList}">
                    <option value="${status}">${status}</option>
                </c:forEach>
              </select>
            </td>
            <td><%= master.getTYPE() %></td>
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
        <c:forEach var="sortCate" items="${sortCateList}">
            <option value="${sortCate}" <c:if test="${p.getSortCate() eq sortCate}"> selected </c:if> >${sortCate}</option>
        </c:forEach>
        </select>

        <select name="sort">
        <c:forEach var="sort" items="${sortList}">
            <option value="${sort}" <c:if test="${p.getSort() eq sort}"> selected </c:if> >${sort}</option>
        </c:forEach>
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