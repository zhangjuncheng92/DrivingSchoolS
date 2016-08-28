package com.zjc.drivingSchoolS.db.response;

import com.zjc.drivingSchoolS.db.model.AppResponse;
import com.zjc.drivingSchoolS.db.models.TeacherItem;

import java.util.List;

/**
 * 教练列表响应
 * 
 * @author LJ
 * @date 2016年7月21日
 */
public class TeacherListResponse extends AppResponse
{	
	private List<TeacherItem> tcitems;// 教练对象

	public List<TeacherItem> getTcitems()
	{
		return tcitems;
	}

	public void setTcitems(List<TeacherItem> tcitems)
	{
		this.tcitems = tcitems;
	}

}
