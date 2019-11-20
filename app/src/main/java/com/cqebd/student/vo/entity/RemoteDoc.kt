package com.cqebd.student.vo.entity


data class RemoteDoc(
		var PageSize: Int,// 20
		var PageIndex: Int,// 1
		var Total: Int,// 1
		var DataList: List<DocItem>
)

data class DocItem(
		var FileName: String,// 导入到校参观模板.xls
		var ExtensionName: String,// .xls
		var UploadFileId: Int,// 3
		var Url: String,// http://platform.ens.cqebd.cn/uploads/template/导入到校参观模板.xls
		var Size: Int,// 27
		var CreateDateTime: String// 2019-10-31T11:02:45.827
)