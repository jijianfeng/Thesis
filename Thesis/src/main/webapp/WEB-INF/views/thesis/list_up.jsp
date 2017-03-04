<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<form id="form" action="${__url__}" method="post">
    <table align="center" class="form-table" style="margin:40px 50px">
    	<tr>
    	    <td style="width:80px" >学院：</td>
    	    <td>
    	        <select class="jq-combobox" id="collegelista" name="collegea" data-options="{
    	        	required:true,
					method:'post',
					editable:true,
					url: '${z:u('public/collegelistwhere_nodefault')}'
					}">
<!-- 					<c:if test="${sessionScope.userType==1}"> -->
<!-- 						<option value="${collegeId}" selected="selected"> -->
<!-- 					</c:if>			 -->
    	        </select>
    	    </td>
    	</tr>
<!--     	<tr> -->
<!--     	    <td>一级学科：</td> -->
<!--     	    <td> -->
<!--     	        <select class="jq-combobox" id="majorlista"  name="majora" data-options="{ -->
<!--     	        	url: '${z:u('public/majorOneList')}', -->
<!--     	        	required:true, -->
<!-- 					method:'post', -->
<!-- 					editable:true, -->
<!-- 					}"> -->
<!-- 				</select> -->
<!--     	    </td> -->
<!--     	</tr> -->
<!--     	<tr> -->
<!--     	    <td>二级学科：</td> -->
<!--     	    <td> -->
<!--     	        <select class="jq-combobox" id="majorlistb"  name="majorb" data-options="{ -->
<!--     	        	url: '${z:u('public/major_two_list')}'+'?id=0', -->
<!--     	        	required:true, -->
<!-- 					method:'post', -->
<!-- 					editable:true, -->
<!-- 					}"> -->
<!-- 				</select> -->
<!--     	    </td> -->
<!--     	</tr> -->
        <tr>
    	    <td>教师：</td>
    	    <td>
    	        <select class="jq-combobox" id="teacherlista"  name="teachera" data-options="{
    	        	required:true,
					method:'post',
					editable:false,
					}">
				</select>
    	    </td>
    	</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
				<button type="submit" class="btn btn-small btn-success">确定</button>
				<a class="btn btn-primary btn-small" href="">返回</a>
			</td>
		</tr>
	</table>
	<input name="id" value="${id}" type="hidden"  >
</form>
<script type="text/javascript">
	$("#collegelista").combobox({
		editable:false,
		onChange:function(){
// 			var a = $("#majorlista").combobox("getValue");
// 			var b = $("#majorlistb").combobox("getValue");
			var c = $("#collegelista").combobox("getValue");
			$('#teacherlista').combobox('reload', '${z:u('public/teacherlist')}'+"?collegeId="
					+c);//"&majora="+a+"&majorb="+b);
			$('#teacherlista').combobox('setValues','');
		}
	});

	$("#majorlistb").combobox({
		editable:false,
		onChange:function(){
			var a = $("#majorlista").combobox("getValue");
			var b = $("#majorlistb").combobox("getValue");
			var c = $("#collegelista").combobox("getValue");
			//alert(a+"::::"+b);
			$('#teacherlista').combobox('reload', '${z:u('public/teacherlist')}'+"?collegeId="
					+c);//+"&majora="+a+"&majorb="+b);
			$('#teacherlista').combobox('setValues','');
		}
	});

	$("#majorlista").combobox({
		editable:false,
		onChange:function(){
			//alert("开始");
			var n = $("#majorlista").combobox("getValue");
			$('#majorlistb').combobox('reload', '${z:u('public/major_two_list')}'+"?id="+n);
			$('#majorlistb').combobox('setValues','');
			var a = $("#majorlista").combobox("getValue");
			var b = $("#majorlistb").combobox("getValue");
			var c = $("#collegelista").combobox("getValue");
			$('#teacherlista').combobox('reload', '${z:u('public/teacherlist')}'+"?collegeId="
					+c);//+"&majora="+a+"&majorb="+b);
			$('#teacherlista').combobox('setValues','');
		}
	});
	$(document).ready(function(){  
	    $("#form").bind("submit", function(){  
	    	$('.jq-datagrid').datagrid('clearSelections');
	    	return true;
	    })
	});
	
</script>