<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>

<div class="jq-layout rel" data-options="fit:true">
	<div data-options="region:'center',border:false">
		<table class="jq-datagrid" fit="true" id="thesisList" data-options="{
 			url: '${z:u('thesis/list_process')}'+'?id=${id}', 
			method:'post',
			onClickRow:ClickRow,
			columns: [[
				{field:'sendUserName',title:'送审人',width:100},
				{field:'status',title:'当前状态',width:100},
 				{field:'public',title:'有无公共平台',width:100,formatter:App.statusFmt}, 
				{field:'university',title:'对方研究生部',width:100},
				{field:'college',title:'对方学院',width:100},
				{field:'teacher',title:'对方老师',width:100},
				{field:'thesisResult',title:'评阅结果',width:100},
				{field:'isfinish',title:'是否评阅完成',width:100,formatter:App.statusFmt},
			]]}">
		</table>
	</div>
	
	 <div data-options="region:'south',border:false">
			<table class="table_xmjz">
				<tr>
					<td style="background:#E7E7F5;width:180px;" align="right">论文标题：</td>
					<td style="width: 250px";>
						<input class="Wdate jq-validatebox" style="width: 250px "
						readonly="readonly" type="text" id="thesisNameaa" name="thesisName"  required="required"
						disabled="disabled"/>
					</td>
					<td  style="background:#E7E7F5;width:180px;" align="right" >论文序号：</td>
					<td  style="width: 250px";>
						<input class="Wdate jq-validatebox" style="width: 250px "
						readonly="readonly" type="text" id="thesisCodeaa" name="thesisCode"  required="required"
						disabled="disabled"/>
					</td>
				</tr>
				<tr>
					<td style="background:#E7E7F5;" align="right">当前状态评审人：</td>
					<td>
						<input class="Wdate jq-validatebox" style="width: 140px "
						readonly="readonly" type="text" id="useraa" name="user"  required="required"
						disabled="disabled"/>
						<input type="hidden" id="hiddenUserId">
					</td>
					<td  style="background:#E7E7F5;" align="right" >评审时间：</td>
					<td>
						<input class="Wdate jq-validatebox" style="width: 250px "
						readonly="readonly" type="text" id="uploadTimeaa" name="uploadTime"  required="required"
						disabled="disabled"/>
					</td>
				</tr>
				<tr>
					<td style="background:#E7E7F5;" align="right">选题与综述：</td>
					<td>
						<select class="jq-combobox" id="thesisResultOneaa"  disabled="disabled" name="thesisResultOne" style="width:170px" >
							<option value=0></option>
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
							<option value=6>暂无数据</option>
						</select>
					</td>
					<td  style="background:#E7E7F5;" align="right" >创新性及论文价值：：</td>
					<td>
						<select class="jq-combobox" id="thesisResultTwoaa" disabled="disabled" name="thesisResultOne" style="width:170px" >
							<option value=0></option>
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
							<option value=6>暂无数据</option>
						</select>
					</td>
				</tr>
				<tr>
					<td style="background:#E7E7F5;" align="right">基础知识和科研能力：</td>
					<td>
						<select class="jq-combobox" id="thesisResultThreeaa" disabled="disabled" name="thesisResultOne" style="width:170px" >
							<option value=0></option>
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
							<option value=6>暂无数据</option>
						</select>
					</td>
					<td  style="background:#E7E7F5;" align="right" >论文规范性：</td>
					<td>
						<select class="jq-combobox" id="thesisResultFouraa" disabled="disabled" name="thesisResultOne" style="width:170px" >
							<option value=0></option>
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
							<option value=6>暂无数据</option>
						</select>
					</td>
				</tr>
				<tr>
					<td style="background:#E7E7F5;" align="right"><input type="radio" disabled="disabled" id="i1" name="c"><label>总体评价1：</label></td>
					<td>
						<input class="mr10 w150" id="thesisResultNumber" disabled="disabled" name="thesisResultNumber"  type="text"  />
					</td>
					<td  style="background:#E7E7F5;" align="right" ><input type="radio" disabled="disabled" id="i2" name="c"><label>总体评价2：</label></td>
					<td>
						<select class="jq-combobox" id="thesisResult" disabled="disabled"  name="thesisResult" style="width:170px" disabled="disabled" >
 							<option value=0></option>
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
							<option value=6>暂无数据</option>
						</select>
					</td>
				</tr>
				<tr>
					<td style="background:#E7E7F5;" align="right">备注：</td>
					<td colspan="2">
						<textarea class="jq-validatebox" id="remark"
							name="remark" readonly="readonly"
							style="width: 430px;height: 50px;resize: none; "></textarea>
					</td>
					<td style="background:#E7E7F5;" align="left">附件：
					<a id="downloadad" href ='' class="btn btn-sm btn-info"><i class="icon icon-edit"></i>下载附件</a>
					<a id="downloadadpdf" href ='' class="btn btn-sm btn-info"><i class="icon icon-edit"></i>导出pdf评语</a>
					</td>
				</tr>
			</table>
	</div> 
</div>
<script type="text/javascript">
$("#downloadad").hide(0);
$("#downloadadpdf").hide(0);
$("#userDetail").hide(0);
$("#userDetail").click(function(){
	var userId = $("#hiddenUserId").val();
	App.popup('${z:u("/user/detail")}?id='+userId, {
		title: "详情",
		width: 800,
		height: 400
	});
});
function ClickRow(rowIndex, row) {
	var id = row.id;
// 	alert(id);
	$.ajax({
		type : "post",
		url : "${z:u('thesis/list_process_detail')}?id=" + id,
		async : true,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {
			//返回的数据用data.data=对象  填充内容
			//隐藏download附件
			$("#downloadad").hide(0);
			$("#downloadadpdf").hide(0);
			$("#userDetail").show(0);
			$("#hiddenUserId").val(data.data.hiddenUserId);
			//alert(data.data.hiddenUserId);
			//alert(data.data.status);
			if(data.data.status==9){
				//显示download附件
				$("#downloadad").show(0);
				$("#downloadadpdf").show(0);
				var down = document.getElementById('downloadad');
				down.href = '${z:u("/thesis/download_comments")}?id='+data.data.resultId;
				
				var downpdf = document.getElementById('downloadadpdf');
				downpdf.href = '${z:u("/thesis/download_pdf")}?id='+data.data.resultId;
			}
			$("#thesisNameaa").val(data.data.thesisName);
			$("#thesisCodeaa").val(data.data.thesisCode);
			$("#uploadTimeaa").val(data.data.date);
			//$("#remark").val(data.data.remark);
			$("#useraa").val(data.data.user);
// 			alert(data.data.isSendUrl);
			if(data.data.isSendUrl==0&&data.data.status==9){
// 				alert(data.data.remark);
				$("#remark").val(data.data.remark);
				$("#remark").css("color","black");
			}
			else if(data.data.isSendUrl==1&&data.data.status==9){
				$("#remark").val('该论文采用自定义模块，请直接下载附件查看评审结果');
				$("#remark").css("color","red");
				$("#downloadadpdf").hide(0);
			}
			else{
				$("#remark").val(data.data.remark);
			}
			if(data.data.resultOne==1){
				$("#thesisResultOneaa").combobox('setValues','1');
			}
			else if(data.data.resultOne==2){
				$("#thesisResultOneaa").combobox('setValues','2');
			}
			else if(data.data.resultOne==3){
				$("#thesisResultOneaa").combobox('setValues','3');
			}
			else if(data.data.resultOne==4){
				$("#thesisResultOneaa").combobox('setValues','4');
			}
			else if(data.data.resultOne==5){
				$("#thesisResultOneaa").combobox('setValues','5');
			}
			else{
				$("#thesisResultOneaa").combobox('setValues','0');
			}
			//two
			if(data.data.resultTwo==1){
				$("#thesisResultTwoaa").combobox('setValues','1');
			}
			else if(data.data.resultTwo==2){
				$("#thesisResultTwoaa").combobox('setValues','2');
			}
			else if(data.data.resultTwo==3){
				$("#thesisResultTwoaa").combobox('setValues','3');
			}
			else if(data.data.resultTwo==4){
				$("#thesisResultTwoaa").combobox('setValues','4');
			}
			else if(data.data.resultTwo==5){
				$("#thesisResultTwoaa").combobox('setValues','5');
			}
			else{
				$("#thesisResultTwoaa").combobox('setValues','0');
			}
			//three
			if(data.data.resultThree==1){
				$("#thesisResultThreeaa").combobox('setValues','1');
			}
			else if(data.data.resultThree==2){
				$("#thesisResultThreeaa").combobox('setValues','2');
			}
			else if(data.data.resultThree==3){
				$("#thesisResultThreeaa").combobox('setValues','3');
			}
			else if(data.data.resultThree==4){
				$("#thesisResultThreeaa").combobox('setValues','4');
			}
			else if(data.data.resultThree==5){
				$("#thesisResultThreeaa").combobox('setValues','5');
			}
			else{
				$("#thesisResultThreeaa").combobox('setValues','0');
			}
			//four
			if(data.data.resultFour==1){
				$("#thesisResultFouraa").combobox('setValues','1');
			}
			else if(data.data.resultFour==2){
				$("#thesisResultFouraa").combobox('setValues','2');
			}
			else if(data.data.resultFour==3){
				$("#thesisResultFouraa").combobox('setValues','3');
			}
			else if(data.data.resultFour==4){
				$("#thesisResultFouraa").combobox('setValues','4');
			}
			else if(data.data.resultFour==5){
				$("#thesisResultFouraa").combobox('setValues','5');
			}
			else{
				$("#thesisResultFouraa").combobox('setValues','0');
			}
// 			alert(data.data.ishundred);
			//初始化
			$("#i1").removeAttr("checked");
			$("#i2").removeAttr("checked");
			$("#thesisResult").combobox('setValues','0');
			$("#thesisResultNumber").val('');
			if(data.data.ishundred==0){
				$("#i2").attr("checked","true");
				$("#thesisResultNumber").val('');
				if(data.data.thesisResult==1){
					$("#thesisResult").combobox('setValues','1');
				}
				else if(data.data.thesisResult==2){
					$("#thesisResult").combobox('setValues','2');
				}
				else if(data.data.thesisResult==3){
					$("#thesisResult").combobox('setValues','3');
				}
				else if(data.data.thesisResult==4){
					$("#thesisResult").combobox('setValues','4');
				}
				else if(data.data.thesisResult==5){
					$("#thesisResult").combobox('setValues','5');
				}
				else{
					$("#thesisResult").combobox('setValues','0');
				}
// 				alert("选中2");
			}
			else if(data.data.ishundred==1){
				$("#i1").attr("checked","true");
				$("#thesisResult").combobox('setValues','0');
				if(data.data.thesisResult!=0){
					$("#thesisResultNumber").val(data.data.thesisResult);
				};
			}
// 			alert(data.data.resultFour);
// 			alert("呵呵"+id);
		},
		error : function(err) {
			alert("错就是错");
		}
	});
}
</script>