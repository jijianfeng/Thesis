<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
 <form id="form" action="${__url__}" method="post" enctype="multipart/form-data" >
	<div data-options="region:'north',border:false">
		<div id="grid-toolbar" class="clearfix p5">
            <div style="float:left;margin-left:5px;padding:3px;">送审数量:
				<select class="jq-combobox"  
				<c:if test="${sessionScope.userType!=3}">
	           	disabled="disabled"
			    </c:if>
				  panelHeight="auto" style="width:100px" id="status" name="status">
				<option value="0">无上限</option>
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<c:if test="${about.sendNumber!=null}"><option value="${about.sendNumber}" selected="selected"></option></c:if>
			</select>
			</div>
           &nbsp;<br/>&nbsp;
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<table align="left" class="form-table" >
        <tr>
	        <td style="background:#E7E7F5;"> 本年流水号【起始】:</td>
	        <td>
	           <input class="w200"
	           <c:if test="${sessionScope.userType!=3}">
	           	disabled="disabled"
			   </c:if>
	            type="text"  value="${about.sendLetter}" name="sendLetter" />          
	        </td>
        </tr>
        <tr>
	        <td></td>
	        <td></td>
        </tr>
        <c:if test="${sessionScope.userType==0||sessionScope.userType==1||sessionScope.userType==3}">
        <tr>
	        <td align="right" style="background:#E7E7F5;">通用送审模板:</td>
	        <td>
	           <a id="download" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载通用评审模块</a>      
	        </td>
        </tr>
        </c:if>
        <tr>
	        <td></td>
	        <td></td>
        </tr>
        <tr>
	        <td></td>
	        <td></td>
        </tr>
        <c:if test="${sessionScope.userType==3}">
        <tr>
        	<td align="right" style="background:#E7E7F5;">通用送审模板修改:</td>
	        <td>
	           <input name="file" type="file">
	        </td>
        </tr>
        </c:if>
        <tr>
	        <td></td>
	        <td></td>
        </tr>
        <c:if test="${sessionScope.userType==3}">
        	<tr>
        		<td align="right" style="background:#E7E7F5;">示例文件修改：</td>
        		<td><input type="file" name="example"></td>
        		<input type="file" name="image" id="hiddenImage" >
        	</tr>
        </c:if>
        <tr>
	        <td></td>
	        <td></td>
        </tr>
        <tr>
        	<c:if test="${sessionScope.userType==2}">
	        <td align="right" style="background:#E7E7F5;">当前签名图片:</td>
	        <td>
	           <a id="downloadUserImage" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载当前签名图片</a> 
	        </td>
	        </c:if>
        </tr>
        <c:if test="${sessionScope.userType==2}">
        	<tr>
        		<td align="right" style="background:#E7E7F5;">签名图片修改：</td>
        		<td><input type="file" name="image" accept=".png,.jpg" ></td>
        		<input type="file" name="example" id="hiddenExample" >
        		<input type="file" name="file" id="hiddenFile" >
        	</tr>
        </c:if>
        <tr>
	        <td style="float:right;">
	        <c:if test="${sessionScope.userType==3||sessionScope.userType==2}">
		        <button type="submit" class="btn btn-small btn-success">保存</button>
		    </c:if>
	        </td>
        </tr>
		</table>
	</div>
	</form>
</div>
<script type="text/javascript">
	App.ajaxForm("#form");
	$("#download").click(function(){
		window.location.href = '${z:u("/basic/download_normal")}';
	});
	$("#downloadUser").click(function(){
		window.location.href = '${z:u("/basic/download_user")}';
	});
	$("#downloadUserImage").click(function(){
		window.location.href = '${z:u("/basic/download_user_image")}';
	});
	$("#hiddenFile").hide(0);
	$("#hiddenExample").hide(0);
	$("#hiddenImage").hide(0);
</script>