<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://zlzkj.com/tags" prefix="z"%>
    <style>    
        .error{color: #FF0000}
        .right{color: #00AA00}
    </style>
<div class="container" style="background:white">
	<form id="form" action="${__url__}" method="post" enctype="multipart/form-data" >
		<table align="center"  class="form-table" style="font-size:15px" bgcolor="white" ; >
			<input  name="thesisName" type="hidden" readonly="readonly" value="${thesis.id}" data-options="required:true," />
			<tbody>
				<tr>
					<td style="width:200px;background:#E7E7F5;"><label>论文题目：</label></td>
					<td>
						<input class="jq-validatebox" name="thesisName" type="text" readonly="readonly" value="${thesis.thesisName}" data-options="required:true," />
					</td>
				</tr>
				<tr>
					<td style="width: 200px;background:#E7E7F5;"><label>专业：</label></td>
					<td>
						<input class="jq-validatebox" name="thesisName" type="text" readonly="readonly" value="${majorTwo}" data-options="required:true," />
					</td>
				</tr>
				<tr>
					<td><label>评议项目：</label></td>
					<td width="100px" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;评价要素</td>
					<td>成绩</td>
				</tr>
				<tr>
					<td style="width:200px;background:#E7E7F5;"><label>选题与综述：</label></td>
					<td>研究的理论意义、实用价值;对本学科及相关学领域国内外发展状况和学术动态的了解程度</td>
					<td>
						<select class="jq-combobox" id="thesisResultOne" name="thesisResultOne" style="width:90px" >
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
						</select>
					</td>
				</tr>
				<tr>
					<td style="width:200px;background:#E7E7F5;"><label>创新性及论文价值：</label></td>
					<td>论文提出的新见解、新方法所具有的价值;论文成果对技术进步、经济建设、国家安全等方面产生的影响或作用</td>
					<td>
						<select class="jq-combobox" id="thesisResultTwo" name="thesisResultTwo" style="width:90px" >
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
						</select>
					</td>
				</tr> 
				<tr>
					<td style="width:200px;background:#E7E7F5;"><label>基础知识和科研能力：</label></td>
					<td>论文体现的理论基础的扎实程度;本学科及相关学科领域专业知识的系统性;分析问题、解决问题的能力;研究方法的科学性,是否采用先进技术、设备、信息等进行论文研究工作</td>
					<td>
						<select class="jq-combobox" id="thesisResultThree" name="thesisResultThree" style="width:90px" >
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
						</select>
					</td>
				</tr>
				<tr>
					<td style="width:200px;background:#E7E7F5;"><label>论文规范性：</label></td>
					<td>论文的规范性,学风的严谨性;论文语言表达的准确性,逻辑的严密性;论文结构的合理性,书写格式及图表的规范性</td>
					<td>
						<select class="jq-combobox" id="thesisResultFour" name="thesisResultFour" style="width:90px" >
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
						</select>
					</td>
				</tr>
				<tr>
					<td style="width:200px;background:#E7E7F5;"><input type="radio" id="i1" name="c">总体评价1：</td>
					<td><label>给出百分制总评成绩,100-90为优秀;89-75为良好;74-60为一般;60分一下为差</label></td>
					<td>
 						<input class="mr10 w50" id="thesisResultNumber" name="thesisResultNumber"  type="text"  />
					</td>
				</tr>
				<tr>
					<td style="width:200px;background:#E7E7F5;"><input type="radio" id="i2" name="c">总体评价2：</td>
					<td><label>给出总评成绩,有优良中和差</label></td>
					<td>
 						<select class="jq-combobox" id="thesisResult"  name="thesisResult" style="width:90px" disabled="disabled" >
 							<option value=0></option>
							<option value=1>优</option>
							<option value=2>良</option>
							<option value=3>中</option>
							<option value=4>合</option>
							<option value=5>差</option>
						</select>
					</td>
				</tr>  
				<tr>
					<td style="width:200px;background:#E7E7F5;"><label>备注：</label></td>
					<td><textarea class="mr10 w200" type="text"  name="thesisRemark" style="width:300px;height: 100px;"/></td>
				</tr>
				<tr>
					<td style="width:200px;background:#E7E7F5;"><label>附件：</label></td>
					<td>
						<input type="file" id="zip" name ="file">
					</td>
				</tr>					    
				<tr>
					<td></td>
					<td><button type="submit"  class="btn btn-info btn-sm">确定评审</button></td>
					<td><span class="error" id="msg"></span></td>
				</tr>				
			</tbody>
		</table>
	</form>
</div>

<script type="text/javascript">
	
	$(document).ready(function(){
		document.getElementById('i1').checked=true;
		$("#i1").click(function(){
			$("#thesisResultNumber").removeAttr("disabled");
			$('#thesisResult').combobox('disable');
			$('#thesisResult').combobox('setValues','0');
		});
	
		$("#i2").click(function(){
			$("#thesisResultNumber").attr("disabled","disabled");
			$('#thesisResult').combobox('enable');
			$("#thesisResultNumber").val("");
		});
	});
	
	$(document).ready(function(){  
	    $("#form").bind("submit", function(){  
	    	$('.jq-datagrid').datagrid('clearSelections');
	    	return true;
	    })
	})
</script>