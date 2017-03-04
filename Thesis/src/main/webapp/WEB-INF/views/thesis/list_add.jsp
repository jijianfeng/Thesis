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
					<td><label>所属学院：</label>
					<td>
						<select class="jq-combobox" id="collegelistwhere" name="CollegeIdWhere" style="width:200px" data-options="{
						required:true,
						method:'post',
						editable:true,
						url: '${z:u('public/collegelistwhere_nodefault')}'}">
						</select>
					</td>
				</tr>
				<tr>
					<td><label>所属专业：</label>
					<td>
						<select class="jq-combobox" id="majorlistwhere" name="MajorIdWhere" style="width:200px" data-options="{
						required:true,
						method:'post',
						editable:false,
						}">
						</select>
					</td>
				</tr>							
				<tr>
					<td><label>论文名称：</label>
					<td><input class="jq-validatebox" style="width:200px" type="text" id="thesisNamea" name="thesisName" placeholder="输入论文名称" data-options="required:true" />
					</td>
				</tr>
				<tr>
					<td><label>论文序号：</label>
					<td><input class="jq-validatebox" style="width:200px" type="text" id="thesisCodea" name="thesisCode" placeholder="输入论文序号" data-options="required:true" />
					</td>
				</tr>
				<tr>
					<td><label>论文类型：</label>
					<td>
						<select class="jq-combobox" panelHeight="auto" style="width:100px" name="thesisType">
							<option value="0">学硕</option>
							<option value="1">专硕</option>
							<option value="2">博士</option>
						</select>
					</td>
				</tr>
				<tr>
					<td><label>&nbsp;&nbsp;注意事项：</label>
					</td>
					<td style="color: red;font-size:16">
							1、只允许上传 PDF 格式的论文<br />
							2、所有论文必须符合命名规范：学生学号_姓名_文章名称<br /> 
							3、如果上传了 zip格式的论文压缩包，则压缩包中不能含有目录<br /> 
							4、如果选择了多个压缩包，请确认所有压缩包中的文件不应有重叠<br />
							5、如果文件的体积较大，程序会花费一定的时间处理，请耐心等待<br />
							6、此项操作较耗资源，强烈建议您在提交之前，仔细核对所选文件的正确性 <br />
						</td>
				</tr>
				<tr>
					<td><label>论文文件：</label></td>
					<td><input type="file" id="zip" name ="file"></td>
					<td><button type="submit"  class="btn btn-info btn-sm">开始上传</td>
					<td><span class="error" id="msg"></span></td>
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
</script>