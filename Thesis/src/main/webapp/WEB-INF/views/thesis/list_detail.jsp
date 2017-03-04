<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>
    <style>    
        .error{color: #FF0000}
        .right{color: #00AA00}
    </style>
<div class="container" style="background:white">
	<form id="form" action="${__url__}" method="post" enctype="multipart/form-data" >
		<table align="center" class="form-table" style="font-size:15px" bgcolor="white">
			<tbody>					
				<tr>
					<td><label>论文名称：</label></td>
					<td>
					<input class="jq-validatebox" disabled="disabled" value="${thesis.thesisName}" style="width:200px" type="text" id="thesisNamea" name="thesisName" placeholder="输入论文名称" data-options="required:true" />
					</td>
				</tr>
				<tr>
					<td><label>英文名称：</label></td>
					<td><input class="jq-validatebox" disabled="disabled" value="${thesis.englishTitle}" style="width:200px" type="text" id="thesisNamea" name="thesisName" placeholder="输入论文名称" data-options="required:true" /></td>
				</tr>
				<tr>
					<td><label>论文作者：</label></td>
					<td><input class="jq-validatebox" disabled="disabled" value="${thesis.stuName}" style="width:200px" type="text" id="thesisNamea" name="thesisName" placeholder="输入论文名称" data-options="required:true" /></td>
				</tr>
				<tr>
					<td><label>指导老师：</label></td>
					<td><input class="jq-validatebox" disabled="disabled" value="${thesis.teacherName}" style="width:200px" type="text" id="thesisNamea" name="thesisName" placeholder="输入论文名称" data-options="required:true" /></td>
				</tr>
				<tr>
					<td><label>关键词：</label></td>
					<td><input class="jq-validatebox" disabled="disabled" value="${thesis.keyWords}" style="width:200px" type="text" id="thesisNamea" name="thesisName" placeholder="输入论文名称" data-options="required:true" /></td>
				</tr>
				<tr>
					<td><label>论文序号：</label></td>
					<td><input class="jq-validatebox" disabled="disabled" value="${thesis.thesisCode}" style="width:200px" type="text" id="thesisCodea" name="thesisCode" placeholder="输入论文序号" data-options="required:true" />
					</td>
				</tr>
				<tr>
					<td><label>论文类型：</label></td>
					<td>
						<select class="jq-combobox" disabled="disabled" panelHeight="auto" style="width:100px" name="thesisType">
							<option value="1">学硕</option>
							<option value="2">专硕</option>
							<option value="3">博士</option>
							<c:if test="${thesis.thesisType!=null}"><option value="${thesis.thesisType}" selected="selected"></option></c:if>
						</select>
					</td>
				</tr>
				<tr>
					<td><label>上传时间：</label></td>
					<td><input disabled="disabled" class="jq-validatebox" value="${date}" style="width:200px" type="text" placeholder="输入论文序号" data-options="required:true" /></td>
				</tr>
				<tr>
					<td><label>应评审次数：</label></td>
					<td><input disabled="disabled" class="jq-validatebox" value="${thesis.requireNumber}" style="width:200px" type="text" name="sendNumber" placeholder="输入论文序号" data-options="required:true" /></td>
				</tr>
				<tr>
					<td><label>论文备注：</label></td>
					<td>
						<textarea  style="width:450px;height:180px;">${thesis.remark}</textarea>
					</td>
				</tr>			
			</tbody>
		</table>
	</form>
</div>


<script type="text/javascript">
	
	$("#collegelistwhere").combobox({
		editable:false,
		onChange:function(){
			var b = $("#collegelistwhere").combobox("getValue");
			$('#majorlistwhere').combobox('reload', '${z:u('public/majorlist_nodefault')}'+"?collegeId="+b);
			$('#majorlistwhere').combobox('setValues','');
		}
	});
	
	$("#downloada").click(function(){
		window.location.href = '${z:u("/thesis/download")}?id='+${thesis.id};
	});
</script>