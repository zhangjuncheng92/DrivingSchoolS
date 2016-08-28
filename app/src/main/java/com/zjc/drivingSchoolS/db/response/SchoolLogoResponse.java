package com.zjc.drivingSchoolS.db.response;


import com.zjc.drivingSchoolS.db.model.AppResponse;

/**
 * 驾校Logo上传响应
 * 
 * @author LJ
 * @date 2016年7月21日
 */
public class SchoolLogoResponse extends AppResponse
{	
	private String uid;// 用户ID
	
	private String logourl;// Logo URL，格式：/upload/images/...

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getLogourl()
	{
		return logourl;
	}

	public void setLogourl(String logourl)
	{
		this.logourl = logourl;
	}
	
}
