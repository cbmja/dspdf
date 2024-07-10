<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>error_list</title>
 <style>


        body{
                text-align: center;
            }

            table {
              margin-left : 10%;
              margin-right : 10%;
              margin-top : 50px;
              margin-bottom : 50px;
              width : 95%;
              border-collapse: collapse;
              margin: 1rem auto;
              border: 1px solid #ddd;
              background-color: white;
            }

        header {
          padding: 7vh 5vw;
          border-bottom: 1px solid #ddd;
        }
        header h1,
        header p {
          margin: 0;
        }
        footer {
          padding: 7vh 5vw;
          border-top: 1px solid #ddd;
        }
        aside {
          padding: 7vh 5vw;
        }
        .primary {
          overflow: auto;
          scroll-snap-type: both mandatory;
          height: 80vh;
        }
        @media (min-width: 40em) {
          main {
            display: flex;
          }
          aside {
            flex: 0 1 20vw;
            order: 1;
            border-right: 1px solid #ddd;
          }
          .primary {
            order: 2;
          }
        }
        table {
          border-collapse: collapse;
          border: 0;
        }
        th,
        td {
          border: 1px solid #aaa;
          background-clip: padding-box;
          scroll-snap-align: start;

        }
        th,
        td {
          padding: 0.6rem;
          min-width: 6rem;
          text-align: center;
          margin: 0;
        }
        thead th,
        tbody th {
          background-color: #f8f8f8;
        }
        ul {
            list-style:none;
            float:left;
            display:inline;
        }
        ul li {
            float:left;
        }
        ul li a {
            float:left;
            padding:4px;
            margin-right:3px;
            width:25px;
            height:25px;
            color:#000;
            font:bold 12px tahoma;
            border:1px solid #eee;
            text-align:center;
            text-decoration:none;
        }
        ul li a:hover, ul li a:focus {
            color:#fff;
            border:1px solid #141414;
            background-color: #a3adac;
        }
        .active{
            background-color : #a3adac;
        }

        .table-row:hover{
            background-color: #a3adac;
        }

        .error-button{
            background-color: #f5e9e9;
        }

        </style>

</head>
<body>
<!-- SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS 검색 SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS -->

    <div style="margin-top: 30px; margin-bottom: 10px;">

        <c:forEach var="status" items="${statusList}">
            / [${status.getSTATUS_NAME()} : ${status.getSTATUS_CODE()}]
        </c:forEach>
    </div>

    <form action="/errors" method="get">
        <input type="hidden" name="sort" value="${p.getSort()}">
        <input type="hidden" name="sortCate" value="${p.getSortCate()}">
        <input type="hidden" name="pageElement" value="${p.getPageElement()}">
        <select name="cate">
            <c:forEach var="cate" items="${cateList}">
                <option value="${cate.key}" <c:if test="${p.getCate() eq cate.key}">selected</c:if> >${cate.value}</option>
            </c:forEach>
        </select>
        <input type="text" name="search" value="${search.getSearch()}">
        <input type="submit" value="검색">
    </form>

<!-- EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE 검색 EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->
<!-- SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS MASTER LIST SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS -->

    <table border="1" >
        <tr>
            <th> NUM </th>
            <th> 내용 </th>
            <th> 일자 </th>
            <th> 에러 코드 </th>
            <th> 그룹 </th>
        </tr>

        <c:forEach var="error" items="${errorList}">
        <tr class="table-row">
            <td>${error.getID()}</td>
            <td>    <button onclick="openNewWindow('${error.getID()}')">내용 확인</button></td>
            <td>${error.getCREATE_DATE()}</td>
            <td>${error.getERROR_CODE()}</td>
            <td>${error.getMASTER_KEY()}</td>
        </tr>
        </c:forEach>
    </table>

<!-- EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE MASTER LIST EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->
    <ul>
        <li>

<!-- SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS 정렬 SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS -->

            <li>
            <form action="/errors" method="get">
                    <input type="hidden" name="search" value="${search.getSearch()}">
                    <input type="hidden" name="page" value="${p.getPage()}">
                    <input type="hidden" name="cate" value="${p.getCate()}">
                    <input type="hidden" name="pageElement" value="${p.getPageElement()}">

                    <select name="sortCate">
                    <c:forEach var="sortCate" items="${sortCateList}">
                        <option value="${sortCate.key}" <c:if test="${p.getSortCate() eq sortCate.key}"> selected </c:if> >${sortCate.value}</option>
                    </c:forEach>
                    </select>

                    <select name="sort">
                    <c:forEach var="sort" items="${sortList}">
                        <option value="${sort.key}" <c:if test="${p.getSort() eq sort.key}"> selected </c:if> >${sort.value}</option>
                    </c:forEach>
                    </select>
                    <input type="submit" value="정렬">
                </form>
            </li>

<!-- EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE 정렬 EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->

            <li>
                <p>&nbsp;&nbsp;&nbsp;&nbsp;</p>
            </li>
<!-- SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS 목록 수 SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS -->

            <li>
            <form action="/errors" method="get">
                    <input type="hidden" name="search" value="${search.getSearch()}">
                    <input type="hidden" name="page" value="${p.getPage()}">
                    <input type="hidden" name="cate" value="${p.getCate()}">
                    <input type="hidden" name="sort" value="${p.getSort()}">
                    <input type="hidden" name="sortCate" value="${p.getSortCate()}">

                    <select name="pageElement" style="margin-lift: 15px;">
                        <option value="10" <c:if test="${p.getPageElement() eq 10}"> selected </c:if> >10</option>
                        <option value="20" <c:if test="${p.getPageElement() eq 20}"> selected </c:if> >20</option>
                        <option value="30" <c:if test="${p.getPageElement() eq 30}"> selected </c:if> >30</option>
                        <option value="40" <c:if test="${p.getPageElement() eq 40}"> selected </c:if> >40</option>
                        <option value="50" <c:if test="${p.getPageElement() eq 50}"> selected </c:if> >50</option>
                        <option value="60" <c:if test="${p.getPageElement() eq 60}"> selected </c:if> >60</option>
                        <option value="70" <c:if test="${p.getPageElement() eq 70}"> selected </c:if> >70</option>
                        <option value="80" <c:if test="${p.getPageElement() eq 80}"> selected </c:if> >80</option>
                        <option value="90" <c:if test="${p.getPageElement() eq 90}"> selected </c:if> >90</option>
                        <option value="100" <c:if test="${p.getPageElement() eq 100}"> selected </c:if> >100</option>
                    </select>
                    <input type="submit" value="목록 수">
                </form>
            </li>

<!-- EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE 목록 수 EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->

            <li>
                <p>&nbsp;&nbsp;&nbsp;&nbsp;</p>
            </li>

            <li>
                <button id="go-list">목록으로</button>
            </li>

<!-- SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS 페이징 버튼 SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS -->

            <li>
            <ul>
                    <li>
                        <a class="page-button" href="/errors?page=1&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> << </a>
                    </li>
                    <li>
                        <a class="page-button" href="/errors?page=${p.getPage()-1}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> < </a>
                    </li>
                    <li>
                    <c:forEach var="i" begin="${p.getStartPage()}" end="${p.getStartPage()+(p.getEndPage()-p.getStartPage())}">
                    <li>
                                <a class="page-button<c:if test='${p.getPage() eq i}'> active </c:if> " href="/errors?page=${i}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}">${i}</a>
                    </li>
                    </c:forEach>

                    <li>
                        <a class="page-button" href="/errors?page=${p.getPage()+1}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> > </a>
                    </li>
                    <li>
                        <a class="page-button" href="/errors?page=${p.getTotalPage()}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> >> </a>
                    </li>
            </ul>
            </li>

<!-- EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE 페이징 버튼 EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->
        <li>
    </ul>
    <script>

        document.getElementById("go-list").onclick = function () {
            location.href = "/masters";
        }

        function openNewWindow(id) {
            window.open('/error/detail?errorId='+id);
        }
    </script>

</body>
</html>