<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>
    <style>    
        .error{color: #FF0000}
        .right{color: #00AA00}
    </style>
<div class="container" style="background:white">
<input type="hidden" value="${pageType}" name="pageType">
	<form id="form" action="${__url__}" method="post" enctype="multipart/form-data" >
		<table align="center" class="form-table" style="font-size:15px" bgcolor="white">
			<tbody>
<!--  				<tr> -->
<!-- 					<td><label>所属学院：</label> -->
<!-- 					<td> -->
<!-- 						<select class="jq-combobox" id="collegelistwhere" name="CollegeIdWhere" style="width:200px" data-options="{ -->
<!-- 						required:true, -->
<!-- 						method:'post', -->
<!-- 						editable:false, -->
<!-- 						url: '${z:u('public/collegelistwhere_nodefault')}'}"> -->
<!-- 						</select> -->
<!-- 					</td> -->
<!-- 				</tr>  -->
				<input type="hidden" value="000" id="collegelistwhere">
				<tr>
					<td><label>评阅模板：</label>
					<td>
						<select class="jq-combobox"  name="DiyCom" style="width:200px" data-options="{
						required:true,
						method:'post',
						editable:false,
						url: '${z:u('public/diy_com')}'}">
						</select>
					</td>
				</tr>
				<tr>
					<td><label>&nbsp;&nbsp;注意事项：</label>
					</td>
					<td style="color: red;font-size:16">
							1、只允许上传xls格式的excel表且压缩为zip 格式的压缩包<br />
							2、所有论文必须符合命名规范：学生学号.后缀名<br /> 
							3、zip压缩包中不能含有目录<br /> 
							4、如果文件的体积较大，程序会花费一定的时间处理，请耐心等待<br />
							5、此项操作较耗资源，强烈建议您在提交之前，仔细核对所选文件的正确性 <br />
					</td>
				</tr>
				<tr>
					<td><label>&nbsp;&nbsp;示例文件：</label>
					</td>
					<td><a id="download_example" class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载示例文件</a></td>
				</tr>
				<tr>
					<td><label>文件：</label></td>
					<td>excel:
					<input type="file" id="excel" name="excel" data-options="{required:true}" accept=".xls">
					zip压缩包:
					<input type="file" id="zip" name ="zip" accept=".zip">
					</td>
					<td><button type="submit" id="submit"  class="btn btn-info btn-sm">开始上传</td>
					<td><span class="error" id="msg"></span></td>
				</tr>				
			</tbody>
		</table>
	</form>
</div>


<script type="text/javascript">
	$("#download_example").click(function(){
		window.location.href = '${z:u("/thesis/download_example")}';
	});
	
	// 提交
	$("#form").ajaxForm({
		type: "post",
		dataType: "json",
		beforeSubmit: function() {
// 			var collegelistwhere = $("#collegelistwhere").combobox("getValue");
			var excel = $("#excel").val();
			var zip = $("#zip").val();
// 			if (collegelistwhere == ""||collegelistwhere==null) {
// 				$("#collegelistwhere").focus();
// 				$("#msg").removeClass("right").addClass("error").html("学院为空");
// 				$("#msg").html("学院为空");
// 				return false;
// 			}
			if (excel == ""||excel	==null) {
				$("#excel").focus();
				$("#msg").removeClass("right").addClass("error").html("excel文件为空");
				$("#msg").html("excel文件为空");
				return false;
			}
			if (zip == ""||zip	==null) {
				$("#zip").focus();
				$("#msg").removeClass("right").addClass("error").html("zip文件为空");
				$("#msg").html("zip文件为空");
				return false;
			}
			$("#msg").removeClass("error").addClass("right").html("正在上传...");			
		},
		success: function(data) {
			if (data.status == 1) {
				$("#msg").removeClass("error").addClass("right").html(data.info);
				alert("上传成功");
				//window.location.href = "${z:u('/')}";
				location.reload();
			} else {
				$("#msg").removeClass("right").addClass("error").html(data.info);
			}

		}
	});
	
	//四级联动
// 	$("#arealist").combobox({
// 		editable:false,
// 		onChange:function(){
// 			var v = $("#arealist").combobox("getValue");
// 			$('#universitylist').combobox('reload', '${z:u('public/universitylist')}'+"?areaId="+v);
// 		}
// 	});
	
// 	$("#universitylist").combobox({
// 		editable:false,
// 		onChange:function(){
// 			var b = $("#universitylist").combobox("getValue");
// 			$('#collegelist').combobox('reload', '${z:u('public/collegelist')}'+"?universityId="+b);
// 		}
// 	});
	
// 	$("#collegelistwhere").combobox({
// 		editable:false,
// 		onChange:function(){
// 			var b = $("#collegelistwhere").combobox("getValue");
// 			$('#majorlistwhere').combobox('reload', '${z:u('public/majorlist_nodefault')}'+"?collegeId="+b);
// 			$('#majorlistwhere').combobox('setValues','');
// 		}
// 	});
</script>