
import java.io.* ;
import java.util.* ;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList ;


public class GetImage implements GetImageMBean,Runnable 
{
	
	private DownloaderThread[] downloadThreads ;
	private String CurrentUrl = "" ;
	private int CurrentDepth = 0 ;
	private String repeateUrl = "" ;
	private int runflag = 0 ;
	private Thread threadGetImage ;
	private String AllFilterList = "AllFilterList:" ;
	
	private String Url = "" ;
	private String filterlist = "+-"  ;
	private String filterFile = ""  ;
	private String piclist = "" ;
	private String Cookie = "" ;
	private String RegForImage = "(?x)src=('|\")*((http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*)*(/)*([\\S]*+/)*([\\S]*\\.(jpg|JPG|jpeg|JPEG)))('|\")*" ;
	private String RegForHtml = "" ;
	private String FlagForHtml = "NULL" ;
	private String DirForJpg = "./jpg/GetImage/" ;
	private String EncodeType = "GB18030" ;
	private String UrlForCookie = "" ;
	private String StrForCookie = "" ;
	private String RegForList = "" ;
	private String TitleSeparator = "" ;
	private String imgUrlReplace = "NULL" ;
	private String defPostfix = "jpg" ;
	private int Depth = 50 ;
	private int NumForList = 1 ;
	private int RepeateOperate = 0 ;
	private int NumForImage = 1 ;
	private int NumForHtml = 1 ;
	private int isTest = 0 ;
	private int isSaveByTitle = 1 ;
	private int UseCookie = 0 ;
	private int filterflag = 0 ;
	private int ThreadNum = 0 ;
	
	private void getImage( String url , int depth ) 
	{
		this.CurrentUrl = url ;
		this.CurrentDepth = depth ;
		System.out.println( " Depth "+depth+" : "+url );
		int flagForImg = 1 ;
		String FilePrefix = ""+((new java.util.Date()).getTime()/1000);
		if( filterflag == 0 )
		{
			if( AllFilterList.indexOf("+"+url+"-") != -1 )
			{
				System.out.println( "   >>> repeated !!!");
				if( RepeateOperate == 0 ) return  ;
				if( repeateUrl.indexOf("+"+url+"-") != -1 ) return ;
				flagForImg = 0 ;
				repeateUrl += "+"+url+"-" ;				
			}
			AllFilterList += "+"+url+"-" ;
		}
		Log.writeLog( "./download.log" , "Get "+url ) ;
		String[] imgUrls = null ;
		String[] htmlUrls= null ;
		int ind = url.lastIndexOf('/');
		String urlhead = url ;
		if( ind > 6) urlhead = url.substring(0,ind);
		String htmlname = url.substring(ind+1) ;

		ParseHtml ph = new ParseHtml() ;

		if( UseCookie == 2 )
		{
			Cookie = ph.getCookie( UrlForCookie , StrForCookie  ) ;
			UseCookie = 1 ;
			System.out.println(" Cookie: " + Cookie ) ;
		}
		if( UseCookie > 0 )
		{
			ph.getContent( url , EncodeType ,Cookie ) ;
		}
		else
		{
			ph.getContent( url , EncodeType ) ;
		}
		if( isTest != 0 ) System.out.println("Info: ph->getContent completed ! ") ;
		if( isTest > 1 )
		{
			ph.printContent() ; 
		}
		if( filterflag == 1 )
		{
			String[] tid = ph.getUrlByReg( RegForList , NumForList ) ;
			if( tid.length > 0 )
			{
				if( AllFilterList.indexOf("+"+tid[0]+"-") != -1 )
				{
					System.out.println( "   >>> TID "+tid[0]+" repeated !!!");
					if( RepeateOperate == 0 ) return  ;
					if( repeateUrl.indexOf("+"+tid[0]+"-") != -1 ) return ;
					flagForImg = 0 ;
					repeateUrl += "+"+tid[0]+"-" ;
				}
				if( flagForImg == 1 ) AllFilterList+="+"+tid[0]+"-" ;
				htmlname = tid[0];
				if( flagForImg == 1 && isTest == 0 ) Log.writeString( this.filterFile ,  "+"+tid[0]+"-" ) ;
			}
			else
			{
				htmlname = "tid=NULL" ;
			}
		
		}
		else
		{
			if( flagForImg == 1 && isTest == 0 ) Log.writeString( this.filterFile , "+"+url+"-" ) ;
		}

		if( flagForImg == 1 )
		{
			if( isGetImg( url ) )
			{
				imgUrls = ph.getUrlByReg( RegForImage , NumForImage ) ;
			}
			else
			{
				flagForImg = 0 ;
			}
		}
		int flagForHtml = 0 ;
		if( isGetUrl(url) )
		{
			flagForHtml=1 ;
			htmlUrls =  ph.getUrlByReg( RegForHtml , NumForHtml ) ;
		}
		String title = "" ;
		if( isSaveByTitle == 1 && flagForImg == 1 )
		{
			title = ph.getTitle() ;
			title = title.replaceAll( "&[\\S]{4};" , "" ).replaceAll( "/" , "" ) ;			
			if( title.length() > 0 && TitleSeparator.length() > 0 ) 
			{
				for( int ind_sep = 0 ; ind_sep < TitleSeparator.length() ; ind_sep++)
				{
					if( title.lastIndexOf(TitleSeparator.charAt(ind_sep)) != -1 )
					{
						title = title.substring( 0 , title.lastIndexOf(TitleSeparator.charAt(ind_sep)) ) ;
						if( isTest != 0 ) System.out.println("Info: TITLE("+TitleSeparator.charAt(ind_sep)+") --> "+title) ;
					}
				}
			}
			System.out.println( "   >>> TITLE: "+title) ;
		}
		System.out.println( "   >>> "+htmlname+" : IMAGE("+(flagForImg==1?imgUrls.length:"NULL")+") , HTML("+(flagForHtml==1?htmlUrls.length:"NULL")+")");
		if( flagForImg == 1 ) 
		{
			String rates = "" ;
			String imgHadDown = "" ;
			for( int i = 0 ; i < imgUrls.length ; i++ )
			{
				int rate = (i+1)*100/imgUrls.length ;
				int ind_img = imgUrls[i].lastIndexOf('/');
				String filename = imgUrls[i].substring(ind_img+1);
				if( piclist.indexOf("+"+filename+"-") != -1 )
					continue ;
				String imgUrl = imgUrls[i] ;
				if( imgUrls[i].toLowerCase().indexOf("http://") != -1 )
				{
					imgUrl = imgUrls[i] ;
				}
				else if ( imgUrls[i].indexOf("/") == 0 )
				{
					String ss = urlhead.substring( urlhead.indexOf("//")+2 ) ;
					if( ss.indexOf("/") == -1 )
						ss += "/" ;
					String hostname = ss.substring( 0 , ss.indexOf("/") );
					imgUrl =  "http://"+hostname+"/"+imgUrls[i] ;
				}
				else
				{
					imgUrl = urlhead+"/"+imgUrls[i] ;
				}
				imgUrl = DealUrl(imgUrl) ;
				filename = DealFilename( filename ) ;
				
				String downFile = ""+FilePrefix+"_"  ;
				if( downFile.lastIndexOf('?') > 0 ) downFile=downFile.substring(downFile.lastIndexOf('?')+1) ;
				if( downFile.lastIndexOf('=') > 0 ) downFile=downFile.substring(downFile.lastIndexOf('=')+1) ;
				downFile += (i>9)?((i>99)?(""+i):("0"+i)):("00"+i) ;
				downFile += "_"+filename ;

				if(isSaveByTitle == 1 && title.length() > 0 ) downFile=DirForJpg+"/"+title+"/"+downFile ;
				else downFile=DirForJpg+"/"+htmlname+"/"+downFile ;
				
				imgUrl = ImgUrlReplace( imgUrl ) ;
	
				if( isDownImg( imgUrl ) )
				{
					if( isTest == 0 ) 
					{
						while( getValid() == -1 ) { }
						if( imgHadDown.indexOf( "+"+imgUrl+"-" ) != -1 ) continue ;
						imgHadDown += "+"+imgUrl+"-" ;
						if( UseCookie == 3 ) downloadThreads[getValid()] = new DownloaderThread( imgUrl, downFile , Cookie) ;
						else downloadThreads[getValid()] = new DownloaderThread( imgUrl, downFile ) ;
					}
					else
					{
						System.out.println( "     "+imgUrl+" ----> " + downFile ) ;
					}
				}
				if( isTest == 0 ) 
				{
					rates = rates + "-" ;
					System.out.print( "\r   "+rate+"% ["+rates+">"  ) ;
				}
			}
			if( isTest == 0 ) System.out.println( "\b]" ) ;
		}
		
		if( flagForHtml == 1 && ( depth > 0 || depth == -1 ) )
		{
			for( int i = 0 ; i < htmlUrls.length ; i++ )
			{
				int flagForhtmlUrls = 0 ;
				for( int j = 0 ; j < i ; j++ )
				{
					if( htmlUrls[i].equals(htmlUrls[j]) ) 
					{
						flagForhtmlUrls = 1 ;
						break ;
					}
				}
				if( flagForhtmlUrls == 1 ) continue ;
				String new_url = htmlUrls[i] ;
				if( htmlUrls[i].toLowerCase().indexOf("http://") == 0 )
				{
					new_url = htmlUrls[i] ;
				}
				else if ( htmlUrls[i].indexOf("/") == 0 )
				{
					String ss = urlhead.substring( urlhead.indexOf("//")+2 ) ;
					if( ss.indexOf("/") == -1 )
						ss += "/" ;
					String hostname = ss.substring( 0 , ss.indexOf("/") );
					new_url =  "http://"+hostname+"/"+htmlUrls[i] ;
				}
				else
				{
					new_url = urlhead+"/"+htmlUrls[i] ;
				}
				new_url = DealUrl(new_url) ;
				
				if( isGetFromUrl( url , new_url ) )
				{
					if( isTest == 0 )
					{
						if( depth > 0 )
						{
							getImage( new_url , depth - 1) ;
						}
						else
						{
							getImage( new_url , -1 ) ;
						}
					}
					else
					{
						System.out.println( new_url ) ;
					}
				}
			}
		}
	}
	
	public void run()
	{
		this.runflag = 1 ;
		getImage(Url , Depth) ;
		this.runflag = 0 ;
		System.out.println("[INFO] GetImage Thread run over !");
	}
	
	public String toStartGetImage()
	{
		if( this.runflag == 1 ) return "[ERROR] GetImage Thread is aready running !" ;
		if( this.Url == null || this.Url.length() == 0 ) return "[ERROR] GetImage url is null !" ;
		if( Depth == 0 ) return "[ERROR] GetImage Depth is 0 !" ;
		
		if( UseCookie == 2 )
		{
			if(  UrlForCookie == null || UrlForCookie.length() == 0 )  return "[ERROR] Property UrlForCookie is null !!! " ;
			if(  StrForCookie == null || StrForCookie.length() == 0 )  return "[ERROR] Property StrForCookie is null !!! " ;
		}
		
		if( filterflag == 1 )
		{
			if(  RegForList == null || RegForList.length() == 0 )  return "[ERROR] Property RegForList is null !!! " ;
		}
		System.out.println("[INFO] Start GetImage Thread ...");
		this.downloadThreads = new DownloaderThread[ this.ThreadNum ] ;
		threadGetImage = new Thread(this , "toStartGetImage");
		threadGetImage.start() ;
		
		return "[INFO] Start GetImage Thread SUCCESS !" ;
	}
	
	@SuppressWarnings("deprecation")
	public String toStopGetImage()
	{
		if( this.runflag == 0 ) return "[ERROR] GetImage Thread is not running !" ;
		threadGetImage.stop();
		this.runflag = 0 ;
		System.out.println("\n[INFO] Stop GetImage Thread !");
		return "[INFO] Stop GetImage Thread SUCCESS !" ;
	}

	public String LoadPropfile( String fileName )
	{
		System.out.println("Info: Load Config from "+fileName+" ...") ;
		
		if ( !Log.isFile(fileName) )
		{
			System.out.println( "[ERROR] "+fileName+" is not a file !!!" ) ;
			return "[ERROR] "+fileName+" is not a file !!!" ;
		}
		
		Properties prop = new Properties() ;
		try
		{
			//InputStream is = getClass().getResourceAsStream(fileName) ;
		
			//String fileurl=this.getClass().getClassLoader().getResource(fileName).toString().substring(6);
			//String empfileurl=fileurl.replaceAll("%20"," ");
			//System.out.println("empfileurl: "+empfileurl) ;
			//InputStream is = new BufferedInputStream(new FileInputStream(empfileurl)) ;
			
			InputStream is = new BufferedInputStream(new FileInputStream(fileName)) ;
			
			prop.load(is) ;
			is.close() ;
		}catch(Exception e )
		{
			e.printStackTrace() ;
			//System.out.println( e+"\n[ERROR] file "+fileName+" not found ." ) ;
			return "[ERROR] file "+fileName+" not found !" ;
		}
		
		String temp ; 
		temp = prop.getProperty("Url");
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property Url is null !!! ") ;  temp="" ;}
		this.Url = temp ;
		temp = prop.getProperty("Depth") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property Depth is null , use Depth=100 ...") ; temp="100" ;}
		this.Depth = Integer.parseInt( temp );
		temp = prop.getProperty("filterlist") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property filterlist is null , use filterlist=FILTER: ...") ; temp="FILTER:" ;}
		this.filterlist = temp;		
		temp = prop.getProperty("RepeateOperate") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property RepeateOperate is null , use RepeateOperate=0 ...") ; temp="0" ;}
		this.RepeateOperate = Integer.parseInt(temp) ;
		temp = prop.getProperty("filterFile") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property filterFile is null , use filterFile=./GetImage.log ...") ; temp="./GetImage.log" ;}
		this.filterFile = temp ;
		temp = prop.getProperty("piclist") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property piclist is null , use piclist= ...") ; temp="" ;}
		this.piclist = temp ;
		temp = prop.getProperty("isTest") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property isTest is null , use isTest=0 ...") ; temp="0" ;}
		this.isTest = Integer.parseInt( temp ) ;
		temp = prop.getProperty("Cookie") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property Cookie is null , use Cookie= ...") ; temp="" ;}
		this.Cookie = temp ;
		temp = prop.getProperty("UseCookie") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property UseCookie is null , use UseCookie=0 ...") ; temp="0" ;}
		this.UseCookie = Integer.parseInt( temp );
		temp = prop.getProperty("RegForImage") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property RegForImage is null !!! ") ; temp = "" ;}
		this.RegForImage = temp ;
		temp = prop.getProperty("RegForHtml") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property RegForHtml is error !!! ") ; temp = "" ;}
		this.RegForHtml = temp ;
		temp = prop.getProperty("FlagForHtml") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property FlagForHtml is null , use FlagForHtml=NULL ...") ; temp="NULL" ;}
		this.FlagForHtml =temp ;
		temp = prop.getProperty("imgUrlReplace") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property imgUrlReplace is null , use imgUrlReplace=NULL ...") ; temp="NULL" ;}
		this.imgUrlReplace =temp ;
		
		temp = prop.getProperty("filterflag") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property filterflag is null , use filterflag=0 ...") ; temp="0" ;}
		this.filterflag = Integer.parseInt( temp ) ;
		temp = prop.getProperty("NumForImage") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property NumForImage is null !!! ") ; temp = "1" ;}
		this.NumForImage = Integer.parseInt( temp );
		temp = prop.getProperty("NumForHtml") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property NumForHtml is null !!! ") ; temp = "1" ;}
		this.NumForHtml = Integer.parseInt( temp );
		temp = prop.getProperty("DirForJpg") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property DirForJpg is null , use DirForJpg=./jpg ...") ; temp="./jpg" ;}
		this.DirForJpg = temp ;
		temp = prop.getProperty("EncodeType") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property EncodeType is null , use EncodeType=GB18030 ...") ; temp="GB18030" ;}
		this.EncodeType = temp ;
		temp = prop.getProperty("isSaveByTitle") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property isSaveByTitle is null , use isSaveByTitle=1 ...") ; temp="1" ;}
		this.isSaveByTitle = Integer.parseInt( temp ) ;
		temp = prop.getProperty("TitleSeparator") ;
		if(  temp == null ) {System.out.println( "Info: Property TitleSeparator is null , use TitleSeparator= ...") ; temp="" ;}
		this.TitleSeparator = temp ;
		temp = prop.getProperty("DefPostfix") ;
		if(  temp == null ) {System.out.println( "Info: Property TitleSeparator is null , use TitleSeparator=jpg ...") ; temp="jpg" ;}
		this.defPostfix = temp ;
		temp = prop.getProperty("UrlForCookie") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property UrlForCookie is null !!! ") ; temp = "" ;}
		this.UrlForCookie = temp ;
			
		temp = prop.getProperty("StrForCookie") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property StrForCookie is null !!! ") ; temp = "" ;}
		this.StrForCookie = temp ;
		
		temp = prop.getProperty("RegForList") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property RegForList is null !!! ") ; temp = "" ;}
		this.RegForList = temp ;
			
		temp = prop.getProperty("NumForList") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Error: Property NumForList is null !!! ") ; temp = "1" ;}
		this.NumForList = Integer.parseInt( temp );			
		
		temp = prop.getProperty("ThreadNum") ;
		if(  temp == null || temp.length() == 0 ) {System.out.println( "Info: Property ThreadNum is null , use ThreadNum=5 ...") ; temp="5" ;}
		this.ThreadNum = Integer.parseInt( temp ) ;
		if( this.ThreadNum < 1 ) this.ThreadNum = 5 ;
		
		this.AllFilterList = this.filterlist ;
		if( Log.isFile(this.filterFile) )
		{
			this.AllFilterList += Log.readString( this.filterFile ) ;
		}
		
		return "[INFO] LOAD "+fileName+" SUCCESS !";

	}
	
	private int getValid()
	{
		int res_ind = -1 ;
		for( int i = 0 ; i < downloadThreads.length ; i++ ) 
		{
			if( downloadThreads[i]== null || downloadThreads[i].t.isAlive() == false ) 
			{
				res_ind = i ;
				break ;
			}
		}
		return res_ind ;
	}

	@SuppressWarnings("unused")
	private int getValidNum()
	{
		int res = 0 ;
		for( int i = 0 ; i < downloadThreads.length ; i++ )
		{
			if( downloadThreads[i]!= null && downloadThreads[i].t.isAlive() == true ) 
			{
				res++ ;
			}
		}
		return res ;
	}

	public String printThreadUrl()
	{		
		if( this.runflag != 1 ) return "[INFO] PROCESS NOT RUNNING !";
		String logString = "[INFO]<br>CurrentUrl: "+this.CurrentUrl+"<br>CurrentDepth: "+this.CurrentDepth+"<br>" ;
		java.util.Date nowDate = new java.util.Date() ;
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]") ;
		for( int i = 0 ; i < downloadThreads.length ; i++ )
		{
			if( downloadThreads[i]!= null && downloadThreads[i].t.isAlive() )
			{
				logString += downloadThreads[i].getStartTime()+" ("+i+") : "+downloadThreads[i].getUrl()+" --> "+downloadThreads[i].getFileName()+"<br>"  ;
			}
			else
			{
				logString += formatter.format(nowDate)+" ("+i+") : WAITING<br>" ;
			}
		}
		return logString ;
	}
	
	private String ImgUrlReplace( String imgUrl )
	{
		if( imgUrlReplace.equals("NULL") ) return imgUrl ;
		
		String rst = imgUrl ;
		int flag = 0 ;
		int rflag = 0 ;
		String olds = "" ;
		String news = "" ;
		if( imgUrlReplace.indexOf("(OLD=") != -1 )
		{
			String[] str = GetRexgexStrings(imgUrlReplace,"\\(OLD=([^\\)]+)\\)",1);
			if( str.length > 0 )
			{
				if( str[0].length() > 0 )
				{
					olds = str[0]  ; flag++ ; 
				}				
			}
		}
		if( imgUrlReplace.indexOf("(NEW=") != -1 )
		{
			String[] str = GetRexgexStrings(imgUrlReplace,"\\(NEW=([^\\)]+)\\)",1);
			if( str.length > 0 )
			{
				if( str[0].length() > 0 )
				{
					news = str[0]  ; flag++ ; 
				}				
			}
		}
		if( imgUrlReplace.indexOf("(FLAG=") != -1 )
		{
			String[] str = GetRexgexStrings(imgUrlReplace,"\\(FLAG=([0-9]+)\\)",1);
			if( str.length > 0 )
			{
				if( str[0].length() > 0 )
				{
					rflag = Integer.parseInt( str[0] ) ;
				}				
			}
		}
		
		if( flag == 2 ) 
		{
			if( rflag == 0 )
			{
				rst = imgUrl.replaceAll( olds , news ) ;
			}
			else
			{
				rst = imgUrl.replace( olds , news ) ;				
			}
		}
		
		return rst ;
	}
	
	private boolean isGetUrl( String oUrl )
	{
		if( FlagForHtml.equals("NULL") ) return true ;
		
		boolean rst = true ;
		if( FlagForHtml.indexOf("(noGetUrl") != -1 )
		{
			String[] str = GetRexgexStrings(FlagForHtml,"\\(noGetUrl=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( oUrl.indexOf( str[i] ) != -1 ) rst = false ;
			}
			if( rst == false ) return rst ;		
		}
		if( FlagForHtml.indexOf("(GetUrl") != -1 )
		{
			rst = false ;
			String[] str = GetRexgexStrings(FlagForHtml,"\\(GetUrl=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( oUrl.indexOf( str[i] ) != -1 ) rst = true ;
			}
			
		}
		return rst ;
	}

	private boolean isGetImg( String oUrl )
	{
		if( FlagForHtml.equals("NULL") ) return true ;
		
		boolean rst = true ;
		if( FlagForHtml.indexOf("(noGetImg") != -1 )
		{
			String[] str = GetRexgexStrings(FlagForHtml,"\\(noGetImg=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( oUrl.indexOf( str[i] ) != -1 ) rst = false ;
			}
			if( rst == false ) return rst ;
		}
		
		if( FlagForHtml.indexOf("(GetImg") != -1 )
		{
			rst = false ;
			String[] str = GetRexgexStrings(FlagForHtml,"\\(GetImg=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( oUrl.indexOf( str[i] ) != -1 ) rst = true ;
			}
			
		}
		return rst ;
	}
	
	private boolean isGetFromUrl( String oUrl , String nUrl )
	{
		if( FlagForHtml.equals("NULL") ) return true ;
		
		boolean rst = true ;
		if( FlagForHtml.indexOf("(Include") != -1 || FlagForHtml.indexOf("(Compare") != -1 || FlagForHtml.indexOf("(CompRegex") != -1 ) rst = false ;
		
		if( FlagForHtml.indexOf("(Include") != -1 )
		{
			String[] str = GetRexgexStrings(FlagForHtml,"\\(Include=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( oUrl.indexOf( str[i] ) != -1 ) rst = true ;
			}			
		}
		
		if( FlagForHtml.indexOf("(Compare") != -1 )
		{
			String[] str = GetRexgexStrings(FlagForHtml,"\\(Compare=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( oUrl.lastIndexOf(str[i]) != -1 ) if( (nUrl.indexOf( oUrl.substring(0,oUrl.lastIndexOf(str[i]) ) ) != -1 ) ) rst = true ;
			}
		}
		
		if( FlagForHtml.indexOf("(CompRegex") != -1 )
		{
			String[] str = GetRexgexStrings(FlagForHtml,"\\(CompRegex=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				String[] str1 = GetRexgexStrings(oUrl,str[i],0) ;
//				String[] str2 = GetRexgexStrings(nUrl,str[i],0) ;
//				if( str1.length > 0 && str2.length > 0 )
				if( str1.length > 0 )
				{
					//System.out.println(" RegexComp ===> "+str1[0]+" --- " +str2[0] ) ;
//					if( str1[0].equals(str2[0]) ) rst = true ;
					if( nUrl.indexOf( str1[0] ) != -1 )  rst = true ;
				}				
			}
		}
		
		if( rst == true && FlagForHtml.indexOf("(noInclude") != -1 )
		{			
			String[] str = GetRexgexStrings(FlagForHtml,"\\(noInclude=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( nUrl.indexOf( str[i] ) != -1 ) rst = false ;
			}
		}
		
		return rst ;
	}
	
	private boolean isDownImg( String imgUrl )
	{
		if( FlagForHtml.equals("NULL") ) return true ;
		
		boolean rst = true ;
		if( FlagForHtml.indexOf("(noDownImg") != -1 )
		{
			String[] str = GetRexgexStrings(FlagForHtml,"\\(noDownImg=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( imgUrl.indexOf( str[i] ) != -1 ) rst = false ;
			}
			if( rst == false ) return rst ;
		}
		
		if( FlagForHtml.indexOf("(DownImg") != -1 )
		{
			rst = false ;
			String[] str = GetRexgexStrings(FlagForHtml,"\\(DownImg=([^\\)]+)\\)",1);
			for( int i = 0 ; i < str.length ; i++ )
			{
				if( imgUrl.indexOf( str[i] ) != -1 ) rst = true ;
			}
			
		}
		return rst ;
	}

	private String[] GetRexgexStrings( String content , String regex ,  int Index )
	{
		ArrayList<String> list = new ArrayList<String>() ;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while(matcher.find())
		{
			list.add(matcher.group(Index));
		}
		String[] strs = ( String[] ) list.toArray( new String[0] ) ;
		return strs ;
	}

	private String DealFilename( String filename )
	{
		String rst = filename.replaceAll("\\?","_").replaceAll("=","_").replaceAll("#","_");
		if( rst.indexOf("." ) != -1 )
		{
			String temp = rst.substring(  rst.lastIndexOf("." ) ) ;
			if( temp.length() > 5 ) rst += "."+defPostfix ;
			if( temp == ".com" ) rst += "."+defPostfix ;
		}
		else
		{
			rst += "."+defPostfix ;
		}

		return rst ;
	}

	private String DealUrl( String url1 )
	{
		String url = url1 ;
		int index1 = url.lastIndexOf("//");
		while( index1 > 6 )
		{
			url = url.substring(0,index1) + url.substring(index1+1) ;
			index1 = url.lastIndexOf("//");
		}

		index1 = url.lastIndexOf("/./");
		while( index1 > 0 )
		{
			url = url.substring(0,index1) + url.substring(index1+2) ;
			index1 = url.lastIndexOf("/./") ;
		}

		index1 = url.indexOf("/../") ;
		while( index1 > 0 )
		{
			int index2 = url.substring(0,index1).lastIndexOf('/') ;
			url = url.substring(0,index2) + url.substring(index1+3) ;
			index1 = url.indexOf("/../") ;
		}
		return url ;
	}
	
	public String SaveRunningPropFile(String fileName )
	{
		Log.removeFile( fileName ) ;
		String content = "# prop for GetImage \n\n" ;
		content +="\n" ;
		content += "# url for start to get\n" ;
		content += "Url="+Url+"\n" ;
		content += "# encoding type : UTF-8 , GB2312\n" ;
		content += "EncodeType="+EncodeType+"\n" ;
		content += "\n" ;
		content += "# flag for test\n" ;
		content += "# 1: show image urls and html urls\n" ;
		content += "# 2: show html content , and contain 1 \n" ;
		content += "isTest="+isTest+"\n" ;
		content += "\n" ;
		content += "# depth for get\n" ;
		content += "# >0: to get Depthes\n" ;
		content += "# -1: to get all\n" ;
		content += "Depth="+Depth+"\n" ;
		content += "# save mulu\n" ;
		content += "DirForJpg="+DirForJpg+"\n" ;
		content += "# save jpg in each mulu named by title\n" ;
		content += "isSaveByTitle="+isSaveByTitle+"\n" ;
		content += "# Title separator , get foreside from title , last index \n" ;
		content += "TitleSeparator="+TitleSeparator+"\n" ;
		content += "# thread num to down load picture\n" ;
		content += "ThreadNum="+ThreadNum+"\n" ;
		content += "\n" ;
		content += "# operate for repeate html\n" ;
		content += "# 0: return if repeate\n" ;
		content += "# 1: get urls if repeate once on this runtime\n" ;
		content += "RepeateOperate="+RepeateOperate+"\n" ;
		content += "\n" ;
		content += "# flag for Cookie\n" ;
		content += "# 0: not use Cookie ; 1: use Cookie ; 2: to get Cookie ; 3: use Cookie include download image\n" ;
		content += "UseCookie="+UseCookie+"\n" ;
		content += "# Cookie string ; used if userCookie=1\n" ;
		content += "Cookie="+Cookie+"\n" ;
		content += "\n" ;
		content += "# url for get Cookie ; used if UserCookie=2\n" ;
		content += "UrlForCookie="+UrlForCookie+"\n" ;
		content += "# post string for get Cookie ; used if UserCookie=2\n" ;
		content += "StrForCookie="+StrForCookie+"\n" ;
		content += "\n" ;
		content += "# Regex for get image url\n" ;
		content += "RegForImage="+RegForImage.replaceAll("\\\\","\\\\\\\\")+"\n" ;
		content += "NumForImage="+NumForImage+"\n" ;
		content += "# Regex for get hyperlink url\n" ;
		content += "RegForHtml="+RegForHtml.replaceAll("\\\\","\\\\\\\\")+"\n" ;
		content += "NumForHtml="+NumForHtml+"\n" ;
		content += "# get hyperlink url flag\n" ;
		content += "# FlagForHtml=NULL : get all urls\n" ;
		content += "# FlagForHtml=(GetUrl=index)(GetUrl=list)(Compare=_)(Compare=.)(Include=index)(Include=list)(noGetUrl=tu)(noGetImg=list)(CompRegex=/[0-9]+/)\n" ;
		content += "#     GetUrl: get urls which url include set chars\n" ;
		content += "#     noGetUrl: not get urls which url include set chars\n" ;
		content += "#     GetImg: get image which url include set chars\n" ;
		content += "#     noGetImg: not get image url which url include set chars\n" ;
		content += "#     DownImg: get image which image url include set chars\n" ;
		content += "#     noDownImg: not get image url which image url include set chars\n" ;
		content += "#     Include: getImage from new url which old url include set chars\n" ;
		content += "#     Compare: getImage from new url which Compare old url before set char\n" ;
		content += "#     CompRegex: getImage from new url which Compare old url get by regex string\n" ;		
		content += "#     noInclude: not getImage from new url which include set chars , if Include Compare CompRegex result true\n" ;
		content += "#     GetUrl noGetUrl used for isGetUrl() , and noGetUrl > GetUrl\n" ;
		content += "#     GetImg noGetImg used for isGetImg() , and noGetImg > GetImg\n" ;
		content += "#     Include Compare CompRegex noInclude used for isGetFromUrl() , and Include : Compare : CompRegex > noInclude\n" ;
		content += "#     noDownImg: not to down image which image url inclue set chars\n" ;
		content += "#     DownImg: to down image which image url inclue set chars , not to down image which not inclue set chars\n" ;
		content += "#     noDownImg DownImg only use one \n" ;
		content += "FlagForHtml="+FlagForHtml+"\n" ;
		content += "\n" ;
		content += "# modify imgurl to get img\n" ;
		content += "# imgUrlReplace=(OLD=/big/)(NEW=/pic/)(FALG=0)\n" ;
		content += "#     OLD: old url chars \n" ;
		content += "#     NEW: new url chars\n" ;
		content += "#     FALG: 0- replace all ; 1-replace first\n" ;
		content += "imgUrlReplace="+imgUrlReplace+"\n" ;
		content += "\n" ;
		content += "# set defualt postfix ; use if getimage file have no postfix\n" ;
		content += "DefPostfix="+defPostfix+"\n" ;
		content += "\n" ;
		content += "# flag for filter html\n" ;
		content += "# 0: url list\n" ;
		content += "# 1: tid list (m6mm)\n" ;
		content += "filterflag="+filterflag+"\n" ;
		content += "# file name for record filter\n" ;
		content += "filterFile="+filterFile+"\n" ;
		content += "# list of html which already got\n" ;
		content += "# filterflag=0 list for url , form is +URL-\n" ;
		content += "# filterflag=1 list for tid\n" ;
		content += "filterlist="+filterlist+"\n" ;
		content += "\n" ;
		content += "# list of pictrue which not to download , form is +picname-\n" ;
		content += "piclist="+piclist+"\n" ;
		content += "\n" ;
		content += "# Regex string for get tid ; used when filtflag=1\n" ;
		content += "RegForList="+RegForList.replaceAll("\\\\","\\\\\\\\")+"\n" ;
		content += "NumForList="+NumForList+"\n" ;
		content += "\n" ;

		Log.writeString(fileName , content) ;
		System.out.println("Info: save prop to "+fileName+" success !" );
		return "[INFO] Save Prop to "+fileName+" Success !" ;
	}
	
	public String showRunningProp()
	{
		String content ;
		content = "===============================================================<br>\n" ;
		content += "# prop for GetImage <br>\n" ;
		content +="<br>\n" ;
		content += "# url for start to get<br>\n" ;
		content += "Url="+Url+"<br>\n" ;
		content += "# encoding type : UTF-8 , GB2312<br>\n" ;
		content += "EncodeType="+EncodeType+"<br>\n" ;
		content += "<br>\n" ;
		content += "# flag for test<br>\n" ;
		content += "# 1: show image urls and html urls<br>\n" ;
		content += "# 2: show html content , and contain 1 <br>\n" ;
		content += "isTest="+isTest+"<br>\n" ;
		content += "<br>\n" ;
		content += "# depth for get<br>\n" ;
		content += "# >0: to get Depthes<br>\n" ;
		content += "# -1: to get all<br>\n" ;
		content += "Depth="+Depth+"<br>\n" ;
		content += "# save mulu<br>\n" ;
		content += "DirForJpg="+DirForJpg+"<br>\n" ;
		content += "# save jpg in each mulu named by title<br>\n" ;
		content += "isSaveByTitle="+isSaveByTitle+"<br>\n" ;
		content += "# Title separator , get foreside from title , last index <br>\n" ;
		content += "TitleSeparator="+TitleSeparator+"<br>\n" ;
		content += "# thread num to down load picture<br>\n" ;
		content += "ThreadNum="+ThreadNum+"<br>\n" ;
		content += "<br>\n" ;
		content += "# operate for repeate html<br>\n" ;
		content += "# 0: return if repeate<br>\n" ;
		content += "# 1: get urls if repeate once on this runtime<br>\n" ;
		content += "RepeateOperate="+RepeateOperate+"<br>\n" ;
		content += "<br>\n" ;
		content += "# flag for Cookie<br>\n" ;
		content += "# 0: not use Cookie ; 1: use Cookie ; 2: to get Cookie ; 3: use Cookie include download image<br>\n" ;
		content += "UseCookie="+UseCookie+"<br>\n" ;
		content += "# Cookie string ; used if userCookie=1<br>\n" ;
		content += "Cookie="+Cookie+"<br>\n" ;
		content += "<br>\n" ;
		content += "# url for get Cookie ; used if UserCookie=2<br>\n" ;
		content += "UrlForCookie="+UrlForCookie+"<br>\n" ;
		content += "# post string for get Cookie ; used if UserCookie=2<br>\n" ;
		content += "StrForCookie="+StrForCookie+"<br>\n" ;
		content += "<br>\n" ;
		content += "# Regex for get image url<br>\n" ;
		content += "RegForImage="+RegForImage.replaceAll("\\\\","\\\\\\\\")+"<br>\n" ;
		content += "NumForImage="+NumForImage+"<br>\n" ;
		content += "# Regex for get hyperlink url<br>\n" ;
		content += "RegForHtml="+RegForHtml.replaceAll("\\\\","\\\\\\\\")+"<br>\n" ;
		content += "NumForHtml="+NumForHtml+"<br>\n" ;
		content += "# get hyperlink url flag<br>\n" ;
		content += "# FlagForHtml=NULL : get all urls<br>\n" ;
		content += "# FlagForHtml=(GetUrl=index)(GetUrl=list)(Compare=_)(Compare=.)(Include=index)(Include=list)(noGetUrl=tu)(noGetImg=list)(CompRegex=/[0-9]+/)<br>\n" ;
		content += "#     GetUrl: get urls which url include set chars<br>\n" ;
		content += "#     noGetUrl: not get urls which url include set chars<br>\n" ;
		content += "#     GetImg: get image which url include set chars<br>\n" ;
		content += "#     noGetImg: not get image url which url include set chars<br>\n" ;
		content += "#     DownImg: get image which image url include set chars<br>\n" ;
		content += "#     noDownImg: not get image url which image url include set chars<br>\n" ;
		content += "#     Include: getImage from new url which old url include set chars<br>\n" ;
		content += "#     Compare: getImage from new url which Compare old url before set char<br>\n" ;
		content += "#     CompRegex: getImage from new url which Compare old url get by regex string<br>\n" ;		
		content += "#     noInclude: not getImage from new url which include set chars , if Include Compare CompRegex result true<br>\n" ;
		content += "#     GetUrl noGetUrl used for isGetUrl() , and noGetUrl > GetUrl<br>\n" ;
		content += "#     GetImg noGetImg used for isGetImg() , and noGetImg > GetImg<br>\n" ;
		content += "#     Include Compare CompRegex noInclude used for isGetFromUrl() , and Include : Compare : CompRegex > noInclude<br>\n" ;
		content += "#     noDownImg: not to down image which image url inclue set chars<br>\n" ;
		content += "#     DownImg: to down image which image url inclue set chars , not to down image which not inclue set chars<br>\n" ;
		content += "#     noDownImg DownImg only use one <br>\n" ;
		content += "FlagForHtml="+FlagForHtml+"<br>\n" ;
		content += "<br>\n" ;
		content += "# modify imgurl to get img<br>\n" ;
		content += "# imgUrlReplace=(OLD=/big/)(NEW=/pic/)(FALG=0)<br>\n" ;
		content += "#     OLD: old url chars <br>\n" ;
		content += "#     NEW: new url chars<br>\n" ;
		content += "#     FALG: 0- replace all ; 1-replace first<br>\n" ;
		content += "imgUrlReplace="+imgUrlReplace+"<br>\n" ;
		content += "<br>\n" ;
		content += "# set defualt postfix ; use if getimage file have no postfix<br>\n" ;
		content += "DefPostfix="+defPostfix+"<br>\n" ;
		content += "<br>\n" ;
		content += "# flag for filter html<br>\n" ;
		content += "# 0: url list<br>\n" ;
		content += "# 1: tid list (m6mm)<br>\n" ;
		content += "filterflag="+filterflag+"<br>\n" ;
		content += "# file name for record filter<br>\n" ;
		content += "filterFile="+filterFile+"<br>\n" ;
		content += "# list of html which already got<br>\n" ;
		content += "# filterflag=0 list for url , form is +URL-<br>\n" ;
		content += "# filterflag=1 list for tid<br>\n" ;
		content += "filterlist="+filterlist+"<br>\n" ;
		content += "<br>\n" ;
		content += "# list of pictrue which not to download , form is +picname-<br>\n" ;
		content += "piclist="+piclist+"<br>\n" ;
		content += "<br>\n" ;
		content += "# Regex string for get tid ; used when filtflag=1<br>\n" ;
		content += "RegForList="+RegForList.replaceAll("\\\\","\\\\\\\\")+"<br>\n" ;
		content += "NumForList="+NumForList+"<br>\n" ;
		content += "<br>\n" ;
		content += "===============================================================<br>\n";
		return content ;
	}
		
	public String SaveDefultPropFile(String fileName)
	{
		Log.removeFile( fileName ) ;
		String content = "# prop for GetImage \n\n" ;
		content +="\n" ;
		content += "# url for start to get\n" ;
		content += "Url=http://www.m6mm.com/read.php?tid=1177902\n" ;
		content += "# encoding type : UTF-8 , GB2312\n" ;
		content += "#EncodeType=UTF-8\n" ;
		content += "EncodeType=GB18030\n" ;
		content += "\n" ;
		content += "# flag for test\n" ;
		content += "# 1: show image urls and html urls\n" ;
		content += "# 2: show html content , and contain 1 \n" ;
		content += "isTest=1\n" ;
		content += "\n" ;
		content += "# depth for get\n" ;
		content += "# >0: to get Depthes\n" ;
		content += "# -1: to get all\n" ;
		content += "Depth=100\n" ;
		content += "# save mulu\n" ;
		content += "DirForJpg=./jpg/m6mm/weimei\n" ;
		content += "# save jpg in each mulu named by title\n" ;
		content += "isSaveByTitle=1\n" ;
		content += "# Title separator , get foreside from title , last index \n" ;
		content += "TitleSeparator=P[\n" ;
		content += "# thread num to down load picture\n" ;
		content += "ThreadNum=10\n" ;
		content += "\n" ;
		content += "# operate for repeate html\n" ;
		content += "# 0: return if repeate\n" ;
		content += "# 1: get urls if repeate once on this runtime\n" ;
		content += "RepeateOperate=0\n" ;
		content += "\n" ;
		content += "# flag for Cookie\n" ;
		content += "# 0: not use Cookie ; 1: use Cookie ; 2: to get Cookie ; 3: use Cookie include download image\n" ;
		content += "UseCookie=1\n" ;
		content += "# Cookie string ; used if userCookie=1\n" ;
		content += "Cookie=lastfid=0;lastvisit=0%091325084900%09%2Flogin.php%3F;ck_info=%2F%09;winduser=Dw8FDQVaOFRUCVQOUFICAFdbW1MDBwBaBAcAXQEKBwFRDlBYXg8C;lastvisit=deleted;\n" ;
		content += "\n" ;
		content += "# url for get Cookie ; used if UserCookie=2\n" ;
		content += "UrlForCookie=http://www.m6mm.com/login.php?\n" ;
		content += "# post string for get Cookie ; used if UserCookie=2\n" ;
		content += "StrForCookie=pwuser=renhongbo777&pwpwd=19790930&hideid=0&cktime=31536000&jumpurl=http%3A%2F%2Fwww.m6mm2009.info%2Findex.php&step=2\n" ;
		content += "\n" ;
		content += "# Regex for get image url\n" ;
		content += "RegForImage=(?x)src=('|\")*((http://([\\w-]+\\.)+[\\w-]+(:[0-9]+)*)*(/)*([\\S]*+/)*([\\S]*\\.(jpg|JPG|jpeg|JPEG)))('|\")*\n" ;
		content += "NumForImage=2\n" ;
		content += "# Regex for get hyperlink url\n" ;
		content += "RegForHtml=(?x)href='(job.php\\?rd_previous=[0-9]*\\&fid=[0-9]*\\&tid=[0-9]*\\&fpage=\\&goto=(previous|next))'\n" ;
		content += "NumForHtml=1\n" ;
		content += "# get hyperlink url flag\n" ;
		content += "# FlagForHtml=NULL : get all urls\n" ;
		content += "# FlagForHtml=(GetUrl=index)(GetUrl=list)(Compare=_)(Compare=.)(Include=index)(Include=list)(noGetUrl=tu)(noGetImg=list)(CompRegex=/[0-9]+/)\n" ;
		content += "#     GetUrl: get urls which url include set chars\n" ;
		content += "#     noGetUrl: not get urls which url include set chars\n" ;
		content += "#     GetImg: get image which url include set chars\n" ;
		content += "#     noGetImg: not get image url which url include set chars\n" ;
		content += "#     DownImg: get image which image url include set chars\n" ;
		content += "#     noDownImg: not get image url which image url include set chars\n" ;
		content += "#     Include: getImage from new url which old url include set chars\n" ;
		content += "#     Compare: getImage from new url which Compare old url before set char\n" ;
		content += "#     CompRegex: getImage from new url which Compare old url get by regex string\n" ;		
		content += "#     noInclude: not getImage from new url which include set chars , if Include Compare CompRegex result true\n" ;
		content += "#     GetUrl noGetUrl used for isGetUrl() , and noGetUrl > GetUrl\n" ;
		content += "#     GetImg noGetImg used for isGetImg() , and noGetImg > GetImg\n" ;
		content += "#     Include Compare CompRegex noInclude used for isGetFromUrl() , and Include : Compare : CompRegex > noInclude\n" ;
		content += "#     noDownImg: not to down image which image url inclue set chars\n" ;
		content += "#     DownImg: to down image which image url inclue set chars , not to down image which not inclue set chars\n" ;
		content += "#     noDownImg DownImg only use one \n" ;
		content += "FlagForHtml=NULL\n" ;
		content += "\n" ;
		content += "# modify imgurl to get img\n" ;
		content += "# imgUrlReplace=(OLD=/big/)(NEW=/pic/)(FALG=0)\n" ;
		content += "#     OLD: old url chars \n" ;
		content += "#     NEW: new url chars\n" ;
		content += "#     FALG: 0- replace all ; 1-replace first\n" ;
		content += "imgUrlReplace=(OLD=/big/)(NEW=/pic/)(FALG=0)\n" ;
		content += "\n" ;
		content += "# set defualt postfix ; use if getimage file have no postfix\n" ;
		content += "DefPostfix=jpg\n" ;
		content += "\n" ;
		content += "# flag for filter html\n" ;
		content += "# 0: url list\n" ;
		content += "# 1: tid list (m6mm)\n" ;
		content += "filterflag=0\n" ;
		content += "# file name for record filter\n" ;
		content += "filterFile=./m6mmlist.log\n" ;
		content += "# list of html which already got\n" ;
		content += "# filterflag=0 list for url , form is +URL-\n" ;
		content += "# filterflag=1 list for tid\n" ;
		content += "filterlist=FILTER:\n" ;
		content += "\n" ;
		content += "# list of pictrue which not to download , form is +picname-\n" ;
		content += "piclist=+gpic.jpg-+21ao.jpg-+iii.jpg-+luo.jpg-+haizei1024_3.jpg-+2a0gjyr.jpg-+th_e923ed88aab022bfa5c27245.jpg-+sampdc13a35fb563007c.jpg-+20081103083413_9877.jpg-+img201001232336579.jpg-+banner2.jpg-+3678027424_1e7d8845df_o.jpg-+6paws7.jpg-+image_4ec53fd3eeea4_small.jpg-+293087_n.jpg-+97b523c27fa758ffeaddd0f06de06a9a.jpg-+8998681bgw1dnpefmmrfzj.jpg-+10089222.jpg-\n" ;
		content += "\n" ;
		content += "# Regex string for get tid ; used when filtflag=1\n" ;
		content += "RegForList=(?x)href='sendemail.php\\?action=tofriend\\&tid=([0-9]*)'\n" ;
		content += "NumForList=1\n" ;
		content += "\n" ;

		Log.writeString(fileName , content) ;
		System.out.println("Info: "+fileName+" had produced !" );
		return "[INFO] "+fileName+" had produced !" ;
	}
	
	
	public int  getThreadNum()
	{
		return this.ThreadNum ;
	}
	public synchronized void setThreadNum(int value)
	{
		this.ThreadNum = value ;
	}
	
	public String showAllFilterList()
	{
		return "["+this.AllFilterList+"]" ;
	}
	
	public String reloadAllFilterList()
	{
		this.AllFilterList = this.filterlist ;
		if( Log.isFile(this.filterFile) )
		{
			this.AllFilterList += Log.readString( this.filterFile ) ;
		}
		return showAllFilterList() ;
	}
	public synchronized int getrunflag() 
	{
		return this.runflag ;
	}
	
	public String  getUrl()
	{
		return this.Url ;
	}
	public synchronized void setUrl(String value)
	{
		this.Url = value ;
	}
	
	public int  getDepth()
	{
		return this.Depth ;
	}
	public synchronized void setDepth(int value)
	{
		this.Depth = value ;
	}
	
	public int  getfilterflag()
	{
		return this.filterflag ;
	}
	public synchronized void setfilterflag(int value)
	{
		this.filterflag = value ;
	}
	
	public int  getUseCookie()
	{
		return this.UseCookie ;
	}
	public synchronized void setUseCookie(int value)
	{
		this.UseCookie = value ;
	}
	
	public int  getisSaveByTitle()
	{
		return this.isSaveByTitle ;
	}
	public synchronized void setisSaveByTitle(int value)
	{
		this.isSaveByTitle = value ;
	}
	
	public int  getisTest()
	{
		return this.isTest ;
	}
	public synchronized void setisTest(int value)
	{
		this.isTest = value ;
	}
	
	public int  getNumForHtml()
	{
		return this.NumForHtml ;
	}
	public synchronized void setNumForHtml(int value)
	{
		this.NumForHtml = value ;
	}
	
	public int  getNumForImage()
	{
		return this.NumForImage ;
	}
	public synchronized void setNumForImage(int value)
	{
		this.NumForImage = value ;
	}
			
	public int  getRepeateOperate()
	{
		return this.RepeateOperate ;
	}
	public synchronized void setRepeateOperate(int value)
	{
		this.RepeateOperate = value ;
	}
	
	public int  getNumForList()
	{
		return this.NumForList ;
	}
	public synchronized void setNumForList(int value)
	{
		this.NumForList = value ;
	}
	
	public String getdefPostfix()
	{
		return this.defPostfix ;
	}
	public synchronized void setdefPostfix(String str)
	{
		this.defPostfix = str ;
	}
	
	public String getimgUrlReplace()
	{
		return this.imgUrlReplace ;
	}
	public synchronized void setimgUrlReplace(String str)
	{
		this.imgUrlReplace = str ;
	}
	
	public String getrepeateUrl()
	{
		return this.repeateUrl ;
	}
	public synchronized void setrepeateUrl(String str)
	{
		this.repeateUrl = str ;
	}
	
	public String getTitleSeparator()
	{
		return this.TitleSeparator ;
	}
	public synchronized void setTitleSeparator(String str)
	{
		this.TitleSeparator = str ;
	}
	
	public String getRegForList()
	{
		return this.RegForList ;
	}
	public synchronized void setRegForList(String str)
	{
		this.RegForList = str ;
	}
	
	public String getStrForCookie()
	{
		return this.StrForCookie ;
	}
	public synchronized void setStrForCookie(String str)
	{
		this.StrForCookie = str ;
	}
	
	public String getUrlForCookie()
	{
		return this.UrlForCookie ;
	}
	public synchronized void setUrlForCookie(String str)
	{
		this.UrlForCookie = str ;
	}
	
	public String getEncodeType()
	{
		return this.EncodeType ;
	}
	public synchronized void setEncodeType(String str)
	{
		this.EncodeType = str ;
	}
	
	public String getDirForJpg()
	{
		return this.DirForJpg ;
	}
	public synchronized void setDirForJpg(String str)
	{
		this.DirForJpg = str ;
	}
	
	public String getFlagForHtml()
	{
		return this.FlagForHtml ;
	}
	public synchronized void setFlagForHtml(String str)
	{
		this.FlagForHtml = str ;
	}
	
	public String getRegForImage()
	{
		return this.RegForImage ;
	}
	public synchronized void setRegForImage(String reg)
	{
		this.RegForImage = reg ;
	}
	
	public String getRegForHtml()
	{
		return this.RegForHtml ;
	}
	public synchronized void setRegForHtml(String reg)
	{
		this.RegForHtml = reg ;
	}
	public String getCookie()
	{
		return this.Cookie ;
	}
	public synchronized void setCookie(String cookie)
	{
		this.Cookie = cookie ;
	}

	public String getfilterlist()
	{
		return this.filterlist ;
	}
	public synchronized void setfilterlist(String list)
	{
		this.filterlist = list ;
	}

	public String getfilterfile()
	{
		return this.filterFile ;
	}
	
	public synchronized void setfilterfile(String filename)
	{
		this.filterFile = filename ;
	}
	
	public synchronized String getpiclist()
	{
		return this.piclist ;
	}
	
	public void setpiclist(String picList)
	{
		this.piclist = picList ;
	}


	public String emptyAllFilterList() {
		Log.removeFile(this.filterFile);
		this.AllFilterList = "AllFilterList:" ;
		return "["+this.AllFilterList+"]" ;
	}
	
	


}

