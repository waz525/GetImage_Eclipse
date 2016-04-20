
public interface GetImageMBean
{
	public String  get01_Url() ;
	public void set01_Url(String  value) ;

	public String get02_EncodeType() ;
	public void set02_EncodeType(String str) ;

	public int  get03_isTest() ;
	public void set03_isTest(int  value) ;

	public int  get04_Depth() ;
	public void set04_Depth(int  value) ;

	public String get05_DirForJpg() ;
	public void set05_DirForJpg(String str) ;

	public int  get06_isSaveByTitle() ;
	public void set06_isSaveByTitle(int  value) ;


	public String get23_filterlist() ;
	public void set23_filterlist(String list) ;
	
	public String get22_filterfile() ;
	public void set22_filterfile(String filename) ;

	public String get24_piclist() ;
	public void set24_piclist(String picList) ;
	
	public String get11_Cookie() ;
	public void set11_Cookie(String cookie) ;
	
	public String get14_RegForImage() ;
	public void set14_RegForImage(String reg) ;
	
	public String get16_RegForHtml() ;
	public void set16_RegForHtml(String reg) ;
	
	public String get18_FlagForHtml() ;
	public void set18_FlagForHtml(String str) ;

	public String get12_UrlForCookie() ;
	public void set12_UrlForCookie(String str) ;

	public String get13_StrForCookie() ;
	public void set13_StrForCookie(String str) ;
	
	public String get25_RegForList() ;
	public void set25_RegForList(String str) ;

	public String get07_TitleSeparator() ;
	public void set07_TitleSeparator(String str) ;

	public String get19_imgUrlReplace() ;
	public void set19_imgUrlReplace(String str) ;
	
	public String get20_defPostfix() ;
	public void set20_defPostfix(String str) ;

	public int  get26_NumForList() ;
	public void set26_NumForList(int  value) ;
	
	public int  get09_RepeateOperate() ;
	public void set09_RepeateOperate(int  value) ;

	public int  get15_NumForImage() ;
	public void set15_NumForImage(int  value) ;

	public int  get17_NumForHtml() ;
	public void set17_NumForHtml(int  value) ;

	public int  get10_UseCookie() ;
	public void set10_UseCookie(int  value) ;

	public int  get21_filterflag() ;
	public void set21_filterflag(int  value) ;

	public int  get08_ThreadNum() ;
	public void set08_ThreadNum(int  value) ;

	public int get99_runflag() ;
	
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