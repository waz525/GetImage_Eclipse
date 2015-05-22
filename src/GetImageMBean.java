
public interface GetImageMBean
{

	public String getfilterlist() ;
	public void setfilterlist(String list) ;
	
	public String getfilterfile() ;
	public void setfilterfile(String filename) ;

	public String getpiclist() ;
	public void setpiclist(String picList) ;
	
	public String getCookie() ;
	public void setCookie(String cookie) ;
	
	public String getRegForImage() ;
	public void setRegForImage(String reg) ;
	
	public String getRegForHtml() ;
	public void setRegForHtml(String reg) ;
	
	public String getFlagForHtml() ;
	public void setFlagForHtml(String str) ;

	public String getDirForJpg() ;
	public void setDirForJpg(String str) ;

	public String getEncodeType() ;
	public void setEncodeType(String str) ;

	public String getUrlForCookie() ;
	public void setUrlForCookie(String str) ;

	public String getStrForCookie() ;
	public void setStrForCookie(String str) ;
	
	public String getRegForList() ;
	public void setRegForList(String str) ;

	public String getTitleSeparator() ;
	public void setTitleSeparator(String str) ;

	public String getrepeateUrl() ;
	public void setrepeateUrl(String str) ;

	public String getimgUrlReplace() ;
	public void setimgUrlReplace(String str) ;
	
	public String getdefPostfix() ;
	public void setdefPostfix(String str) ;

	public int  getNumForList() ;
	public void setNumForList(int  value) ;
	
	public int  getRepeateOperate() ;
	public void setRepeateOperate(int  value) ;

	public int  getNumForImage() ;
	public void setNumForImage(int  value) ;

	public int  getNumForHtml() ;
	public void setNumForHtml(int  value) ;

	public int  getisTest() ;
	public void setisTest(int  value) ;

	public int  getisSaveByTitle() ;
	public void setisSaveByTitle(int  value) ;

	public int  getUseCookie() ;
	public void setUseCookie(int  value) ;

	public int  getfilterflag() ;
	public void setfilterflag(int  value) ;

	public int  getDepth() ;
	public void setDepth(int  value) ;

	public String  getUrl() ;
	public void setUrl(String  value) ;
	
	public int  getThreadNum() ;
	public void setThreadNum(int  value) ;

	public int getrunflag() ;
	
	public String showAllFilterList() ;
	public String reloadAllFilterList() ;
	public String emptyAllFilterList();
	
	public String LoadPropfile( String fileName );
	public String SaveRunningPropFile(String fileName ) ;
	public String showRunningProp() ;

	public String printThreadUrl();
	public String SaveDefultPropFile(String fileName) ;
	
	public String toStartGetImage() ;
	public String toStopGetImage() ;
}