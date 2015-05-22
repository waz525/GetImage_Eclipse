
public class DownloaderThread implements Runnable
{
	String Url  ;
	String FileName ;
	String startTime ;
	String Cookie ;
	Thread t ;
	DownloaderThread( String url , String filename )
	{
		Url = url ;
		FileName = filename ;
		Cookie = "" ;
		java.util.Date nowDate = new java.util.Date() ;
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]") ;
		startTime = formatter.format(nowDate);
		t = new Thread(this , filename);
		t.start();
	}
	
	DownloaderThread( String url , String filename , String cookie )
	{
		Url = url ;
		FileName = filename ;
		Cookie = cookie ;
		java.util.Date nowDate = new java.util.Date() ;
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]") ;
		startTime = formatter.format(nowDate);
		t = new Thread(this , filename);
		t.start();
	}

	public void setParam( String url , String filename )
	{
		Url = url ;
		FileName = filename ;
		java.util.Date nowDate = new java.util.Date() ;
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]") ;
		startTime = formatter.format(nowDate);
		Cookie = "" ;
		t = new Thread(this , filename);
		t.start();
	}	
	
	public void setParam( String url , String filename , String cookie )
	{
		Url = url ;
		FileName = filename ;
		java.util.Date nowDate = new java.util.Date() ;
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]") ;
		startTime = formatter.format(nowDate);
		Cookie = cookie;
		t = new Thread(this , filename);
		t.start();
	}	
	
	public String getUrl()
	{
		return Url ;
	}

	public String getFileName()
	{
		return FileName ;
	}

	public String getStartTime()
	{
		return startTime ;
	}

	public void run()
	{
		//System.out.println( "--- download by thread !" );
		Downloader.download( Url , FileName , Cookie ) ;
		Url="NULL";
	}
}

