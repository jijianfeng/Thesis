<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'north',border:false">
		
	</div>
	<div data-options="region:'center',border:false">
	<form id="form" action="${__url__}" method="post">
		<table width="800px" style="margin:10px;">
        <tr>
           <td width="100px">联系人:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="${about.linkUser}" id="search_text" name="linkUser" />
           </td>
        </tr>
        <tr><td></td><tr>
        <tr>
           <td width="100px">联系电话:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="${about.linkTel}" name="linkTel" />
           </td>
        </tr>
        <tr><td></td><tr>
        <tr><td></td><tr> 
        <tr>
           <td width="50px">E-mail:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="${about.linkMail}"  name="linkMail" />
           </td>
        </tr>
        <tr><td></td><tr>
        <tr>
           <td width="50px">地址:</td>
           <td>                    
           <input class="ml10 w200" type="text" value="${about.linkAddress}" name="linkAddress" style="width:700px"/>
           </td>
        </tr>
        <tr><td></td><tr>
        <tr>
        <td></td>
        <td style="float:left;">
        &nbsp;&nbsp;&nbsp;
        <c:if test="${sessionScope.userType==3}">
        <button type="submit" class="btn btn-small btn-success">保存</button>
        </c:if>
        </td>
        </tr>
		</table>
		</form>
	</div>
</div>
<script type="text/javascript">
App.ajaxForm("#form");
</script>