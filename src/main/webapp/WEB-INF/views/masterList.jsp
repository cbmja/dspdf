<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>master_list</title>

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
        상태 검색시에는 코드로 검색해주세요
        <c:forEach var="status" items="${statusList}">
            / [${status.getSTATUS_NAME()} : ${status.getSTATUS_CODE()}]
        </c:forEach>
    </div>

    <form action="/masters" method="get">
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

    <form action="/masters/update" method="post">
    <table border="1" >
        <tr>
            <th> 그룹키 </th>
            <th> 현재 수신 건수 / 총 건수 </th>
            <th> 마지막 건수 수신 시각</th>
            <th> 현재 상태 시작 시각</th>
            <th> 현재 상태 </th>
            <th> 상태 수정 </th>
            <th> 타입 </th>
        </tr>

        <c:forEach var="master" items="${masterList}">
        <tr class="table-row">
            <td>${master.getMASTER_KEY()}</td>
            <td>${master.getSEND_CNT()} / ${master.getTOTAL_SEND_CNT()}</td>
            <td>${master.getRECEIVED_TIME()}</td>
            <td>${master.getSTATUS_TIME()}</td>
            <td>${master.getStatusName()}</td>
            <td>
              <select name="${master.getMASTER_KEY()}" onchange="updateStatus('${ master.getMASTER_KEY()}', this.value)">
                <c:forEach var="status" items="${statusList}">
                    <option value="${status.getSTATUS_CODE()}" <c:if test="${status.getSTATUS_NAME() eq master.getStatusName()}"> selected </c:if> >${status.getSTATUS_NAME()}</option>
                </c:forEach>
              </select>
            </td>
            <td>${master.getTypeName()}</td>
        </tr>
        </c:forEach>
        <tr>
            <td colspan="5">
                총 검색 건수 : ${p.getTotal()}
                <input type="hidden" id="statusData" name="statusData">
                <input type="hidden" name="search" value="${search.getSearch()}">
                <input type="hidden" name="page" value="${p.getPage()}">
                <input type="hidden" name="cate" value="${p.getCate()}">
                <input type="hidden" name="sort" value="${p.getSort()}">
                <input type="hidden" name="sortCate" value="${p.getSortCate()}">
                <input type="hidden" name="pageElement" value="${p.getPageElement()}">
            </td>
            <td><input type="submit" value="상태 저장" onclick="prepareData()"></td>
            <td></td>
        </tr>
    </table>
</form>

<!-- EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE MASTER LIST EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->

    <ul>
        <li>

<!-- SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS 정렬 SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS -->

            <li>
            <form action="/masters" method="get">
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
            <form action="/masters" method="get">
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
                <button id="err-button">에러 확인</button>
            </li>

<!-- SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS 페이징 버튼 SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS -->

            <li>
            <ul>
                    <li>
                        <a class="page-button" href="/masters?page=1&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> << </a>
                    </li>
                    <li>
                        <a class="page-button" href="/masters?page=${p.getPage()-1}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> < </a>
                    </li>
                    <li>
                    <c:forEach var="i" begin="${p.getStartPage()}" end="${p.getStartPage()+(p.getEndPage()-p.getStartPage())}">
                    <li>
                                <a class="page-button<c:if test='${p.getPage() eq i}'> active </c:if> " href="/masters?page=${i}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}">${i}</a>
                    </li>
                    </c:forEach>

                    <li>
                        <a class="page-button" href="/masters?page=${p.getPage()+1}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> > </a>
                    </li>
                    <li>
                        <a class="page-button" href="/masters?page=${p.getTotalPage()}&search=${search.getSearch()}&cate=${p.getCate()}&sort=${p.getSort()}&sortCate=${p.getSortCate()}&pageElement=${p.getPageElement()}"> >> </a>
                    </li>
            </ul>
            </li>

<!-- EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE 페이징 버튼 EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE -->
        <li>
    </ul>

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

    document.getElementById("err-button").onclick = function () {
        location.href = "/errors";
    }

</script>


</body>
</html>